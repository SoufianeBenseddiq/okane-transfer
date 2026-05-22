package com.okanetransfer.service.facade.aml;

import java.util.List;

import com.okanetransfer.entity.aml.ListeOFAC;

public interface IListeOFACService {

    ListeOFAC create(ListeOFAC listeOFAC);

    ListeOFAC update(Long id, ListeOFAC listeOFAC);

    ListeOFAC getById(Long id);

    List<ListeOFAC> getAll();

    void delete(Long id);
}
