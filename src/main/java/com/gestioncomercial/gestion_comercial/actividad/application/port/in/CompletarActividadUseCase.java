package com.gestioncomercial.gestion_comercial.actividad.application.port.in;

import com.gestioncomercial.gestion_comercial.actividad.domain.model.Actividad;

public interface CompletarActividadUseCase {

    // "dime qué actividad (id) y te la devuelvo marcada como completada"
    Actividad completar(Long id);
}
