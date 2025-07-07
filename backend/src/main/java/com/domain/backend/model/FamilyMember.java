package com.domain.backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Document("family_members")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FamilyMember {
    @Id
    private String id;
    private String fullName;
    private String gender;
    private LocalDate dateOfBirth;
    private LocalDate dateOfDeath;
    private String avatarUrl;
    private String description;
    private List<String> parents = new ArrayList<>();
    private List<String> spouses = new ArrayList<>();
    private List<String> children = new ArrayList<>();
}
