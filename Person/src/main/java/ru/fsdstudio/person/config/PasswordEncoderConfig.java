package ru.fsdstudio.person.config;

import lombok.Getter;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Getter
@Configuration
public class PasswordEncoderConfig {
    
    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    public String encode(String password) {
        return passwordEncoder.encode(password);
    }
}
