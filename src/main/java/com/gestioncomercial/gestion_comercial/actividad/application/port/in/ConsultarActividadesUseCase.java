package com.gestioncomercial.gestion_comercial.actividad.application.port.in;

import java.util.List;

import com.gestioncomercial.gestion_comercial.actividad.domain.model.Actividad;

// caso de uso: leer actividades (no modifica nada)
public interface ConsultarActividadesUseCase {
    Actividad buscarPorId(Long id);

    List<Actividad> listarPorEmpresa(Long empresaId);

    List<Actividad> listarVencidas(); // pendientes con fecha ya pasada
}
