package com.okanetransfer.service.facade.aml;

import com.okanetransfer.entity.aml.DeclarationSoupcon;
import com.okanetransfer.entity.transfert.Transfert;

import java.util.List;

public interface IAmlService {

    /** Blocks transfer if sender or beneficiary matches the OFAC sanctions list. */
    void verifierOFAC(Transfert transfert);

    /** Evaluates a transfer against all active AML rules and creates declarations. */
    List<DeclarationSoupcon> evaluerTransfert(Transfert transfert);

    /** Returns all suspicious declarations. */
    List<DeclarationSoupcon> getDeclarations();

    /** Marks a declaration as processed. */
    DeclarationSoupcon traiterDeclaration(Long declarationId);
}
