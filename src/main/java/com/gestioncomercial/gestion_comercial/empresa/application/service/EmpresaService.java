package com.gestioncomercial.gestion_comercial.empresa.application.service;

import java.util.List;

import com.gestioncomercial.gestion_comercial.exception.ConflictoException;
import com.gestioncomercial.gestion_comercial.empresa.application.port.in.ActualizarEmpresaUseCase;
import com.gestioncomercial.gestion_comercial.empresa.application.port.in.BorrarEmpresaUseCase;
import com.gestioncomercial.gestion_comercial.empresa.application.port.in.ConsultarEmpresasUseCase;
import com.gestioncomercial.gestion_comercial.empresa.application.port.in.CrearEmpresaUseCase;
import com.gestioncomercial.gestion_comercial.empresa.application.port.in.DatosEmpresa;
import com.gestioncomercial.gestion_comercial.empresa.application.port.out.DatosRelacionadosPort;
import com.gestioncomercial.gestion_comercial.empresa.application.port.out.EmpresaRepositoryPort;
import com.gestioncomercial.gestion_comercial.empresa.domain.exception.EmpresaNoEncontradaException;
import com.gestioncomercial.gestion_comercial.empresa.domain.model.Empresa;

// implementa los 4 casos de uso. SIN @Service: el núcleo no conoce Spring
public class EmpresaService implements CrearEmpresaUseCase, ConsultarEmpresasUseCase, ActualizarEmpresaUseCase,
        BorrarEmpresaUseCase {

    private final EmpresaRepositoryPort empresaRepository;
    private final DatosRelacionadosPort datosRelacionados;

    public EmpresaService(EmpresaRepositoryPort empresaRepository, DatosRelacionadosPort datosRelacionados) {
        this.empresaRepository = empresaRepository;
        this.datosRelacionados = datosRelacionados;
    }

    @Override
    public Empresa crear(DatosEmpresa datos) {
        return empresaRepository.guardar(aNueva(datos)); // aNueva valida (400 si falta algo)
    }

    @Override
    public List<Empresa> crearLote(List<DatosEmpresa> lote) {
        if (lote == null || lote.isEmpty()) {
            throw new IllegalArgumentException("La lista de empresas no puede estar vacía");
        }
        // primero se crean (y validan) TODAS; si una falla, salta la excepción y no se guarda ninguna
        List<Empresa> empresas = lote.stream().map(this::aNueva).toList();
        return empresaRepository.guardarTodas(empresas);
    }

    @Override
    public List<Empresa> listar() {
        return empresaRepository.buscarTodas();
    }

    @Override
    public Empresa buscarPorId(Long id) {
        return obtener(id);
    }

    @Override
    public List<Empresa> buscarPorProvincia(String provincia) {
        return empresaRepository.buscarPorProvincia(provincia);
    }

    @Override
    public List<Empresa> buscarPorSector(String sector) {
        return empresaRepository.buscarPorSector(sector);
    }

    @Override
    public List<Empresa> buscar(String nombre, String sector, String provincia, String estado) {
        return empresaRepository.buscarConFiltros(nombre, sector, provincia, estado);
    }

    @Override
    public Empresa actualizar(Long id, DatosEmpresa datos) {
        Empresa empresa = obtener(id); // 404 si no existe
        empresa.actualizar(datos.nombre(), datos.sector(), datos.provincia(), datos.estado()); // valida
        return empresaRepository.guardar(empresa);
    }

    @Override
    public void borrar(Long id) {
        obtener(id); // 404 si no existe
        // no se borra si tiene datos relacionados → 409
        if (datosRelacionados.tieneContactos(id)) {
            throw new ConflictoException("No se puede borrar la empresa " + id + " porque tiene contactos");
        }
        if (datosRelacionados.tieneOportunidades(id)) {
            throw new ConflictoException("No se puede borrar la empresa " + id + " porque tiene oportunidades");
        }
        empresaRepository.borrar(id);
    }

    // busca o lanza 404
    private Empresa obtener(Long id) {
        return empresaRepository.buscarPorId(id)
                .orElseThrow(() -> new EmpresaNoEncontradaException(id));
    }

    // DatosEmpresa → Empresa nueva (validada)
    private Empresa aNueva(DatosEmpresa datos) {
        return Empresa.nueva(datos.nombre(), datos.sector(), datos.provincia(), datos.estado());
    }
}
