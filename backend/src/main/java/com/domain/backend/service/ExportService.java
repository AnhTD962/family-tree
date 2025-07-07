package com.domain.backend.service;

import com.domain.backend.utils.TreeBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExportService {
    private final TreeBuilder treeBuilder;
    private final ObjectMapper mapper;

    public String exportTreeAsJson(String rootId) {
        try {
            Object tree = treeBuilder.buildTree(rootId);
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(tree);
        } catch (Exception e) {
            throw new RuntimeException("Export failed", e);
        }
    }
}