package com.gestioncomercial.gestion_comercial.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.gestioncomercial.gestion_comercial.Contacto;
import com.gestioncomercial.gestion_comercial.Fase;
import com.gestioncomercial.gestion_comercial.Oportunidad;

// datos que devuelve la API al cliente
public record OportunidadResponse(Long id, String titulo, BigDecimal importe, Fase fase,
        LocalDate fechaCierrePrevista, Long empresaId, String empresaNombre, Long contactoId,
        String contactoNombre) {

    // convierte la entidad de la BD en el DTO que se envía
    public static OportunidadResponse desde(Oportunidad oportunidad) {
        Contacto contacto = oportunidad.getContacto(); // puede ser null: el contacto es opcional
        return new OportunidadResponse(
                oportunidad.getId(),
                oportunidad.getTitulo(),
                oportunidad.getImporte(),
                oportunidad.getFase(),
                oportunidad.getFechaCierrePrevista(),
                oportunidad.getEmpresa().getId(),
                oportunidad.getEmpresa().getNombre(),
                contacto != null ? contacto.getId() : null,
                contacto != null ? contacto.getNombre() : null);
    }
}
