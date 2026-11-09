package com.gestioncomercial.gestion_comercial.oportunidad.application.port.in;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.gestioncomercial.gestion_comercial.oportunidad.domain.model.Fase;

// los datos que llegan para crear o actualizar una oportunidad (sin id)
public record DatosOportunidad(String titulo, BigDecimal importe, Fase fase, LocalDate fechaCierrePrevista,
        Long empresaId, Long contactoId) {
}
