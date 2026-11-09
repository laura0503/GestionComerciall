package com.gestioncomercial.gestion_comercial.contacto.infrastructure.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.gestioncomercial.gestion_comercial.contacto.application.port.out.ContactoRepositoryPort;
import com.gestioncomercial.gestion_comercial.contacto.domain.model.Contacto;
import com.gestioncomercial.gestion_comercial.empresa.infrastructure.persistence.EmpresaEntity;
import com.gestioncomercial.gestion_comercial.empresa.infrastructure.persistence.EmpresaJpaRepository;

// quien RESPONDE al puerto OUT usando JPA
@Component
public class ContactoJpaAdapter implements ContactoRepositoryPort {

    private final ContactoJpaRepository jpaRepository;
    private final EmpresaJpaRepository empresaJpaRepository;

    public ContactoJpaAdapter(ContactoJpaRepository jpaRepository, EmpresaJpaRepository empresaJpaRepository) {
        this.jpaRepository = jpaRepository;
        this.empresaJpaRepository = empresaJpaRepository;
    }

    @Override
    public Contacto guardar(Contacto contacto) {
        // getReferenceById: una "referencia" a la empresa con ese id, sin hacer SELECT.
        // A JPA le basta para rellenar la clave foránea empresas_id
        EmpresaEntity empresa = empresaJpaRepository.getReferenceById(contacto.getEmpresaId());
        return ContactoMapper.aDominio(jpaRepository.save(ContactoMapper.aEntity(contacto, empresa)));
    }

    @Override
    public Optional<Contacto> buscarPorId(Long id) {
        return jpaRepository.findById(id).map(ContactoMapper::aDominio);
    }

    @Override
    public List<Contacto> buscarTodos() {
        return aDominio(jpaRepository.findAll());
    }

    @Override
    public List<Contacto> buscarPorEmpresa(Long empresaId) {
        return aDominio(jpaRepository.findByEmpresaId(empresaId));
    }

    @Override
    public void borrar(Long id) {
        jpaRepository.deleteById(id);
    }

    private List<Contacto> aDominio(List<ContactoEntity> entities) {
        return entities.stream().map(ContactoMapper::aDominio).toList();
    }
}
