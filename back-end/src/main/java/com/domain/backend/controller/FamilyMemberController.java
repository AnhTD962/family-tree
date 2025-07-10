package com.domain.backend.controller;

import com.domain.backend.dto.FamilyMemberDTO;
import com.domain.backend.dto.response.FamilyTreeResponse;
import com.domain.backend.service.FamilyMemberService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/family-members")
@CrossOrigin(origins = "*")
public class FamilyMemberController {

    @Autowired
    private FamilyMemberService familyMemberService;

    @GetMapping("/tree")
    public FamilyTreeResponse getFamilyTree() {
        return familyMemberService.getFamilyTree();
    }

    @GetMapping("/{id}")
    public FamilyMemberDTO getFamilyMember(@PathVariable String id) {
        return familyMemberService.getFamilyMember(id);
    }

    @GetMapping("/search")
    public List<FamilyMemberDTO> searchMembers(@RequestParam String query) {
        return familyMemberService.searchMembers(query);
    }

    @PostMapping(consumes = {"multipart/form-data"})
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<FamilyMemberDTO> createFamilyMember(
            @RequestPart("member") @Valid FamilyMemberDTO memberDTO,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        FamilyMemberDTO created = familyMemberService.createFamilyMember(memberDTO, file);
        return ResponseEntity.ok(created);
    }

    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<FamilyMemberDTO> updateFamilyMember(
            @PathVariable String id,
            @RequestPart("member") @Valid FamilyMemberDTO memberDTO,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        FamilyMemberDTO updated = familyMemberService.updateFamilyMember(id, memberDTO, file);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteFamilyMember(@PathVariable String id) {
        familyMemberService.deleteFamilyMember(id);
        return ResponseEntity.ok("Member deleted successfully");
    }
}
