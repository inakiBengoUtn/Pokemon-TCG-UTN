package com.pokemon.tcg.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JWTUtils {

    @Value("${security.jwt.issuer}")
    private String issuer;
    @Value("${security.jwt.secret}")
    private String secret;
    @Value("${security.jwt.expires.access}")
    private Long expiresAccessToken;
    @Value("${security.jwt.expires.refresh}")
    private Long expiresRefreshToken;

    public String createAccessToken(String subject, Map<String, Object> claims) {
        claims.put("token_type", "ACCESS_TOKEN");
        return buildJWT(subject, claims, expiresAccessToken);
    }

    public String createRefreshToken(String subject) {
        HashMap<String, Object> claims = new HashMap<>();
        claims.put("token_type", "REFRESH_TOKEN");
        return buildJWT(subject, claims, expiresAccessToken);
    }

    // valida que el jwt que nos envian sea valido
    public DecodedJWT validateJWT(String jwt) {
        Algorithm algorithm = Algorithm.HMAC256(this.secret);
        JWTVerifier verifier = JWT.require(algorithm).withIssuer(this.issuer).build();
        return verifier.verify(jwt);
    }

    private String buildJWT(String subject, Map<String, Object> claims, final long expiration) {
        Algorithm algorithm = Algorithm.HMAC256(this.secret);

        Date now = new Date();

        return JWT.create()
                .withIssuer(this.issuer)
                .withIssuedAt(now) // fecha de creacion
                .withExpiresAt(new Date(now.getTime() + Duration.ofHours(expiration).toMillis())) //fecha de expiracion
                .withSubject(subject)
                .sign(algorithm);
    }
}
