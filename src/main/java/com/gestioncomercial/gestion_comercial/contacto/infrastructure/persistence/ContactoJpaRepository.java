package com.gestioncomercial.gestion_comercial.contacto.infrastructure.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

// Spring Data (antes se llamaba ContactoRepository)
public interface ContactoJpaRepository extends JpaRepository<ContactoEntity, Long> {

    List<ContactoEntity> findByEmpresaId(Long empresaId);

    // ¿tiene la empresa algún contacto? → true / false
    boolean existsByEmpresaId(Long empresaId);
}
