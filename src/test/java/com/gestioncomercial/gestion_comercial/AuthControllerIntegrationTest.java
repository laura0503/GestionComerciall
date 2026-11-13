package com.gestioncomercial.gestion_comercial;

import static org.hamcrest.Matchers.startsWith;
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

// tests de integración de registro y login: la cadena entera con la BD de test
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private void registrar(String email, String password) throws Exception {
        mockMvc.perform(post("/api/auth/registro").contentType(MediaType.APPLICATION_JSON).content("""
                { "email": "%s", "password": "%s" }
                """.formatted(email, password)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("POST /registro crea el usuario como COMERCIAL y NO devuelve la contraseña")
    void registrarUsuario() throws Exception {
        mockMvc.perform(post("/api/auth/registro").contentType(MediaType.APPLICATION_JSON).content("""
                { "email": "Ana@Email.com", "password": "password123" }
                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("ana@email.com"))
                .andExpect(jsonPath("$.rol").value("COMERCIAL"))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.passwordCifrada").doesNotExist());
    }

    @Test
    @DisplayName("POST /registro con un email repetido responde 409")
    void registrarEmailRepetido() throws Exception {
        registrar("ana@email.com", "password123");

        mockMvc.perform(post("/api/auth/registro").contentType(MediaType.APPLICATION_JSON).content("""
                { "email": "ana@email.com", "password": "otraPassword1" }
                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensaje").value("Ya existe un usuario con el email ana@email.com"));
    }

    @Test
    @DisplayName("POST /login correcto devuelve un token JWT")
    void loginCorrecto() throws Exception {
        registrar("ana@email.com", "password123");

        mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content("""
                { "email": "ana@email.com", "password": "password123" }
                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(startsWith("eyJ"))) // todos los JWT empiezan así
                .andExpect(jsonPath("$.email").value("ana@email.com"))
                .andExpect(jsonPath("$.rol").value("COMERCIAL"));
    }

    @Test
    @DisplayName("POST /login con la contraseña incorrecta responde 401")
    void loginPasswordIncorrecta() throws Exception {
        registrar("ana@email.com", "password123");

        mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content("""
                { "email": "ana@email.com", "password": "otraPassword" }
                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.mensaje").value("Email o contraseña incorrectos"));
    }
}
