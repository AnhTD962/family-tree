package com.domain.backend.dto;

import com.domain.backend.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private String id;
    private String username;
    private String email;
    private String avatarUrl;
    private String phoneNumber;
    private String fullName;
    private Set<User.Role> roles;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}