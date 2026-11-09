package com.gestioncomercial.gestion_comercial.empresa.infrastructure.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

// Spring Data: genera las consultas (antes se llamaba EmpresaRepository)
public interface EmpresaJpaRepository
        extends JpaRepository<EmpresaEntity, Long>, JpaSpecificationExecutor<EmpresaEntity> {
    List<EmpresaEntity> findByProvincia(String provincia);

    List<EmpresaEntity> findBySector(String sector);
}
