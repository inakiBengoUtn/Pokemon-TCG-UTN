package com.pokemon.tcg.modules.user.controllers;

import com.pokemon.tcg.modules.user.dto.requests.LoginUserRequest;
import com.pokemon.tcg.modules.user.dto.requests.RegisterUserRequest;
import com.pokemon.tcg.modules.user.dto.responses.RefreshTokenResponse;
import com.pokemon.tcg.modules.user.dto.responses.UserLoggedResponse;
import com.pokemon.tcg.modules.user.dto.responses.UserRegisteredResponse;
import com.pokemon.tcg.modules.user.services.AuthService;
import com.pokemon.tcg.modules.user.services.CookiesService;
import com.pokemon.tcg.utils.CookieUtils;
import com.pokemon.tcg.utils.JWTUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping(value = "/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final CookiesService cookiesService;

    @PostMapping("/register")
    public ResponseEntity<UserRegisteredResponse> registerUser(@RequestBody @Valid RegisterUserRequest request,
                                                               HttpServletResponse response) {
        UserRegisteredResponse userRegistered = authService.register(request);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(userRegistered.getUserId()).toUri();

        HttpHeaders headers = cookiesService.createAuthHeaders(
                userRegistered.getAccessToken(),
                userRegistered.getRefreshToken()
        );

        return ResponseEntity.created(uri)
                .headers(headers)
                .body(userRegistered);
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody @Valid LoginUserRequest request,
                                                        HttpServletResponse response) {
        UserLoggedResponse userLogger = authService.login(request);

        HttpHeaders headers = cookiesService.createAuthHeaders(
                userLogger.getAccessToken(),
                userLogger.getRefreshToken()
        );

        return ResponseEntity.ok()
                .headers(headers).build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<String> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        String token = CookieUtils.extractCookie(request,"refreshToken");

        RefreshTokenResponse tokenResponse = authService.validateRefreshToken(token);

        HttpHeaders headers = cookiesService.createAuthHeaders(
                tokenResponse.getAccessToken(),
                tokenResponse.getRefreshToken()
        );

        return ResponseEntity.ok()
                .headers(headers)
                .build();
    }
}
