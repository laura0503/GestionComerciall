package com.gestioncomercial.gestion_comercial.contacto.application.port.in;

import com.gestioncomercial.gestion_comercial.contacto.domain.model.Contacto;

// lo que devuelven los casos de uso: el contacto + el nombre de su empresa.
// El dominio solo tiene empresaId, pero la API enseña también el nombre
public record ContactoDetalle(Contacto contacto, String empresaNombre) {
}
