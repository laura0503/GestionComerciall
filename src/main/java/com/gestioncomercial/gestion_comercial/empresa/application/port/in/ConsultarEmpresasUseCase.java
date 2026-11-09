package com.gestioncomercial.gestion_comercial.empresa.application.port.in;

import java.util.List;

import com.gestioncomercial.gestion_comercial.empresa.domain.model.Empresa;

// caso de uso: leer empresas (no modifica nada)
public interface ConsultarEmpresasUseCase {
    List<Empresa> listar();

    Empresa buscarPorId(Long id);

    List<Empresa> buscarPorProvincia(String provincia);

    List<Empresa> buscarPorSector(String sector);

    List<Empresa> buscar(String nombre, String sector, String provincia, String estado);
}
