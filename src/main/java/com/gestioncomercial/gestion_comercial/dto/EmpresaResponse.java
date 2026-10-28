package com.gestioncomercial.gestion_comercial.dto;

import com.gestioncomercial.gestion_comercial.Empresas;

// datos que devuelve la API al cliente
public record EmpresaResponse(Long id, String nombre, String sector, String provincia, String estado) {

    // convierte la entidad de la BD en el DTO que se envía
    public static EmpresaResponse desde(Empresas empresa) {
        return new EmpresaResponse(
                empresa.getId(),
                empresa.getNombre(),
                empresa.getSector(),
                empresa.getProvincia(),
                empresa.getEstado());
    }
}
