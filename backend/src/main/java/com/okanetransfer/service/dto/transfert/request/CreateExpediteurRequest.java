package com.okanetransfer.service.dto.transfert.request;

import jakarta.validation.constraints.NotNull;

public class CreateExpediteurRequest {

    @NotNull
    private Long clientId;

    @NotNull
    private Long pieceIdentiteId;

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public Long getPieceIdentiteId() {
        return pieceIdentiteId;
    }

    public void setPieceIdentiteId(Long pieceIdentiteId) {
        this.pieceIdentiteId = pieceIdentiteId;
    }
}
