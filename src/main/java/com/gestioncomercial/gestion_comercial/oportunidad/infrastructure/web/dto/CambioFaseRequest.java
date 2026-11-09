package com.gestioncomercial.gestion_comercial.oportunidad.infrastructure.web.dto;

import com.gestioncomercial.gestion_comercial.oportunidad.domain.model.Fase;

// cuerpo del PATCH para mover una oportunidad de fase: { "fase": "NEGOCIACION" }
public record CambioFaseRequest(Fase fase) {
}
