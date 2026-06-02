package com.okanetransfer.service.converter.transfert;

import com.okanetransfer.entity.transfert.Expediteur;
import com.okanetransfer.entity.user.Client;
import com.okanetransfer.entity.user.PieceIdentite;
import com.okanetransfer.service.dto.transfert.response.ExpediteurResponse;

public final class ExpediteurConverter {

    private ExpediteurConverter() {
    }

    public static ExpediteurResponse toResponse(Expediteur expediteur) {
        ExpediteurResponse response = new ExpediteurResponse();
        response.setId(expediteur.getId());

        Client client = expediteur.getClient();
        if (client != null) {
            response.setClientId(client.getId());
            response.setNomClient(client.getNom());
            response.setPrenomClient(client.getPrenom());
            response.setTelephoneClient(client.getTelephone());
            response.setPaysClient(client.getPays() != null ? client.getPays().getNom() : null);
        }

        PieceIdentite pieceIdentite = expediteur.getPieceConfirmee();
        if (pieceIdentite != null) {
            response.setPieceIdentiteId(pieceIdentite.getId());
            response.setTypePiece(pieceIdentite.getType());
            response.setPaysEmetteurPiece(pieceIdentite.getPaysEmetteur());
        }

        return response;
    }
}
