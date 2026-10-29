package com.gestioncomercial.gestion_comercial;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import com.gestioncomercial.gestion_comercial.dto.OportunidadRequest;
import com.gestioncomercial.gestion_comercial.dto.OportunidadResponse;
import com.gestioncomercial.gestion_comercial.exception.ContactoNoEncontradoException;
import com.gestioncomercial.gestion_comercial.exception.EmpresaNoEncontradaException;
import com.gestioncomercial.gestion_comercial.exception.OportunidadNoEncontradaException;

// reglas y comprobaciones de oportunidades de venta
@Service
public class OportunidadService {

    private final OportunidadRepository oportunidadRepository;
    private final EmpresaRepository empresaRepository;
    private final ContactoRepository contactoRepository;

    public OportunidadService(OportunidadRepository oportunidadRepository, EmpresaRepository empresaRepository,
            ContactoRepository contactoRepository) {
        this.oportunidadRepository = oportunidadRepository;
        this.empresaRepository = empresaRepository;
        this.contactoRepository = contactoRepository;
    }

    // sin fase → todas; con fase → solo las de esa fase
    public List<OportunidadResponse> listar(Fase fase) {
        if (fase == null) {
            return aResponse(oportunidadRepository.findAll());
        }
        return aResponse(oportunidadRepository.findByFase(fase));
    }

    public List<OportunidadResponse> listarPorEmpresa(Long empresaId) {
        obtenerEmpresa(empresaId); // si la empresa no existe → 404
        return aResponse(oportunidadRepository.findByEmpresaId(empresaId));
    }

    public OportunidadResponse buscarPorId(Long id) {
        return OportunidadResponse.desde(obtenerOportunidad(id));
    }

    public OportunidadResponse crear(OportunidadRequest request) {
        validarOportunidad(request);
        Empresas empresa = obtenerEmpresa(request.empresaId());
        Contacto contacto = obtenerContactoDeEmpresa(request.contactoId(), empresa);

        Oportunidad oportunidad = request.aEntidad(empresa, contacto);
        if (oportunidad.getFase() == null) {
            oportunidad.setFase(Fase.PROSPECCION); // toda oportunidad nueva empieza por arriba del embudo
        }
        return OportunidadResponse.desde(oportunidadRepository.save(oportunidad));
    }

    public OportunidadResponse actualizar(Long id, OportunidadRequest datos) {
        validarOportunidad(datos);
        Oportunidad existente = obtenerOportunidad(id);
        Empresas empresa = obtenerEmpresa(datos.empresaId());
        Contacto contacto = obtenerContactoDeEmpresa(datos.contactoId(), empresa);

        existente.setTitulo(datos.titulo());
        existente.setImporte(datos.importe());
        if (datos.fase() != null) {
            existente.setFase(datos.fase()); // si no envían fase, se queda la que tenía
        }
        existente.setFechaCierrePrevista(datos.fechaCierrePrevista());
        existente.setEmpresa(empresa);
        existente.setContacto(contacto);

        return OportunidadResponse.desde(oportunidadRepository.save(existente));
    }

    // mueve la oportunidad a otra fase sin tocar el resto de datos
    public OportunidadResponse cambiarFase(Long id, Fase fase) {
        if (fase == null) {
            throw new IllegalArgumentException("La fase es obligatoria");
        }
        Oportunidad existente = obtenerOportunidad(id);
        existente.setFase(fase);
        return OportunidadResponse.desde(oportunidadRepository.save(existente));
    }

    public void borrar(Long id) {
        obtenerOportunidad(id); // si no existe → 404
        oportunidadRepository.deleteById(id);
    }

    // busca la oportunidad en la BD o lanza 404
    private Oportunidad obtenerOportunidad(Long id) {
        return oportunidadRepository.findById(id)
                .orElseThrow(() -> new OportunidadNoEncontradaException(id));
    }

    // busca la empresa en la BD o lanza 404
    private Empresas obtenerEmpresa(Long empresaId) {
        return empresaRepository.findById(empresaId)
                .orElseThrow(() -> new EmpresaNoEncontradaException(empresaId));
    }

    // el contacto es opcional; si viene, tiene que existir y ser de la misma empresa
    private Contacto obtenerContactoDeEmpresa(Long contactoId, Empresas empresa) {
        if (contactoId == null) {
            return null;
        }
        Contacto contacto = contactoRepository.findById(contactoId)
                .orElseThrow(() -> new ContactoNoEncontradoException(contactoId));
        if (!contacto.getEmpresa().getId().equals(empresa.getId())) {
            throw new IllegalArgumentException("El contacto " + contactoId + " no pertenece a la empresa "
                    + empresa.getId());
        }
        return contacto;
    }

    // convierte una lista de entidades en una lista de DTOs
    private List<OportunidadResponse> aResponse(List<Oportunidad> oportunidades) {
        return oportunidades.stream()
                .map(OportunidadResponse::desde)
                .toList();
    }

    private void validarOportunidad(OportunidadRequest oportunidad) {
        if (oportunidad.titulo() == null || oportunidad.titulo().isBlank()) {
            throw new IllegalArgumentException("El título de la oportunidad es obligatorio");
        }
        if (oportunidad.importe() == null) {
            throw new IllegalArgumentException("El importe de la oportunidad es obligatorio");
        }
        if (oportunidad.importe().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El importe no puede ser negativo");
        }
        if (oportunidad.empresaId() == null) {
            throw new IllegalArgumentException("La empresa de la oportunidad es obligatoria");
        }
    }
}
