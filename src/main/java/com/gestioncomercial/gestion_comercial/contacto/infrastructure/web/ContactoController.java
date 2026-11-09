package com.gestioncomercial.gestion_comercial.contacto.infrastructure.web;

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

import com.gestioncomercial.gestion_comercial.contacto.application.port.in.ActualizarContactoUseCase;
import com.gestioncomercial.gestion_comercial.contacto.application.port.in.BorrarContactoUseCase;
import com.gestioncomercial.gestion_comercial.contacto.application.port.in.ConsultarContactosUseCase;
import com.gestioncomercial.gestion_comercial.contacto.application.port.in.ContactoDetalle;
import com.gestioncomercial.gestion_comercial.contacto.application.port.in.CrearContactoUseCase;
import com.gestioncomercial.gestion_comercial.contacto.infrastructure.web.dto.ContactoRequest;
import com.gestioncomercial.gestion_comercial.contacto.infrastructure.web.dto.ContactoResponse;

// adaptador de ENTRADA: traduce HTTP ⇄ casos de uso. Las URLs son las mismas de siempre
@RestController
@RequestMapping("/api")
public class ContactoController {

    private final CrearContactoUseCase crearUseCase;
    private final ConsultarContactosUseCase consultarUseCase;
    private final ActualizarContactoUseCase actualizarUseCase;
    private final BorrarContactoUseCase borrarUseCase;

    public ContactoController(CrearContactoUseCase crearUseCase, ConsultarContactosUseCase consultarUseCase,
            ActualizarContactoUseCase actualizarUseCase, BorrarContactoUseCase borrarUseCase) {
        this.crearUseCase = crearUseCase;
        this.consultarUseCase = consultarUseCase;
        this.actualizarUseCase = actualizarUseCase;
        this.borrarUseCase = borrarUseCase;
    }

    @GetMapping("/contactos")
    public List<ContactoResponse> listar() {
        return aResponse(consultarUseCase.listar());
    }

    @GetMapping("/contactos/{id}")
    public ContactoResponse buscarPorId(@PathVariable Long id) {
        return ContactoResponse.desde(consultarUseCase.buscarPorId(id));
    }

    // contactos de una empresa: /api/empresas/1/contactos
    @GetMapping("/empresas/{empresaId}/contactos")
    public List<ContactoResponse> listarPorEmpresa(@PathVariable Long empresaId) {
        return aResponse(consultarUseCase.listarPorEmpresa(empresaId));
    }

    @PostMapping("/contactos")
    @ResponseStatus(HttpStatus.CREATED)
    public ContactoResponse crear(@RequestBody ContactoRequest contacto) {
        return ContactoResponse.desde(crearUseCase.crear(contacto.aDatos()));
    }

    @PutMapping("/contactos/{id}")
    public ContactoResponse actualizar(@PathVariable Long id, @RequestBody ContactoRequest contacto) {
        return ContactoResponse.desde(actualizarUseCase.actualizar(id, contacto.aDatos()));
    }

    @DeleteMapping("/contactos/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void borrar(@PathVariable Long id) {
        borrarUseCase.borrar(id);
    }

    private List<ContactoResponse> aResponse(List<ContactoDetalle> detalles) {
        return detalles.stream().map(ContactoResponse::desde).toList();
    }
}
