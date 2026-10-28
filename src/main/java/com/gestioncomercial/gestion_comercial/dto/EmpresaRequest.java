package com.gestioncomercial.gestion_comercial.dto;

import com.gestioncomercial.gestion_comercial.Empresas;

// datos que envía el cliente para crear o actualizar una empresa (sin id)
public record EmpresaRequest(String nombre, String sector, String provincia, String estado) {

    // convierte el DTO en la entidad que se guarda en la BD
    public Empresas aEntidad() {
        return new Empresas(null, nombre, sector, provincia, estado);
    }
}
