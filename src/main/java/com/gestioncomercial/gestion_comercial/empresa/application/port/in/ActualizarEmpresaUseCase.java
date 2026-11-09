package com.gestioncomercial.gestion_comercial.empresa.application.port.in;

import com.gestioncomercial.gestion_comercial.empresa.domain.model.Empresa;

// caso de uso: cambiar los datos de una empresa
public interface ActualizarEmpresaUseCase {
    Empresa actualizar(Long id, DatosEmpresa datos);
}
