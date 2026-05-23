export interface TokenResponse {
  accessToken: string | null;
  refreshToken: string | null;
  tokenType: string | null;
  expiresIn: number | null;
  requiresOtp: boolean | null;
}
