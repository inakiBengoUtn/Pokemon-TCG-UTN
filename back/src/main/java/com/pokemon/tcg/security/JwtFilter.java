package com.pokemon.tcg.security;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.pokemon.tcg.utils.CookieUtils;
import com.pokemon.tcg.utils.JWTUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final JWTUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = CookieUtils.extractCookie(request, "accessToken");

            if (token != null) {
                // validamos y decodificamos el JWT
                DecodedJWT decodedJWT = jwtUtils.validateJWT(token);
                String username = decodedJWT.getSubject();
                String tokenType = decodedJWT.getClaim("token_type").asString();

                if ("ACCESS_TOKEN".equals(tokenType)) {
                    // cargamos la sesion del usuario en el contexto
                    Authentication authentication = new UsernamePasswordAuthenticationToken(
                            username,
                            null,
                            new ArrayList<>()
                    );
                    SecurityContextHolder.getContext()
                            .setAuthentication(authentication);
                }
            }
        } catch (Exception ex) {
            SecurityContextHolder.clearContext();
        }
        filterChain.doFilter(request, response);
    }
}
