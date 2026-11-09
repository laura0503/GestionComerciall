package com.gestioncomercial.gestion_comercial.oportunidad.application.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gestioncomercial.gestion_comercial.contacto.domain.exception.ContactoNoEncontradoException;
import com.gestioncomercial.gestion_comercial.empresa.domain.exception.EmpresaNoEncontradaException;
import com.gestioncomercial.gestion_comercial.oportunidad.application.port.in.ActualizarOportunidadUseCase;
import com.gestioncomercial.gestion_comercial.oportunidad.application.port.in.BorrarOportunidadUseCase;
import com.gestioncomercial.gestion_comercial.oportunidad.application.port.in.CambiarFaseUseCase;
import com.gestioncomercial.gestion_comercial.oportunidad.application.port.in.ConsultarOportunidadesUseCase;
import com.gestioncomercial.gestion_comercial.oportunidad.application.port.in.CrearOportunidadUseCase;
import com.gestioncomercial.gestion_comercial.oportunidad.application.port.in.DatosOportunidad;
import com.gestioncomercial.gestion_comercial.oportunidad.application.port.in.OportunidadDetalle;
import com.gestioncomercial.gestion_comercial.oportunidad.application.port.out.ContactoDeOportunidadPort;
import com.gestioncomercial.gestion_comercial.oportunidad.application.port.out.ContactoResumen;
import com.gestioncomercial.gestion_comercial.oportunidad.application.port.out.EmpresaDeOportunidadPort;
import com.gestioncomercial.gestion_comercial.oportunidad.application.port.out.OportunidadRepositoryPort;
import com.gestioncomercial.gestion_comercial.oportunidad.domain.exception.OportunidadNoEncontradaException;
import com.gestioncomercial.gestion_comercial.oportunidad.domain.model.Fase;
import com.gestioncomercial.gestion_comercial.oportunidad.domain.model.Oportunidad;

// implementa los 5 casos de uso. SIN @Service: el núcleo no conoce Spring
public class OportunidadService implements CrearOportunidadUseCase, ConsultarOportunidadesUseCase,
        ActualizarOportunidadUseCase, CambiarFaseUseCase, BorrarOportunidadUseCase {

    private final OportunidadRepositoryPort oportunidadRepository;
    private final EmpresaDeOportunidadPort empresas;
    private final ContactoDeOportunidadPort contactos;

    public OportunidadService(OportunidadRepositoryPort oportunidadRepository, EmpresaDeOportunidadPort empresas,
            ContactoDeOportunidadPort contactos) {
        this.oportunidadRepository = oportunidadRepository;
        this.empresas = empresas;
        this.contactos = contactos;
    }

    @Override
    public OportunidadDetalle crear(DatosOportunidad datos) {
        Oportunidad oportunidad = Oportunidad.nueva(datos.titulo(), datos.importe(), datos.fase(),
                datos.fechaCierrePrevista(), datos.empresaId(), datos.contactoId()); // valida → 400
        String empresaNombre = nombreEmpresa(datos.empresaId()); // 404
        String contactoNombre = nombreContactoDeEmpresa(datos.contactoId(), datos.empresaId()); // 404 / 400
        return new OportunidadDetalle(oportunidadRepository.guardar(oportunidad), empresaNombre, contactoNombre);
    }

    @Override
    public List<OportunidadDetalle> listar(Fase fase) {
        List<Oportunidad> oportunidades = fase == null
                ? oportunidadRepository.buscarTodas()
                : oportunidadRepository.buscarPorFase(fase);
        return conNombres(oportunidades);
    }

    @Override
    public OportunidadDetalle buscarPorId(Long id) {
        return conNombres(List.of(obtener(id))).get(0);
    }

    @Override
    public List<OportunidadDetalle> listarPorEmpresa(Long empresaId) {
        nombreEmpresa(empresaId); // 404 si la empresa no existe
        return conNombres(oportunidadRepository.buscarPorEmpresa(empresaId));
    }

    @Override
    public OportunidadDetalle actualizar(Long id, DatosOportunidad datos) {
        Oportunidad oportunidad = obtener(id); // 404
        oportunidad.actualizar(datos.titulo(), datos.importe(), datos.fase(), datos.fechaCierrePrevista(),
                datos.empresaId(), datos.contactoId()); // valida → 400
        String empresaNombre = nombreEmpresa(datos.empresaId());
        String contactoNombre = nombreContactoDeEmpresa(datos.contactoId(), datos.empresaId());
        return new OportunidadDetalle(oportunidadRepository.guardar(oportunidad), empresaNombre, contactoNombre);
    }

    @Override
    public OportunidadDetalle cambiarFase(Long id, Fase fase) {
        Oportunidad oportunidad = obtener(id); // 404
        oportunidad.cambiarFase(fase); // la regla (fase obligatoria) está en el dominio
        return conNombres(List.of(oportunidadRepository.guardar(oportunidad))).get(0);
    }

    @Override
    public void borrar(Long id) {
        obtener(id); // 404
        oportunidadRepository.borrar(id);
    }

    private Oportunidad obtener(Long id) {
        return oportunidadRepository.buscarPorId(id)
                .orElseThrow(() -> new OportunidadNoEncontradaException(id));
    }

    private String nombreEmpresa(Long empresaId) {
        return empresas.buscarNombre(empresaId)
                .orElseThrow(() -> new EmpresaNoEncontradaException(empresaId));
    }

    // el contacto es opcional; si viene, tiene que existir y ser de la misma empresa
    private String nombreContactoDeEmpresa(Long contactoId, Long empresaId) {
        if (contactoId == null) {
            return null;
        }
        ContactoResumen contacto = contactos.buscar(contactoId)
                .orElseThrow(() -> new ContactoNoEncontradoException(contactoId));
        if (!contacto.perteneceA(empresaId)) {
            throw new IllegalArgumentException("El contacto " + contactoId + " no pertenece a la empresa "
                    + empresaId);
        }
        return contacto.nombre();
    }

    // añade los nombres de empresa y contacto, preguntando UNA vez por cada id
    private List<OportunidadDetalle> conNombres(List<Oportunidad> oportunidades) {
        Map<Long, String> nombresEmpresa = new HashMap<>();
        Map<Long, String> nombresContacto = new HashMap<>();
        return oportunidades.stream()
                .map(o -> new OportunidadDetalle(o,
                        nombresEmpresa.computeIfAbsent(o.getEmpresaId(), this::nombreEmpresa),
                        o.getContactoId() == null ? null
                                : nombresContacto.computeIfAbsent(o.getContactoId(),
                                        id -> contactos.buscar(id).map(ContactoResumen::nombre).orElse(null))))
                .toList();
    }
}
