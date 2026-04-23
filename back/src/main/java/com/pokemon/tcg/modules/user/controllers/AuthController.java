package com.pokemon.tcg.modules.user.controllers;

import com.pokemon.tcg.modules.user.dto.requests.RegisterUserRequest;
import com.pokemon.tcg.modules.user.services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService service;

    @PostMapping
    public ResponseEntity registerUser(@RequestBody @Valid RegisterUserRequest request) {
        service.

        return ResponseEntity.created().build();
    }
}
