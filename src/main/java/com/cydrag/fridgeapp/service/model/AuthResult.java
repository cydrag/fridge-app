package com.cydrag.fridgeapp.service.model;

public record AuthResult(String accessToken, String refreshToken, String tokenType, long accessTokenExpiry) {

    public AuthResult(String accessToken, String refreshToken, long accessTokenExpiry) {
        this(accessToken, refreshToken, "Bearer", accessTokenExpiry);
    }
}
