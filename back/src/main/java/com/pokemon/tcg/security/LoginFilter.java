package com.pokemon.tcg.security;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.pokemon.tcg.utils.JWTUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class LoginFilter extends OncePerRequestFilter {
    private final JWTUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authBearer = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authBearer != null) {
            // Le quitan el bearer del principio
            String token = authBearer.substring(7);

            // validamos y decodificamos el JWT
            DecodedJWT decodedJWT = jwtUtils.validateJWT(token);
            String username = decodedJWT.getSubject();
            decodedJWT.getClaim("token_type");

            // cargamos la sesion del usuario en el contexto
            SecurityContext context = SecurityContextHolder.getContext();
            Authentication authentication = new UsernamePasswordAuthenticationToken(username,null);
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);
        }

        filterChain.doFilter(request, response);
    }
}
