package com.gestioncomercial.gestion_comercial.exception;

// error propio para cuando un contacto no existe → 404
public class ContactoNoEncontradoException extends RuntimeException {

    public ContactoNoEncontradoException(Long id) {
        super("Contacto no encontrado con id " + id);
    }
}
