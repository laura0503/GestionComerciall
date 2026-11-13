package com.gestioncomercial.gestion_comercial.usuario.infrastructure.security;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.stereotype.Component;

import com.gestioncomercial.gestion_comercial.usuario.application.port.out.GeneradorTokenPort;
import com.gestioncomercial.gestion_comercial.usuario.domain.model.Usuario;
import com.nimbusds.jose.jwk.source.ImmutableSecret;

// quien RESPONDE al puerto de tokens, generando un JWT firmado
@Component
public class JwtGeneradorTokenAdapter implements GeneradorTokenPort {

    private final JwtEncoder encoder;
    private final long minutosValidez;

    // @Value lee los valores de application.properties (y estos, del .env)
    public JwtGeneradorTokenAdapter(@Value("${jwt.secreto}") String secreto,
            @Value("${jwt.minutos-validez}") long minutosValidez) {
        // la clave secreta con la que se FIRMA el token. Si alguien cambia el token, la firma ya no cuadra
        SecretKey clave = new SecretKeySpec(secreto.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        this.encoder = new NimbusJwtEncoder(new ImmutableSecret<>(clave));
        this.minutosValidez = minutosValidez;
    }

    @Override
    public String generar(Usuario usuario) {
        Instant ahora = Instant.now();

        // los "claims": los datos que viajan DENTRO del token
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("gestion-comercial") // quién lo emite
                .subject(usuario.getEmail()) // de quién es
                .claim("rol", usuario.getRol().name()) // qué puede hacer
                .issuedAt(ahora) // cuándo se creó
                .expiresAt(ahora.plus(minutosValidez, ChronoUnit.MINUTES)) // cuándo caduca
                .build();

        JwsHeader cabecera = JwsHeader.with(MacAlgorithm.HS256).build(); // algoritmo de firma

        return encoder.encode(JwtEncoderParameters.from(cabecera, claims)).getTokenValue();
    }
}
