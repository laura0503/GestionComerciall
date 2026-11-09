package com.gestioncomercial.gestion_comercial.dto;

import com.gestioncomercial.gestion_comercial.contacto.infrastructure.persistence.ContactoEntity;

import com.gestioncomercial.gestion_comercial.empresa.infrastructure.persistence.EmpresaEntity;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.gestioncomercial.gestion_comercial.Fase;
import com.gestioncomercial.gestion_comercial.Oportunidad;

// datos que envía el cliente para crear o actualizar una oportunidad (sin id)
public record OportunidadRequest(String titulo, BigDecimal importe, Fase fase, LocalDate fechaCierrePrevista,
        Long empresaId, Long contactoId) {

    // convierte el DTO en la entidad; empresa y contacto los busca antes el Service
    public Oportunidad aEntidad(EmpresaEntity empresa, ContactoEntity contacto) {
        return new Oportunidad(null, titulo, importe, fase, fechaCierrePrevista, empresa, contacto);
    }
}
