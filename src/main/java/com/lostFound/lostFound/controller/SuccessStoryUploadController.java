package com.lostFound.lostFound.controller;

import com.lostFound.lostFound.service.FileStorageService;
import com.lostFound.lostFound.repository.SuccessStoryRepo;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Path;

@RestController
public class SuccessStoryUploadController {
    private final FileStorageService storage;
    private final SuccessStoryRepo storyRepo;

    public SuccessStoryUploadController(FileStorageService storage, SuccessStoryRepo storyRepo) {
        this.storage = storage;
        this.storyRepo = storyRepo;
    }

    @GetMapping("/story-uploads/{fileName:.+}")
    public Resource upload(@PathVariable String fileName) {
        Path path = storage.uploadPath().resolve(fileName).normalize();
        if (!path.startsWith(storage.uploadPath())) {
            throw new IllegalArgumentException("Invalid story image path");
        }
        if (storyRepo.findAllByOrderByCreatedAtDesc().stream()
                .noneMatch(story -> fileName.equals(story.getImagePath()))) {
            throw new com.lostFound.lostFound.exception.ResourceNotFoundException("Story image not found");
        }
        return new FileSystemResource(path);
    }
}
