package com.gestioncomercial.gestion_comercial.oportunidad.application.port.in;

import com.gestioncomercial.gestion_comercial.oportunidad.domain.model.Fase;

// caso de uso: mover una oportunidad por el embudo de ventas
public interface CambiarFaseUseCase {
    OportunidadDetalle cambiarFase(Long id, Fase fase);
}
