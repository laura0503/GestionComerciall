package com.gestioncomercial.gestion_comercial.actividad.infrastructure.web.dto;

import java.time.LocalDateTime;

import com.gestioncomercial.gestion_comercial.actividad.domain.model.TipoActividad;

// lo que envía el cliente (sin id ni completada: eso lo decide el servidor)
public record ActividadRequest(
        TipoActividad tipo,
        String asunto,
        String descripcion,
        LocalDateTime fecha,
        Long empresaId) {
}
