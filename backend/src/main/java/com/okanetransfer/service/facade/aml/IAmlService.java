package com.okanetransfer.service.facade.aml;

import java.util.List;

import com.okanetransfer.entity.aml.DeclarationSoupcon;
import com.okanetransfer.entity.transfert.Transfert;

public interface IAmlService {

    /**
     * Vérifie si l'expéditeur ou le bénéficiaire est sur la liste OFAC
     * @param transfert le transfert à vérifier
     * @throws OFACViolationException si une personne est trouvée sur la liste
     */
    void verifierOFAC(Transfert transfert);

    /**
     * Évalue un transfert contre toutes les règles AML actives
     * @param transfert le transfert à évaluer
     * @return la liste des déclarations générées (peut être vide)
     */
    List<DeclarationSoupcon> evaluerTransfert(Transfert transfert);

    /**
     * Récupère toutes les déclarations de soupçon
     * @return liste de toutes les déclarations
     */
    List<DeclarationSoupcon> getDeclarations();

    /**
     * Marque une déclaration comme traitée par l'admin
     * @param declarationId l'id de la déclaration
     * @return la déclaration mise à jour
     */
    DeclarationSoupcon traiterDeclaration(Long declarationId);
}
