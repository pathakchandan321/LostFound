package com.lostFound.lostFound.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AuthDtos {
    public record RegisterRequest(
            @NotBlank String username,
            @Email @NotBlank String email,
            @Size(min = 6, message = "Password must contain at least 6 characters") String password,
            String phone) {
    }

    public record UserResponse(Long id, String username, String email, String phone, String role) {
    }
}
