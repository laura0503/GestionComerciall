package com.gestioncomercial.gestion_comercial.empresa.application.port.out;

import java.util.List;
import java.util.Optional;

import com.gestioncomercial.gestion_comercial.empresa.domain.model.Empresa;

// lo que el núcleo necesita para guardar/leer empresas (no sabe que existe JPA)
public interface EmpresaRepositoryPort {
    Empresa guardar(Empresa empresa);

    List<Empresa> guardarTodas(List<Empresa> empresas);

    Optional<Empresa> buscarPorId(Long id);

    List<Empresa> buscarTodas();

    List<Empresa> buscarPorProvincia(String provincia);

    List<Empresa> buscarPorSector(String sector);

    // filtros opcionales: si llega null, ese filtro no se aplica
    List<Empresa> buscarConFiltros(String nombre, String sector, String provincia, String estado);

    void borrar(Long id);
}
