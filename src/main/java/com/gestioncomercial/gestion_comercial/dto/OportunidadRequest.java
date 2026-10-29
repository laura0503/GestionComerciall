package com.gestioncomercial.gestion_comercial.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.gestioncomercial.gestion_comercial.Contacto;
import com.gestioncomercial.gestion_comercial.Empresas;
import com.gestioncomercial.gestion_comercial.Fase;
import com.gestioncomercial.gestion_comercial.Oportunidad;

// datos que envía el cliente para crear o actualizar una oportunidad (sin id)
public record OportunidadRequest(String titulo, BigDecimal importe, Fase fase, LocalDate fechaCierrePrevista,
        Long empresaId, Long contactoId) {

    // convierte el DTO en la entidad; empresa y contacto los busca antes el Service
    public Oportunidad aEntidad(Empresas empresa, Contacto contacto) {
        return new Oportunidad(null, titulo, importe, fase, fechaCierrePrevista, empresa, contacto);
    }
}
