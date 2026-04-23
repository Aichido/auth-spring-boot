package com.example.springbackend.security;

import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

@Component
public class PasswordPolicyService {

    private static final Pattern STRONG_PASSWORD_PATTERN = Pattern.compile(
        "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{12,}$"
    );

    public boolean isStrong(String password) {
        return password != null && STRONG_PASSWORD_PATTERN.matcher(password).matches();
    }
}
