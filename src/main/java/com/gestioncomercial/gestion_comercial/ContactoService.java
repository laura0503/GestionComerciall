package com.gestioncomercial.gestion_comercial;

import java.util.List;

import org.springframework.stereotype.Service;

import com.gestioncomercial.gestion_comercial.dto.ContactoRequest;
import com.gestioncomercial.gestion_comercial.dto.ContactoResponse;
import com.gestioncomercial.gestion_comercial.exception.ConflictoException;
import com.gestioncomercial.gestion_comercial.exception.ContactoNoEncontradoException;
import com.gestioncomercial.gestion_comercial.exception.EmpresaNoEncontradaException;

// reglas y comprobaciones de contactos
@Service
public class ContactoService {

    private final ContactoRepository contactoRepository;
    private final EmpresaRepository empresaRepository;
    private final OportunidadRepository oportunidadRepository;

    public ContactoService(ContactoRepository contactoRepository, EmpresaRepository empresaRepository,
            OportunidadRepository oportunidadRepository) {
        this.contactoRepository = contactoRepository;
        this.empresaRepository = empresaRepository;
        this.oportunidadRepository = oportunidadRepository;
    }

    public List<ContactoResponse> listar() {
        return aResponse(contactoRepository.findAll());
    }

    public List<ContactoResponse> listarPorEmpresa(Long empresaId) {
        obtenerEmpresa(empresaId); // si la empresa no existe → 404
        return aResponse(contactoRepository.findByEmpresaId(empresaId));
    }

    public ContactoResponse buscarPorId(Long id) {
        return ContactoResponse.desde(obtenerContacto(id));
    }

    public ContactoResponse crear(ContactoRequest request) {
        validarContacto(request);
        Empresas empresa = obtenerEmpresa(request.empresaId());
        Contacto guardado = contactoRepository.save(request.aEntidad(empresa));
        return ContactoResponse.desde(guardado);
    }

    public ContactoResponse actualizar(Long id, ContactoRequest datos) {
        validarContacto(datos);
        Contacto existente = obtenerContacto(id);
        Empresas empresa = obtenerEmpresa(datos.empresaId());

        existente.setNombre(datos.nombre());
        existente.setApellidos(datos.apellidos());
        existente.setCargo(datos.cargo());
        existente.setEmail(datos.email());
        existente.setTelefono(datos.telefono());
        existente.setEmpresa(empresa);

        return ContactoResponse.desde(contactoRepository.save(existente));
    }

    public void borrar(Long id) {
        obtenerContacto(id); // si no existe → 404
        if (oportunidadRepository.existsByContactoId(id)) {
            throw new ConflictoException("No se puede borrar el contacto " + id + " porque tiene oportunidades");
        }
        contactoRepository.deleteById(id);
    }

    // busca el contacto en la BD o lanza 404
    private Contacto obtenerContacto(Long id) {
        return contactoRepository.findById(id)
                .orElseThrow(() -> new ContactoNoEncontradoException(id));
    }

    // busca la empresa en la BD o lanza 404
    private Empresas obtenerEmpresa(Long empresaId) {
        return empresaRepository.findById(empresaId)
                .orElseThrow(() -> new EmpresaNoEncontradaException(empresaId));
    }

    // convierte una lista de entidades en una lista de DTOs
    private List<ContactoResponse> aResponse(List<Contacto> contactos) {
        return contactos.stream()
                .map(ContactoResponse::desde)
                .toList();
    }

    private void validarContacto(ContactoRequest contacto) {
        if (contacto.nombre() == null || contacto.nombre().isBlank()) {
            throw new IllegalArgumentException("El nombre del contacto es obligatorio");
        }
        if (contacto.empresaId() == null) {
            throw new IllegalArgumentException("La empresa del contacto es obligatoria");
        }
        if (contacto.email() != null && !contacto.email().contains("@")) {
            throw new IllegalArgumentException("El email del contacto no es válido");
        }
    }
}
