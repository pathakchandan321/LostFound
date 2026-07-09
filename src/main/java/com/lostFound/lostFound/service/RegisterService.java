package com.lostFound.lostFound.service;

import com.lostFound.lostFound.Entity.User;
import com.lostFound.lostFound.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class RegisterService {

    @Autowired
    private UserRepo userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public boolean register(String username, String email, String password) {

        // Check if username already exists
        if (userRepository.findByUsername(username).isPresent() || userRepository.findByEmail(email).isPresent()) {
            return false;
        }

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setEmail(email);

        // ✅ Encode password before saving — NEVER save plain text
        newUser.setPassword(passwordEncoder.encode(password));

        userRepository.save(newUser);
        return true;
    }
}
