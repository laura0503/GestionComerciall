package com.gestioncomercial.gestion_comercial;

import com.gestioncomercial.gestion_comercial.empresa.infrastructure.persistence.EmpresaJpaRepository;

import com.gestioncomercial.gestion_comercial.empresa.infrastructure.persistence.EmpresaEntity;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

// tests de integración: arrancan la aplicación entera y hacen peticiones HTTP
// de verdad
@SpringBootTest
@AutoConfigureMockMvc
@Transactional // al acabar cada test se deshacen los cambios en la BD
class EmpresaControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc; // el postman de los test

    @Autowired
    private EmpresaJpaRepository empresaRepository;

    @Autowired
    private ContactoRepository contactoRepository;

    // guarda una empresa directamente en la BD de test
    private EmpresaEntity guardarEmpresa(String nombre, String provincia) {
        return empresaRepository.save(new EmpresaEntity(null, nombre, "Tecnología", provincia, "Cliente"));
    }

    @Test
    @DisplayName("POST /api/empresas crea la empresa y responde 201")
    void crearEmpresa() throws Exception {
        String json = """
                { "nombre": "Acme", "sector": "Tecnología", "provincia": "Madrid", "estado": "Cliente" }
                """;

        mockMvc.perform(post("/api/empresas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber()) // la BD le ha puesto un id
                .andExpect(jsonPath("$.nombre").value("Acme"));
    }

    @Test
    @DisplayName("POST /api/empresas sin nombre responde 400 con el mensaje de error")
    void crearSinNombre() throws Exception {
        String json = """
                  { "sector": "Tecnología", "provincia": "Madrid", "estado": "Cliente" }
                """;
        mockMvc.perform(post("/api/empresas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)) // el body
                .andExpect(status().isBadRequest()) // es 400
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.mensaje").value("El nombre de la empresa es obligatorio"));

        /*
         * status().isOk() 200
         * status().isCreated() 201
         * status().isNoContent() 204
         * status().isBadRequest() 400
         * status().isNotFound() 404
         * status().isConflict() 409
         */

    }

    @Test
    @DisplayName("GET /api/empresas/{id} de una empresa que no existe responde 404")
    void buscarInexistente() throws Exception {
        mockMvc.perform(get("/api/empresas/999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("Empresa no encontrada con id 999999"));
    }

    @Test
    @DisplayName("DELETE de una empresa con contactos responde 409")
    void borrarConContactos() throws Exception {
        EmpresaEntity acme = guardarEmpresa("Acme", "Madrid");
        contactoRepository.save(new Contacto(null, "Ana", null, null, null, null, acme));

        mockMvc.perform(delete("/api/empresas/" + acme.getId()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensaje")
                        .value("No se puede borrar la empresa " + acme.getId() + " porque tiene contactos"));
    }

    @Test
    @DisplayName("DELETE de una empresa sin datos relacionados responde 204 y deja de existir")
    void borrarSinRelacionados() throws Exception {
        EmpresaEntity acme = guardarEmpresa("Acme", "Madrid");

        mockMvc.perform(delete("/api/empresas/" + acme.getId()))
                .andExpect(status().isNoContent());

        // comprobamos que de verdad se ha borrado
        mockMvc.perform(get("/api/empresas/" + acme.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/empresas/buscar?provincia=Madrid solo devuelve las de Madrid")
    void buscarConFiltros() throws Exception {
        guardarEmpresa("Acme", "Madrid");
        guardarEmpresa("Bar Pepe", "Sevilla");

        mockMvc.perform(get("/api/empresas/buscar").param("provincia", "Madrid"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1))) // la lista tiene 1 elemento
                .andExpect(jsonPath("$[0].nombre").value("Acme")); // y es Acme
    }
}
