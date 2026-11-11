package com.gestioncomercial.gestion_comercial.usuario.infrastructure.persistence;

import com.gestioncomercial.gestion_comercial.usuario.domain.model.Usuario;

// traductor entre el dominio y la tabla
public class UsuarioMapper {

    private UsuarioMapper() {
        // constructor privado: solo tiene métodos static
    }

    // dominio → tabla (para guardar)
    public static UsuarioEntity aEntity(Usuario u) {
        return new UsuarioEntity(u.getId(), u.getEmail(), u.getPasswordCifrada(), u.getRol());
    }

    // tabla → dominio (al leer)
    public static Usuario aDominio(UsuarioEntity e) {
        return new Usuario(e.getId(), e.getEmail(), e.getPasswordCifrada(), e.getRol());
    }
}
