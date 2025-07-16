package com.domain.backend.controller;

import com.domain.backend.dto.FamilyTreeHistoryDTO;
import com.domain.backend.dto.UserDTO;
import com.domain.backend.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/users")
    public List<UserDTO> getAllUsers() {
        return adminService.getAllUsers();
    }

    @PutMapping("/users/{id}/toggle-status")
    public String toggleUserStatus(@PathVariable String id) {
        adminService.toggleUserStatus(id);
        return "User status toggled successfully";
    }

    @GetMapping("/history")
    public List<FamilyTreeHistoryDTO> getFamilyTreeHistory() {
        return adminService.getFamilyTreeHistory();
    }
}
