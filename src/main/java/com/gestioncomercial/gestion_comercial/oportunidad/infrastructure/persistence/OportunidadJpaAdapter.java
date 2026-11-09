package com.gestioncomercial.gestion_comercial.oportunidad.infrastructure.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.gestioncomercial.gestion_comercial.contacto.infrastructure.persistence.ContactoEntity;
import com.gestioncomercial.gestion_comercial.contacto.infrastructure.persistence.ContactoJpaRepository;
import com.gestioncomercial.gestion_comercial.empresa.infrastructure.persistence.EmpresaEntity;
import com.gestioncomercial.gestion_comercial.empresa.infrastructure.persistence.EmpresaJpaRepository;
import com.gestioncomercial.gestion_comercial.oportunidad.application.port.out.OportunidadRepositoryPort;
import com.gestioncomercial.gestion_comercial.oportunidad.domain.model.Fase;
import com.gestioncomercial.gestion_comercial.oportunidad.domain.model.Oportunidad;

// quien RESPONDE al puerto OUT usando JPA
@Component
public class OportunidadJpaAdapter implements OportunidadRepositoryPort {

    private final OportunidadJpaRepository jpaRepository;
    private final EmpresaJpaRepository empresaJpaRepository;
    private final ContactoJpaRepository contactoJpaRepository;

    public OportunidadJpaAdapter(OportunidadJpaRepository jpaRepository, EmpresaJpaRepository empresaJpaRepository,
            ContactoJpaRepository contactoJpaRepository) {
        this.jpaRepository = jpaRepository;
        this.empresaJpaRepository = empresaJpaRepository;
        this.contactoJpaRepository = contactoJpaRepository;
    }

    @Override
    public Oportunidad guardar(Oportunidad oportunidad) {
        // referencias (sin SELECT) para rellenar las claves foráneas
        EmpresaEntity empresa = empresaJpaRepository.getReferenceById(oportunidad.getEmpresaId());
        ContactoEntity contacto = oportunidad.getContactoId() != null
                ? contactoJpaRepository.getReferenceById(oportunidad.getContactoId())
                : null;
        OportunidadEntity guardada = jpaRepository.save(OportunidadMapper.aEntity(oportunidad, empresa, contacto));
        return OportunidadMapper.aDominio(guardada);
    }

    @Override
    public Optional<Oportunidad> buscarPorId(Long id) {
        return jpaRepository.findById(id).map(OportunidadMapper::aDominio);
    }

    @Override
    public List<Oportunidad> buscarTodas() {
        return aDominio(jpaRepository.findAll());
    }

    @Override
    public List<Oportunidad> buscarPorFase(Fase fase) {
        return aDominio(jpaRepository.findByFase(fase));
    }

    @Override
    public List<Oportunidad> buscarPorEmpresa(Long empresaId) {
        return aDominio(jpaRepository.findByEmpresaId(empresaId));
    }

    @Override
    public void borrar(Long id) {
        jpaRepository.deleteById(id);
    }

    private List<Oportunidad> aDominio(List<OportunidadEntity> entities) {
        return entities.stream().map(OportunidadMapper::aDominio).toList();
    }
}
