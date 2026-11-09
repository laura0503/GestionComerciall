package com.gestioncomercial.gestion_comercial.oportunidad.infrastructure.web.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.gestioncomercial.gestion_comercial.oportunidad.application.port.in.OportunidadDetalle;
import com.gestioncomercial.gestion_comercial.oportunidad.domain.model.Fase;
import com.gestioncomercial.gestion_comercial.oportunidad.domain.model.Oportunidad;

// datos que devuelve la API al cliente (el mismo JSON de siempre)
public record OportunidadResponse(Long id, String titulo, BigDecimal importe, Fase fase,
        LocalDate fechaCierrePrevista, Long empresaId, String empresaNombre, Long contactoId,
        String contactoNombre) {

    // resultado del caso de uso → DTO
    public static OportunidadResponse desde(OportunidadDetalle detalle) {
        Oportunidad o = detalle.oportunidad();
        return new OportunidadResponse(
                o.getId(),
                o.getTitulo(),
                o.getImporte(),
                o.getFase(),
                o.getFechaCierrePrevista(),
                o.getEmpresaId(),
                detalle.empresaNombre(),
                o.getContactoId(), // puede ser null: el contacto es opcional
                detalle.contactoNombre());
    }
}
