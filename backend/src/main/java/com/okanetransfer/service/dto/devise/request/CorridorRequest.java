package com.okanetransfer.service.dto.devise.request;

import jakarta.validation.constraints.NotNull;

public class CorridorRequest {

    @NotNull(message = "La devise source est obligatoire")
    private Long deviseSourceId;

    @NotNull(message = "La devise destination est obligatoire")
    private Long deviseDestinationId;

    // getters / setters
    public Long getDeviseSourceId() { return deviseSourceId; }
    public void setDeviseSourceId(Long deviseSourceId) { this.deviseSourceId = deviseSourceId; }

    public Long getDeviseDestinationId() { return deviseDestinationId; }
    public void setDeviseDestinationId(Long deviseDestinationId) { this.deviseDestinationId = deviseDestinationId; }
}