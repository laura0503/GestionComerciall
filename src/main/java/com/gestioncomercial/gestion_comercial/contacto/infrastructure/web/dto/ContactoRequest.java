package com.gestioncomercial.gestion_comercial.contacto.infrastructure.web.dto;

import com.gestioncomercial.gestion_comercial.contacto.application.port.in.DatosContacto;

// datos que envía el cliente para crear o actualizar un contacto (sin id)
public record ContactoRequest(String nombre, String apellidos, String cargo, String email, String telefono,
        Long empresaId) {

    // JSON → datos que entiende el caso de uso
    public DatosContacto aDatos() {
        return new DatosContacto(nombre, apellidos, cargo, email, telefono, empresaId);
    }
}
