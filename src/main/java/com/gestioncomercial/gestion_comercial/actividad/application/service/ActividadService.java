package com.gestioncomercial.gestion_comercial.actividad.application.service;

import java.time.LocalDateTime;
import java.util.List;

import com.gestioncomercial.gestion_comercial.actividad.application.port.in.BorrarActividadUseCase;
import com.gestioncomercial.gestion_comercial.actividad.application.port.in.CompletarActividadUseCase;
import com.gestioncomercial.gestion_comercial.actividad.application.port.in.ConsultarActividadesUseCase;
import com.gestioncomercial.gestion_comercial.actividad.application.port.in.RegistrarActividadUseCase;
import com.gestioncomercial.gestion_comercial.actividad.application.port.out.ActividadRepositoryPort;
import com.gestioncomercial.gestion_comercial.actividad.application.port.out.EmpresaConsultaPort;
import com.gestioncomercial.gestion_comercial.actividad.domain.exception.ActividadNoEncontradaException;
import com.gestioncomercial.gestion_comercial.actividad.domain.model.Actividad;
import com.gestioncomercial.gestion_comercial.actividad.domain.model.TipoActividad;

// implementa los 4 casos de uso (puertos IN) y usa los 2 puertos OUT.
// SIN @Service: el núcleo no conoce Spring
public class ActividadService implements RegistrarActividadUseCase, CompletarActividadUseCase,
        ConsultarActividadesUseCase, BorrarActividadUseCase {

    // puertos OUT: interfaces, no sabe quién hay detrás
    private final ActividadRepositoryPort actividadRepository;
    private final EmpresaConsultaPort empresaConsulta;

    public ActividadService(ActividadRepositoryPort actividadRepository, EmpresaConsultaPort empresaConsulta) {
        this.actividadRepository = actividadRepository;
        this.empresaConsulta = empresaConsulta;
    }

    @Override
    public Actividad registrar(TipoActividad tipo, String asunto, String descripcion, LocalDateTime fecha,
            Long empresaId) {
        Actividad actividad = Actividad.nueva(tipo, asunto, descripcion, fecha, empresaId);

        if (!empresaConsulta.existeEmpresa(empresaId)) {
            throw new IllegalArgumentException("La empresa " + empresaId + " no existe");
        }

        return actividadRepository.guardar(actividad);
    }

    @Override
    public Actividad completar(Long id) {
        Actividad actividad = obtener(id);
        actividad.completar();
        return actividadRepository.guardar(actividad);
    }

    @Override
    public Actividad buscarPorId(Long id) {
        return obtener(id);
    }

    @Override
    public List<Actividad> listarPorEmpresa(Long empresaId) {
        return actividadRepository.buscarPorEmpresa(empresaId);
    }

    @Override
    public List<Actividad> listarVencidas() {
        LocalDateTime ahora = LocalDateTime.now();
        return actividadRepository.buscarPendientes().stream().filter(actividad -> actividad.estaVencida(ahora))
                .toList();
    }

    @Override
    public void borrar(Long id) {
        obtener(id);  // si no existe → 404 (no borramos "a ciegas")
        actividadRepository.borrar(id);
    }

    // método de ayuda: busca o lanza 404
    private Actividad obtener(Long id) {
        return actividadRepository.buscarPorId(id)
                .orElseThrow(() -> new ActividadNoEncontradaException(id));
    }

}
