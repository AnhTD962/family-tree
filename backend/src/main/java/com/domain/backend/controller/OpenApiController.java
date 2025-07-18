package com.domain.backend.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/docs")
public class OpenApiController {

    @RequestMapping(value = "/user", produces = "application/x-yaml")
    public ResponseEntity<String> getUserApiDocs() throws IOException {
        return getApiDocs("api/user-openapi.yaml");
    }

    @RequestMapping(value = "/family-member", produces = "application/x-yaml")
    public ResponseEntity<String> getFamilyMemberApiDocs() throws IOException {
        return getApiDocs("api/family-member-openapi.yaml");
    }

    @RequestMapping(value = "/admin", produces = "application/x-yaml")
    public ResponseEntity<String> getAdminApiDocs() throws IOException {
        return getApiDocs("api/admin-openapi.yaml");
    }

    @RequestMapping(value = "/auth", produces = "application/x-yaml")
    public ResponseEntity<String> getAuthApiDocs() throws IOException {
        return getApiDocs("api/auth-openapi.yaml");
    }

    private ResponseEntity<String> getApiDocs(String resourcePath) throws IOException {
        ClassPathResource resource = new ClassPathResource(resourcePath);
        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        String content = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        return ResponseEntity.ok(content);
    }
}
