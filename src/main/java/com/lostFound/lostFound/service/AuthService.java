package com.lostFound.lostFound.service;

import com.lostFound.lostFound.Entity.User;

import com.lostFound.lostFound.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    public boolean login(String username, String password) {

        Optional<User> user = userRepository.findByUsername(username);

        if (user.isEmpty()) {
            return false;
        }

        User foundUser = user.get();

        // Direct password match (plain text — no encoding)
        return foundUser.getPassword().equals(password);
    }
}