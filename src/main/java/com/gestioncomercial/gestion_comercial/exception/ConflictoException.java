package com.gestioncomercial.gestion_comercial.exception;

// error para operaciones que chocan con datos relacionados → 409
public class ConflictoException extends RuntimeException {

    public ConflictoException(String mensaje) {
        super(mensaje);
    }
}
