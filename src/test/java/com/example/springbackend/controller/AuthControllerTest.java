package com.example.springbackend.controller;

import com.example.springbackend.exception.InvalidOldPasswordException;
import com.example.springbackend.exception.WeakPasswordException;
import com.example.springbackend.dto.ApiMessageResponse;
import com.example.springbackend.dto.AuthRequest;
import com.example.springbackend.dto.AuthResponse;
import com.example.springbackend.dto.ChangePasswordRequest;
import com.example.springbackend.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldLoginSuccessfully() throws Exception {
        AuthRequest request = new AuthRequest("user@example.com", "password");
        AuthResponse response = new AuthResponse("jwt-token");

        when(authService.login(request)).thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    void shouldFailLoginWithInvalidCredentials() throws Exception {
        AuthRequest request = new AuthRequest("user@example.com", "wrongpassword");

        when(authService.login(request)).thenThrow(new InvalidOldPasswordException("Invalid credentials"));

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf()))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user@example.com")
    void shouldChangePasswordSuccessfully() throws Exception {
        ChangePasswordRequest request = new ChangePasswordRequest(
            "user@example.com",
            "oldpass",
            "newpass",
            "newpass"
        );

        doNothing().when(authService).changePassword(eq(request), eq("user@example.com"));

        mockMvc.perform(put("/api/auth/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(new ApiMessageResponse("Password changed successfully"))));
    }

    @Test
    @WithMockUser(username = "user@example.com")
    void shouldFailChangePasswordWhenValidationFails() throws Exception {
        ChangePasswordRequest request = new ChangePasswordRequest(
            "user@example.com",
            "oldpass",
            "new",
            "new"
        );

        doThrow(new WeakPasswordException("Password too weak")).when(authService).changePassword(any(), any());

        mockMvc.perform(put("/api/auth/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf()))
            .andExpect(status().isBadRequest());
    }
}