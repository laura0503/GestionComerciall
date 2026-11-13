package com.gestioncomercial.gestion_comercial;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.jayway.jsonpath.JsonPath;

// comprueba el "portero": SIN @WithMockUser, con tokens de verdad
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SeguridadIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    // registra un usuario, hace login y devuelve su token
    private String obtenerToken() throws Exception {
        String json = """
                { "email": "ana@email.com", "password": "password123" }
                """;
        mockMvc.perform(post("/api/auth/registro").contentType(MediaType.APPLICATION_JSON).content(json));

        String respuesta = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON).content(json))
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(respuesta, "$.token");
    }

    @Test
    @DisplayName("Sin token, la API responde 401")
    void sinToken() throws Exception {
        mockMvc.perform(get("/api/empresas"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Con un token inventado, la API responde 401")
    void tokenFalso() throws Exception {
        mockMvc.perform(get("/api/empresas").header("Authorization", "Bearer esto.no.esunjwt"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Con el token del login, la API responde 200")
    void tokenValido() throws Exception {
        String token = obtenerToken();

        mockMvc.perform(get("/api/empresas").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Swagger sigue abierto sin token")
    void swaggerAbierto() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk());
    }
}
