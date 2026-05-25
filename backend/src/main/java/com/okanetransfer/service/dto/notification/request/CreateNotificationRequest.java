package com.okanetransfer.service.dto.notification.request;

import com.okanetransfer.shared.enums.TypeNotification;

public class CreateNotificationRequest {

    private Long destinataireId;

    private String message;

    private TypeNotification type;

    public CreateNotificationRequest() {
    }

    public CreateNotificationRequest(Long destinataireId, String message, TypeNotification type) {
        this.destinataireId = destinataireId;
        this.message = message;
        this.type = type;
    }

    public Long getDestinataireId() {
        return destinataireId;
    }

    public void setDestinataireId(Long destinataireId) {
        this.destinataireId = destinataireId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public TypeNotification getType() {
        return type;
    }

    public void setType(TypeNotification type) {
        this.type = type;
    }
}
