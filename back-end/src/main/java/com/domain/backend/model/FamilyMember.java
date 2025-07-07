package com.domain.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "family_members")
public class FamilyMember {
    @Id
    private String id;

    @Indexed
    private String fullName;

    private String nickname;
    private Gender gender;
    private LocalDate birthDate;
    private LocalDate deathDate;
    private String birthPlace;
    private String occupation;
    private String description;
    private String avatarUrl;

    // Quan hệ gia đình
    private String spouseId;
    private String fatherId;
    private String motherId;
    private List<String> childrenIds = new ArrayList<>();

    // Metadata
    private String createdBy;
    private String updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Vị trí trong cây (generation level)
    private int generation;

    public enum Gender {
        MALE, FEMALE
    }
}