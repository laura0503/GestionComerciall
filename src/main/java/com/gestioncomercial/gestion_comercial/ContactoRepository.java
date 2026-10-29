package com.gestioncomercial.gestion_comercial;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactoRepository extends JpaRepository<Contacto, Long> {

    List<Contacto> findByEmpresaId(Long empresaId);

    // ¿tiene la empresa algún contacto? → true / false
    boolean existsByEmpresaId(Long empresaId);
}
