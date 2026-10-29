package com.gestioncomercial.gestion_comercial.exception;

// error propio para cuando una oportunidad no existe → 404
public class OportunidadNoEncontradaException extends RuntimeException {

    public OportunidadNoEncontradaException(Long id) {
        super("Oportunidad no encontrada con id " + id);
    }
}
