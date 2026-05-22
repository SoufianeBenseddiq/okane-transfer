package com.okanetransfer.service.converter.user;

import com.okanetransfer.entity.user.PieceIdentite;
import com.okanetransfer.service.dto.user.request.PieceIdentiteRequest;
import com.okanetransfer.service.dto.user.response.PieceIdentiteResponse;
import com.okanetransfer.shared.enums.TypePiece;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PieceIdentiteConverter {

    public PieceIdentite toEntity(PieceIdentiteRequest request) {
        if (request == null) return null;

        PieceIdentite piece = new PieceIdentite();

        piece.setNumero(request.getNumero());

        piece.setType(TypePiece.valueOf(request.getType()));

        piece.setPaysEmetteur(request.getPaysEmetteur());

        piece.setDateExpiration(request.getDateExpiration());

        return piece;
    }

    public PieceIdentiteResponse toResponse(PieceIdentite piece) {
        if (piece == null) return null;

        PieceIdentiteResponse r = new PieceIdentiteResponse();

        r.setId(piece.getId());

        r.setNumero(piece.getNumero());

        r.setType(piece.getType().name());

        r.setPaysEmetteur(piece.getPaysEmetteur());
        r.setDateExpiration(piece.getDateExpiration());
        r.setPrincipale(piece.getPrincipale());

        return r;
    }

    public List<PieceIdentiteResponse> toResponseList(List<PieceIdentite> pieces) {
        if (pieces == null || pieces.isEmpty()) return List.of();

        return pieces.stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }
}
