package com.okanetransfer.controller.user;

import com.okanetransfer.service.dto.user.request.LoginRequest;
import com.okanetransfer.service.dto.user.request.RefreshTokenRequest;
import com.okanetransfer.service.dto.user.response.TokenResponse;
import com.okanetransfer.service.facade.user.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentification", description = "Connexion, rafraîchissement de token JWT et déconnexion")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @Operation(summary = "Connexion utilisateur",
               description = "Authentifie un utilisateur par email/mot de passe et retourne un access token + refresh token JWT.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Authentification réussie — tokens JWT retournés"),
        @ApiResponse(responseCode = "401", description = "Identifiants invalides"),
        @ApiResponse(responseCode = "400", description = "Corps de requête invalide")
    })
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Rafraîchir le token d'accès",
               description = "Échange un refresh token valide contre un nouveau pair access/refresh tokens.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Nouveau couple de tokens retourné"),
        @ApiResponse(responseCode = "401", description = "Refresh token invalide ou expiré")
    })
    public ResponseEntity<TokenResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request));
    }

    @PostMapping("/logout")
    @Operation(summary = "Déconnexion",
               description = "Invalide le token JWT actuel côté serveur. Passer le token dans l'en-tête Authorization: Bearer <token>.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Déconnexion réussie"),
        @ApiResponse(responseCode = "401", description = "Token manquant ou invalide")
    })
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            authService.logout(header.substring(7));
        }
        return ResponseEntity.noContent().build();
    }
}
