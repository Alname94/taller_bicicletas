package com.tallerbicicletas.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;

import com.tallerbicicletas.config.SecurityConfig;
import com.tallerbicicletas.exceptions.BadRequestException;
import com.tallerbicicletas.models.entities.Servicio;
import com.tallerbicicletas.repositories.IPresupuestoRepository;
import com.tallerbicicletas.repositories.IServicioRepository;

@ExtendWith(MockitoExtension.class)
@Import(SecurityConfig.class)
@WithMockUser(username = "admin", roles = {"ADMIN"})
public class ServicioServiceTest {

    @Mock
    private IServicioRepository servicioRepository;

    @Mock
    private IPresupuestoRepository presupuestoRepository;

    @InjectMocks
    private ServicioService servicioService;

    private Servicio servicioMock;

    @BeforeEach
    void setUp() {
        servicioMock = new Servicio();
        servicioMock.setId(1L);
        servicioMock.setNombre("Mantenimiento General");
        servicioMock.setDescripcion("Limpieza, lubricación y ajuste de transmisión");
        servicioMock.setValor(5500.0);
        servicioMock.setActivo(true);
    }

    // --- TEST SAVE ---
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void saveServicio_DebeGuardar_CuandoDatosSonValidos() {
        given(servicioRepository.existsByNombreIgnoreCase("Mantenimiento General")).willReturn(false);
        given(servicioRepository.save(any(Servicio.class))).willReturn(servicioMock);

        Servicio resultado = servicioService.saveServicio(servicioMock);

        assertNotNull(resultado);
        assertEquals("Mantenimiento General", resultado.getNombre());
        assertTrue(resultado.isActivo());
        verify(servicioRepository).save(servicioMock);
    }

    // --- TEST SAVE (Error: Nombre Duplicado) ---
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void saveServicio_DebeLanzarExcepcion_CuandoNombreYaExiste() {
        given(servicioRepository.existsByNombreIgnoreCase("Mantenimiento General")).willReturn(true);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            servicioService.saveServicio(servicioMock);
        });

        assertTrue(exception.getMessage().contains("ya está registrado"));
        verify(servicioRepository, never()).save(any());
    }

    // --- TEST SAVE (Error: Valor <= 0) ---
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void saveServicio_DebeLanzarExcepcion_CuandoValorEsInvalido() {
        servicioMock.setValor(0.0);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            servicioService.saveServicio(servicioMock);
        });

        assertEquals("El valor del servicio debe ser mayor a 0.", exception.getMessage());
        verify(servicioRepository, never()).save(any());
    }

    // --- TEST DELETE: Baja Lógica (Soft Delete) ---
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deleteServicio_DebeDesactivar_CuandoExisteEnPresupuestos() {
        given(servicioRepository.findById(1L)).willReturn(Optional.of(servicioMock));
        given(presupuestoRepository.existsByServicioId(1L)).willReturn(true);

        servicioService.deleteServicio(1L);

        assertFalse(servicioMock.isActivo());
        verify(servicioRepository).save(servicioMock);
        verify(servicioRepository, never()).deleteById(1L);
    }

    // --- TEST DELETE: Baja Física (Hard Delete) ---
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deleteServicio_DebeBorrarFisicamente_CuandoNoTienePresupuestos() {
        given(servicioRepository.findById(1L)).willReturn(Optional.of(servicioMock));
        given(presupuestoRepository.existsByServicioId(1L)).willReturn(false);

        servicioService.deleteServicio(1L);

        verify(servicioRepository).deleteById(1L);
        verify(servicioRepository, never()).save(any());
    }

    // --- TEST EDIT---
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void editServicio_DebeActualizarCampos_CuandoEsValido() {
        Servicio servicioEditado = new Servicio();
        servicioEditado.setId(1L);
        servicioEditado.setNombre("Mantenimiento Premium");
        servicioEditado.setDescripcion("Incluye centrado de ruedas");
        servicioEditado.setValor(8000.0);

        given(servicioRepository.findById(1L)).willReturn(Optional.of(servicioMock));
        given(servicioRepository.existsByNombreIgnoreCase("Mantenimiento Premium")).willReturn(false);
        given(servicioRepository.save(any(Servicio.class))).willReturn(servicioEditado);

        Servicio resultado = servicioService.editServicio(servicioEditado);

        assertEquals("Mantenimiento Premium", resultado.getNombre());
        assertEquals(8000.0, resultado.getValor());
        verify(servicioRepository).save(any(Servicio.class));
    }
}
