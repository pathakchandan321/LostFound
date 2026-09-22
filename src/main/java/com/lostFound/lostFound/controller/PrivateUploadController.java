package com.lostFound.lostFound.controller;

import com.lostFound.lostFound.Entity.User;
import com.lostFound.lostFound.exception.ResourceNotFoundException;
import com.lostFound.lostFound.repository.FoundItemRepo;
import com.lostFound.lostFound.repository.LostItemRepo;
import com.lostFound.lostFound.repository.UserRepo;
import com.lostFound.lostFound.service.FileStorageService;
import com.lostFound.lostFound.service.UserService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import java.nio.file.Path;

@RestController
public class PrivateUploadController {
    private final LostItemRepo lostRepo;
    private final FoundItemRepo foundRepo;
    private final FileStorageService storage;
    private final UserService users;
    private final UserRepo userRepo;

    public PrivateUploadController(LostItemRepo lostRepo, FoundItemRepo foundRepo, FileStorageService storage, UserService users, UserRepo userRepo) {
        this.lostRepo = lostRepo;
        this.foundRepo = foundRepo;
        this.storage = storage;
        this.users = users;
        this.userRepo = userRepo;
    }

    @GetMapping("/private-uploads/{fileName:.+}")
    public Resource upload(@PathVariable String fileName, Authentication authentication) {
        User user = users.currentOrAnonymous(authentication == null ? null : authentication.getName());
        boolean admin = "ADMIN".equalsIgnoreCase(user.getRole());
        boolean owns = lostRepo.findByImagePath(fileName).map(item -> item.getReporter().getId().equals(user.getId())).orElse(false)
                || foundRepo.findByImagePath(fileName).map(item -> item.getReporter().getId().equals(user.getId())).orElse(false)
                || userRepo.findByProfileImage(fileName).map(profile -> profile.getId().equals(user.getId())).orElse(false);
        if (!admin && !owns) throw new ResourceNotFoundException("Image not found");
        Path path = storage.uploadPath().resolve(fileName).normalize();
        if (!path.startsWith(storage.uploadPath())) throw new ResourceNotFoundException("Image not found");
        return new FileSystemResource(path);
    }
}
