package com.domain.backend.dto.response;

import com.domain.backend.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String refreshToken;
    private String username;
    private String email;
    private String avatarUrl;
    private String phoneNumber;
    private String fullName;
    private Set<User.Role> roles;
    private boolean isActive;
}
