package com.gestioncomercial.gestion_comercial.oportunidad.application.port.in;

import java.util.List;

import com.gestioncomercial.gestion_comercial.oportunidad.domain.model.Fase;

// caso de uso: leer oportunidades (no modifica nada)
public interface ConsultarOportunidadesUseCase {
    List<OportunidadDetalle> listar(Fase fase); // fase null → todas

    OportunidadDetalle buscarPorId(Long id);

    List<OportunidadDetalle> listarPorEmpresa(Long empresaId);
}
