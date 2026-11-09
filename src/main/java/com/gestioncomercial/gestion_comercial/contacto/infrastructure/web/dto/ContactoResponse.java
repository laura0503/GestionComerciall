package com.gestioncomercial.gestion_comercial.contacto.infrastructure.web.dto;

import com.gestioncomercial.gestion_comercial.contacto.application.port.in.ContactoDetalle;
import com.gestioncomercial.gestion_comercial.contacto.domain.model.Contacto;

// datos que devuelve la API al cliente (el mismo JSON de siempre)
public record ContactoResponse(Long id, String nombre, String apellidos, String cargo, String email,
        String telefono, Long empresaId, String empresaNombre) {

    // resultado del caso de uso → DTO
    public static ContactoResponse desde(ContactoDetalle detalle) {
        Contacto contacto = detalle.contacto();
        return new ContactoResponse(
                contacto.getId(),
                contacto.getNombre(),
                contacto.getApellidos(),
                contacto.getCargo(),
                contacto.getEmail(),
                contacto.getTelefono(),
                contacto.getEmpresaId(),
                detalle.empresaNombre());
    }
}
