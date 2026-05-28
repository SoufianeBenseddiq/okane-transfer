package com.okanetransfer.service.dto.transfert.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class SendTransfertReceiptEmailRequest {

    @NotBlank(message = "Le code de retrait est obligatoire.")
    private String codeRetrait;

    @Email(message = "Format email invalide")
    private String destinataireEmail;

    public String getCodeRetrait() {
        return codeRetrait;
    }

    public void setCodeRetrait(String codeRetrait) {
        this.codeRetrait = codeRetrait;
    }

    public String getDestinataireEmail() {
        return destinataireEmail;
    }

    public void setDestinataireEmail(String destinataireEmail) {
        this.destinataireEmail = destinataireEmail;
    }
}