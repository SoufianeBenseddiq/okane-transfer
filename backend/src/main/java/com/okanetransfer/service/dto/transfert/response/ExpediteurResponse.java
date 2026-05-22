package com.okanetransfer.service.dto.transfert.response;

import com.okanetransfer.shared.enums.TypePiece;

public class ExpediteurResponse {

    private Long id;
    private Long clientId;
    private String nomClient;
    private String prenomClient;
    private String telephoneClient;
    private String paysClient;
    private Long pieceIdentiteId;
    private TypePiece typePiece;
    private String paysEmetteurPiece;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public String getNomClient() {
        return nomClient;
    }

    public void setNomClient(String nomClient) {
        this.nomClient = nomClient;
    }

    public String getPrenomClient() {
        return prenomClient;
    }

    public void setPrenomClient(String prenomClient) {
        this.prenomClient = prenomClient;
    }

    public String getTelephoneClient() {
        return telephoneClient;
    }

    public void setTelephoneClient(String telephoneClient) {
        this.telephoneClient = telephoneClient;
    }

    public String getPaysClient() {
        return paysClient;
    }

    public void setPaysClient(String paysClient) {
        this.paysClient = paysClient;
    }

    public Long getPieceIdentiteId() {
        return pieceIdentiteId;
    }

    public void setPieceIdentiteId(Long pieceIdentiteId) {
        this.pieceIdentiteId = pieceIdentiteId;
    }

    public TypePiece getTypePiece() {
        return typePiece;
    }

    public void setTypePiece(TypePiece typePiece) {
        this.typePiece = typePiece;
    }

    public String getPaysEmetteurPiece() {
        return paysEmetteurPiece;
    }

    public void setPaysEmetteurPiece(String paysEmetteurPiece) {
        this.paysEmetteurPiece = paysEmetteurPiece;
    }
}
