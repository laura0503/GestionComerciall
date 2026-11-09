package com.gestioncomercial.gestion_comercial.oportunidad.infrastructure.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gestioncomercial.gestion_comercial.oportunidad.domain.model.Fase;

// Spring Data (antes se llamaba OportunidadRepository)
public interface OportunidadJpaRepository extends JpaRepository<OportunidadEntity, Long> {

    // SELECT * FROM OPORTUNIDAD WHERE EMPRESA_ID = ?
    List<OportunidadEntity> findByEmpresaId(Long empresaId);

    // SELECT * FROM OPORTUNIDAD WHERE FASE = ?
    List<OportunidadEntity> findByFase(Fase fase);

    // ¿tiene la empresa alguna oportunidad? → true / false
    boolean existsByEmpresaId(Long empresaId);

    // ¿aparece el contacto en alguna oportunidad? → true / false
    boolean existsByContactoId(Long contactoId);
}
