package com.gestioncomercial.gestion_comercial;

import com.gestioncomercial.gestion_comercial.dto.EmpresaRequest;
import com.gestioncomercial.gestion_comercial.dto.EmpresaResponse;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import com.gestioncomercial.gestion_comercial.exception.EmpresaNoEncontradaException;

//reglas y comporbaciones
@Service
public class EmpresaService {

    private final EmpresaRepository empresaRepository;

    public EmpresaService(EmpresaRepository empresaRepository) {
        this.empresaRepository = empresaRepository;
    }

    public List<EmpresaResponse> listar() {
        return aResponse(empresaRepository.findAll());
    }

    public EmpresaResponse buscarPorId(Long id) {
        return EmpresaResponse.desde(obtenerEntidad(id));
    }

    public List<EmpresaResponse> buscarPorProvincia(String provincia) {
        return aResponse(empresaRepository.findByProvincia(provincia));
    }

    public List<EmpresaResponse> buscarPorSector(String sector) {
        return aResponse(empresaRepository.findBySector(sector));
    }

    public EmpresaResponse crear(EmpresaRequest request) {
        validarEmpresas(request);
        Empresas guardada = empresaRepository.save(request.aEntidad());
        return EmpresaResponse.desde(guardada);
    }

    // crea varias empresas de golpe: o se guardan todas o ninguna
    @Transactional
    public List<EmpresaResponse> crearLote(List<EmpresaRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            throw new IllegalArgumentException("La lista de empresas no puede estar vacía");
        }
        // primero se validan todas; si una falla, no se guarda ninguna
        for (EmpresaRequest request : requests) {
            validarEmpresas(request);
        }
        List<Empresas> entidades = requests.stream()
                .map(EmpresaRequest::aEntidad)
                .toList();
        return aResponse(empresaRepository.saveAll(entidades));
    }

    public EmpresaResponse actualizar(Long id, EmpresaRequest datos) {
        validarEmpresas(datos);
        Empresas existente = obtenerEntidad(id);

        existente.setNombre(datos.nombre());
        existente.setSector(datos.sector());
        existente.setProvincia(datos.provincia());
        existente.setEstado(datos.estado());

        return EmpresaResponse.desde(empresaRepository.save(existente));
    }

    public void borrar(Long id) {
        obtenerEntidad(id); // si no existe, lanza "Empresa no encontrada"
        empresaRepository.deleteById(id);
    }

    public List<EmpresaResponse> buscar(String nombre, String sector, String provincia, String estado) {
        Specification<Empresas> spec = EmpresaSpecifications.nombreContiene(nombre)
                .and(EmpresaSpecifications.conSector(sector))
                .and(EmpresaSpecifications.conProvincia(provincia))
                .and(EmpresaSpecifications.conEstado(estado));
        return aResponse(empresaRepository.findAll(spec));
    }

    // busca la entidad en la BD; la usan buscarPorId, actualizar y borrar
    private Empresas obtenerEntidad(Long id) {
        return empresaRepository.findById(id)
                .orElseThrow(() -> new EmpresaNoEncontradaException(id));
        // orElseThrow: "dame lo que hay en la caja o, si está vacía, lanza este error".
        // () -> new EmpresaNoEncontradaException(id): es una lambda, una función corta
        // sin nombre que crea el error solo si hace falta.
    }

    // convierte una lista de entidades en una lista de DTOs
    private List<EmpresaResponse> aResponse(List<Empresas> empresas) {
        return empresas.stream() // recorre la lista
                .map(EmpresaResponse::desde)
                .toList();
    }
    /*
     * Método Por qué hace falta
     * obtenerEntidad actualizar y borrar necesitan la entidad (para modificarla o
     * borrarla), pero buscarPorId va a devolver un DTO. Por eso la búsqueda en la
     * BD pasa a un método propio
     * aResponse Pasar de lista de entidades a lista de DTOs se repite 5 veces. Así
     * se escribe una vez
     */

    private void validarEmpresas(EmpresaRequest empresa) {
        if (empresa.nombre() == null || empresa.nombre().isBlank()) {
            throw new IllegalArgumentException("El nombre de la empresa es obligatorio");
        }
        if (empresa.sector() == null || empresa.sector().isBlank()) {
            throw new IllegalArgumentException("El sector de la empresa es obligatorio");
        }
        if (empresa.provincia() == null || empresa.provincia().isBlank()) {
            throw new IllegalArgumentException("La provincia de la empresa es obligatoria");
        }
        if (empresa.estado() == null || empresa.estado().isBlank()) {
            throw new IllegalArgumentException("El estado de la empresa es obligatorio");
        }
    }

}
