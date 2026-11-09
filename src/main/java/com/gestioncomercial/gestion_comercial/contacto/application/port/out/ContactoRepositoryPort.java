package com.gestioncomercial.gestion_comercial.contacto.application.port.out;

import java.util.List;
import java.util.Optional;

import com.gestioncomercial.gestion_comercial.contacto.domain.model.Contacto;

// lo que el núcleo necesita para guardar/leer contactos
public interface ContactoRepositoryPort {
    Contacto guardar(Contacto contacto);

    Optional<Contacto> buscarPorId(Long id);

    List<Contacto> buscarTodos();

    List<Contacto> buscarPorEmpresa(Long empresaId);

    void borrar(Long id);
}
