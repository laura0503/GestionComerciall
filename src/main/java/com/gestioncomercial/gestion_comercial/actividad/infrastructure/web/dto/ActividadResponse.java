package com.gestioncomercial.gestion_comercial.actividad.infrastructure.web.dto;

import java.time.LocalDateTime;

import com.gestioncomercial.gestion_comercial.actividad.domain.model.Actividad;
import com.gestioncomercial.gestion_comercial.actividad.domain.model.TipoActividad;

// lo que devolvemos al cliente
public record ActividadResponse(
        Long id,
        TipoActividad tipo,
        String asunto,
        String descripcion,
        LocalDateTime fecha,
        boolean completada,
        Long empresaId) {

    // dominio → DTO
    public static ActividadResponse desde(Actividad actividad) {
        return new ActividadResponse(
                actividad.getId(),
                actividad.getTipo(),
                actividad.getAsunto(),
                actividad.getDescripcion(),
                actividad.getFecha(),
                actividad.isCompletada(),
                actividad.getEmpresaId());
    }
}
