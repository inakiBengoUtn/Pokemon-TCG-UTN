package com.pokemon.tcg.modules.user.controllers;

import com.pokemon.tcg.modules.user.dto.requests.LoginUserRequest;
import com.pokemon.tcg.modules.user.dto.requests.RegisterUserRequest;
import com.pokemon.tcg.modules.user.dto.responses.UserLoggedResponse;
import com.pokemon.tcg.modules.user.dto.responses.UserRegisteredResponse;
import com.pokemon.tcg.modules.user.services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping(value = "/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService service;

    @PostMapping("/register")
    public ResponseEntity<UserRegisteredResponse> registerUser(@RequestBody @Valid RegisterUserRequest request) {
        UserRegisteredResponse userRegistered = service.register(request);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(userRegistered.getUserId()).toUri();

        return ResponseEntity.created(uri).body(userRegistered);
    }

    @PostMapping("/login")
    public ResponseEntity<UserLoggedResponse> loginUser(@RequestBody @Valid LoginUserRequest request) {
        UserLoggedResponse userLogger = service.login(request);
        return ResponseEntity.ok(userLogger);
    }
}
