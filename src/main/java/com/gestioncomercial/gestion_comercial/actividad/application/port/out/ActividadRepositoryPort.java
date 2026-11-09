package com.gestioncomercial.gestion_comercial.actividad.application.port.out;

import java.util.List;
import java.util.Optional;

import com.gestioncomercial.gestion_comercial.actividad.domain.model.Actividad;

// lo que el núcleo necesita para guardar/leer actividades.
// No sabe si detrás hay H2, PostgreSQL o un fichero: eso lo decide el adaptador
public interface ActividadRepositoryPort {
    Actividad guardar(Actividad actividad);

    Optional<Actividad> buscarPorId(Long id);

    List<Actividad> buscarPorEmpresa(Long empresaId);

    List<Actividad> buscarPendientes(); // las no completadas

    void borrar(Long id);
}
