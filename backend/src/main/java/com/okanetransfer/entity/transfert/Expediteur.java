package com.okanetransfer.entity.transfert;

import com.okanetransfer.entity.user.Client;
import com.okanetransfer.entity.user.PieceIdentite;
import jakarta.persistence.*;

@Entity
@Table(name = "expediteurs")
public class Expediteur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // le client qui envoie — nom, prenom, tel viennent de Utilisateur
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")//nullable=false
    private Client client;

    // la pièce qu'il a présentée physiquement au guichet
    // doit appartenir à ce client (vérification dans le service)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "piece_identite_id") //nullable=false
    private PieceIdentite pieceConfirmee;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }

    public PieceIdentite getPieceConfirmee() { return pieceConfirmee; }
    public void setPieceConfirmee(PieceIdentite pieceConfirmee) {
        this.pieceConfirmee = pieceConfirmee;
    }
}