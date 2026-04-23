package com.pokemon.tcg.modules.user.services;

import com.pokemon.tcg.modules.user.dto.requests.RegisterUserRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final PasswordEncoder passwordEncoder;

    public void register(RegisterUserRequest request) {
        String password = passwordEncoder.encode(request.getPassword());
    }
}
