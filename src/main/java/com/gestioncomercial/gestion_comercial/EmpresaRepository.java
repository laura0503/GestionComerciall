package com.gestioncomercial.gestion_comercial;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

//interface de acceso a datos
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmpresaRepository extends JpaRepository<Empresas, Long>, JpaSpecificationExecutor<Empresas> {
    List<Empresas> findByProvincia(String provincia);

    List<Empresas> findBySector(String sector);
}
