package com.irallyin.server.core.admin.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
public class AdminTokenService {

    private final SecretKey key;
    private final AdminProperties adminProperties;

    public AdminTokenService(
            @Value("${jwt.secret}") String secret,
            AdminProperties adminProperties
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.adminProperties = adminProperties;
    }

    public String generateToken(String email) {
        Date now = new Date();
        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(email)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + adminProperties.getTokenExpirationMs()))
                .claim("role", "ADMIN")
                .signWith(key)
                .compact();
    }

    public Optional<String> validateAndGetAdminEmail(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            if (!"ADMIN".equals(claims.get("role", String.class))) {
                return Optional.empty();
            }
            String email = claims.getSubject();
            if (!adminProperties.getEmail().equalsIgnoreCase(email)) {
                return Optional.empty();
            }
            return Optional.of(email);
        } catch (JwtException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
