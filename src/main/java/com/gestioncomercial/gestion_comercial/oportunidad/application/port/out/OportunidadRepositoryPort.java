package com.gestioncomercial.gestion_comercial.oportunidad.application.port.out;

import java.util.List;
import java.util.Optional;

import com.gestioncomercial.gestion_comercial.oportunidad.domain.model.Fase;
import com.gestioncomercial.gestion_comercial.oportunidad.domain.model.Oportunidad;

// lo que el núcleo necesita para guardar/leer oportunidades
public interface OportunidadRepositoryPort {
    Oportunidad guardar(Oportunidad oportunidad);

    Optional<Oportunidad> buscarPorId(Long id);

    List<Oportunidad> buscarTodas();

    List<Oportunidad> buscarPorFase(Fase fase);

    List<Oportunidad> buscarPorEmpresa(Long empresaId);

    void borrar(Long id);
}
