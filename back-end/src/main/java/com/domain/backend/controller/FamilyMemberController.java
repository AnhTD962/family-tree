package com.domain.backend.controller;

import com.domain.backend.dto.FamilyMemberDTO;
import com.domain.backend.dto.response.FamilyTreeResponse;
import com.domain.backend.service.FamilyMemberService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/family-members")
@CrossOrigin(origins = "*")
public class FamilyMemberController {

    @Autowired
    private FamilyMemberService familyMemberService;

    @GetMapping("/tree")
    public ResponseEntity<FamilyTreeResponse> getFamilyTree() {
        return ResponseEntity.ok(familyMemberService.getFamilyTree());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FamilyMemberDTO> getFamilyMember(@PathVariable String id) {
        return ResponseEntity.ok(familyMemberService.getFamilyMember(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<FamilyMemberDTO>> searchMembers(@RequestParam String query) {
        return ResponseEntity.ok(familyMemberService.searchMembers(query));
    }

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<FamilyMemberDTO> createFamilyMember(@Valid @RequestBody FamilyMemberDTO memberDTO) {
        return ResponseEntity.ok(familyMemberService.createFamilyMember(memberDTO));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<FamilyMemberDTO> updateFamilyMember(@PathVariable String id, @Valid @RequestBody FamilyMemberDTO memberDTO) {
        return ResponseEntity.ok(familyMemberService.updateFamilyMember(id, memberDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteFamilyMember(@PathVariable String id) {
        familyMemberService.deleteFamilyMember(id);
        return ResponseEntity.noContent().build();
    }
}