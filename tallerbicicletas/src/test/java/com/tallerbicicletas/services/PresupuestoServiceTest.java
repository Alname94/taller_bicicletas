package com.tallerbicicletas.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;
import java.util.ArrayList;
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
import com.tallerbicicletas.models.entities.Bicicleta;
import com.tallerbicicletas.models.entities.Cliente;
import com.tallerbicicletas.models.entities.Presupuesto;
import com.tallerbicicletas.models.entities.Servicio;
import com.tallerbicicletas.repositories.IPresupuestoRepository;
import com.tallerbicicletas.services.interfaces.IBicicletaService;
import com.tallerbicicletas.services.interfaces.IClienteService;
import com.tallerbicicletas.services.interfaces.IDetalleService;
import com.tallerbicicletas.services.interfaces.IServicioService;

@ExtendWith(MockitoExtension.class)
@Import(SecurityConfig.class)
@WithMockUser(username = "admin", roles = {"ADMIN"})
public class PresupuestoServiceTest {

    @Mock
    private IPresupuestoRepository presupuestoRepository;
    @Mock
    private IClienteService clienteService;
    @Mock
    private IBicicletaService bicicletaService;
    @Mock
    private IServicioService servicioService;
    @Mock
    private IDetalleService detalleService;

    @InjectMocks
    private PresupuestoService presupuestoService;

    private Cliente clienteMock;
    private Bicicleta bicicletaMock;
    private Presupuesto presupuestoMock;
    private Servicio servicioMock;

    @BeforeEach
    void setUp() {
        clienteMock = new Cliente(1L, "Juan", "Perez", "12345678", "11223344", "juan@mail.com");

        bicicletaMock = new Bicicleta(10L, clienteMock, "Vairo", "XR", "Negro", "29", LocalDate.now(),
                new ArrayList<>());

        servicioMock = new Servicio(1L, "Mantenimiento General", "Desc.", 5000.0, true);

        presupuestoMock = new Presupuesto();
        presupuestoMock.setNumero(100L);
        presupuestoMock.setFecha(LocalDate.now());
        presupuestoMock.setCliente(clienteMock);
        presupuestoMock.setBicicleta(bicicletaMock);
        presupuestoMock.setServicio(servicioMock);
        presupuestoMock.setEstado("PENDIENTE");
    }

    // --- TEST SAVE ---
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void savePresupuesto_DebeCalcularTotalYGuardar_CuandoDatosSonValidos() {
        given(clienteService.findCliente(1L)).willReturn(clienteMock);
        given(bicicletaService.findBicicleta(10L)).willReturn(bicicletaMock);
        given(servicioService.findServicio(1L)).willReturn(servicioMock);
        given(presupuestoRepository.save(any(Presupuesto.class))).willReturn(presupuestoMock);

        Presupuesto resultado = presupuestoService.savePresupuesto(presupuestoMock);

        assertNotNull(resultado);
        assertEquals(5000.0, resultado.getValorTotal());
        verify(presupuestoRepository).save(any(Presupuesto.class));
    }

    // --- TEST VALIDACIÓN: Bicicleta no pertenece al cliente ---
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void savePresupuesto_DebeLanzarExcepcion_CuandoBicicletaNoEsDelCliente() {
        Cliente otroCliente = new Cliente(2L, "Pedro", "Gomez", "87654321", "99887766", "pedro@mail.com");
        bicicletaMock.setCliente(otroCliente);

        given(clienteService.findCliente(1L)).willReturn(clienteMock);
        given(bicicletaService.findBicicleta(10L)).willReturn(bicicletaMock);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            presupuestoService.savePresupuesto(presupuestoMock);
        });

        assertTrue(exception.getMessage().contains("no pertenece al cliente"));
        verify(presupuestoRepository, never()).save(any());
    }

    // --- TEST CAMBIO DE ESTADO A ANULADO ---
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void cambiarEstado_DebeDevolverStock_CuandoSeAnula() {
        given(presupuestoRepository.findById(100L)).willReturn(Optional.of(presupuestoMock));

        presupuestoService.cambiarEstado(100L, "ANULADO");

        assertEquals("ANULADO", presupuestoMock.getEstado());
        verify(detalleService).devolverStockPorAnulacion(100L);
        verify(presupuestoRepository).save(presupuestoMock);
    }

    // --- TEST CAMBIO DE ESTADO: Error si está Anulado ---
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void cambiarEstado_DebeLanzarExcepcion_CuandoPresupuestoYaEstaAnulado() {
        presupuestoMock.setEstado("ANULADO");
        given(presupuestoRepository.findById(100L)).willReturn(Optional.of(presupuestoMock));

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            presupuestoService.cambiarEstado(100L, "FACTURADO");
        });

        assertEquals("No se puede cambiar el estado de un presupuesto ya ANULADO.", exception.getMessage());

        verify(presupuestoRepository, never()).save(any());
        verify(detalleService, never()).devolverStockPorAnulacion(anyLong());
    }

    // --- TEST DELETE: Error si está Facturado ---
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deletePresupuesto_DebeLanzarExcepcion_CuandoEstaFacturado() {
        presupuestoMock.setEstado("FACTURADO");
        given(presupuestoRepository.findById(100L)).willReturn(Optional.of(presupuestoMock));

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            presupuestoService.deletePresupuesto(100L);
        });

        assertEquals("No se puede eliminar un presupuesto en estado " + presupuestoMock.getEstado(), exception.getMessage());
        verify(presupuestoRepository, never()).delete(any());
    }

    // --- TEST DELETE: Error si está Anulado ---
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deletePresupuesto_DebeLanzarExcepcion_CuandoEstaAnulado() {
        presupuestoMock.setEstado("ANULADO");
        given(presupuestoRepository.findById(100L)).willReturn(Optional.of(presupuestoMock));

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            presupuestoService.deletePresupuesto(100L);
        });

        assertEquals("No se puede eliminar un presupuesto en estado " + presupuestoMock.getEstado(), exception.getMessage());
        verify(presupuestoRepository, never()).delete(any());
    }
}
