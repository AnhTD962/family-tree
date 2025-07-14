package com.domain.backend.controller;

import com.domain.backend.dto.request.AuthRequest;
import com.domain.backend.dto.request.RegisterRequest;
import com.domain.backend.dto.response.AuthResponse;
import com.domain.backend.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody AuthRequest request) {
        return authService.login(request);
    }

    @PostMapping("/refresh")
    public AuthResponse refresh(@RequestHeader("Authorization") String token) {
        return authService.refresh(token);
    }
}
