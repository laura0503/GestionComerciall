package com.gestioncomercial.gestion_comercial.usuario.application.port.out;

import java.util.Optional;

import com.gestioncomercial.gestion_comercial.usuario.domain.model.Usuario;

public interface UsuarioRepositoryPort {
    Usuario guardar(Usuario usuario);

    Optional<Usuario> buscarPorEmail(String email);

    boolean existePorEmail(String email);
}
