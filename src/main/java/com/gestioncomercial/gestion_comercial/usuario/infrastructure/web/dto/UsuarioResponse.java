package com.gestioncomercial.gestion_comercial.usuario.infrastructure.web.dto;

import com.gestioncomercial.gestion_comercial.usuario.domain.model.Rol;
import com.gestioncomercial.gestion_comercial.usuario.domain.model.Usuario;

// lo que devolvemos al registrar: SIN la contraseña (ni siquiera la cifrada)
public record UsuarioResponse(Long id, String email, Rol rol) {

    public static UsuarioResponse desde(Usuario usuario) {
        return new UsuarioResponse(usuario.getId(), usuario.getEmail(), usuario.getRol());
    }
}
