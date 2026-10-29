package com.gestioncomercial.gestion_comercial;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

// conexión con la tabla OPORTUNIDAD
public interface OportunidadRepository extends JpaRepository<Oportunidad, Long> {

    // SELECT * FROM OPORTUNIDAD WHERE EMPRESA_ID = ?
    List<Oportunidad> findByEmpresaId(Long empresaId);

    // SELECT * FROM OPORTUNIDAD WHERE FASE = ?
    List<Oportunidad> findByFase(Fase fase);

    // ¿tiene la empresa alguna oportunidad? → true / false
    boolean existsByEmpresaId(Long empresaId);

    // ¿aparece el contacto en alguna oportunidad? → true / false
    boolean existsByContactoId(Long contactoId);
}
