package com.cydrag.fridgeapp.controller;

import com.cydrag.fridgeapp.dto.request.LoginRequest;
import com.cydrag.fridgeapp.dto.request.RegisterRequest;
import com.cydrag.fridgeapp.dto.request.TokenRefreshRequest;
import com.cydrag.fridgeapp.dto.response.AuthResponse;
import com.cydrag.fridgeapp.exception.TokenRefreshException;
import com.cydrag.fridgeapp.security.CookieFactory;
import com.cydrag.fridgeapp.service.AuthService;
import com.cydrag.fridgeapp.service.RefreshTokenService;
import com.cydrag.fridgeapp.service.model.AuthResult;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.cydrag.fridgeapp.security.CookieFactory.REFRESH_TOKEN_COOKIE;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;
    private final CookieFactory cookieFactory;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody @Valid RegisterRequest request) {
        AuthResult result = authService.register(request.getEmail(), request.getPassword());

        return buildResponseWithCookie(result);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginRequest request) {
        AuthResult result = authService.login(request.getEmail(), request.getPassword());

        return buildResponseWithCookie(result);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody(required = false) TokenRefreshRequest request,
                                                     @CookieValue(name = REFRESH_TOKEN_COOKIE, required = false) String cookieRefreshToken) {
        String tokenToUse = (cookieRefreshToken != null) ? cookieRefreshToken : request.getRefreshToken();

        if (ObjectUtils.isEmpty(tokenToUse)) {
            throw new TokenRefreshException("Refresh token is missing!");
        }

        AuthResult result = refreshTokenService.processRefreshToken(tokenToUse);

        return buildResponseWithCookie(result);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            @RequestBody(required = false) TokenRefreshRequest request,
            @CookieValue(name = "refresh_token", required = false) String cookieRefreshToken) {
        String tokenToDelete = (cookieRefreshToken != null) ? cookieRefreshToken :
                (request != null ? request.getRefreshToken() : null);

        if (tokenToDelete != null) {
            refreshTokenService.deleteByToken(tokenToDelete);
        }

        ResponseCookie cleanCookie = cookieFactory.getCleanRefreshTokenCookie();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cleanCookie.toString())
                .build();
    }

    private ResponseEntity<AuthResponse> buildResponseWithCookie(AuthResult result) {
        ResponseCookie cookie = cookieFactory.createRefreshTokenCookie(result.refreshToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new AuthResponse(
                        result.accessToken(),
                        result.refreshToken(),
                        result.tokenType(),
                        result.accessTokenExpiry()
                ));
    }
}
