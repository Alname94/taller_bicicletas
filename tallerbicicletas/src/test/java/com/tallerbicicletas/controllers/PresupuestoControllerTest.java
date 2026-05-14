package com.tallerbicicletas.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
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
import com.tallerbicicletas.dtos.presupuestos.PresupuestoListDTO;
import com.tallerbicicletas.models.entities.Bicicleta;
import com.tallerbicicletas.models.entities.Cliente;
import com.tallerbicicletas.models.entities.Presupuesto;
import com.tallerbicicletas.services.interfaces.IPresupuestoService;

import tools.jackson.databind.ObjectMapper;

@WebMvcTest(PresupuestoController.class)
@Import(SecurityConfig.class)
@WithMockUser(username = "admin", roles = { "ADMIN" })
public class PresupuestoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IPresupuestoService presupuestoService;

    @Autowired
    private ObjectMapper objectMapper;

    // --- OBJETOS MOCK PARA REUTILIZAR ---
    private Cliente clienteMock;
    private Bicicleta bicicletaMock;
    private Presupuesto presupuestoMock;

    @BeforeEach
    void setUp() {
        clienteMock = new Cliente(1L, "Juan", "Perez", "12345678", "11223344", "juan@mail.com");
        bicicletaMock = new Bicicleta(1L, clienteMock, "Vairo", "XR", "Negro", "29", LocalDate.now(), null);
        presupuestoMock = new Presupuesto();
        presupuestoMock.setNumero(100L);
        presupuestoMock.setFecha(LocalDate.now());
        presupuestoMock.setCliente(clienteMock);
        presupuestoMock.setBicicleta(bicicletaMock);
        presupuestoMock.setValorTotal(5000.0);
        presupuestoMock.setEstado("PENDIENTE");
    }

    // --- GET ALL ---
    @Test
    @WithMockUser(username = "admin", roles = { "ADMIN" })
    void getPresupuestos_DebeRetornarListaYOk() throws Exception {
        given(presupuestoService.getPresupuestos()).willReturn(List.of(presupuestoMock));

        mockMvc.perform(get("/presupuestos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].numero").value(100))
                .andExpect(jsonPath("$[0].cliente.nombre").value("Juan"));
    }

    // --- POST ---
    @Test
    @WithMockUser(username = "admin", roles = { "ADMIN" })
    void savePresupuesto_DebeRetornarCreated_CuandoEsValido() throws Exception {
        given(presupuestoService.savePresupuesto(any(Presupuesto.class))).willReturn(presupuestoMock);

        mockMvc.perform(post("/presupuestos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(presupuestoMock)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.numero").value(100));
    }

    // --- PATCH (Cambio de Estado) ---
    @Test
    @WithMockUser(username = "admin", roles = { "ADMIN" })
    void cambiarEstado_DebeRetornarOk() throws Exception {
        doNothing().when(presupuestoService).cambiarEstado(100L, "FACTURADO");

        mockMvc.perform(patch("/presupuestos/100/estado")
                .param("nuevoEstado", "FACTURADO"))
                .andExpect(status().isOk())
                .andExpect(content().string("Estado actualizado a FACTURADO"));
    }

    // --- BUSCAR CON MULTIPLES PARAMS ---
    @Test
    @WithMockUser(username = "admin", roles = { "ADMIN" })
    void search_DebeRetornarPaginaDePresupuestosDTO() throws Exception {
        PresupuestoListDTO dto = new PresupuestoListDTO(
                presupuestoMock.getNumero(),
                presupuestoMock.getFecha().toString(),
                String.format("#%d - %s %s", clienteMock.getId(), clienteMock.getNombre(), clienteMock.getApellido()),
                String.format("#%d - %s %s", bicicletaMock.getId(), bicicletaMock.getMarca(),
                        bicicletaMock.getModelo()),
                presupuestoMock.getValorTotal(),
                presupuestoMock.getEstado());

        // Implementación de Page para pruebas
        Page<PresupuestoListDTO> pageResponse = new PageImpl<>(List.of(dto));

        // mock del service con los parámetros que envía el controlador por defecto
        given(presupuestoService.buscarPresupuestosDTO("Juan", 0, 10))
                .willReturn(pageResponse);

        mockMvc.perform(get("/presupuestos/buscar")
                .param("query", "Juan")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].numero").value(100))
                .andExpect(jsonPath("$.content[0].cliente").value("#1 - Juan Perez"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    // --- TESTS DE VALIDACIÓN (Bean Validation) ---

    @Test
    @WithMockUser(username = "admin", roles = { "ADMIN" })
    void savePresupuesto_DebeRetornarBadRequest_CuandoFechaEsFutura() throws Exception {
        presupuestoMock.setFecha(LocalDate.now().plusDays(1)); // Mañana

        mockMvc.perform(post("/presupuestos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(presupuestoMock)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin", roles = { "ADMIN" })
    void savePresupuesto_DebeRetornarBadRequest_CuandoEstadoEsInvalido() throws Exception {
        presupuestoMock.setEstado("TERMINADO");

        mockMvc.perform(post("/presupuestos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(presupuestoMock)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin", roles = { "ADMIN" })
    void savePresupuesto_DebeRetornarBadRequest_CuandoValorTotalEsNegativo() throws Exception {
        presupuestoMock.setValorTotal(-1.0);

        mockMvc.perform(post("/presupuestos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(presupuestoMock)))
                .andExpect(status().isBadRequest());
    }
}
