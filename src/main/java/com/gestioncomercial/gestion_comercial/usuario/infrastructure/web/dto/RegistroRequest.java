package com.gestioncomercial.gestion_comercial.usuario.infrastructure.web.dto;

import com.gestioncomercial.gestion_comercial.usuario.domain.model.Rol;

// lo que envía el cliente para darse de alta
public record RegistroRequest(String email, String password, Rol rol) {
}
