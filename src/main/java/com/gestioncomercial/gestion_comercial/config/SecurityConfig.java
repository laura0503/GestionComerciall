package com.gestioncomercial.gestion_comercial.config;

import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

// el "portero" de la API: decide qué peticiones pasan y cuáles no
@Configuration
public class SecurityConfig {

    // 1. LAS REGLAS: qué se puede hacer sin token y qué no
    @Bean
    public SecurityFilterChain filtroSeguridad(HttpSecurity http) throws Exception {
        http
                // CSRF protege formularios web con cookies; nuestra API usa tokens, no cookies
                .csrf(csrf -> csrf.disable())

                // STATELESS: el servidor no se acuerda de nadie; cada petición trae su token
                .sessionManagement(sesion -> sesion.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth
                        // registro y login: abiertos (si no, nadie podría conseguir un token)
                        .requestMatchers("/api/auth/**").permitAll()
                        // Swagger: abierto, para poder ver la documentación
                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        // todo lo demás: hace falta un token válido
                        .anyRequest().authenticated())

                // los tokens que llegan son JWT: compruébalos con el jwtDecoder de abajo
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));

        return http.build();
    }

    // 2. CÓMO SE COMPRUEBA UN TOKEN: con la MISMA clave con la que se firmó al hacer login
    @Bean
    public JwtDecoder jwtDecoder(@Value("${jwt.secreto}") String secreto) {
        SecretKey clave = new SecretKeySpec(secreto.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        return NimbusJwtDecoder.withSecretKey(clave).macAlgorithm(MacAlgorithm.HS256).build();
    }
}
