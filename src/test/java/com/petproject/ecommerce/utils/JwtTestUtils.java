package com.petproject.ecommerce.utils;

import com.petproject.ecommerce.config.PropertiesReader;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;

public final class JwtTestUtils {

    private static final String SECRET_KEY = PropertiesReader.get("jwt.secret");

    private JwtTestUtils() {
    }

    public static String getPayload(String token) {
        String[] parts = token.split("\\.");

        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid JWT format");
        }

        return new String(
                Base64.getUrlDecoder().decode(parts[1]),
                StandardCharsets.UTF_8
        );
    }

    public static String replacePayload(String token, String payload) {
        String[] parts = token.split("\\.");

        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid JWT format");
        }

        String encodedPayload = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(payload.getBytes(StandardCharsets.UTF_8));

        return parts[0] + "." + encodedPayload + "." + parts[2];
    }

    public static String createExpiredToken() {
        SecretKey key = Keys.hmacShaKeyFor(
                SECRET_KEY.getBytes(StandardCharsets.UTF_8)
        );

        Date now = new Date();
        Date expiration = new Date(now.getTime() - 60_000);

        return Jwts.builder()
                .subject("1")
                .claim("email", "test@test.com")
                .claim("role", "ADMIN")
                .issuedAt(now)
                .expiration(expiration)
                .signWith(key)
                .compact();
    }
}
