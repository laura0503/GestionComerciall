package com.gestioncomercial.gestion_comercial.actividad.infrastructure.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.gestioncomercial.gestion_comercial.actividad.application.port.out.ActividadRepositoryPort;
import com.gestioncomercial.gestion_comercial.actividad.domain.model.Actividad;

// quien RESPONDE al puerto OUT. @Component = Spring lo crea y lo puede inyectar
@Component
public class ActividadJpaAdapter implements ActividadRepositoryPort {

    private final ActividadJpaRepository jpaRepository;

    public ActividadJpaAdapter(ActividadJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Actividad guardar(Actividad actividad) {
        // dominio → entity → BD → entity (ya con id) → dominio
        ActividadEntity guardada = jpaRepository.save(ActividadMapper.aEntity(actividad));
        return ActividadMapper.aDominio(guardada);
    }

    @Override
    public Optional<Actividad> buscarPorId(Long id) {
        // si hay algo dentro del Optional, lo convierte a dominio
        return jpaRepository.findById(id).map(ActividadMapper::aDominio);
    }

    @Override
    public List<Actividad> buscarPorEmpresa(Long empresaId) {
        return jpaRepository.findByEmpresaId(empresaId).stream()
                .map(ActividadMapper::aDominio)
                .toList();
    }

    @Override
    public List<Actividad> buscarPendientes() {
        return jpaRepository.findByCompletadaFalse().stream()
                .map(ActividadMapper::aDominio)
                .toList();
    }

    @Override
    public void borrar(Long id) {
        jpaRepository.deleteById(id);
    }
}
