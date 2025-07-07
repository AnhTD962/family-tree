package com.domain.backend.utils;

import com.domain.backend.model.FamilyMember;
import com.domain.backend.repository.FamilyMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class TreeBuilder {
    private final FamilyMemberRepository memberRepo;

    public Map<String, Object> buildTree(String rootId) {
        FamilyMember root = memberRepo.findById(rootId).orElse(null);
        if (root == null) return null;
        return buildNode(root);
    }

    private Map<String, Object> buildNode(FamilyMember member) {
        Map<String, Object> node = new HashMap<>();
        node.put("id", member.getId());
        node.put("name", member.getFullName());
        node.put("gender", member.getGender());

        List<Map<String, Object>> children = new ArrayList<>();
        for (String childId : member.getChildren()) {
            memberRepo.findById(childId).ifPresent(child -> {
                children.add(buildNode(child));
            });
        }
        node.put("children", children);
        return node;
    }
}