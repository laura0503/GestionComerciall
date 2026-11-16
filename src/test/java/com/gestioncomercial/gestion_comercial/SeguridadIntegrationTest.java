package com.gestioncomercial.gestion_comercial;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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

import com.gestioncomercial.gestion_comercial.usuario.application.port.in.RegistrarUsuarioUseCase;
import com.gestioncomercial.gestion_comercial.usuario.domain.model.Rol;
import com.jayway.jsonpath.JsonPath;

// comprueba el "portero": SIN @WithMockUser, con tokens de verdad
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SeguridadIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RegistrarUsuarioUseCase registrarUseCase;

    // crea un usuario con ese rol (sin pasar por la API) y hace login de verdad
    // para conseguir su token
    private String obtenerToken(Rol rol) throws Exception {
        String email = rol.name().toLowerCase() + "@email.com"; // admin@email.com o comercial@email.com
        registrarUseCase.registrar(email, "password123", rol);

        String respuesta = mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content("""
                        { "email": "%s", "password": "password123" }
                        """.formatted(email)))
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
        String token = obtenerToken(Rol.COMERCIAL);

        mockMvc.perform(get("/api/empresas").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Swagger sigue abierto sin token")
    void swaggerAbierto() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Un COMERCIAL no puede borrar: 403")
    void comercialNoPuedeBorrar() throws Exception {
        String token = obtenerToken(Rol.COMERCIAL);

        mockMvc.perform(delete("/api/empresas/999").header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Un ADMIN sí puede borrar: pasa el portero (404 porque la empresa 999 no existe)")
    void adminPuedeBorrar() throws Exception {
        String token = obtenerToken(Rol.ADMIN);

        mockMvc.perform(delete("/api/empresas/999").header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Un COMERCIAL no puede crear usuarios: 403")
    void comercialNoPuedeRegistrar() throws Exception {
        String token = obtenerToken(Rol.COMERCIAL);

        mockMvc.perform(post("/api/auth/registro").header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        { "email": "nuevo@email.com", "password": "password123" }
                        """))
                .andExpect(status().isForbidden());
    }

}
