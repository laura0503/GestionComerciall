package com.gestioncomercial.gestion_comercial.contacto.domain.exception;

// error de negocio: el contacto no existe → 404
public class ContactoNoEncontradoException extends RuntimeException {

    public ContactoNoEncontradoException(Long id) {
        super("Contacto no encontrado con id " + id);
    }
}
