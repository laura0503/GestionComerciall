package com.gestioncomercial.gestion_comercial.dto;

import com.gestioncomercial.gestion_comercial.Contacto;
import com.gestioncomercial.gestion_comercial.Empresas;

// datos que envía el cliente para crear o actualizar un contacto (sin id)
public record ContactoRequest(String nombre, String apellidos, String cargo, String email, String telefono,
        Long empresaId) {

    // convierte el DTO en la entidad; la empresa la busca antes el Service
    public Contacto aEntidad(Empresas empresa) {
        return new Contacto(null, nombre, apellidos, cargo, email, telefono, empresa);
    }
}
