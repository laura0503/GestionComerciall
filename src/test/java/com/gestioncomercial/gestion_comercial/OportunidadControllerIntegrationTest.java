package com.gestioncomercial.gestion_comercial;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.gestioncomercial.gestion_comercial.contacto.infrastructure.persistence.ContactoEntity;
import com.gestioncomercial.gestion_comercial.contacto.infrastructure.persistence.ContactoJpaRepository;
import com.gestioncomercial.gestion_comercial.empresa.infrastructure.persistence.EmpresaEntity;
import com.gestioncomercial.gestion_comercial.empresa.infrastructure.persistence.EmpresaJpaRepository;
import com.jayway.jsonpath.JsonPath;

// tests de integración de oportunidades: la cadena entera con la BD de test
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class OportunidadControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmpresaJpaRepository empresaRepository;

    @Autowired
    private ContactoJpaRepository contactoRepository;

    private EmpresaEntity guardarEmpresa(String nombre) {
        return empresaRepository.save(new EmpresaEntity(null, nombre, "Tecnología", "Madrid", "Cliente"));
    }

    private ContactoEntity guardarContacto(String nombre, EmpresaEntity empresa) {
        return contactoRepository.save(new ContactoEntity(null, nombre, null, null, null, null, empresa));
    }

    // crea una oportunidad por la API y devuelve su id
    private Integer crearOportunidad(String titulo, Long empresaId, Long contactoId) throws Exception {
        String json = """
                { "titulo": "%s", "importe": 1000, "empresaId": %d, "contactoId": %s }
                """.formatted(titulo, empresaId, contactoId);
        String respuesta = mockMvc.perform(post("/api/oportunidades")
                .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(respuesta, "$.id");
    }

    @Test
    @DisplayName("POST crea la oportunidad en PROSPECCION con los nombres de empresa y contacto")
    void crearOportunidad() throws Exception {
        EmpresaEntity acme = guardarEmpresa("Acme");
        ContactoEntity ana = guardarContacto("Ana", acme);
        String json = """
                { "titulo": "50 licencias", "importe": 12000.50, "empresaId": %d, "contactoId": %d }
                """.formatted(acme.getId(), ana.getId());

        mockMvc.perform(post("/api/oportunidades").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.fase").value("PROSPECCION"))
                .andExpect(jsonPath("$.empresaNombre").value("Acme"))
                .andExpect(jsonPath("$.contactoId").value(ana.getId()))
                .andExpect(jsonPath("$.contactoNombre").value("Ana"));
    }

    @Test
    @DisplayName("POST con un contacto de otra empresa responde 400")
    void crearConContactoDeOtraEmpresa() throws Exception {
        EmpresaEntity acme = guardarEmpresa("Acme");
        EmpresaEntity pepe = guardarEmpresa("Bar Pepe");
        ContactoEntity luis = guardarContacto("Luis", pepe);
        String json = """
                { "titulo": "50 licencias", "importe": 100, "empresaId": %d, "contactoId": %d }
                """.formatted(acme.getId(), luis.getId());

        mockMvc.perform(post("/api/oportunidades").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje")
                        .value("El contacto " + luis.getId() + " no pertenece a la empresa " + acme.getId()));
    }

    @Test
    @DisplayName("PATCH /fase mueve la oportunidad y GET ?fase= la encuentra")
    void cambiarFaseYFiltrar() throws Exception {
        EmpresaEntity acme = guardarEmpresa("Acme");
        Integer id = crearOportunidad("50 licencias", acme.getId(), null);
        crearOportunidad("Otra", acme.getId(), null); // se queda en PROSPECCION

        mockMvc.perform(patch("/api/oportunidades/" + id + "/fase")
                .contentType(MediaType.APPLICATION_JSON).content("""
                        { "fase": "NEGOCIACION" }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fase").value("NEGOCIACION"))
                .andExpect(jsonPath("$.titulo").value("50 licencias"));

        mockMvc.perform(get("/api/oportunidades").param("fase", "NEGOCIACION"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(id));
    }

    @Test
    @DisplayName("DELETE de un contacto que está en una oportunidad responde 409")
    void borrarContactoConOportunidad() throws Exception {
        EmpresaEntity acme = guardarEmpresa("Acme");
        ContactoEntity ana = guardarContacto("Ana", acme);
        crearOportunidad("50 licencias", acme.getId(), ana.getId());

        mockMvc.perform(delete("/api/contactos/" + ana.getId()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensaje")
                        .value("No se puede borrar el contacto " + ana.getId() + " porque tiene oportunidades"));
    }

    @Test
    @DisplayName("DELETE borra la oportunidad y después responde 404")
    void borrarOportunidad() throws Exception {
        EmpresaEntity acme = guardarEmpresa("Acme");
        Integer id = crearOportunidad("50 licencias", acme.getId(), null);

        mockMvc.perform(delete("/api/oportunidades/" + id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/oportunidades/" + id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("Oportunidad no encontrada con id " + id));
    }
}
