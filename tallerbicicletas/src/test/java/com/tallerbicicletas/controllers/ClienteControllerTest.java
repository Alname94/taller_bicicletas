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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.tallerbicicletas.config.SecurityConfig;
import com.tallerbicicletas.dtos.clientes.ClienteListDTO;
import com.tallerbicicletas.exceptions.BadRequestException;
import com.tallerbicicletas.models.entities.Cliente;
import com.tallerbicicletas.services.interfaces.IClienteService;

import tools.jackson.databind.ObjectMapper;

@WebMvcTest(ClienteController.class)
@Import(SecurityConfig.class)
@WithMockUser(username = "admin", roles = {"ADMIN"})
public class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IClienteService clienteService;

    @Autowired
    private ObjectMapper objectMapper;

    private Cliente clienteMock;

    @BeforeEach
    void setUp() {
        clienteMock = new Cliente(1L, "Juan", "Perez", "12345678", "11223344", "juan@mail.com");
    }

    // --- GET ALL ---
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getClientes_DebeRetornarListaYOk() throws Exception {
        given(clienteService.getClientes()).willReturn(List.of(clienteMock));

        mockMvc.perform(get("/clientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Juan"))
                .andExpect(jsonPath("$[0].dni").value("12345678"));
    }

    // --- GET BY ID ---
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void findCliente_DebeRetornarCliente_CuandoExiste() throws Exception {
        given(clienteService.findCliente(1L)).willReturn(clienteMock);

        mockMvc.perform(get("/clientes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.apellido").value("Perez"));
    }

    // --- POST ---
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
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
    @WithMockUser(username = "admin", roles = {"ADMIN"})
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
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deleteCliente_DebeRetornarOk_CuandoSeElimina() throws Exception {
        doNothing().when(clienteService).deleteCliente(1L);

        mockMvc.perform(delete("/clientes/borrar/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Cliente eliminado correctamente"));
    }

    // --- GET BUSCAR (QUERY PARAM) ---
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void search_DebeRetornarPaginaDeClientesDTO() throws Exception {
        ClienteListDTO dto = new ClienteListDTO(
                clienteMock.getId(),
                clienteMock.getNombre(),
                clienteMock.getApellido(),
                clienteMock.getDni());
        Page<ClienteListDTO> pageResponse = new PageImpl<>(List.of(dto));
        given(clienteService.buscarClientesDTO("Juan", 0, 10)).willReturn(pageResponse);

        mockMvc.perform(get("/clientes/buscar")
                .param("query", "Juan")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].nombre").value("Juan"));
    }

    // --- TESTS DE VALIDACIÓN (BEAN VALIDATION) ---

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
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
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void saveCliente_DebeRetornarBadRequest_CuandoEmailInvalido() throws Exception {
        // Email sin @ o formato incorrecto
        Cliente c = new Cliente(null, "Juan", "Perez", "12345678", "1122334455", "juan.com");

        mockMvc.perform(post("/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(c)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void saveCliente_DebeRetornarBadRequest_CuandoTelefonoInvalido() throws Exception {
        // Teléfono con caracteres no permitidos
        Cliente c = new Cliente(null, "Juan", "Perez", "12345678", "abc-12345", "juan@mail.com");

        mockMvc.perform(post("/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(c)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void saveCliente_DebeRetornarBadRequest_CuandoNombreEsMuyCorto() throws Exception {
        // Nombre con 1 solo caracter
        Cliente c = new Cliente(null, "J", "Perez", "12345678", "1122334455", "juan@mail.com");

        mockMvc.perform(post("/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(c)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
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
    @WithMockUser(username = "admin", roles = {"ADMIN"})
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
