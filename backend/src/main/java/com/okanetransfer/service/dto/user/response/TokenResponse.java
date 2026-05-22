package com.okanetransfer.service.dto.user.response;

public class TokenResponse {

    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private Long expiresIn;

    // true si on attend encore la vérification OTP
    // dans ce cas accessToken contient juste l'email encodé — pas un vrai token
    private Boolean requiresOtp;

    // ── constructeurs statiques (remplacent le builder) ───────────────────────

    // token complet après authentification réussie
    public static TokenResponse of(String accessToken,
                                   String refreshToken,
                                   Long expiresIn) {
        TokenResponse r = new TokenResponse();
        r.setAccessToken(accessToken);
        r.setRefreshToken(refreshToken);
        r.setTokenType("Bearer");
        r.setExpiresIn(expiresIn);
        r.setRequiresOtp(false);
        return r;
    }

    // token partiel — en attente du code OTP (2FA)
    public static TokenResponse partial(String email) {
        TokenResponse r = new TokenResponse();
        r.setAccessToken(null);
        r.setRefreshToken(null);
        r.setTokenType(null);
        r.setExpiresIn(null);
        r.setRequiresOtp(true);
        return r;
    }

    // ── builder manuel (utilisé dans AuthServiceImpl) ─────────────────────────

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final TokenResponse r = new TokenResponse();
        public Builder accessToken(String v)  { r.accessToken  = v; return this; }
        public Builder refreshToken(String v) { r.refreshToken = v; return this; }
        public Builder tokenType(String v)    { r.tokenType    = v; return this; }
        public Builder expiresIn(Long v)      { r.expiresIn    = v; return this; }
        public TokenResponse build()          { return r; }
    }

    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }

    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }

    public String getTokenType() { return tokenType; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }

    public Long getExpiresIn() { return expiresIn; }
    public void setExpiresIn(Long expiresIn) { this.expiresIn = expiresIn; }

    public Boolean getRequiresOtp() { return requiresOtp; }
    public void setRequiresOtp(Boolean requiresOtp) { this.requiresOtp = requiresOtp; }
}