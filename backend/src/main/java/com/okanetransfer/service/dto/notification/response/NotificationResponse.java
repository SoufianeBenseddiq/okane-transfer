package com.okanetransfer.service.dto.notification.response;

import com.okanetransfer.shared.enums.TypeNotification;
import java.time.LocalDateTime;

public class NotificationResponse {

    private Long id;

    private Long destinataireId;

    private String message;

    private TypeNotification type;

    private Boolean lue;

    private LocalDateTime envoyeLe;

    public NotificationResponse() {
    }

    public NotificationResponse(Long id, Long destinataireId, String message, TypeNotification type, Boolean lue, LocalDateTime envoyeLe) {
        this.id = id;
        this.destinataireId = destinataireId;
        this.message = message;
        this.type = type;
        this.lue = lue;
        this.envoyeLe = envoyeLe;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Boolean getLue() {
        return lue;
    }

    public void setLue(Boolean lue) {
        this.lue = lue;
    }

    public LocalDateTime getEnvoyeLe() {
        return envoyeLe;
    }

    public void setEnvoyeLe(LocalDateTime envoyeLe) {
        this.envoyeLe = envoyeLe;
    }
}
