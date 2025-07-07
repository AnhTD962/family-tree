package com.domain.backend.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    String storeFile(String memberId, MultipartFile file);
}
