package com.gestioncomercial.gestion_comercial;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.gestioncomercial.gestion_comercial.dto.OportunidadRequest;
import com.gestioncomercial.gestion_comercial.dto.OportunidadResponse;

@ExtendWith(MockitoExtension.class)
class OportunidadServiceTest {
    @Mock
    private OportunidadRepository oportunidadRepository;

    @Mock
    private EmpresaRepository empresaRepository;

    @Mock
    private ContactoRepository contactoRepository;

    @InjectMocks
    private OportunidadService oportunidadService;

    // datos de prueba: dos empresas distintas y una oportunidad
    private Empresas acme() {
        return new Empresas(1L, "Acme", "Tecnología", "Madrid", "Cliente");
    }

    private Empresas barPepe() {
        return new Empresas(2L, "Bar Pepe", "Hostelería", "Sevilla", "Potencial");
    }

    // recibe la fase, así cada test crea la oportunidad en la fase que necesita
    private Oportunidad licencias(Fase fase) {
        return new Oportunidad(1L, "50 licencias", new BigDecimal("12000.50"), fase, null, acme(), null);
    }

    @Test
    @DisplayName("Una oportunidad nueva sin fase empieza en PROSPECCION")
    void crearSinFaseEmpiezaEnProspeccion() {
        // fase = null y contactoId = null
        OportunidadRequest request = new OportunidadRequest("50 licencias", new BigDecimal("12000.50"), null,
                null, 1L, null);
        when(empresaRepository.findById(1L)).thenReturn(Optional.of(acme()));
        when(oportunidadRepository.save(any(Oportunidad.class))).thenAnswer(invocacion -> {
            Oportunidad oportunidad = invocacion.getArgument(0);
            oportunidad.setId(1L);
            return oportunidad;
        });

        OportunidadResponse respuesta = oportunidadService.crear(request);

        assertEquals(Fase.PROSPECCION, respuesta.fase()); // se ha puesto la fase por defecto
        assertEquals("Acme", respuesta.empresaNombre());
        assertNull(respuesta.contactoId()); // no tiene contacto
    }

    @Test
    @DisplayName("Un contacto de otra empresa lanza 400 y no guarda")
    void crearConContactoDeOtraEmpresa() {
        // oportunidad para Acme (1) con Pepe (5), que es de Bar Pepe (2)
        OportunidadRequest request = new OportunidadRequest("50 licencias", new BigDecimal("100"), null, null,
                1L, 5L);
        Contacto pepe = new Contacto(5L, "Pepe", null, null, null, null, barPepe());
        when(empresaRepository.findById(1L)).thenReturn(Optional.of(acme()));
        when(contactoRepository.findById(5L)).thenReturn(Optional.of(pepe));

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> oportunidadService.crear(request));
        assertEquals("El contacto 5 no pertenece a la empresa 1", error.getMessage());
        verify(oportunidadRepository, never()).save(any());
    }

    @Test
    @DisplayName("Un importe negativo lanza 400 y no guarda")
    void crearConImporteNegativo() {
        OportunidadRequest request = new OportunidadRequest("50 licencias", new BigDecimal("-5"), null, null,
                1L, null);

        assertThrows(IllegalArgumentException.class, () -> oportunidadService.crear(request));
        verify(oportunidadRepository, never()).save(any());
    }

    @Test
    @DisplayName("Cambiar de fase solo cambia la fase")
    void cambiarFase() {
        when(oportunidadRepository.findById(1L)).thenReturn(Optional.of(licencias(Fase.PROSPECCION)));
        // devuelve lo mismo que te pasen
        when(oportunidadRepository.save(any(Oportunidad.class))).thenAnswer(invocacion -> invocacion.getArgument(0));

        OportunidadResponse respuesta = oportunidadService.cambiarFase(1L, Fase.NEGOCIACION);

        assertEquals(Fase.NEGOCIACION, respuesta.fase()); // la fase ha cambiado
        assertEquals("50 licencias", respuesta.titulo()); // el resto sigue igual
    }

    @Test
    @DisplayName("Cambiar a una fase nula lanza 400")
    void cambiarFaseNula() {
        assertThrows(IllegalArgumentException.class, () -> oportunidadService.cambiarFase(1L, null));
        verify(oportunidadRepository, never()).save(any());
    }

    @Test
    @DisplayName("Listar con fase solo busca las de esa fase")
    void listarPorFase() {
        when(oportunidadRepository.findByFase(Fase.PROPUESTA)).thenReturn(List.of(licencias(Fase.PROPUESTA)));

        List<OportunidadResponse> resultado = oportunidadService.listar(Fase.PROPUESTA);

        assertEquals(1, resultado.size());
        assertEquals(Fase.PROPUESTA, resultado.get(0).fase());
        verify(oportunidadRepository, never()).findAll(); // no ha traído todas
    }

}