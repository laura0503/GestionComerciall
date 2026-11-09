package com.gestioncomercial.gestion_comercial.actividad.infrastructure.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

// igual que tus repositorios: Spring genera las consultas por el nombre del método
public interface ActividadJpaRepository extends JpaRepository<ActividadEntity, Long> {
    List<ActividadEntity> findByEmpresaId(Long empresaId); // WHERE empresa_id = ?

    List<ActividadEntity> findByCompletadaFalse(); // WHERE completada = false

    // ¿tiene la empresa alguna actividad? → true / false
    boolean existsByEmpresaId(Long empresaId);
}
