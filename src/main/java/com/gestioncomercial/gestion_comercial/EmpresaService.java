package com.gestioncomercial.gestion_comercial;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

//reglas y comporbaciones
@Service
public class EmpresaService {

    private final EmpresaRepository empresaRepository;

    public EmpresaService(EmpresaRepository empresaRepository) {
        this.empresaRepository = empresaRepository;
    }

    public List<Empresas> listar() {
        return empresaRepository.findAll();
    }

    public Empresas buscarPorId(Long id) {
        return empresaRepository.findById(id).orElseThrow(() -> new RuntimeException("Empresa no encontrada con id " + id));
        // orElseThrow: "dame lo que hay en la caja o, si está vacía, lanza este error".
        // () -> new RuntimeException(...): es una lambda, una función corta sin nombre
        // que crea el error solo si hace falta.
    }

    public List<Empresas> buscarPorProvincia(String provincia) {
        return empresaRepository.findByProvincia(provincia);
    }

    public List<Empresas> buscarPorSector(String sector) {
        return empresaRepository.findBySector(sector);
    }

    public Empresas crear(Empresas empresa) {
        validarEmpresas(empresa);
        return empresaRepository.save(empresa);
    }

    // crea varias empresas de golpe: o se guardan todas o ninguna
    @Transactional
    public List<Empresas> crearLote(List<Empresas> empresas) {
        if (empresas == null || empresas.isEmpty()) {
            throw new RuntimeException("La lista de empresas no puede estar vacía");
        }
        // primero se validan todas; si una falla, no se guarda ninguna
        for (Empresas empresa : empresas) {
            validarEmpresas(empresa);
        }
        return empresaRepository.saveAll(empresas);
    }

    public Empresas actualizar(Long id, Empresas datos) {
        validarEmpresas(datos);
        Empresas existente = buscarPorId(id);

        existente.setNombre(datos.getNombre());
        existente.setSector(datos.getSector());
        existente.setProvincia(datos.getProvincia());
        existente.setEstado(datos.getEstado());

        return empresaRepository.save(existente);
    }

    public void borrar(Long id) {
        buscarPorId(id); // si no existe, lanza "Empresa no encontrada"
        empresaRepository.deleteById(id);
    }

    private void validarEmpresas(Empresas empresa) {
        if (empresa.getNombre() == null || empresa.getNombre().isBlank()) {
            throw new RuntimeException("El nombre de la empresa es obligatorio");
        }
        if (empresa.getSector() == null || empresa.getSector().isBlank()) {
            throw new RuntimeException("El sector de la empresa es obligatorio");
        }
        if (empresa.getProvincia() == null || empresa.getProvincia().isBlank()) {
            throw new RuntimeException("La provincia de la empresa es obligatoria");
        }
        if (empresa.getEstado() == null || empresa.getEstado().isBlank()) {
            throw new RuntimeException("El estado de la empresa es obligatorio");
        }
    }

}
