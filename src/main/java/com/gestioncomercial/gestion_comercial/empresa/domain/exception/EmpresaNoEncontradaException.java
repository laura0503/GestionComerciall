package com.gestioncomercial.gestion_comercial.empresa.domain.exception;

// error de negocio: la empresa no existe → 404
public class EmpresaNoEncontradaException extends RuntimeException {
    public EmpresaNoEncontradaException(Long id) {
        super("Empresa no encontrada con id " + id);
    }
}
