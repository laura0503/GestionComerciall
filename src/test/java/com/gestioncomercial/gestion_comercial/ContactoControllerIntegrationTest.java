package com.gestioncomercial.gestion_comercial;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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

import com.gestioncomercial.gestion_comercial.empresa.infrastructure.persistence.EmpresaEntity;
import com.gestioncomercial.gestion_comercial.empresa.infrastructure.persistence.EmpresaJpaRepository;

// tests de integración de contactos: comprueban la cadena entera
// controller → caso de uso → service → adaptador JPA → BD (con su @ManyToOne)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ContactoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmpresaJpaRepository empresaRepository;

    private EmpresaEntity guardarEmpresa(String nombre) {
        return empresaRepository.save(new EmpresaEntity(null, nombre, "Tecnología", "Madrid", "Cliente"));
    }

    // crea un contacto por la API y devuelve el JSON de la respuesta
    private String crearContacto(String nombre, Long empresaId) throws Exception {
        String json = """
                { "nombre": "%s", "email": "%s@acme.com", "empresaId": %d }
                """.formatted(nombre, nombre.toLowerCase(), empresaId);
        return mockMvc.perform(post("/api/contactos").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
    }

    @Test
    @DisplayName("POST /api/contactos crea el contacto y devuelve el nombre de su empresa")
    void crearContacto() throws Exception {
        EmpresaEntity acme = guardarEmpresa("Acme");
        String json = """
                { "nombre": "Ana", "email": "ana@acme.com", "empresaId": %d }
                """.formatted(acme.getId());

        mockMvc.perform(post("/api/contactos").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.nombre").value("Ana"))
                .andExpect(jsonPath("$.empresaId").value(acme.getId()))
                .andExpect(jsonPath("$.empresaNombre").value("Acme"));
    }

    @Test
    @DisplayName("POST /api/contactos con una empresa que no existe responde 404")
    void crearConEmpresaInexistente() throws Exception {
        String json = """
                { "nombre": "Ana", "empresaId": 999999 }
                """;

        mockMvc.perform(post("/api/contactos").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("Empresa no encontrada con id 999999"));
    }

    @Test
    @DisplayName("GET /api/empresas/{id}/contactos solo devuelve los de esa empresa")
    void listarPorEmpresa() throws Exception {
        EmpresaEntity acme = guardarEmpresa("Acme");
        EmpresaEntity pepe = guardarEmpresa("Bar Pepe");
        crearContacto("Ana", acme.getId());
        crearContacto("Luis", pepe.getId());

        mockMvc.perform(get("/api/empresas/" + acme.getId() + "/contactos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nombre").value("Ana"))
                .andExpect(jsonPath("$[0].empresaNombre").value("Acme"));
    }

    @Test
    @DisplayName("PUT /api/contactos/{id} puede cambiar el contacto de empresa")
    void cambiarDeEmpresa() throws Exception {
        EmpresaEntity acme = guardarEmpresa("Acme");
        EmpresaEntity pepe = guardarEmpresa("Bar Pepe");
        String creado = crearContacto("Ana", acme.getId());
        Integer id = com.jayway.jsonpath.JsonPath.read(creado, "$.id");

        String json = """
                { "nombre": "Ana", "empresaId": %d }
                """.formatted(pepe.getId());

        mockMvc.perform(put("/api/contactos/" + id).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.empresaId").value(pepe.getId()))
                .andExpect(jsonPath("$.empresaNombre").value("Bar Pepe"));
    }

    @Test
    @DisplayName("DELETE /api/contactos/{id} lo borra y después responde 404")
    void borrarContacto() throws Exception {
        EmpresaEntity acme = guardarEmpresa("Acme");
        Integer id = com.jayway.jsonpath.JsonPath.read(crearContacto("Ana", acme.getId()), "$.id");

        mockMvc.perform(delete("/api/contactos/" + id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/contactos/" + id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("Contacto no encontrado con id " + id));
    }
}
