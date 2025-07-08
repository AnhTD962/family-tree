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

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public FamilyMemberDTO createFamilyMember(@Valid @RequestBody FamilyMemberDTO memberDTO) {
        return familyMemberService.createFamilyMember(memberDTO);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public FamilyMemberDTO updateFamilyMember(@PathVariable String id, @Valid @RequestBody FamilyMemberDTO memberDTO) {
        return familyMemberService.updateFamilyMember(id, memberDTO);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteFamilyMember(@PathVariable String id) {
        familyMemberService.deleteFamilyMember(id);
        return "Member deleted successfully";
    }
}