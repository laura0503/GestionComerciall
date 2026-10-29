package com.gestioncomercial.gestion_comercial.dto;

import com.gestioncomercial.gestion_comercial.Fase;

// cuerpo del PATCH para mover una oportunidad de fase: { "fase": "NEGOCIACION" }
public record CambioFaseRequest(Fase fase) {
}
