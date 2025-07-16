package com.domain.backend.dto;

import com.domain.backend.model.FamilyMember;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FamilyMemberDTO {
    private String id;

    @NotBlank(message = "Full name is required")
    private String fullName;

    private String nickname;

    @NotNull(message = "Gender is required")
    private FamilyMember.Gender gender;

    private LocalDate birthDate;
    private LocalDate deathDate;
    private String birthPlace;
    private String occupation;
    private String description;
    private String avatarUrl;

    // Relationships
    private String spouseId;
    private String fatherId;
    private String motherId;
    private List<String> childrenIds;
    private List<String> siblingsIds;

    // Related objects for detail view
    private FamilyMemberDTO spouse;
    private FamilyMemberDTO father;
    private FamilyMemberDTO mother;
    private List<FamilyMemberDTO> children;
    private List<FamilyMemberDTO> siblings;

    private int generation;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
