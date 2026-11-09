package com.gestioncomercial.gestion_comercial.actividad.infrastructure.web;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.gestioncomercial.gestion_comercial.actividad.application.port.in.BorrarActividadUseCase;
import com.gestioncomercial.gestion_comercial.actividad.application.port.in.CompletarActividadUseCase;
import com.gestioncomercial.gestion_comercial.actividad.application.port.in.ConsultarActividadesUseCase;
import com.gestioncomercial.gestion_comercial.actividad.application.port.in.RegistrarActividadUseCase;
import com.gestioncomercial.gestion_comercial.actividad.infrastructure.web.dto.ActividadRequest;
import com.gestioncomercial.gestion_comercial.actividad.infrastructure.web.dto.ActividadResponse;

// adaptador de ENTRADA: traduce HTTP ⇄ casos de uso
@RestController
@RequestMapping("/api")
public class ActividadController {

    // depende de los puertos IN (interfaces), no de ActividadService
    private final RegistrarActividadUseCase registrarUseCase;
    private final CompletarActividadUseCase completarUseCase;
    private final ConsultarActividadesUseCase consultarUseCase;
    private final BorrarActividadUseCase borrarUseCase;

    public ActividadController(RegistrarActividadUseCase registrarUseCase,
            CompletarActividadUseCase completarUseCase,
            ConsultarActividadesUseCase consultarUseCase,
            BorrarActividadUseCase borrarUseCase) {
        this.registrarUseCase = registrarUseCase;
        this.completarUseCase = completarUseCase;
        this.consultarUseCase = consultarUseCase;
        this.borrarUseCase = borrarUseCase;
    }

    // POST /api/actividades → 201
    @PostMapping("/actividades")
    @ResponseStatus(HttpStatus.CREATED)
    public ActividadResponse crear(@RequestBody ActividadRequest request) {
        return ActividadResponse.desde(registrarUseCase.registrar(
                request.tipo(), request.asunto(), request.descripcion(), request.fecha(), request.empresaId()));
    }

    // GET /api/actividades/5
    @GetMapping("/actividades/{id}")
    public ActividadResponse buscarPorId(@PathVariable Long id) {
        return ActividadResponse.desde(consultarUseCase.buscarPorId(id));
    }

    // GET /api/actividades/vencidas
    @GetMapping("/actividades/vencidas")
    public List<ActividadResponse> vencidas() {
        return consultarUseCase.listarVencidas().stream().map(ActividadResponse::desde).toList();
    }

    // GET /api/empresas/1/actividades
    @GetMapping("/empresas/{empresaId}/actividades")
    public List<ActividadResponse> porEmpresa(@PathVariable Long empresaId) {
        return consultarUseCase.listarPorEmpresa(empresaId).stream().map(ActividadResponse::desde).toList();
    }

    // PATCH /api/actividades/5/completar
    @PatchMapping("/actividades/{id}/completar")
    public ActividadResponse completar(@PathVariable Long id) {
        return ActividadResponse.desde(completarUseCase.completar(id));
    }

    // DELETE /api/actividades/5 → 204
    @DeleteMapping("/actividades/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void borrar(@PathVariable Long id) {
        borrarUseCase.borrar(id);
    }
}
