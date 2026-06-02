package com.okanetransfer.service.dto.devise.request;

import jakarta.validation.constraints.NotNull;

public class CorridorRequest {

    @NotNull(message = "La devise source est obligatoire")
    private Long deviseSourceId;

    @NotNull(message = "La devise destination est obligatoire")
    private Long deviseDestinationId;

    @NotNull(message = "Le pays source est obligatoire")
    private Long paysSourceId;

    @NotNull(message = "Le pays destination est obligatoire")
    private Long paysDestinationId;

    public Long getDeviseSourceId() { return deviseSourceId; }
    public void setDeviseSourceId(Long deviseSourceId) { this.deviseSourceId = deviseSourceId; }

    public Long getDeviseDestinationId() { return deviseDestinationId; }
    public void setDeviseDestinationId(Long deviseDestinationId) { this.deviseDestinationId = deviseDestinationId; }

    public Long getPaysSourceId() { return paysSourceId; }
    public void setPaysSourceId(Long paysSourceId) { this.paysSourceId = paysSourceId; }

    public Long getPaysDestinationId() { return paysDestinationId; }
    public void setPaysDestinationId(Long paysDestinationId) { this.paysDestinationId = paysDestinationId; }
}
