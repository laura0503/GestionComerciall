package com.gestioncomercial.gestion_comercial;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.gestioncomercial.gestion_comercial.dto.ContactoRequest;
import com.gestioncomercial.gestion_comercial.dto.ContactoResponse;

// recibe peticiones de contactos y responde
@RestController
@RequestMapping("/api")
public class ContactoController {

    private final ContactoService contactoService;

    public ContactoController(ContactoService contactoService) {
        this.contactoService = contactoService;
    }

    @GetMapping("/contactos")
    public List<ContactoResponse> listar() {
        return contactoService.listar();
    }

    @GetMapping("/contactos/{id}")
    public ContactoResponse buscarPorId(@PathVariable Long id) {
        return contactoService.buscarPorId(id);
    }

    // contactos de una empresa: /api/empresas/1/contactos
    @GetMapping("/empresas/{empresaId}/contactos")
    public List<ContactoResponse> listarPorEmpresa(@PathVariable Long empresaId) {
        return contactoService.listarPorEmpresa(empresaId);
    }

    @PostMapping("/contactos")
    @ResponseStatus(HttpStatus.CREATED)
    public ContactoResponse crear(@RequestBody ContactoRequest contacto) {
        return contactoService.crear(contacto);
    }

    @PutMapping("/contactos/{id}")
    public ContactoResponse actualizar(@PathVariable Long id, @RequestBody ContactoRequest contacto) {
        return contactoService.actualizar(id, contacto);
    }

    @DeleteMapping("/contactos/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void borrar(@PathVariable Long id) {
        contactoService.borrar(id);
    }
}
