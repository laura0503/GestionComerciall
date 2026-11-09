package com.gestioncomercial.gestion_comercial.empresa.infrastructure.web.dto;

import com.gestioncomercial.gestion_comercial.empresa.domain.model.Empresa;

// datos que devuelve la API al cliente
public record EmpresaResponse(Long id, String nombre, String sector, String provincia, String estado) {

    // dominio → DTO
    public static EmpresaResponse desde(Empresa empresa) {
        return new EmpresaResponse(
                empresa.getId(),
                empresa.getNombre(),
                empresa.getSector(),
                empresa.getProvincia(),
                empresa.getEstado());
    }
}
