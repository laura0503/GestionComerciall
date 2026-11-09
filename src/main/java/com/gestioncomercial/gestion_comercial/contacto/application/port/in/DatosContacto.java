package com.gestioncomercial.gestion_comercial.contacto.application.port.in;

// los datos que llegan para crear o actualizar un contacto (sin id)
public record DatosContacto(String nombre, String apellidos, String cargo, String email, String telefono,
        Long empresaId) {
}
