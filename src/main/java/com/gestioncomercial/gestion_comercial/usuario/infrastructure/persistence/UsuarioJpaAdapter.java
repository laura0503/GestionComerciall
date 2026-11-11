package com.gestioncomercial.gestion_comercial.usuario.infrastructure.persistence;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.gestioncomercial.gestion_comercial.usuario.application.port.out.UsuarioRepositoryPort;
import com.gestioncomercial.gestion_comercial.usuario.domain.model.Usuario;

// quien RESPONDE al puerto OUT de usuarios
@Component
public class UsuarioJpaAdapter implements UsuarioRepositoryPort {

    private final UsuarioJpaRepository jpaRepository;

    public UsuarioJpaAdapter(UsuarioJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Usuario guardar(Usuario usuario) {
        // dominio → entity → BD → entity (ya con id) → dominio
        UsuarioEntity guardado = jpaRepository.save(UsuarioMapper.aEntity(usuario));
        return UsuarioMapper.aDominio(guardado);
    }

    @Override
    public Optional<Usuario> buscarPorEmail(String email) {
        return jpaRepository.findByEmail(email).map(UsuarioMapper::aDominio);
    }

    @Override
    public boolean existePorEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }
}
