package com.domain.backend.controller;

import com.domain.backend.dto.UserDetailDTO;
import com.domain.backend.dto.request.ChangePasswordRequest;
import com.domain.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    public UserDetailDTO getUserProfile() {
        return userService.getCurrentUserProfile();
    }

    @PutMapping(value = "/profile", consumes = "multipart/form-data")
    public UserDetailDTO updateUserProfile(
            @RequestPart("user") UserDetailDTO userDTO,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        return userService.updateCurrentUserProfile(userDTO, file);
    }

    @PutMapping("/change-password")
    public String changePassword(@RequestBody ChangePasswordRequest request) {
        userService.changePassword(request);
        return "Đổi mật khẩu thành công";
    }
}
