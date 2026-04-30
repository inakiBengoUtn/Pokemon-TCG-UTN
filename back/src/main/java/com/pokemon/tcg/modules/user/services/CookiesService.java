package com.pokemon.tcg.modules.user.services;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.util.Arrays;


@Service
public class CookiesService {
    public HttpHeaders createAuthHeaders(String accessToken, String refreshToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, createCookie("accessToken", accessToken, "/", 48 * 3600).toString());
        headers.add(HttpHeaders.SET_COOKIE, createCookie("refreshToken", refreshToken, "/", 240 * 3600).toString());
        return headers;
    }

    private ResponseCookie createCookie(String name, String value, String path, long maxAge) {
        return ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(false) // Cambiar a true en producción
                .path(path)
                .maxAge(maxAge)
                .sameSite("Lax")
                .build();
    }
}
