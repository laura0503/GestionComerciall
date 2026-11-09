package com.gestioncomercial.gestion_comercial.empresa.infrastructure.web;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.gestioncomercial.gestion_comercial.empresa.application.port.in.ActualizarEmpresaUseCase;
import com.gestioncomercial.gestion_comercial.empresa.application.port.in.BorrarEmpresaUseCase;
import com.gestioncomercial.gestion_comercial.empresa.application.port.in.ConsultarEmpresasUseCase;
import com.gestioncomercial.gestion_comercial.empresa.application.port.in.CrearEmpresaUseCase;
import com.gestioncomercial.gestion_comercial.empresa.domain.model.Empresa;
import com.gestioncomercial.gestion_comercial.empresa.infrastructure.web.dto.EmpresaRequest;
import com.gestioncomercial.gestion_comercial.empresa.infrastructure.web.dto.EmpresaResponse;

// adaptador de ENTRADA: traduce HTTP ⇄ casos de uso. Las URLs son las mismas de siempre
@RestController
@RequestMapping("/api/empresas")
public class EmpresaController {

    private final CrearEmpresaUseCase crearUseCase;
    private final ConsultarEmpresasUseCase consultarUseCase;
    private final ActualizarEmpresaUseCase actualizarUseCase;
    private final BorrarEmpresaUseCase borrarUseCase;

    public EmpresaController(CrearEmpresaUseCase crearUseCase, ConsultarEmpresasUseCase consultarUseCase,
            ActualizarEmpresaUseCase actualizarUseCase, BorrarEmpresaUseCase borrarUseCase) {
        this.crearUseCase = crearUseCase;
        this.consultarUseCase = consultarUseCase;
        this.actualizarUseCase = actualizarUseCase;
        this.borrarUseCase = borrarUseCase;
    }

    @GetMapping("/{id}")
    public EmpresaResponse buscarPorId(@PathVariable Long id) {
        return EmpresaResponse.desde(consultarUseCase.buscarPorId(id));
    }

    @GetMapping
    public List<EmpresaResponse> listar() {
        return aResponse(consultarUseCase.listar());
    }

    @GetMapping("/provincia/{provincia}")
    public List<EmpresaResponse> buscarPorProvincia(@PathVariable String provincia) {
        return aResponse(consultarUseCase.buscarPorProvincia(provincia));
    }

    @GetMapping("/sector/{sector}")
    public List<EmpresaResponse> buscarPorSector(@PathVariable String sector) {
        return aResponse(consultarUseCase.buscarPorSector(sector));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EmpresaResponse crearEmpresas(@RequestBody EmpresaRequest empresa) {
        return EmpresaResponse.desde(crearUseCase.crear(empresa.aDatos()));
    }

    @PostMapping("/lote")
    @ResponseStatus(HttpStatus.CREATED)
    public List<EmpresaResponse> crearLote(@RequestBody List<EmpresaRequest> empresas) {
        return aResponse(crearUseCase.crearLote(empresas.stream().map(EmpresaRequest::aDatos).toList()));
    }

    @PutMapping("/{id}")
    public EmpresaResponse actualizarEmpresas(@PathVariable Long id, @RequestBody EmpresaRequest empresa) {
        return EmpresaResponse.desde(actualizarUseCase.actualizar(id, empresa.aDatos()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarEmpresa(@PathVariable Long id) {
        borrarUseCase.borrar(id);
    }

    @GetMapping("/buscar")
    public List<EmpresaResponse> buscar(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String sector,
            @RequestParam(required = false) String provincia,
            @RequestParam(required = false) String estado) {
        return aResponse(consultarUseCase.buscar(nombre, sector, provincia, estado));
    }

    // lista de dominio → lista de DTOs
    private List<EmpresaResponse> aResponse(List<Empresa> empresas) {
        return empresas.stream().map(EmpresaResponse::desde).toList();
    }
}
