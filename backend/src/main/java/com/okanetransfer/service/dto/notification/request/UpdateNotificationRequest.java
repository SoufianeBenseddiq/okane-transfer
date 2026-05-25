package com.okanetransfer.service.dto.notification.request;

public class UpdateNotificationRequest {

    private Boolean lue;

    private String message;

    public UpdateNotificationRequest() {
    }

    public UpdateNotificationRequest(Boolean lue, String message) {
        this.lue = lue;
        this.message = message;
    }

    public Boolean getLue() {
        return lue;
    }

    public void setLue(Boolean lue) {
        this.lue = lue;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
