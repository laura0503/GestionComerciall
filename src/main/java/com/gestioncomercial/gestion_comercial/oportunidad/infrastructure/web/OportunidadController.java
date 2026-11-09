package com.gestioncomercial.gestion_comercial.oportunidad.infrastructure.web;

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

import com.gestioncomercial.gestion_comercial.oportunidad.application.port.in.ActualizarOportunidadUseCase;
import com.gestioncomercial.gestion_comercial.oportunidad.application.port.in.BorrarOportunidadUseCase;
import com.gestioncomercial.gestion_comercial.oportunidad.application.port.in.CambiarFaseUseCase;
import com.gestioncomercial.gestion_comercial.oportunidad.application.port.in.ConsultarOportunidadesUseCase;
import com.gestioncomercial.gestion_comercial.oportunidad.application.port.in.CrearOportunidadUseCase;
import com.gestioncomercial.gestion_comercial.oportunidad.application.port.in.OportunidadDetalle;
import com.gestioncomercial.gestion_comercial.oportunidad.domain.model.Fase;
import com.gestioncomercial.gestion_comercial.oportunidad.infrastructure.web.dto.CambioFaseRequest;
import com.gestioncomercial.gestion_comercial.oportunidad.infrastructure.web.dto.OportunidadRequest;
import com.gestioncomercial.gestion_comercial.oportunidad.infrastructure.web.dto.OportunidadResponse;

// adaptador de ENTRADA: traduce HTTP ⇄ casos de uso. Las URLs son las mismas de siempre
@RestController
@RequestMapping("/api")
public class OportunidadController {

    private final CrearOportunidadUseCase crearUseCase;
    private final ConsultarOportunidadesUseCase consultarUseCase;
    private final ActualizarOportunidadUseCase actualizarUseCase;
    private final CambiarFaseUseCase cambiarFaseUseCase;
    private final BorrarOportunidadUseCase borrarUseCase;

    public OportunidadController(CrearOportunidadUseCase crearUseCase,
            ConsultarOportunidadesUseCase consultarUseCase, ActualizarOportunidadUseCase actualizarUseCase,
            CambiarFaseUseCase cambiarFaseUseCase, BorrarOportunidadUseCase borrarUseCase) {
        this.crearUseCase = crearUseCase;
        this.consultarUseCase = consultarUseCase;
        this.actualizarUseCase = actualizarUseCase;
        this.cambiarFaseUseCase = cambiarFaseUseCase;
        this.borrarUseCase = borrarUseCase;
    }

    // /api/oportunidades → todas; /api/oportunidades?fase=PROPUESTA → filtradas
    @GetMapping("/oportunidades")
    public List<OportunidadResponse> listar(@RequestParam(required = false) Fase fase) {
        return aResponse(consultarUseCase.listar(fase));
    }

    @GetMapping("/oportunidades/{id}")
    public OportunidadResponse buscarPorId(@PathVariable Long id) {
        return OportunidadResponse.desde(consultarUseCase.buscarPorId(id));
    }

    // oportunidades de una empresa: /api/empresas/1/oportunidades
    @GetMapping("/empresas/{empresaId}/oportunidades")
    public List<OportunidadResponse> listarPorEmpresa(@PathVariable Long empresaId) {
        return aResponse(consultarUseCase.listarPorEmpresa(empresaId));
    }

    @PostMapping("/oportunidades")
    @ResponseStatus(HttpStatus.CREATED)
    public OportunidadResponse crear(@RequestBody OportunidadRequest oportunidad) {
        return OportunidadResponse.desde(crearUseCase.crear(oportunidad.aDatos()));
    }

    @PutMapping("/oportunidades/{id}")
    public OportunidadResponse actualizar(@PathVariable Long id, @RequestBody OportunidadRequest oportunidad) {
        return OportunidadResponse.desde(actualizarUseCase.actualizar(id, oportunidad.aDatos()));
    }

    // cambia solo la fase: PATCH /api/oportunidades/1/fase con { "fase": "NEGOCIACION" }
    @PatchMapping("/oportunidades/{id}/fase")
    public OportunidadResponse cambiarFase(@PathVariable Long id, @RequestBody CambioFaseRequest cambio) {
        return OportunidadResponse.desde(cambiarFaseUseCase.cambiarFase(id, cambio.fase()));
    }

    @DeleteMapping("/oportunidades/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void borrar(@PathVariable Long id) {
        borrarUseCase.borrar(id);
    }

    private List<OportunidadResponse> aResponse(List<OportunidadDetalle> detalles) {
        return detalles.stream().map(OportunidadResponse::desde).toList();
    }
}
