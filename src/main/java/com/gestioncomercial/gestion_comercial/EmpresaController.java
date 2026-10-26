package com.gestioncomercial.gestion_comercial;

import java.util.List;

import org.aspectj.weaver.ast.Not;
import org.hibernate.Internal;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;

// recibe peticiones y responde
@RestController
@RequestMapping("/api/empresas")
public class EmpresaController {

    private final EmpresaService empresaService;

    public EmpresaController(EmpresaService empresaService) {
        this.empresaService = empresaService;
    }

    @GetMapping("/{id}")
    public Empresas buscarPorId(@PathVariable Long id) {
        return empresaService.buscarPorId(id);
    }

    @GetMapping
    public List<Empresas> listar() {
        return empresaService.listar();
    }

    @GetMapping("/provincia/{provincia}")
    public List<Empresas> buscarPorProvincia(@PathVariable String provincia) {
        return empresaService.buscarPorProvincia(provincia);
    }

    @GetMapping("/sector/{sector}")
    public List<Empresas> buscarPorSector(@PathVariable String sector) {
        return empresaService.buscarPorSector(sector);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Empresas crearEmpresas(@RequestBody Empresas empresa) {
        return empresaService.crear(empresa);
    }

    @PutMapping("/{id}")
    public Empresas actualizarEmpresas(@PathVariable Long id, @RequestBody Empresas empresa) {
        return empresaService.actualizar(id, empresa);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarEmpresa(@PathVariable Long id) {
        empresaService.borrar(id);
    }

    /*
     * 200 OK Todo bien, aquí tienes los datos
     * 201 Created Todo bien, he creado algo nuevo
     * 204 No Content Todo bien, pero no te devuelvo nada
     * 400 Bad Request Me has enviado datos incorrectos
     * 404 Not Found Eso no existe
     * 500 Internal Server Error Ha fallado algo en el servidor
     */
}
