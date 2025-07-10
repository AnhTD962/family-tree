package com.domain.backend.service;

import com.domain.backend.dto.FamilyMemberDTO;
import com.domain.backend.dto.response.FamilyTreeResponse;
import com.domain.backend.model.FamilyMember;
import com.domain.backend.model.FamilyTreeHistory;
import com.domain.backend.repository.FamilyMemberRepository;
import com.domain.backend.repository.FamilyTreeHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FamilyMemberService {

    @Autowired
    private FamilyMemberRepository familyMemberRepository;

    @Autowired
    private FamilyTreeHistoryRepository historyRepository;

    @Value("${upload.dir}")
    private String uploadDir;

    public FamilyTreeResponse getFamilyTree() {
        List<FamilyMember> allMembers = familyMemberRepository.findAll();
        List<FamilyMemberDTO> memberDTOs = allMembers.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return new FamilyTreeResponse(memberDTOs);
    }

    public FamilyMemberDTO getFamilyMember(String id) {
        FamilyMember member = familyMemberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        FamilyMemberDTO dto = convertToDTO(member);

        // Spouse
        if (member.getSpouseId() != null && !member.getSpouseId().isBlank()) {
            familyMemberRepository.findById(member.getSpouseId())
                    .ifPresent(spouse -> dto.setSpouse(convertToDTO(spouse)));
        }

        // Father
        if (member.getFatherId() != null && !member.getFatherId().isBlank()) {
            familyMemberRepository.findById(member.getFatherId())
                    .ifPresent(father -> dto.setFather(convertToDTO(father)));
        }

        // Mother
        if (member.getMotherId() != null && !member.getMotherId().isBlank()) {
            familyMemberRepository.findById(member.getMotherId())
                    .ifPresent(mother -> dto.setMother(convertToDTO(mother)));
        }

        // Children
        List<FamilyMember> children = familyMemberRepository.findChildren(id);
        dto.setChildren(children.stream().map(this::convertToDTO).collect(Collectors.toList()));

        // Siblings (chỉ khi có cha hoặc mẹ hợp lệ)
        if ((member.getFatherId() != null && !member.getFatherId().isBlank())
                || (member.getMotherId() != null && !member.getMotherId().isBlank())) {
            List<FamilyMember> siblings = familyMemberRepository.findSiblings(
                    member.getFatherId(), member.getMotherId(), member.getId()
            );
            dto.setSiblings(siblings.stream().map(this::convertToDTO).collect(Collectors.toList()));
        } else {
            dto.setSiblings(Collections.emptyList());
        }

        return dto;
    }

    public List<FamilyMemberDTO> searchMembers(String query) {
        List<FamilyMember> members = familyMemberRepository.findByFullNameContainingIgnoreCase(query);
        return members.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private String saveAvatarFile(MultipartFile file) {
        try {
            String extension = Optional.ofNullable(file.getOriginalFilename())
                    .map(f -> f.substring(f.lastIndexOf('.') + 1))
                    .orElse("jpg");

            String filename = UUID.randomUUID() + "." + extension;
            Path path = Paths.get(uploadDir, filename);
            Files.createDirectories(path.getParent());
            Files.write(path, file.getBytes());
            return "/api/images/" + filename;
        } catch (IOException e) {
            throw new RuntimeException("Could not save avatar image", e);
        }
    }

    @Transactional
    public FamilyMemberDTO createFamilyMember(FamilyMemberDTO memberDTO, MultipartFile avatarFile) {
        FamilyMember member = convertToEntity(memberDTO);
        member.setCreatedAt(LocalDateTime.now());
        member.setUpdatedAt(LocalDateTime.now());
        member.setCreatedBy(getCurrentUserId());
        if (avatarFile != null && !avatarFile.isEmpty()) {
            String avatarUrl = saveAvatarFile(avatarFile);
            memberDTO.setAvatarUrl(avatarUrl);
        }

        // Auto fill spouse and parents
        autoFillSpouseAndParents(member);

        // Calculate generation AFTER filling
        calculateGeneration(member);

        FamilyMember saved = familyMemberRepository.save(member);

        // Update parent-child relationships
        updateParentChildren(saved);

        // Log
        logHistory("CREATE", saved.getId(), saved.getFullName(), "Created new family member");

        return convertToDTO(saved);
    }

    private void updateEntityFromDTO(FamilyMember entity, FamilyMemberDTO dto) {
        entity.setFullName(dto.getFullName());
        entity.setNickname(dto.getNickname());
        entity.setGender(dto.getGender());
        entity.setBirthDate(dto.getBirthDate());
        entity.setDeathDate(dto.getDeathDate());
        entity.setBirthPlace(dto.getBirthPlace());
        entity.setOccupation(dto.getOccupation());
        entity.setDescription(dto.getDescription());
        entity.setAvatarUrl(dto.getAvatarUrl());
        entity.setFatherId(dto.getFatherId());
        entity.setMotherId(dto.getMotherId());
        entity.setSpouseId(dto.getSpouseId());
        entity.setChildrenIds(dto.getChildrenIds());
    }

    @Transactional
    public FamilyMemberDTO updateFamilyMember(String id, FamilyMemberDTO memberDTO, MultipartFile avatarFile) {
        FamilyMember existingMember = familyMemberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Member not found"));
        if (avatarFile != null && !avatarFile.isEmpty()) {
            String avatarUrl = saveAvatarFile(avatarFile);
            memberDTO.setAvatarUrl(avatarUrl);
        } else {
            // Nếu không cập nhật avatar, giữ nguyên ảnh cũ
            memberDTO.setAvatarUrl(existingMember.getAvatarUrl());
        }
        // Gán các trường từ DTO vào entity
        updateEntityFromDTO(existingMember, memberDTO);

        // Set metadata
        existingMember.setUpdatedAt(LocalDateTime.now());
        existingMember.setUpdatedBy(getCurrentUserId());

        // Xử lý logic liên kết và thế hệ
        autoFillSpouseAndParents(existingMember);
        calculateGeneration(existingMember);

        FamilyMember updated = familyMemberRepository.save(existingMember);

        logHistory("UPDATE", updated.getId(), updated.getFullName(), "Updated family member information");

        return convertToDTO(updated);
    }

    @Transactional
    public void deleteFamilyMember(String id) {
        FamilyMember member = familyMemberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        deleteDescendants(id);
        familyMemberRepository.deleteById(id);

        logHistory("DELETE", id, member.getFullName(), "Deleted family member and all descendants");
    }

    private void deleteDescendants(String parentId) {
        List<FamilyMember> children = familyMemberRepository.findChildren(parentId);
        for (FamilyMember child : children) {
            deleteDescendants(child.getId());
            familyMemberRepository.deleteById(child.getId());
        }
    }

    private void calculateGeneration(FamilyMember member) {
        int generation = 0;
        if (member.getFatherId() != null) {
            FamilyMember father = familyMemberRepository.findById(member.getFatherId()).orElse(null);
            if (father != null) {
                generation = father.getGeneration() + 1;
            }
        } else if (member.getMotherId() != null) {
            FamilyMember mother = familyMemberRepository.findById(member.getMotherId()).orElse(null);
            if (mother != null) {
                generation = mother.getGeneration() + 1;
            }
        }
        member.setGeneration(generation);
    }

    private void autoFillSpouseAndParents(FamilyMember member) {
        String memberId = member.getId();

        // 1. Spouse sync and generation sync (bidirectional)
        if (member.getSpouseId() != null) {
            familyMemberRepository.findById(member.getSpouseId()).ifPresent(spouse -> {

                // Generation sync
                if (member.getGeneration() == 0 && spouse.getGeneration() > 0) {
                    member.setGeneration(spouse.getGeneration());
                } else if (spouse.getGeneration() == 0 && member.getGeneration() > 0) {
                    spouse.setGeneration(member.getGeneration());
                    familyMemberRepository.save(spouse);
                }

                // Ensure bidirectional spouse link
                if (memberId != null && !memberId.equals(spouse.getSpouseId())) {
                    if (spouse.getSpouseId() != null && !spouse.getSpouseId().equals(memberId)) {
                        throw new RuntimeException("Người bạn chọn làm vợ/chồng đã kết hôn với người khác.");
                    }

                    // Gán ngược spouseId nếu chưa đúng
                    spouse.setSpouseId(memberId);
                    familyMemberRepository.save(spouse);
                }
            });
        }

        // 2. Fill mother from father's spouse
        if (member.getFatherId() != null && member.getMotherId() == null) {
            familyMemberRepository.findById(member.getFatherId()).ifPresent(father -> {
                if (father.getSpouseId() != null) {
                    member.setMotherId(father.getSpouseId());
                }
            });
        }

        // 3. Fill father from mother's spouse
        if (member.getMotherId() != null && member.getFatherId() == null) {
            familyMemberRepository.findById(member.getMotherId()).ifPresent(mother -> {
                if (mother.getSpouseId() != null) {
                    member.setFatherId(mother.getSpouseId());
                }
            });
        }

        // 4. Update children lists of parents
        updateParentChildren(member);
    }


    private void updateParentChildren(FamilyMember member) {
        if (member.getFatherId() != null) {
            FamilyMember father = familyMemberRepository.findById(member.getFatherId()).orElse(null);
            if (father != null && !father.getChildrenIds().contains(member.getId())) {
                father.getChildrenIds().add(member.getId());
                familyMemberRepository.save(father);
            }
        }

        if (member.getMotherId() != null) {
            FamilyMember mother = familyMemberRepository.findById(member.getMotherId()).orElse(null);
            if (mother != null && !mother.getChildrenIds().contains(member.getId())) {
                mother.getChildrenIds().add(member.getId());
                familyMemberRepository.save(mother);
            }
        }
    }

    private void logHistory(String action, String memberId, String memberName, String details) {
        FamilyTreeHistory history = new FamilyTreeHistory();
        history.setUserId(getCurrentUserId());
        history.setAction(action);
        history.setTargetMemberId(memberId);
        history.setTargetMemberName(memberName);
        history.setDetails(details);
        history.setTimestamp(LocalDateTime.now());

        historyRepository.save(history);
    }

    private String getCurrentUserId() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    private FamilyMemberDTO convertToDTO(FamilyMember member) {
        FamilyMemberDTO dto = new FamilyMemberDTO();
        dto.setId(member.getId());
        dto.setFullName(member.getFullName());
        dto.setNickname(member.getNickname());
        dto.setGender(member.getGender());
        dto.setBirthDate(member.getBirthDate());
        dto.setDeathDate(member.getDeathDate());
        dto.setBirthPlace(member.getBirthPlace());
        dto.setOccupation(member.getOccupation());
        dto.setDescription(member.getDescription());
        dto.setAvatarUrl(member.getAvatarUrl());
        dto.setSpouseId(member.getSpouseId());
        dto.setFatherId(member.getFatherId());
        dto.setMotherId(member.getMotherId());
        dto.setChildrenIds(member.getChildrenIds());
        dto.setGeneration(member.getGeneration());
        dto.setCreatedAt(member.getCreatedAt());
        dto.setUpdatedAt(member.getUpdatedAt());
        return dto;
    }

    private FamilyMember convertToEntity(FamilyMemberDTO dto) {
        FamilyMember member = new FamilyMember();
        member.setId(dto.getId());
        member.setFullName(dto.getFullName());
        member.setNickname(dto.getNickname());
        member.setGender(dto.getGender());
        member.setBirthDate(dto.getBirthDate());
        member.setDeathDate(dto.getDeathDate());
        member.setBirthPlace(dto.getBirthPlace());
        member.setOccupation(dto.getOccupation());
        member.setDescription(dto.getDescription());
        member.setAvatarUrl(dto.getAvatarUrl());
        member.setSpouseId(dto.getSpouseId());
        member.setFatherId(dto.getFatherId());
        member.setMotherId(dto.getMotherId());
        member.setChildrenIds(dto.getChildrenIds());
        return member;
    }
}
