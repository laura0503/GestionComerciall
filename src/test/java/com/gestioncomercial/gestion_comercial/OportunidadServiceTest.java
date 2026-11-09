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

import com.gestioncomercial.gestion_comercial.oportunidad.application.port.in.DatosOportunidad;
import com.gestioncomercial.gestion_comercial.oportunidad.application.port.in.OportunidadDetalle;
import com.gestioncomercial.gestion_comercial.oportunidad.application.port.out.ContactoDeOportunidadPort;
import com.gestioncomercial.gestion_comercial.oportunidad.application.port.out.ContactoResumen;
import com.gestioncomercial.gestion_comercial.oportunidad.application.port.out.EmpresaDeOportunidadPort;
import com.gestioncomercial.gestion_comercial.oportunidad.application.port.out.OportunidadRepositoryPort;
import com.gestioncomercial.gestion_comercial.oportunidad.application.service.OportunidadService;
import com.gestioncomercial.gestion_comercial.oportunidad.domain.model.Fase;
import com.gestioncomercial.gestion_comercial.oportunidad.domain.model.Oportunidad;

// los mismos 6 casos de antes, ahora mockeando los PUERTOS
@ExtendWith(MockitoExtension.class)
class OportunidadServiceTest {
    @Mock
    private OportunidadRepositoryPort oportunidadRepository;

    @Mock
    private EmpresaDeOportunidadPort empresas;

    @Mock
    private ContactoDeOportunidadPort contactos;

    @InjectMocks
    private OportunidadService oportunidadService;

    // recibe la fase, así cada test crea la oportunidad en la fase que necesita (empresa 1, sin contacto)
    private Oportunidad licencias(Fase fase) {
        return new Oportunidad(1L, "50 licencias", new BigDecimal("12000.50"), fase, null, 1L, null);
    }

    // simula que la BD guarda y devuelve lo mismo con id 1
    private void guardarDevuelveConId() {
        when(oportunidadRepository.guardar(any(Oportunidad.class))).thenAnswer(invocacion -> {
            Oportunidad o = invocacion.getArgument(0);
            return new Oportunidad(1L, o.getTitulo(), o.getImporte(), o.getFase(), o.getFechaCierrePrevista(),
                    o.getEmpresaId(), o.getContactoId());
        });
    }

    @Test
    @DisplayName("Una oportunidad nueva sin fase empieza en PROSPECCION")
    void crearSinFaseEmpiezaEnProspeccion() {
        // fase = null y contactoId = null
        DatosOportunidad datos = new DatosOportunidad("50 licencias", new BigDecimal("12000.50"), null, null, 1L,
                null);
        when(empresas.buscarNombre(1L)).thenReturn(Optional.of("Acme"));
        guardarDevuelveConId();

        OportunidadDetalle resultado = oportunidadService.crear(datos);

        assertEquals(Fase.PROSPECCION, resultado.oportunidad().getFase()); // fase por defecto
        assertEquals("Acme", resultado.empresaNombre());
        assertNull(resultado.oportunidad().getContactoId()); // no tiene contacto
    }

    @Test
    @DisplayName("Un contacto de otra empresa lanza 400 y no guarda")
    void crearConContactoDeOtraEmpresa() {
        // oportunidad para Acme (1) con Pepe (5), que es de Bar Pepe (2)
        DatosOportunidad datos = new DatosOportunidad("50 licencias", new BigDecimal("100"), null, null, 1L, 5L);
        when(empresas.buscarNombre(1L)).thenReturn(Optional.of("Acme"));
        when(contactos.buscar(5L)).thenReturn(Optional.of(new ContactoResumen(5L, "Pepe", 2L)));

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> oportunidadService.crear(datos));
        assertEquals("El contacto 5 no pertenece a la empresa 1", error.getMessage());
        verify(oportunidadRepository, never()).guardar(any());
    }

    @Test
    @DisplayName("Un importe negativo lanza 400 y no guarda")
    void crearConImporteNegativo() {
        DatosOportunidad datos = new DatosOportunidad("50 licencias", new BigDecimal("-5"), null, null, 1L, null);

        assertThrows(IllegalArgumentException.class, () -> oportunidadService.crear(datos));
        verify(oportunidadRepository, never()).guardar(any());
    }

    @Test
    @DisplayName("Cambiar de fase solo cambia la fase")
    void cambiarFase() {
        when(oportunidadRepository.buscarPorId(1L)).thenReturn(Optional.of(licencias(Fase.PROSPECCION)));
        when(empresas.buscarNombre(1L)).thenReturn(Optional.of("Acme"));
        guardarDevuelveConId();

        OportunidadDetalle resultado = oportunidadService.cambiarFase(1L, Fase.NEGOCIACION);

        assertEquals(Fase.NEGOCIACION, resultado.oportunidad().getFase()); // la fase ha cambiado
        assertEquals("50 licencias", resultado.oportunidad().getTitulo()); // el resto sigue igual
    }

    @Test
    @DisplayName("Cambiar a una fase nula lanza 400 y no guarda")
    void cambiarFaseNula() {
        when(oportunidadRepository.buscarPorId(1L)).thenReturn(Optional.of(licencias(Fase.PROSPECCION)));

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> oportunidadService.cambiarFase(1L, null));
        assertEquals("La fase es obligatoria", error.getMessage());
        verify(oportunidadRepository, never()).guardar(any());
    }

    @Test
    @DisplayName("Listar con fase solo busca las de esa fase")
    void listarPorFase() {
        when(oportunidadRepository.buscarPorFase(Fase.PROPUESTA)).thenReturn(List.of(licencias(Fase.PROPUESTA)));
        when(empresas.buscarNombre(1L)).thenReturn(Optional.of("Acme"));

        List<OportunidadDetalle> resultado = oportunidadService.listar(Fase.PROPUESTA);

        assertEquals(1, resultado.size());
        assertEquals(Fase.PROPUESTA, resultado.get(0).oportunidad().getFase());
        verify(oportunidadRepository, never()).buscarTodas(); // no ha traído todas
    }
}
