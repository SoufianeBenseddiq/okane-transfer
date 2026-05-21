package com.okanetransfer.service.facade.aml;

import java.util.List;

import com.okanetransfer.entity.aml.RegleAML;

public interface IRegleAMLService {

    RegleAML create(RegleAML regleAML);

    RegleAML update(Long id, RegleAML regleAML);

    RegleAML getById(Long id);

    List<RegleAML> getAll();

    void delete(Long id);
}
