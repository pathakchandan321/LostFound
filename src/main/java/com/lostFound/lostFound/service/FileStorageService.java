package com.lostFound.lostFound.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileStorageService {
    private final Path uploadPath;

    public FileStorageService(@Value("${file.upload-dir}") String uploadDir) {
        this.uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    public String store(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }
        try {
            Files.createDirectories(uploadPath);
            String original = StringUtils.cleanPath(file.getOriginalFilename() == null ? "item-image" : file.getOriginalFilename());
            String fileName = System.currentTimeMillis() + "_" + original.replaceAll("[^a-zA-Z0-9._-]", "_");
            Path target = uploadPath.resolve(fileName).normalize();
            if (!target.startsWith(uploadPath)) {
                throw new IllegalArgumentException("Invalid upload path");
            }
            file.transferTo(target);
            return fileName;
        } catch (IOException ex) {
            throw new IllegalArgumentException("Could not store uploaded file: " + ex.getMessage());
        }
    }

    public Path uploadPath() {
        return uploadPath;
    }
}
