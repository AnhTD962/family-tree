package com.domain.backend.service;

import com.domain.backend.dto.FamilyTreeHistoryDTO;
import com.domain.backend.dto.UserDTO;
import com.domain.backend.model.FamilyTreeHistory;
import com.domain.backend.model.User;
import com.domain.backend.repository.FamilyTreeHistoryRepository;
import com.domain.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FamilyTreeHistoryRepository historyRepository;

    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(this::convertToUserDTO)
                .collect(Collectors.toList());
    }

    public void toggleUserStatus(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setActive(!user.isActive());
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    public List<FamilyTreeHistoryDTO> getFamilyTreeHistory() {
        List<FamilyTreeHistory> historyList = historyRepository.findAll();

        return historyList.stream()
                .sorted(Comparator.comparing(FamilyTreeHistory::getTimestamp).reversed())
                .map(this::convertToHistoryDTO)
                .collect(Collectors.toList());
    }

    private UserDTO convertToUserDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setAvatarUrl(user.getAvatarUrl());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setFullName(user.getFullName());
        dto.setRoles(user.getRoles());
        dto.setActive(user.isActive());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        return dto;
    }

    private FamilyTreeHistoryDTO convertToHistoryDTO(FamilyTreeHistory history) {
        FamilyTreeHistoryDTO dto = new FamilyTreeHistoryDTO();
        dto.setId(history.getId());
        dto.setUserId(history.getUserId());
        dto.setAction(history.getAction());
        dto.setTargetMemberId(history.getTargetMemberId());
        dto.setTargetMemberName(history.getTargetMemberName());
        dto.setDetails(history.getDetails());
        dto.setTimestamp(history.getTimestamp());

        userRepository.findById(history.getUserId())
                .ifPresent(user -> dto.setUsername(user.getUsername()));

        return dto;
    }
}
