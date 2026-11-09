package com.gestioncomercial.gestion_comercial.empresa.application.port.in;

import java.util.List;

import com.gestioncomercial.gestion_comercial.empresa.domain.model.Empresa;

// caso de uso: dar de alta empresas (una o varias)
public interface CrearEmpresaUseCase {
    Empresa crear(DatosEmpresa datos);

    List<Empresa> crearLote(List<DatosEmpresa> lote);
}
