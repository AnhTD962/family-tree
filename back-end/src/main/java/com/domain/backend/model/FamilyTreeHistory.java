package com.domain.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "family_tree_history")
public class FamilyTreeHistory {
    @Id
    private String id;

    private String userId;
    private String action; // CREATE, UPDATE, DELETE
    private String targetMemberId;
    private String targetMemberName;
    private String details;
    private LocalDateTime timestamp;
}
