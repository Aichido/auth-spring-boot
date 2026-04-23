package com.example.springbackend.service;

import com.example.springbackend.dto.AuthRequest;
import com.example.springbackend.dto.AuthResponse;
import com.example.springbackend.dto.ChangePasswordRequest;
import com.example.springbackend.entity.UserAccount;
import com.example.springbackend.exception.InvalidOldPasswordException;
import com.example.springbackend.exception.PasswordConfirmationMismatchException;
import com.example.springbackend.exception.UnauthorizedPasswordChangeException;
import com.example.springbackend.exception.UserNotFoundException;
import com.example.springbackend.exception.WeakPasswordException;
import com.example.springbackend.repository.UserRepository;
import com.example.springbackend.security.JwtService;
import com.example.springbackend.security.MasterKeyPasswordService;
import com.example.springbackend.security.PasswordPolicyService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final MasterKeyPasswordService masterKeyPasswordService;
    private final PasswordPolicyService passwordPolicyService;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
                       MasterKeyPasswordService masterKeyPasswordService,
                       PasswordPolicyService passwordPolicyService,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.masterKeyPasswordService = masterKeyPasswordService;
        this.passwordPolicyService = passwordPolicyService;
        this.jwtService = jwtService;
    }

    public AuthResponse login(AuthRequest authRequest) {
        UserAccount user = userRepository.findByEmail(authRequest.email())
            .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!masterKeyPasswordService.matches(authRequest.password(), user.getPassword())) {
            throw new InvalidOldPasswordException("Invalid credentials");
        }

        return new AuthResponse(jwtService.generateToken(user.getEmail()));
    }

    @Transactional
    public void changePassword(ChangePasswordRequest request, String authenticatedEmail) {
        if (authenticatedEmail == null || !authenticatedEmail.equalsIgnoreCase(request.email())) {
            throw new UnauthorizedPasswordChangeException("Authenticated user does not match request email");
        }

        UserAccount user = userRepository.findByEmail(request.email())
            .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!masterKeyPasswordService.matches(request.oldPassword(), user.getPassword())) {
            throw new InvalidOldPasswordException("Old password is incorrect");
        }

        if (!request.newPassword().equals(request.confirmPassword())) {
            throw new PasswordConfirmationMismatchException("newPassword and confirmPassword do not match");
        }

        if (!passwordPolicyService.isStrong(request.newPassword())) {
            throw new WeakPasswordException("New password is too weak");
        }

        user.setPassword(masterKeyPasswordService.encode(request.newPassword()));
        userRepository.save(user);
    }
}
