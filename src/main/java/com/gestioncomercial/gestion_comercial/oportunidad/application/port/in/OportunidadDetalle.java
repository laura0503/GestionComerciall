package com.gestioncomercial.gestion_comercial.oportunidad.application.port.in;

import com.gestioncomercial.gestion_comercial.oportunidad.domain.model.Oportunidad;

// lo que devuelven los casos de uso: la oportunidad + los nombres que enseña la API
public record OportunidadDetalle(Oportunidad oportunidad, String empresaNombre, String contactoNombre) {
}
