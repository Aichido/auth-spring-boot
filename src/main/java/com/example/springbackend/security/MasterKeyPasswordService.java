package com.example.springbackend.security;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class MasterKeyPasswordService implements PasswordEncoder {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Value("${app.security.master-key}")
    private String masterKey;

    public String encode(CharSequence rawPassword) {
        return encoder.encode(applyMasterKey(rawPassword.toString()));
    }

    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return encoder.matches(applyMasterKey(rawPassword.toString()), encodedPassword);
    }

    private String applyMasterKey(String password) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec keySpec = new SecretKeySpec(masterKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(keySpec);
            byte[] digest = mac.doFinal(password.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(digest);
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to process password with master key", ex);
        }
    }
}
