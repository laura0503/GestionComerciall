package com.gestioncomercial.gestion_comercial.usuario.infrastructure.web.dto;

import com.gestioncomercial.gestion_comercial.usuario.application.port.in.Sesion;
import com.gestioncomercial.gestion_comercial.usuario.domain.model.Rol;

// lo que devolvemos al hacer login: el token y quién eres
public record LoginResponse(String token, String email, Rol rol) {

    public static LoginResponse desde(Sesion sesion) {
        return new LoginResponse(sesion.token(), sesion.email(), sesion.rol());
    }
}
