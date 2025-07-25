package com.domain.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailDTO {
    private String id;
    private String username;
    private String email;
    private String avatarUrl;
    private String phoneNumber;
    private String fullName;
}
