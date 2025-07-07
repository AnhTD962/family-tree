package com.domain.backend.controller;

import com.domain.backend.model.FamilyMember;
import com.domain.backend.service.FamilyMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class FamilyMemberController {

    private final FamilyMemberService memberService;

    @GetMapping
    public ResponseEntity<List<FamilyMember>> getAll() {
        return ResponseEntity.ok(memberService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FamilyMember> getById(@PathVariable String id) {
        return ResponseEntity.ok(memberService.getById(id));
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @PostMapping
    public ResponseEntity<FamilyMember> create(@RequestBody FamilyMember member) {
        return ResponseEntity.ok(memberService.create(member));
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @PutMapping("/{id}")
    public ResponseEntity<FamilyMember> update(@PathVariable String id, @RequestBody FamilyMember member) {
        return ResponseEntity.ok(memberService.update(id, member));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        memberService.delete(id);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @PostMapping("/{id}/avatar")
    public ResponseEntity<String> uploadAvatar(@PathVariable String id, @RequestParam("file") MultipartFile file) {
        String url = memberService.uploadAvatar(id, file);
        return ResponseEntity.ok(url);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @PostMapping("/{id}/relations")
    public ResponseEntity<FamilyMember> addRelation(
            @PathVariable String id,
            @RequestParam String targetId,
            @RequestParam String type
    ) {
        return ResponseEntity.ok(memberService.addRelation(id, targetId, type));
    }

    @GetMapping("/search")
    public ResponseEntity<List<FamilyMember>> search(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) String birthYear
    ) {
        return ResponseEntity.ok(memberService.search(name, gender, birthYear));
    }

    @GetMapping("/tree/{rootId}")
    public ResponseEntity<Object> getTree(@PathVariable String rootId) {
        return ResponseEntity.ok(memberService.generateTreeFromRoot(rootId));
    }

    @GetMapping("/export/{rootId}")
    public ResponseEntity<String> exportTreeJson(@PathVariable String rootId) {
        return ResponseEntity.ok(memberService.exportTreeAsJson(rootId));
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @PostMapping("/{id}/undo")
    public ResponseEntity<Void> undoLastChange(@PathVariable String id) {
        memberService.undoLastChange(id);
        return ResponseEntity.ok().build();
    }
}

