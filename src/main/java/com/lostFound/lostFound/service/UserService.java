package com.lostFound.lostFound.service;

import com.lostFound.lostFound.Entity.User;
import com.lostFound.lostFound.dto.AuthDtos.RegisterRequest;
import com.lostFound.lostFound.exception.ResourceNotFoundException;
import com.lostFound.lostFound.repository.UserRepo;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class UserService {
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final FileStorageService storage;

    public UserService(UserRepo userRepo, PasswordEncoder passwordEncoder, FileStorageService storage) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.storage = storage;
    }

    public User register(RegisterRequest request) {
        if (userRepo.findByUsername(request.username()).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepo.findByEmail(request.email()).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }
        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPhone(request.phone());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole("USER");
        return userRepo.save(user);
    }

    public User currentOrAnonymous(String username) {
        if (username != null && !username.isBlank()) {
            return userRepo.findByUsername(username)
                    .orElseThrow(() -> new ResourceNotFoundException("Logged in user not found"));
        }
        return userRepo.findByUsername("anonymous")
                .orElseGet(() -> {
                    User user = new User();
                    user.setUsername("anonymous");
                    user.setEmail("anonymous@lostfound.local");
                    user.setPassword(passwordEncoder.encode("anonymous"));
                    return userRepo.save(user);
                });
    }

    public List<User> findAll() {
        return userRepo.findAll();
    }

    public User updateProfileImage(User user, MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw new IllegalArgumentException("Please choose a profile picture.");
        }
        user.setProfileImage(storage.store(image));
        return userRepo.save(user);
    }

    public void delete(Long id) {
        if (!userRepo.existsById(id)) {
            throw new ResourceNotFoundException("User not found");
        }
        userRepo.deleteById(id);
    }
}
