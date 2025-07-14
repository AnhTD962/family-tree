package com.domain.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User {
    @Id
    private String id;

    @Indexed(unique = true)
    private String username;

    @Indexed(unique = true)
    private String email;

    private String avatarUrl;
    private String phoneNumber;
    private String password;
    private String fullName;
    private Set<Role> roles;
    private boolean isActive = true;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public enum Role {
        GUEST, USER, ADMIN
    }
}
