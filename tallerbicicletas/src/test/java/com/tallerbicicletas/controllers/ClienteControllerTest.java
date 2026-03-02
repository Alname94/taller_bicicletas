package com.tallerbicicletas.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.tallerbicicletas.exceptions.BadRequestException;
import com.tallerbicicletas.models.entities.Cliente;
import com.tallerbicicletas.services.interfaces.IClienteService;

import tools.jackson.databind.ObjectMapper;

@WebMvcTest(ClienteController.class)
public class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IClienteService clienteService;

    @Autowired
    private ObjectMapper objectMapper;

    // --- GET ALL ---
    @Test
    void getClientes_DebeRetornarListaYOk() throws Exception {
        Cliente c = new Cliente(1L, "Juan", "Perez", "12345678", "+5411223344", "juan@mail.com");
        given(clienteService.getClientes()).willReturn(List.of(c));

        mockMvc.perform(get("/clientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Juan"))
                .andExpect(jsonPath("$[0].dni").value("12345678"));
    }

    // --- GET BY ID ---
    @Test
    void findCliente_DebeRetornarCliente_CuandoExiste() throws Exception {
        Cliente c = new Cliente(1L, "Ana", "Gomez", "87654321", "1199887766", "ana@mail.com");
        given(clienteService.findCliente(1L)).willReturn(c);

        mockMvc.perform(get("/clientes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.apellido").value("Gomez"));
    }

    // --- POST ---
    @Test
    void saveCliente_DebeRetornarCreated_CuandoEsValido() throws Exception {
        Cliente c = new Cliente(null, "Luis", "Sosa", "11223344", "2233445566", "luis@mail.com");
        given(clienteService.saveCliente(any(Cliente.class))).willReturn(c);

        mockMvc.perform(post("/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(c)))
                .andExpect(status().isCreated());
    }

    // --- PUT ---
    @Test
    void editCliente_DebeRetornarOk_CuandoEsExitoso() throws Exception {
        Cliente c = new Cliente(1L, "Juan", "Modificado", "12345678", "11223344", "juan@mail.com");
        given(clienteService.editCliente(any(Cliente.class))).willReturn(c);

        mockMvc.perform(put("/clientes/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(c)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.apellido").value("Modificado"));
    }

    // --- DELETE ---
    @Test
    void deleteCliente_DebeRetornarOk_CuandoSeElimina() throws Exception {
        doNothing().when(clienteService).deleteCliente(1L);

        mockMvc.perform(delete("/clientes/borrar/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Cliente eliminado correctamente"));
    }

    // --- GET BUSCAR (QUERY PARAM) ---
    @Test
    void findByNombreOrApellido_DebeRetornarListaFiltrada() throws Exception {
        Cliente c = new Cliente(1L, "Juan", "Perez", "12345678", "11223344", "juan@mail.com");
        given(clienteService.findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCase("Juan", "Juan"))
                .willReturn(List.of(c));

        mockMvc.perform(get("/clientes/buscar")
                .param("nombre", "Juan"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Juan"));
    }

    // --- TESTS DE VALIDACIÓN (BEAN VALIDATION) ---

    @Test
    void saveCliente_DebeRetornarBadRequest_CuandoDniInvalido() throws Exception {
        // DNI con letras o longitud incorrecta
        Cliente c = new Cliente(null, "Juan", "Perez", "1234567A", "1122334455", "juan@mail.com");

        mockMvc.perform(post("/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(c)))
                .andExpect(status().isBadRequest());
        
        verify(clienteService, times(0)).saveCliente(any());
    }

    @Test
    void saveCliente_DebeRetornarBadRequest_CuandoEmailInvalido() throws Exception {
        // Email sin @ o formato incorrecto
        Cliente c = new Cliente(null, "Juan", "Perez", "12345678", "1122334455", "juan.com");

        mockMvc.perform(post("/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(c)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void saveCliente_DebeRetornarBadRequest_CuandoTelefonoInvalido() throws Exception {
        // Teléfono con caracteres no permitidos
        Cliente c = new Cliente(null, "Juan", "Perez", "12345678", "abc-12345", "juan@mail.com");

        mockMvc.perform(post("/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(c)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void saveCliente_DebeRetornarBadRequest_CuandoNombreEsMuyCorto() throws Exception {
        // Nombre con 1 solo caracter
        Cliente c = new Cliente(null, "J", "Perez", "12345678", "1122334455", "juan@mail.com");

        mockMvc.perform(post("/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(c)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void saveCliente_DebeRetornarBadRequest_CuandoCamposObligatoriosEstanVacios() throws Exception {
        // Cliente con campos en blanco
        Cliente c = new Cliente(null, "", "", "", "", "");

        mockMvc.perform(post("/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(c)))
                .andExpect(status().isBadRequest());
    }

    // --- TEST DNI DUPLICADO ---

    @Test
    void saveCliente_DebeRetornarBadRequest_CuandoDniYaExiste() throws Exception {
        Cliente c = new Cliente(null, "Juan", "Perez", "12345678", "1122334455", "juan@mail.com");
        given(clienteService.saveCliente(any(Cliente.class)))
                .willThrow(new BadRequestException("El DNI 12345678 ya está registrado"));

        mockMvc.perform(post("/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(c)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("El DNI 12345678 ya está registrado"));
    }
}
