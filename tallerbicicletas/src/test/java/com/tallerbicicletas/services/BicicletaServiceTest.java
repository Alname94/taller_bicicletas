package com.tallerbicicletas.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.tallerbicicletas.exceptions.BadRequestException;
import com.tallerbicicletas.exceptions.ResourceNotFoundException;
import com.tallerbicicletas.models.entities.Bicicleta;
import com.tallerbicicletas.models.entities.Cliente;
import com.tallerbicicletas.models.entities.Presupuesto;
import com.tallerbicicletas.repositories.IBicicletaRepository;
import com.tallerbicicletas.services.interfaces.IClienteService;

@ExtendWith(MockitoExtension.class)
public class BicicletaServiceTest {

    @Mock
    private IBicicletaRepository bicicletaRepository;

    @Mock
    private IClienteService clienteService;

    @InjectMocks
    private BicicletaService bicicletaService;

    private Bicicleta bicicletaMock;
    private Cliente clienteMock;

    @BeforeEach
    void setUp() {
        clienteMock = new Cliente(1L, "Juan", "Perez", "12345678", "11223344", "juan@mail.com");
        bicicletaMock = new Bicicleta(10L, clienteMock, "Vairo", "XR", "Negro", "29", LocalDate.now(), new ArrayList<>());
    }

    // --- TEST SAVE ---
    @Test
    void saveBicicleta_DebeGuardar_CuandoClienteExiste() {
        given(clienteService.findCliente(1L)).willReturn(clienteMock);
        given(bicicletaRepository.save(any(Bicicleta.class))).willReturn(bicicletaMock);

        Bicicleta resultado = bicicletaService.saveBicicleta(bicicletaMock);

        assertNotNull(resultado);
        assertEquals("Vairo", resultado.getMarca());
        verify(clienteService).findCliente(1L);
        verify(bicicletaRepository).save(bicicletaMock);
    }

    // --- TEST SAVE (Error: Cliente Null) ---
    @Test
    void saveBicicleta_DebeLanzarExcepcion_CuandoClienteEsNull() {
        bicicletaMock.setCliente(null);

        assertThrows(BadRequestException.class, () -> {
            bicicletaService.saveBicicleta(bicicletaMock);
        });
        verify(bicicletaRepository, never()).save(any());
    }

    // --- TEST DELETE (Error: Tiene Presupuestos) ---
    @Test
    void deleteBicicleta_DebeLanzarExcepcion_CuandoTienePresupuestos() {
        bicicletaMock.getPresupuestos().add(new Presupuesto());
        given(bicicletaRepository.findById(10L)).willReturn(Optional.of(bicicletaMock));

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            bicicletaService.deleteBicicleta(10L);
        });

        assertEquals("No se puede eliminar la bicicleta porque tiene presupuestos asociados.", exception.getMessage());
        verify(bicicletaRepository, never()).delete(any());
    }

    // --- TEST FIND BY CLIENTE ID (Error: Sin bicis) ---
    @Test
    void findByClienteId_DebeLanzarExcepcion_CuandoClienteNoTieneBicis() {
        given(clienteService.findCliente(1L)).willReturn(clienteMock);
        given(bicicletaRepository.findByClienteId(1L)).willReturn(Collections.emptyList());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            bicicletaService.findByClienteId(1L);
        });

        assertEquals("El cliente existe, pero aún no tiene bicicletas registradas.", exception.getMessage());
    }
}

