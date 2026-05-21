package com.okanetransfer.service.dto.user.request;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO pour renouveler l'access token.
 * Endpoint : POST /api/auth/refresh
 */
public class RefreshTokenRequest {

    @NotBlank(message = "Le refresh token est obligatoire")
    private String refreshToken;

    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
}
