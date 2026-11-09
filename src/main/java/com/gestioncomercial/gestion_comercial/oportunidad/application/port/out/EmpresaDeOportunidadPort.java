package com.gestioncomercial.gestion_comercial.oportunidad.application.port.out;

import java.util.Optional;

// nombre de la empresa (vacío si no existe)
public interface EmpresaDeOportunidadPort {
    Optional<String> buscarNombre(Long empresaId);
}
