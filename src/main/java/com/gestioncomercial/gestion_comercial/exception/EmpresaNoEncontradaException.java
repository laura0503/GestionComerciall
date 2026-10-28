package com.gestioncomercial.gestion_comercial.exception;

// error propio para cuando una empresa no existe → 404
public class EmpresaNoEncontradaException extends RuntimeException {

    public EmpresaNoEncontradaException(Long id) {
        super("Empresa no encontrada con id " + id);
    }
}
