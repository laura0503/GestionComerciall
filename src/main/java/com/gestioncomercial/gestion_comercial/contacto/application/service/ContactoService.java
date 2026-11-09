package com.gestioncomercial.gestion_comercial.contacto.application.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gestioncomercial.gestion_comercial.contacto.application.port.in.ActualizarContactoUseCase;
import com.gestioncomercial.gestion_comercial.contacto.application.port.in.BorrarContactoUseCase;
import com.gestioncomercial.gestion_comercial.contacto.application.port.in.ConsultarContactosUseCase;
import com.gestioncomercial.gestion_comercial.contacto.application.port.in.ContactoDetalle;
import com.gestioncomercial.gestion_comercial.contacto.application.port.in.CrearContactoUseCase;
import com.gestioncomercial.gestion_comercial.contacto.application.port.in.DatosContacto;
import com.gestioncomercial.gestion_comercial.contacto.application.port.out.ContactoRepositoryPort;
import com.gestioncomercial.gestion_comercial.contacto.application.port.out.EmpresaDeContactoPort;
import com.gestioncomercial.gestion_comercial.contacto.application.port.out.OportunidadesDeContactoPort;
import com.gestioncomercial.gestion_comercial.contacto.domain.exception.ContactoNoEncontradoException;
import com.gestioncomercial.gestion_comercial.contacto.domain.model.Contacto;
import com.gestioncomercial.gestion_comercial.empresa.domain.exception.EmpresaNoEncontradaException;
import com.gestioncomercial.gestion_comercial.exception.ConflictoException;

// implementa los 4 casos de uso. SIN @Service: el núcleo no conoce Spring
public class ContactoService implements CrearContactoUseCase, ConsultarContactosUseCase, ActualizarContactoUseCase,
        BorrarContactoUseCase {

    private final ContactoRepositoryPort contactoRepository;
    private final EmpresaDeContactoPort empresas;
    private final OportunidadesDeContactoPort oportunidades;

    public ContactoService(ContactoRepositoryPort contactoRepository, EmpresaDeContactoPort empresas,
            OportunidadesDeContactoPort oportunidades) {
        this.contactoRepository = contactoRepository;
        this.empresas = empresas;
        this.oportunidades = oportunidades;
    }

    @Override
    public ContactoDetalle crear(DatosContacto datos) {
        Contacto contacto = Contacto.nuevo(datos.nombre(), datos.apellidos(), datos.cargo(), datos.email(),
                datos.telefono(), datos.empresaId()); // valida → 400
        String empresaNombre = nombreEmpresa(datos.empresaId()); // 404 si la empresa no existe
        return new ContactoDetalle(contactoRepository.guardar(contacto), empresaNombre);
    }

    @Override
    public List<ContactoDetalle> listar() {
        return conNombres(contactoRepository.buscarTodos());
    }

    @Override
    public ContactoDetalle buscarPorId(Long id) {
        Contacto contacto = obtener(id);
        return new ContactoDetalle(contacto, nombreEmpresa(contacto.getEmpresaId()));
    }

    @Override
    public List<ContactoDetalle> listarPorEmpresa(Long empresaId) {
        String empresaNombre = nombreEmpresa(empresaId); // 404 si la empresa no existe
        return contactoRepository.buscarPorEmpresa(empresaId).stream()
                .map(contacto -> new ContactoDetalle(contacto, empresaNombre))
                .toList();
    }

    @Override
    public ContactoDetalle actualizar(Long id, DatosContacto datos) {
        Contacto contacto = obtener(id); // 404 si no existe
        contacto.actualizar(datos.nombre(), datos.apellidos(), datos.cargo(), datos.email(), datos.telefono(),
                datos.empresaId()); // valida → 400
        String empresaNombre = nombreEmpresa(datos.empresaId()); // 404 si la nueva empresa no existe
        return new ContactoDetalle(contactoRepository.guardar(contacto), empresaNombre);
    }

    @Override
    public void borrar(Long id) {
        obtener(id); // 404 si no existe
        if (oportunidades.tieneOportunidades(id)) {
            throw new ConflictoException("No se puede borrar el contacto " + id + " porque tiene oportunidades");
        }
        contactoRepository.borrar(id);
    }

    private Contacto obtener(Long id) {
        return contactoRepository.buscarPorId(id)
                .orElseThrow(() -> new ContactoNoEncontradoException(id));
    }

    // nombre de la empresa o 404 si no existe
    private String nombreEmpresa(Long empresaId) {
        return empresas.buscarNombre(empresaId)
                .orElseThrow(() -> new EmpresaNoEncontradaException(empresaId));
    }

    // añade el nombre de la empresa a cada contacto, preguntando UNA vez por empresa
    private List<ContactoDetalle> conNombres(List<Contacto> contactos) {
        Map<Long, String> nombres = new HashMap<>();
        return contactos.stream()
                .map(c -> new ContactoDetalle(c, nombres.computeIfAbsent(c.getEmpresaId(), this::nombreEmpresa)))
                .toList();
    }
}
