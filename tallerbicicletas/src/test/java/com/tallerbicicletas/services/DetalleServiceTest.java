package com.tallerbicicletas.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
import com.tallerbicicletas.models.entities.Detalle;
import com.tallerbicicletas.models.entities.DetalleId;
import com.tallerbicicletas.models.entities.Presupuesto;
import com.tallerbicicletas.models.entities.Repuesto;
import com.tallerbicicletas.repositories.IDetalleRepository;
import com.tallerbicicletas.repositories.IPresupuestoRepository;
import com.tallerbicicletas.services.interfaces.IRepuestoService;

@ExtendWith(MockitoExtension.class)
@Import(SecurityConfig.class)
@WithMockUser(username = "admin", roles = {"ADMIN"})
public class DetalleServiceTest {

    @Mock
    private IDetalleRepository detalleRepository;
    @Mock
    private IRepuestoService repuestoService;
    @Mock
    private IPresupuestoRepository presupuestoRepository;

    @InjectMocks
    private DetalleService detalleService;

    private Presupuesto presupuestoMock;
    private Repuesto repuestoMock;
    private Detalle detalleMock;
    private DetalleId detalleId;

    @BeforeEach
    void setUp() {
        presupuestoMock = new Presupuesto();
        presupuestoMock.setNumero(100L);
        presupuestoMock.setEstado("PENDIENTE");

        repuestoMock = new Repuesto();
        repuestoMock.setCodigo("CAD-KMC");
        repuestoMock.setProducto("Cadena KMC");
        repuestoMock.setStock(10);
        repuestoMock.setPrecioVenta(1500.0);

        detalleId = new DetalleId(100L, "CAD-KMC");
        detalleMock = new Detalle();
        detalleMock.setId(detalleId);
        detalleMock.setCantidadAgregada(2);
    }

    // --- TEST SAVE: Descontar Stock ---
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void saveDetalle_DebeDescontarStockYGuardar_CuandoHayStockYEstadoPendiente() {
        given(presupuestoRepository.findById(100L)).willReturn(Optional.of(presupuestoMock));
        given(repuestoService.findRepuesto("CAD-KMC")).willReturn(repuestoMock);
        given(detalleRepository.save(any(Detalle.class))).willReturn(detalleMock);

        Detalle resultado = detalleService.saveDetalle(detalleMock);

        assertNotNull(resultado);
        assertEquals(8, repuestoMock.getStock()); // Tenía 10, restó 2
        verify(repuestoService).editRepuesto(repuestoMock);
        verify(detalleRepository).save(detalleMock);
    }

    // --- TEST SAVE: Error por Stock Insuficiente ---
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void saveDetalle_DebeLanzarExcepcion_CuandoStockEsInsuficiente() {
        detalleMock.setCantidadAgregada(15); // Quiere 15 y hay 10
        given(presupuestoRepository.findById(100L)).willReturn(Optional.of(presupuestoMock));
        given(repuestoService.findRepuesto("CAD-KMC")).willReturn(repuestoMock);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            detalleService.saveDetalle(detalleMock);
        });

        assertTrue(exception.getMessage().contains("Stock insuficiente"));
        verify(detalleRepository, never()).save(any());
    }

    // --- TEST DELETE: Devolver Stock ---
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deleteDetalle_DebeSumarStockYBorrar() {
        detalleMock.setPresupuesto(presupuestoMock);
        detalleMock.setRepuesto(repuestoMock);
        given(detalleRepository.findById(any(DetalleId.class))).willReturn(Optional.of(detalleMock));
        given(presupuestoRepository.findById(100L)).willReturn(Optional.of(presupuestoMock));

        detalleService.deleteDetalle(100L, "CAD-KMC");

        assertEquals(12, repuestoMock.getStock()); // Tenía 10, devolvió los 2 que se borraron
        verify(repuestoService).editRepuesto(repuestoMock);
        verify(detalleRepository).delete(detalleMock);
    }

    // --- TEST VALIDACIÓN: No agregar si no está PENDIENTE ---
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void saveDetalle_DebeLanzarExcepcion_CuandoPresupuestoEstaFacturado() {
        presupuestoMock.setEstado("FACTURADO");
        given(presupuestoRepository.findById(100L)).willReturn(Optional.of(presupuestoMock));

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            detalleService.saveDetalle(detalleMock);
        });

        assertTrue(exception.getMessage().contains("No se pueden agregar repuestos"));
        verify(repuestoService, never()).findRepuesto(anyString());
    }
}
