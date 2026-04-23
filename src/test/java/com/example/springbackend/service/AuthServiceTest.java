package com.example.springbackend.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.springbackend.dto.AuthRequest;
import com.example.springbackend.dto.AuthResponse;
import com.example.springbackend.dto.ChangePasswordRequest;
import com.example.springbackend.entity.UserAccount;
import com.example.springbackend.exception.InvalidOldPasswordException;
import com.example.springbackend.exception.PasswordConfirmationMismatchException;
import com.example.springbackend.exception.UserNotFoundException;
import com.example.springbackend.exception.WeakPasswordException;
import com.example.springbackend.repository.UserRepository;
import com.example.springbackend.security.JwtService;
import com.example.springbackend.security.MasterKeyPasswordService;
import com.example.springbackend.security.PasswordPolicyService;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private MasterKeyPasswordService masterKeyPasswordService;

    @Mock
    private PasswordPolicyService passwordPolicyService;

    @Mock
    private JwtService jwtService;

    private AuthService authService;

    @BeforeEach
    void setup() {
        authService = new AuthService(userRepository, masterKeyPasswordService, passwordPolicyService, jwtService);
    }

    @Test
    void shouldChangePasswordSuccessfully() {
        UserAccount user = new UserAccount("user@example.com", "encodedOld");
        ChangePasswordRequest request = new ChangePasswordRequest(
            "user@example.com",
            "OldPassword@123",
            "NewStrongPassword@123",
            "NewStrongPassword@123"
        );

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(masterKeyPasswordService.matches("OldPassword@123", "encodedOld")).thenReturn(true);
        when(passwordPolicyService.isStrong("NewStrongPassword@123")).thenReturn(true);
        when(masterKeyPasswordService.encode("NewStrongPassword@123")).thenReturn("encodedNew");

        assertDoesNotThrow(() -> authService.changePassword(request, "user@example.com"));

        verify(userRepository).save(user);
    }

    @Test
    void shouldFailWhenOldPasswordIsIncorrect() {
        UserAccount user = new UserAccount("user@example.com", "encodedOld");
        ChangePasswordRequest request = new ChangePasswordRequest(
            "user@example.com",
            "bad-old",
            "NewStrongPassword@123",
            "NewStrongPassword@123"
        );

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(masterKeyPasswordService.matches("bad-old", "encodedOld")).thenReturn(false);

        assertThrows(InvalidOldPasswordException.class, () -> authService.changePassword(request, "user@example.com"));
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldFailWhenConfirmationDoesNotMatch() {
        UserAccount user = new UserAccount("user@example.com", "encodedOld");
        ChangePasswordRequest request = new ChangePasswordRequest(
            "user@example.com",
            "OldPassword@123",
            "NewStrongPassword@123",
            "Different@123"
        );

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(masterKeyPasswordService.matches("OldPassword@123", "encodedOld")).thenReturn(true);

        assertThrows(PasswordConfirmationMismatchException.class,
            () -> authService.changePassword(request, "user@example.com"));
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldFailWhenPasswordIsWeak() {
        UserAccount user = new UserAccount("user@example.com", "encodedOld");
        ChangePasswordRequest request = new ChangePasswordRequest(
            "user@example.com",
            "OldPassword@123",
            "weak",
            "weak"
        );

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(masterKeyPasswordService.matches("OldPassword@123", "encodedOld")).thenReturn(true);
        when(passwordPolicyService.isStrong("weak")).thenReturn(false);

        assertThrows(WeakPasswordException.class, () -> authService.changePassword(request, "user@example.com"));
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldFailWhenUserDoesNotExist() {
        ChangePasswordRequest request = new ChangePasswordRequest(
            "missing@example.com",
            "OldPassword@123",
            "NewStrongPassword@123",
            "NewStrongPassword@123"
        );

        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> authService.changePassword(request, "missing@example.com"));
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldLoginSuccessfully() {
        UserAccount user = new UserAccount("user@example.com", "encodedPass");
        AuthRequest request = new AuthRequest("user@example.com", "password");

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(masterKeyPasswordService.matches("password", "encodedPass")).thenReturn(true);
        when(jwtService.generateToken("user@example.com")).thenReturn("jwt-token");

        AuthResponse response = authService.login(request);

        assertEquals("jwt-token", response.token());
        verify(jwtService).generateToken("user@example.com");
    }

    @Test
    void shouldFailLoginWhenUserNotFound() {
        AuthRequest request = new AuthRequest("missing@example.com", "password");

        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> authService.login(request));
    }

    @Test
    void shouldFailLoginWhenPasswordIncorrect() {
        UserAccount user = new UserAccount("user@example.com", "encodedPass");
        AuthRequest request = new AuthRequest("user@example.com", "wrongpassword");

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(masterKeyPasswordService.matches("wrongpassword", "encodedPass")).thenReturn(false);

        assertThrows(InvalidOldPasswordException.class, () -> authService.login(request));
    }
}
