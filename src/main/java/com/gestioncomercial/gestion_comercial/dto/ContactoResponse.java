package com.gestioncomercial.gestion_comercial.dto;

import com.gestioncomercial.gestion_comercial.Contacto;

// datos que devuelve la API al cliente
public record ContactoResponse(Long id, String nombre, String apellidos, String cargo, String email,
        String telefono, Long empresaId, String empresaNombre) {

    // convierte la entidad de la BD en el DTO que se envía
    public static ContactoResponse desde(Contacto contacto) {
        return new ContactoResponse(
                contacto.getId(),
                contacto.getNombre(),
                contacto.getApellidos(),
                contacto.getCargo(),
                contacto.getEmail(),
                contacto.getTelefono(),
                contacto.getEmpresa().getId(),
                contacto.getEmpresa().getNombre());
    }
}
