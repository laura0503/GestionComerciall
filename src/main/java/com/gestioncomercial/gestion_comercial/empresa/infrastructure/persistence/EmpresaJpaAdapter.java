package com.gestioncomercial.gestion_comercial.empresa.infrastructure.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import com.gestioncomercial.gestion_comercial.empresa.application.port.out.EmpresaRepositoryPort;
import com.gestioncomercial.gestion_comercial.empresa.domain.model.Empresa;

// quien RESPONDE al puerto OUT usando JPA
@Component
public class EmpresaJpaAdapter implements EmpresaRepositoryPort {

    private final EmpresaJpaRepository jpaRepository;

    public EmpresaJpaAdapter(EmpresaJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Empresa guardar(Empresa empresa) {
        return EmpresaMapper.aDominio(jpaRepository.save(EmpresaMapper.aEntity(empresa)));
    }

    @Override
    public List<Empresa> guardarTodas(List<Empresa> empresas) {
        List<EmpresaEntity> entities = empresas.stream().map(EmpresaMapper::aEntity).toList();
        return aDominio(jpaRepository.saveAll(entities)); // saveAll: todas o ninguna
    }

    @Override
    public Optional<Empresa> buscarPorId(Long id) {
        return jpaRepository.findById(id).map(EmpresaMapper::aDominio);
    }

    @Override
    public List<Empresa> buscarTodas() {
        return aDominio(jpaRepository.findAll());
    }

    @Override
    public List<Empresa> buscarPorProvincia(String provincia) {
        return aDominio(jpaRepository.findByProvincia(provincia));
    }

    @Override
    public List<Empresa> buscarPorSector(String sector) {
        return aDominio(jpaRepository.findBySector(sector));
    }

    @Override
    public List<Empresa> buscarConFiltros(String nombre, String sector, String provincia, String estado) {
        // las Specifications son cosa de JPA: por eso viven aquí y no en el service
        Specification<EmpresaEntity> spec = EmpresaSpecifications.nombreContiene(nombre)
                .and(EmpresaSpecifications.conSector(sector))
                .and(EmpresaSpecifications.conProvincia(provincia))
                .and(EmpresaSpecifications.conEstado(estado));
        return aDominio(jpaRepository.findAll(spec));
    }

    @Override
    public void borrar(Long id) {
        jpaRepository.deleteById(id);
    }

    private List<Empresa> aDominio(List<EmpresaEntity> entities) {
        return entities.stream().map(EmpresaMapper::aDominio).toList();
    }
}
