package com.gestioncomercial.gestion_comercial.actividad.domain.exception;

public class ActividadNoEncontradaException extends RuntimeException {
    public ActividadNoEncontradaException(Long id) {
        super("Actividad no encontrada con id " + id);
    }

}
