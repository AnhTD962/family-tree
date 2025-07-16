package com.domain.backend.service;

import com.domain.backend.dto.request.AuthRequest;
import com.domain.backend.dto.request.RegisterRequest;
import com.domain.backend.dto.response.AuthResponse;
import com.domain.backend.model.User;
import com.domain.backend.repository.UserRepository;
import com.domain.backend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setRoles(Set.of(User.Role.USER));
        user.setActive(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        User savedUser = userRepository.save(user);

        // ✅ Convert roles to List<String>
        var roles = savedUser.getRoles().stream()
                .map(Enum::name)
                .toList();

        String token = jwtUtil.generateToken(savedUser.getUsername(), roles);
        String refreshToken = jwtUtil.generateRefreshToken(savedUser.getUsername());

        return new AuthResponse(
                token,
                refreshToken,
                savedUser.getUsername(),
                savedUser.getEmail(),
                savedUser.getAvatarUrl(),
                savedUser.getPhoneNumber(),
                savedUser.getFullName(),
                savedUser.getRoles(),
                savedUser.isActive()
        );
    }


    public AuthResponse login(AuthRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            User user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (!user.isActive()) {
                throw new RuntimeException("Account is disabled");
            }

            var roles = user.getRoles().stream()
                    .map(Enum::name)
                    .toList();

            String token = jwtUtil.generateToken(user.getUsername(), roles);
            String refreshToken = jwtUtil.generateRefreshToken(user.getUsername());

            return new AuthResponse(
                    token,
                    refreshToken,
                    user.getUsername(),
                    user.getEmail(),
                    user.getAvatarUrl(),
                    user.getPhoneNumber(),
                    user.getFullName(),
                    user.getRoles(),
                    user.isActive()
            );

        } catch (AuthenticationException e) {
            throw new RuntimeException("Invalid credentials");
        }
    }


    public AuthResponse refresh(String refreshToken) {
        // 1. Kiểm tra hạn token
        if (jwtUtil.isTokenExpired(refreshToken)) {
            throw new RuntimeException("Refresh token expired");
        }

        // 2. Lấy username từ refresh token
        String username = jwtUtil.extractUsername(refreshToken);

        // 3. Tìm người dùng trong DB
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.isActive()) {
            throw new RuntimeException("Account is disabled");
        }

        // 4. Tạo access token mới + refresh token mới
        var roles = user.getRoles().stream()
                .map(Enum::name)
                .toList();

        String newToken = jwtUtil.generateToken(username, roles);
        String newRefreshToken = jwtUtil.generateRefreshToken(username);

        return new AuthResponse(
                newToken,
                newRefreshToken,
                user.getUsername(),
                user.getEmail(),
                user.getAvatarUrl(),
                user.getPhoneNumber(),
                user.getFullName(),
                user.getRoles(),
                user.isActive()
        );
    }

}
