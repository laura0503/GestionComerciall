package com.gestioncomercial.gestion_comercial.usuario.infrastructure.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

// Spring genera las consultas por el nombre del método
public interface UsuarioJpaRepository extends JpaRepository<UsuarioEntity, Long> {
    Optional<UsuarioEntity> findByEmail(String email); // WHERE email = ?

    boolean existsByEmail(String email); // ¿existe ya ese email? → true / false
}
