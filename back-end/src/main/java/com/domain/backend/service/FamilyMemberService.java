package com.domain.backend.service;

import com.domain.backend.dto.FamilyMemberDTO;
import com.domain.backend.dto.response.FamilyTreeResponse;
import com.domain.backend.model.FamilyMember;
import com.domain.backend.model.FamilyTreeHistory;
import com.domain.backend.repository.FamilyMemberRepository;
import com.domain.backend.repository.FamilyTreeHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FamilyMemberService {

    @Autowired
    private FamilyMemberRepository familyMemberRepository;

    @Autowired
    private FamilyTreeHistoryRepository historyRepository;

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

        // Lấy thông tin người thân
        if (member.getSpouseId() != null) {
            familyMemberRepository.findById(member.getSpouseId())
                    .ifPresent(spouse -> dto.setSpouse(convertToDTO(spouse)));
        }

        if (member.getFatherId() != null) {
            familyMemberRepository.findById(member.getFatherId())
                    .ifPresent(father -> dto.setFather(convertToDTO(father)));
        }

        if (member.getMotherId() != null) {
            familyMemberRepository.findById(member.getMotherId())
                    .ifPresent(mother -> dto.setMother(convertToDTO(mother)));
        }

        List<FamilyMember> children = familyMemberRepository.findChildren(id);
        dto.setChildren(children.stream().map(this::convertToDTO).collect(Collectors.toList()));

        return dto;
    }

    public List<FamilyMemberDTO> searchMembers(String query) {
        List<FamilyMember> members = familyMemberRepository.findByFullNameContainingIgnoreCase(query);
        return members.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public FamilyMemberDTO createFamilyMember(FamilyMemberDTO memberDTO) {
        FamilyMember member = convertToEntity(memberDTO);
        member.setCreatedAt(LocalDateTime.now());
        member.setUpdatedAt(LocalDateTime.now());
        member.setCreatedBy(getCurrentUserId());

        autoFillSpouseAndParents(member);
        // Tính toán generation level
        calculateGeneration(member);

        FamilyMember saved = familyMemberRepository.save(member);

        // Cập nhật parent's children list
        updateParentChildren(saved);

        // Log history
        logHistory("CREATE", saved.getId(), saved.getFullName(), "Created new family member");

        return convertToDTO(saved);
    }

    @Transactional
    public FamilyMemberDTO updateFamilyMember(String id, FamilyMemberDTO memberDTO) {
        FamilyMember existingMember = familyMemberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        // Update fields
        existingMember.setFullName(memberDTO.getFullName());
        existingMember.setNickname(memberDTO.getNickname());
        existingMember.setGender(memberDTO.getGender());
        existingMember.setBirthDate(memberDTO.getBirthDate());
        existingMember.setDeathDate(memberDTO.getDeathDate());
        existingMember.setBirthPlace(memberDTO.getBirthPlace());
        existingMember.setOccupation(memberDTO.getOccupation());
        existingMember.setDescription(memberDTO.getDescription());
        existingMember.setAvatarUrl(memberDTO.getAvatarUrl());
        existingMember.setUpdatedAt(LocalDateTime.now());
        existingMember.setUpdatedBy(getCurrentUserId());

        autoFillSpouseAndParents(existingMember);

        FamilyMember updated = familyMemberRepository.save(existingMember);

        // Log history
        logHistory("UPDATE", updated.getId(), updated.getFullName(), "Updated family member information");

        return convertToDTO(updated);
    }

    private void autoFillSpouseAndParents(FamilyMember member) {
        String memberId = member.getId();

        // ===== 1. Cập nhật spouse hai chiều nếu hợp lệ =====
        if (member.getSpouseId() != null) {
            familyMemberRepository.findById(member.getSpouseId()).ifPresent(spouse -> {
                if (spouse.getSpouseId() == null) {
                    spouse.setSpouseId(memberId);
                    familyMemberRepository.save(spouse);
                } else if (!spouse.getSpouseId().equals(memberId)) {
                    // Đã có spouse khác => log cảnh báo
                    logHistory("WARNING", memberId, member.getFullName(),
                            "Spouse " + spouse.getFullName() + " already has another spouse: " + spouse.getSpouseId());
                }
            });
        }

        // ===== 2. Nếu có father nhưng chưa có mother => gán theo father.spouseId =====
        if (member.getFatherId() != null && member.getMotherId() == null) {
            familyMemberRepository.findById(member.getFatherId()).ifPresent(father -> {
                if (father.getSpouseId() != null) {
                    member.setMotherId(father.getSpouseId());
                }
            });
        }

        // ===== 3. Nếu có mother nhưng chưa có father => gán theo mother.spouseId =====
        if (member.getMotherId() != null && member.getFatherId() == null) {
            familyMemberRepository.findById(member.getMotherId()).ifPresent(mother -> {
                if (mother.getSpouseId() != null) {
                    member.setFatherId(mother.getSpouseId());
                }
            });
        }

        // ===== 4. Cập nhật lại danh sách con cho cha mẹ =====
        updateParentChildren(member);
    }



    @Transactional
    public void deleteFamilyMember(String id) {
        FamilyMember member = familyMemberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        // Xóa tất cả con cháu
        deleteDescendants(id);

        // Xóa member chính
        familyMemberRepository.deleteById(id);

        // Log history
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
