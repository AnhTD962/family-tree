package com.domain.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FamilyTreeHistoryDTO {
    private String id;
    private String userId;
    private String username;
    private String action;
    private String targetMemberId;
    private String targetMemberName;
    private String details;
    private LocalDateTime timestamp;
}
