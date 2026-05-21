package com.okanetransfer.service.facade.user;


import com.okanetransfer.service.dto.user.request.LoginRequest;
import com.okanetransfer.service.dto.user.request.OtpRequest;
import com.okanetransfer.service.dto.user.request.RefreshTokenRequest;
import com.okanetransfer.service.dto.user.response.TokenResponse;

public interface AuthService {

    // étape 1 : vérifier email + mot de passe
    // si 2FA actif → envoie OTP par SMS et retourne un token partiel
    // si 2FA inactif → retourne directement access + refresh tokens
    TokenResponse login(LoginRequest request);

    // étape 2 (si 2FA actif) : vérifier le code OTP reçu par SMS
    // retourne access + refresh tokens si OTP valide
//    TokenResponse verifyOtp(OtpRequest request);

    // renouveler l'access token avec le refresh token
    TokenResponse refreshToken(RefreshTokenRequest request);

    // invalider le token (logout)
    void logout(String accessToken);
}