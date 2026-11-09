package com.gestioncomercial.gestion_comercial.empresa.infrastructure.web.dto;

import com.gestioncomercial.gestion_comercial.empresa.application.port.in.DatosEmpresa;

// datos que envía el cliente para crear o actualizar una empresa (sin id)
public record EmpresaRequest(String nombre, String sector, String provincia, String estado) {

    // JSON → datos que entiende el caso de uso
    public DatosEmpresa aDatos() {
        return new DatosEmpresa(nombre, sector, provincia, estado);
    }
}
