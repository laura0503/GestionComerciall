package com.gestioncomercial.gestion_comercial.oportunidad.domain.exception;

// error de negocio: la oportunidad no existe → 404
public class OportunidadNoEncontradaException extends RuntimeException {

    public OportunidadNoEncontradaException(Long id) {
        super("Oportunidad no encontrada con id " + id);
    }
}
