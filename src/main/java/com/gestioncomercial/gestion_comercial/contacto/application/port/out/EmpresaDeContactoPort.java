package com.gestioncomercial.gestion_comercial.contacto.application.port.out;

import java.util.Optional;

// lo único que el módulo Contacto necesita de las empresas: su nombre.
// Si la empresa no existe → Optional vacío (así sirve también para comprobar que existe)
public interface EmpresaDeContactoPort {
    Optional<String> buscarNombre(Long empresaId);
}
