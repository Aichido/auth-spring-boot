package com.example.springbackend.controller;

import com.example.springbackend.dto.ApiMessageResponse;
import com.example.springbackend.dto.AuthRequest;
import com.example.springbackend.dto.AuthResponse;
import com.example.springbackend.dto.ChangePasswordRequest;
import com.example.springbackend.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody AuthRequest request) {
        return authService.login(request);
    }

    @PutMapping("/change-password")
    public ApiMessageResponse changePassword(@Valid @RequestBody ChangePasswordRequest request,
                                             @AuthenticationPrincipal UserDetails userDetails) {
        authService.changePassword(request, userDetails.getUsername());
        return new ApiMessageResponse("Password changed successfully");
    }
}
