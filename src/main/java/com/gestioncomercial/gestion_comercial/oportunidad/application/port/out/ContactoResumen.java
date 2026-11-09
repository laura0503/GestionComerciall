package com.gestioncomercial.gestion_comercial.oportunidad.application.port.out;

// lo poco que el módulo Oportunidad necesita saber de un contacto
public record ContactoResumen(Long id, String nombre, Long empresaId) {

    // ¿es de esta empresa?
    public boolean perteneceA(Long otraEmpresaId) {
        return empresaId.equals(otraEmpresaId);
    }
}
