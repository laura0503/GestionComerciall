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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.gestioncomercial.gestion_comercial.empresa.infrastructure.persistence.EmpresaEntity;
import com.gestioncomercial.gestion_comercial.empresa.infrastructure.persistence.EmpresaJpaRepository;
import com.jayway.jsonpath.JsonPath;

// tests de integración de actividades: la cadena entera con la BD de test
@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser
@Transactional
class ActividadControllerIntegrationTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private EmpresaJpaRepository empresaRepository;

        private EmpresaEntity guardarEmpresa(String nombre) {
                return empresaRepository.save(new EmpresaEntity(null, nombre, "Tecnología", "Madrid", "Cliente"));
        }

        // registra una actividad por la API con la fecha indicada y devuelve su id
        private Integer crearActividad(Long empresaId, String fecha) throws Exception {
                String json = """
                                { "tipo": "LLAMADA", "asunto": "Llamar a Ana", "fecha": "%s", "empresaId": %d }
                                """.formatted(fecha, empresaId);
                String respuesta = mockMvc.perform(post("/api/actividades")
                                .contentType(MediaType.APPLICATION_JSON).content(json))
                                .andExpect(status().isCreated())
                                .andReturn().getResponse().getContentAsString();
                return JsonPath.read(respuesta, "$.id");
        }

        @Test
        @DisplayName("POST registra la actividad pendiente y aparece en las de su empresa")
        void registrarYListarPorEmpresa() throws Exception {
                EmpresaEntity acme = guardarEmpresa("Acme");
                String json = """
                                { "tipo": "REUNION", "asunto": "Demo del producto", "fecha": "2030-01-15T10:00:00", "empresaId": %d }
                                """
                                .formatted(acme.getId());

                mockMvc.perform(post("/api/actividades").contentType(MediaType.APPLICATION_JSON).content(json))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.tipo").value("REUNION"))
                                .andExpect(jsonPath("$.completada").value(false));

                mockMvc.perform(get("/api/empresas/" + acme.getId() + "/actividades"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", hasSize(1)))
                                .andExpect(jsonPath("$[0].asunto").value("Demo del producto"));
        }

        @Test
        @DisplayName("POST en una empresa que no existe responde 400")
        void registrarEnEmpresaInexistente() throws Exception {
                mockMvc.perform(post("/api/actividades").contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                                { "tipo": "LLAMADA", "asunto": "Llamar", "fecha": "2030-01-15T10:00:00", "empresaId": 999999 }
                                                """))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.mensaje").value("La empresa 999999 no existe"));
        }

        @Test
        @DisplayName("PATCH /completar la completa, y completarla otra vez responde 409")
        void completarDosVeces() throws Exception {
                EmpresaEntity acme = guardarEmpresa("Acme");
                Integer id = crearActividad(acme.getId(), "2030-01-15T10:00:00");

                mockMvc.perform(patch("/api/actividades/" + id + "/completar"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.completada").value(true));

                mockMvc.perform(patch("/api/actividades/" + id + "/completar"))
                                .andExpect(status().isConflict())
                                .andExpect(jsonPath("$.mensaje").value("La actividad " + id + " ya está completada"));
        }

        @Test
        @DisplayName("GET /vencidas devuelve solo las pendientes con fecha pasada")
        void listarVencidas() throws Exception {
                EmpresaEntity acme = guardarEmpresa("Acme");
                Integer vencida = crearActividad(acme.getId(), "2020-01-15T10:00:00");
                crearActividad(acme.getId(), "2030-01-15T10:00:00"); // futura: no está vencida

                mockMvc.perform(get("/api/actividades/vencidas"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", hasSize(1)))
                                .andExpect(jsonPath("$[0].id").value(vencida));
        }

        @Test
        @DisplayName("DELETE de una empresa con actividades responde 409")
        void borrarEmpresaConActividades() throws Exception {
                EmpresaEntity acme = guardarEmpresa("Acme");
                crearActividad(acme.getId(), "2030-01-15T10:00:00");

                mockMvc.perform(delete("/api/empresas/" + acme.getId()))
                                .andExpect(status().isConflict())
                                .andExpect(jsonPath("$.mensaje")
                                                .value("No se puede borrar la empresa " + acme.getId()
                                                                + " porque tiene actividades"));
        }

        @Test
        @DisplayName("DELETE borra la actividad y después responde 404")
        void borrarActividad() throws Exception {
                EmpresaEntity acme = guardarEmpresa("Acme");
                Integer id = crearActividad(acme.getId(), "2030-01-15T10:00:00");

                mockMvc.perform(delete("/api/actividades/" + id))
                                .andExpect(status().isNoContent());

                mockMvc.perform(get("/api/actividades/" + id))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.mensaje").value("Actividad no encontrada con id " + id));
        }
}
