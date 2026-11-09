package com.gestioncomercial.gestion_comercial.oportunidad.infrastructure.persistence;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.gestioncomercial.gestion_comercial.contacto.infrastructure.persistence.ContactoJpaRepository;
import com.gestioncomercial.gestion_comercial.oportunidad.application.port.out.ContactoDeOportunidadPort;
import com.gestioncomercial.gestion_comercial.oportunidad.application.port.out.ContactoResumen;

// responde "¿quién es el contacto X y de qué empresa es?" (vacío si no existe)
@Component
public class ContactoDeOportunidadAdapter implements ContactoDeOportunidadPort {

    private final ContactoJpaRepository contactoJpaRepository;

    public ContactoDeOportunidadAdapter(ContactoJpaRepository contactoJpaRepository) {
        this.contactoJpaRepository = contactoJpaRepository;
    }

    @Override
    public Optional<ContactoResumen> buscar(Long contactoId) {
        return contactoJpaRepository.findById(contactoId)
                .map(c -> new ContactoResumen(c.getId(), c.getNombre(), c.getEmpresa().getId()));
    }
}
