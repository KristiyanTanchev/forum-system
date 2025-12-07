package com.team3.forum.services;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    String storeFile(MultipartFile file, int userId);
    void deleteFile(String filename);
    boolean isValidImageFile(MultipartFile file);
}
