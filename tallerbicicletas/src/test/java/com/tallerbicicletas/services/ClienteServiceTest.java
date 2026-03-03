package com.tallerbicicletas.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

import com.tallerbicicletas.exceptions.BadRequestException;
import com.tallerbicicletas.exceptions.ResourceNotFoundException;
import com.tallerbicicletas.models.entities.Bicicleta;
import com.tallerbicicletas.models.entities.Cliente;
import com.tallerbicicletas.repositories.IClienteRepository;

@ExtendWith(MockitoExtension.class)
public class ClienteServiceTest {

    @Mock
    private IClienteRepository clienteRepository;

    @InjectMocks
    private ClienteService clienteService;

    private Cliente clienteMock;

    @BeforeEach
    void setUp() {
        clienteMock = new Cliente(1L, "Juan", "Perez", "12345678", "11223344", "juan@mail.com");
    }

    // --- TEST SAVE ---
    @Test
    void saveCliente_DebeGuardar_CuandoDatosSonUnicos() {
        given(clienteRepository.existsByDni(anyString())).willReturn(false);
        given(clienteRepository.existsByEmail(anyString())).willReturn(false);
        given(clienteRepository.existsByTelefono(anyString())).willReturn(false);
        given(clienteRepository.save(any(Cliente.class))).willReturn(clienteMock);

        Cliente resultado = clienteService.saveCliente(clienteMock);

        assertNotNull(resultado);
        assertEquals("12345678", resultado.getDni());
        verify(clienteRepository).save(clienteMock);
    }

    // --- TEST SAVE (Error por DNI duplicado) ---
    @Test
    void saveCliente_DebeLanzarExcepcion_CuandoDniYaExiste() {
        given(clienteRepository.existsByDni("12345678")).willReturn(true);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            clienteService.saveCliente(clienteMock);
        });

        assertEquals("El DNI '12345678' ya está registrado.", exception.getMessage());
        verify(clienteRepository, never()).save(any());
    }

    // --- TEST DELETE (Error por Bicicletas asociadas) ---
    @Test
    void deleteCliente_DebeLanzarExcepcion_CuandoTieneBicicletas() {
        clienteMock.getBicicletas().add(new Bicicleta());
        given(clienteRepository.findById(1L)).willReturn(Optional.of(clienteMock));

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            clienteService.deleteCliente(1L);
        });

        assertEquals("No se puede eliminar el cliente porque tiene bicicletas asociadas.", exception.getMessage());
        verify(clienteRepository, never()).delete(any());
    }

    // --- TEST FIND BY ID (Error Not Found) ---
    @Test
    void findCliente_DebeLanzarExcepcion_CuandoNoExiste() {
        given(clienteRepository.findById(99L)).willReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            clienteService.findCliente(99L);
        });
    }

    // --- TEST EDIT (Lógica de filtrado de ID) ---
    @Test
    void editCliente_DebeLanzarExcepcion_CuandoEmailLoTieneOtroCliente() {
        Cliente otroCliente = new Cliente(2L, "Ana", "Gomez", "88888888", "11555555", "ana@mail.com");

        given(clienteRepository.findById(1L)).willReturn(Optional.of(clienteMock));
        given(clienteRepository.findByEmail(anyString())).willReturn(Optional.of(otroCliente));

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            clienteService.editCliente(clienteMock);
        });

        assertEquals("El Email ya está registrado por otro cliente.", exception.getMessage());
    }

    @Test
    void editCliente_DebeLanzarExcepcion_CuandoTelefonoLoTieneOtroCliente() {
        Cliente otroCliente = new Cliente(2L, "Ana", "Gomez", "88888888", "11223344", "ana@mail.com");

        given(clienteRepository.findById(1L)).willReturn(Optional.of(clienteMock));
        given(clienteRepository.findByTelefono(anyString())).willReturn(Optional.of(otroCliente));

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            clienteService.editCliente(clienteMock);
        });

        assertEquals("El teléfono ya está registrado por otro cliente.", exception.getMessage());
    }

    @Test
    void editCliente_DebeLanzarExcepcion_CuandoDniLoTieneOtroCliente() {
        Cliente otroCliente = new Cliente(2L, "Ana", "Gomez", "12345678", "11222233", "ana@mail.com");

        given(clienteRepository.findById(1L)).willReturn(Optional.of(clienteMock));
        given(clienteRepository.findByDni(anyString())).willReturn(Optional.of(otroCliente));

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            clienteService.editCliente(clienteMock);
        });

        assertEquals("El DNI ya está registrado por otro cliente.", exception.getMessage());
    }
}
