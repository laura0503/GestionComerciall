package com.gestioncomercial.gestion_comercial;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.gestioncomercial.gestion_comercial.dto.CambioFaseRequest;
import com.gestioncomercial.gestion_comercial.dto.OportunidadRequest;
import com.gestioncomercial.gestion_comercial.dto.OportunidadResponse;

// recibe peticiones de oportunidades y responde
@RestController
@RequestMapping("/api")
public class OportunidadController {

    private final OportunidadService oportunidadService;

    public OportunidadController(OportunidadService oportunidadService) {
        this.oportunidadService = oportunidadService;
    }

    // /api/oportunidades → todas; /api/oportunidades?fase=PROPUESTA → filtradas
    @GetMapping("/oportunidades")
    public List<OportunidadResponse> listar(@RequestParam(required = false) Fase fase) {
        return oportunidadService.listar(fase);
    }

    @GetMapping("/oportunidades/{id}")
    public OportunidadResponse buscarPorId(@PathVariable Long id) {
        return oportunidadService.buscarPorId(id);
    }

    // oportunidades de una empresa: /api/empresas/1/oportunidades
    @GetMapping("/empresas/{empresaId}/oportunidades")
    public List<OportunidadResponse> listarPorEmpresa(@PathVariable Long empresaId) {
        return oportunidadService.listarPorEmpresa(empresaId);
    }

    @PostMapping("/oportunidades")
    @ResponseStatus(HttpStatus.CREATED)
    public OportunidadResponse crear(@RequestBody OportunidadRequest oportunidad) {
        return oportunidadService.crear(oportunidad);
    }

    @PutMapping("/oportunidades/{id}")
    public OportunidadResponse actualizar(@PathVariable Long id, @RequestBody OportunidadRequest oportunidad) {
        return oportunidadService.actualizar(id, oportunidad);
    }

    // cambia solo la fase: PATCH /api/oportunidades/1/fase con { "fase": "NEGOCIACION" }
    @PatchMapping("/oportunidades/{id}/fase")
    public OportunidadResponse cambiarFase(@PathVariable Long id, @RequestBody CambioFaseRequest cambio) {
        return oportunidadService.cambiarFase(id, cambio.fase());
    }

    @DeleteMapping("/oportunidades/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void borrar(@PathVariable Long id) {
        oportunidadService.borrar(id);
    }
}
