package com.tallerbicicletas.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.List;
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
import com.tallerbicicletas.models.entities.Detalle;
import com.tallerbicicletas.models.entities.Repuesto;
import com.tallerbicicletas.repositories.IRepuestoRepository;

@ExtendWith(MockitoExtension.class)
@Import(SecurityConfig.class)
@WithMockUser(username = "admin", roles = {"ADMIN"})
public class RepuestoServiceTest {

    @Mock
    private IRepuestoRepository repuestoRepository;

    @InjectMocks
    private RepuestoService repuestoService;

    private Repuesto repuestoMock;

    @BeforeEach
    void setUp() {
        repuestoMock = new Repuesto();
        repuestoMock.setCodigo("CUB-29-MAX");
        repuestoMock.setProducto("Cubierta 29");
        repuestoMock.setMarca("Maxxis");
        repuestoMock.setPrecioCosto(30000.0);
        repuestoMock.setPrecioVenta(45000.0);
        repuestoMock.setStock(10);
    }

    // --- TEST SAVE ---
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void saveRepuesto_DebeGuardar_CuandoDatosSonCorrectos() {
        given(repuestoRepository.existsById("CUB-29-MAX")).willReturn(false);
        given(repuestoRepository.save(any(Repuesto.class))).willReturn(repuestoMock);

        Repuesto resultado = repuestoService.saveRepuesto(repuestoMock);

        assertNotNull(resultado);
        assertEquals(45000.0, resultado.getPrecioVenta());
        verify(repuestoRepository).save(repuestoMock);
    }

    // --- TEST SAVE (Error: Precio de Venta menor al Costo) ---
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void saveRepuesto_DebeLanzarExcepcion_CuandoPrecioVentaEsMenorAlCosto() {
        repuestoMock.setPrecioVenta(20000.0);

        given(repuestoRepository.existsById(anyString())).willReturn(false);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            repuestoService.saveRepuesto(repuestoMock);
        });

        assertEquals("El precio de venta no puede ser menor al precio de costo.", exception.getMessage());
        verify(repuestoRepository, never()).save(any());
    }

    // --- TEST SAVE (Error: Código duplicado) ---
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void saveRepuesto_DebeLanzarExcepcion_CuandoCodigoYaExiste() {
        given(repuestoRepository.existsById("CUB-29-MAX")).willReturn(true);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            repuestoService.saveRepuesto(repuestoMock);
        });

        assertEquals("El código de repuesto '" + repuestoMock.getCodigo() + "' ya existe.", exception.getMessage());
        verify(repuestoRepository, never()).save(any());
    }

    // --- TEST DELETE (Error: Repuesto en uso) ---
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deleteRepuesto_DebeLanzarExcepcion_CuandoEstaEnPresupuestos() {
        repuestoMock.setDetalles(List.of(new Detalle()));
        given(repuestoRepository.findById("CUB-29-MAX")).willReturn(Optional.of(repuestoMock));

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            repuestoService.deleteRepuesto("CUB-29-MAX");
        });

        assertEquals("No se puede eliminar el repuesto porque ya figura en presupuestos existentes.", exception.getMessage());
        verify(repuestoRepository, never()).delete(any());
    }

    // --- TEST EDIT (Validación de Precios en Edición) ---
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void editRepuesto_DebeLanzarExcepcion_CuandoSeIntentaPonerPrecioVentaInvalido() {
        given(repuestoRepository.findById("CUB-29-MAX")).willReturn(Optional.of(repuestoMock));

        repuestoMock.setPrecioVenta(10.0);

        assertThrows(BadRequestException.class, () -> {
            repuestoService.editRepuesto(repuestoMock);
        });
    }
}
