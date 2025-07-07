package com.domain.backend.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@Slf4j
public class FileStorageServiceImpl implements FileStorageService {
    private static final String UPLOAD_DIR = "uploads";

    @Override
    public String storeFile(String memberId, MultipartFile file) {
        try {
            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(UPLOAD_DIR, filename);
            Files.createDirectories(filePath.getParent());
            file.transferTo(filePath);
            return "/" + UPLOAD_DIR + "/" + filename;
        } catch (IOException e) {
            log.error("Upload failed", e);
            throw new RuntimeException("Failed to store file", e);
        }
    }
}
