package com.cydrag.fridgeapp.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class CookieFactory {

    public static final String REFRESH_TOKEN_COOKIE = "refresh_token";

    @Value("${app.security.jwtRefreshExpirationMs}")
    private long jwtRefreshExpirationMs;

    public ResponseCookie createRefreshTokenCookie(String token) {
        return ResponseCookie.from(REFRESH_TOKEN_COOKIE, token)
                .httpOnly(true)
                .secure(true)
                .path("/api/auth")
                .maxAge(jwtRefreshExpirationMs / 1000)
                .sameSite("Strict")
                .build();
    }

    public ResponseCookie getCleanRefreshTokenCookie() {
        return ResponseCookie.from(REFRESH_TOKEN_COOKIE, "")
                .path("/api/auth")
                .maxAge(0)
                .build();
    }
}
