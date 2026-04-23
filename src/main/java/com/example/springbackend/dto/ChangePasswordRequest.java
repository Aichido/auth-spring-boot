package com.example.springbackend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ChangePasswordRequest(
    @Email @NotBlank String email,
    @NotBlank String oldPassword,
    @NotBlank String newPassword,
    @NotBlank String confirmPassword
) {
}
