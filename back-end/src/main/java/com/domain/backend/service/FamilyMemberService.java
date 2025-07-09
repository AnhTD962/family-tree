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

        // Spouse
        if (member.getSpouseId() != null) {
            familyMemberRepository.findById(member.getSpouseId())
                    .ifPresent(spouse -> dto.setSpouse(convertToDTO(spouse)));
        }

        // Father
        if (member.getFatherId() != null) {
            familyMemberRepository.findById(member.getFatherId())
                    .ifPresent(father -> dto.setFather(convertToDTO(father)));
        }

        // Mother
        if (member.getMotherId() != null) {
            familyMemberRepository.findById(member.getMotherId())
                    .ifPresent(mother -> dto.setMother(convertToDTO(mother)));
        }

        // Children
        List<FamilyMember> children = familyMemberRepository.findChildren(id);
        dto.setChildren(children.stream().map(this::convertToDTO).collect(Collectors.toList()));

        // Siblings
        List<FamilyMember> siblings = familyMemberRepository.findSiblings(
                member.getFatherId(), member.getMotherId(), member.getId()
        );
        dto.setSiblings(siblings.stream().map(this::convertToDTO).collect(Collectors.toList()));

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
        existingMember.setFatherId(memberDTO.getFatherId());
        existingMember.setMotherId(memberDTO.getMotherId());
        existingMember.setSpouseId(memberDTO.getSpouseId());

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

        // 1. Spouse sync and generation sync
        if (member.getSpouseId() != null) {
            familyMemberRepository.findById(member.getSpouseId()).ifPresent(spouse -> {

                // Generation sync
                if (member.getGeneration() == 0 && spouse.getGeneration() > 0) {
                    member.setGeneration(spouse.getGeneration());
                } else if (spouse.getGeneration() == 0 && member.getGeneration() > 0) {
                    spouse.setGeneration(member.getGeneration());
                    familyMemberRepository.save(spouse);
                }

                // Spouse link
                if (spouse.getSpouseId() == null) {
                    spouse.setSpouseId(memberId);
                    familyMemberRepository.save(spouse);
                } else if (!spouse.getSpouseId().equals(memberId)) {
                    logHistory("WARNING", memberId, member.getFullName(),
                            "Spouse " + spouse.getFullName() + " already has another spouse: " + spouse.getSpouseId());
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
