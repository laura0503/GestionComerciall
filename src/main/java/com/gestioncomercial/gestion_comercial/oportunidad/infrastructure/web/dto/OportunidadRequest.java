package com.gestioncomercial.gestion_comercial.oportunidad.infrastructure.web.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.gestioncomercial.gestion_comercial.oportunidad.application.port.in.DatosOportunidad;
import com.gestioncomercial.gestion_comercial.oportunidad.domain.model.Fase;

// datos que envía el cliente para crear o actualizar una oportunidad (sin id)
public record OportunidadRequest(String titulo, BigDecimal importe, Fase fase, LocalDate fechaCierrePrevista,
        Long empresaId, Long contactoId) {

    // JSON → datos que entiende el caso de uso
    public DatosOportunidad aDatos() {
        return new DatosOportunidad(titulo, importe, fase, fechaCierrePrevista, empresaId, contactoId);
    }
}
