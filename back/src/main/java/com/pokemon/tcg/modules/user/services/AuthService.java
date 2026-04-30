package com.pokemon.tcg.modules.user.services;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.pokemon.tcg.modules.user.dto.requests.LoginUserRequest;
import com.pokemon.tcg.modules.user.dto.requests.RegisterUserRequest;
import com.pokemon.tcg.modules.user.dto.responses.RefreshTokenResponse;
import com.pokemon.tcg.modules.user.dto.responses.UserLoggedResponse;
import com.pokemon.tcg.modules.user.dto.responses.UserRegisteredResponse;
import com.pokemon.tcg.modules.user.exceptions.BadCredentialsException;
import com.pokemon.tcg.modules.user.exceptions.UserAlreadyExistsException;
import com.pokemon.tcg.modules.user.models.User;
import com.pokemon.tcg.modules.user.repo.UserRepo;
import com.pokemon.tcg.utils.JWTUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager manager;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtils jwtUtils;
    private final UserRepo repo;

    public UserRegisteredResponse register(RegisterUserRequest request) {
        // encriptamos el password
        String password = passwordEncoder.encode(request.getPassword());

        if (repo.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException();
        }
        // guardamos user
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(password);

        User userSaved = repo.save(user);

        // Login al user
        LoginUserRequest loginUser = new LoginUserRequest();
        loginUser.setUsername(request.getUsername());
        loginUser.setPassword(request.getPassword());
        UserLoggedResponse userLogged = this.login(loginUser);

        // devolvemos dto
        UserRegisteredResponse userRegistered = new UserRegisteredResponse();
        userRegistered.setUserId(userSaved.getId());
        userRegistered.setAccessToken(userLogged.getAccessToken());
        userRegistered.setRefreshToken(userLogged.getRefreshToken());
        return userRegistered;
    }

    public UserLoggedResponse login(LoginUserRequest request) {
        if (!repo.existsByUsername(request.getUsername())) {
            throw new BadCredentialsException();
        }

        Authentication auth = new UsernamePasswordAuthenticationToken(request.getUsername(),request.getPassword());
        Authentication authenticated = manager.authenticate(auth);
        SecurityContextHolder.getContext().setAuthentication(authenticated);

        HashMap<String,Object> map = new HashMap<>();
        String accessToken = jwtUtils.createAccessToken(request.getUsername(),map);
        String refreshToken = jwtUtils.createRefreshToken(request.getUsername());

        UserLoggedResponse response = new UserLoggedResponse();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        return response;
    }

    public RefreshTokenResponse validateRefreshToken(String token) {
        DecodedJWT decodedJWT = jwtUtils.validateJWT(token);
        Claim tokenType = decodedJWT.getClaim("token_type");

        if ("REFRESH_TOKEN".equals(tokenType.asString())) throw new JWTVerificationException("The token is not for refreshing.");

        String subject = decodedJWT.getSubject();
        HashMap<String, Object> map = new HashMap<>();
        String accessToken = jwtUtils.createAccessToken(subject, map);
        String refreshToken = jwtUtils.createRefreshToken(subject);

        RefreshTokenResponse response = new RefreshTokenResponse();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        return response;
    }
}
