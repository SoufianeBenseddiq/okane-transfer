package com.okanetransfer.service.facade.aml;

import java.util.List;

import com.okanetransfer.entity.aml.DeclarationSoupcon;

public interface IDeclarationSoupconService {

    DeclarationSoupcon create(DeclarationSoupcon declaration);

    DeclarationSoupcon update(Long id, DeclarationSoupcon declaration);

    DeclarationSoupcon getById(Long id);

    List<DeclarationSoupcon> getAll();

    void delete(Long id);
}
