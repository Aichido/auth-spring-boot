package com.example.springbackend.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PasswordPolicyServiceTest {

    private final PasswordPolicyService passwordPolicyService = new PasswordPolicyService();

    @Test
    void shouldReturnTrueForStrongPassword() {
        assertTrue(passwordPolicyService.isStrong("StrongPass123!"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"short", "nouppercase123!", "NOLOWERCASE123!", "NoDigits!", "NoSpecial123", ""})
    void shouldReturnFalseForWeakPasswords(String password) {
        assertFalse(passwordPolicyService.isStrong(password));
    }

    @Test
    void shouldReturnFalseForNullPassword() {
        assertFalse(passwordPolicyService.isStrong(null));
    }
}