package com.domain.backend.service;

import com.domain.backend.dto.UserDetailDTO;
import com.domain.backend.dto.request.ChangePasswordRequest;
import com.domain.backend.model.User;
import com.domain.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${upload.dir}")
    private String uploadDir;

    public UserDetailDTO getCurrentUserProfile() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return new UserDetailDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getAvatarUrl(),
                user.getPhoneNumber(),
                user.getFullName()
        );
    }

    public UserDetailDTO updateCurrentUserProfile(UserDetailDTO dto, MultipartFile avatarFile) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (dto.getFullName() != null) user.setFullName(dto.getFullName());
        if (dto.getPhoneNumber() != null) user.setPhoneNumber(dto.getPhoneNumber());

        // Nếu có avatar file mới
        if (avatarFile != null && !avatarFile.isEmpty()) {
            String avatarUrl = saveAvatarFile(avatarFile);
            user.setAvatarUrl(avatarUrl);
        }

        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
        return mapToDTO(user);
    }

    private String saveAvatarFile(MultipartFile file) {
        try {
            String ext = Optional.ofNullable(file.getOriginalFilename())
                    .map(f -> f.substring(f.lastIndexOf('.') + 1))
                    .orElse("jpg");
            String filename = UUID.randomUUID() + "." + ext;
            Path path = Paths.get(uploadDir, filename);
            Files.createDirectories(path.getParent());
            Files.write(path, file.getBytes());
            return "/api/images/" + filename;
        } catch (IOException e) {
            throw new RuntimeException("Failed to save avatar", e);
        }
    }

    private UserDetailDTO mapToDTO(User user) {
        return new UserDetailDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getAvatarUrl(),
                user.getPhoneNumber(),
                user.getFullName()
        );
    }

    public void changePassword(ChangePasswordRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("Mật khẩu hiện tại không đúng");
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Mật khẩu xác nhận không khớp");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }
}

