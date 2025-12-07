package com.team3.forum.services;

import com.team3.forum.exceptions.FileStorageException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    private static final String INVALID_FILE_TYPE_ERROR = "Invalid file type. Only JPG, PNG, GIF, and WebP images are allowed.";
    private static final String FILE_SIZE_EXCEEDED_ERROR = "File size exceeds maximum limit of 5MB";
    private static final String FILENAME_EMPTY_ERROR = "Filename cannot be empty";
    private static final String FILE_STORE_ERROR = "Could not store file";
    private static final String FILE_DELETE_ERROR = "Could not delete file: ";
    private static final String DIRECTORY_CREATE_ERROR = "Could not create upload directory";

    private final Path fileStorageLocation;
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png", "gif", "webp");
    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
        "image/jpeg", "image/png", "image/gif", "image/webp"
    );
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    public FileStorageServiceImpl(@Value("${file.upload.dir}") String uploadDir) {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (IOException ex) {
            throw new FileStorageException(DIRECTORY_CREATE_ERROR, ex);
        }
    }

    @Override
    public String storeFile(MultipartFile file, int userId) {
        if (!isValidImageFile(file)) {
            throw new FileStorageException(INVALID_FILE_TYPE_ERROR);
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new FileStorageException(FILE_SIZE_EXCEEDED_ERROR);
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new FileStorageException(FILENAME_EMPTY_ERROR);
        }

        String fileExtension = getFileExtension(originalFilename);
        String newFilename = String.format("user-%d-%s.%s", userId, UUID.randomUUID(), fileExtension);

        try {
            Path targetLocation = this.fileStorageLocation.resolve(newFilename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return "/images/avatars/" + newFilename;
        } catch (IOException ex) {
            throw new FileStorageException(FILE_STORE_ERROR + " " + newFilename, ex);
        }
    }

    @Override
    public void deleteFile(String filename) {
        if (filename == null || filename.isEmpty()) {
            return;
        }

        try {
            String filenameOnly = filename.substring(filename.lastIndexOf('/') + 1);
            Path filePath = this.fileStorageLocation.resolve(filenameOnly).normalize();

            if (Files.exists(filePath)) {
                Files.delete(filePath);
            }
        } catch (IOException ex) {
            System.err.println(FILE_DELETE_ERROR + filename);
        }
    }

    @Override
    public boolean isValidImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return false;
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            return false;
        }

        String filename = file.getOriginalFilename();
        if (filename == null) {
            return false;
        }

        String extension = getFileExtension(filename);
        return ALLOWED_EXTENSIONS.contains(extension.toLowerCase());
    }

    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        return filename.substring(lastDotIndex + 1);
    }
}
