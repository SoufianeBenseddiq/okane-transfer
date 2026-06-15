package com.okanetransfer.service.dto.user.request;

public class ChangePasswordRequest {
    private String currentPassword;
    private String newPassword;

    // Getters & Setters
    public String getCurrentPassword() { return currentPassword; }
    public void setCurrentPassword(String currentPassword) { this.currentPassword = currentPassword; }
    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
}
