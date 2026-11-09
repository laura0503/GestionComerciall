package com.gestioncomercial.gestion_comercial.contacto.application.port.in;

import java.util.List;

// caso de uso: leer contactos (no modifica nada)
public interface ConsultarContactosUseCase {
    List<ContactoDetalle> listar();

    ContactoDetalle buscarPorId(Long id);

    List<ContactoDetalle> listarPorEmpresa(Long empresaId);
}
