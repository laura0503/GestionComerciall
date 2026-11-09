package com.gestioncomercial.gestion_comercial.actividad.domain.exception;

// error de negocio: no se puede completar dos veces → 409
public class ActividadYaCompletadaException extends RuntimeException {
    public ActividadYaCompletadaException(Long id) {
        super("La actividad " + id + " ya está completada");
    }
}
