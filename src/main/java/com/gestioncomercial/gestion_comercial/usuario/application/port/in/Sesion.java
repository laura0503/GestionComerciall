package com.gestioncomercial.gestion_comercial.usuario.application.port.in;

import com.gestioncomercial.gestion_comercial.usuario.domain.model.Rol;

// lo que devuelve el login: el token y quién eres
public record Sesion(String token, String email, Rol rol) {
}
// Es un record, igual que ContactoDetalle u OportunidadDetalle. Agrupa varios
// datos para devolverlos juntos.