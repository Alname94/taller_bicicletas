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
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.tallerbicicletas.models.entities.Bicicleta;
import com.tallerbicicletas.models.entities.Cliente;
import com.tallerbicicletas.models.entities.Presupuesto;
import com.tallerbicicletas.services.interfaces.IPresupuestoService;

import tools.jackson.databind.ObjectMapper;

@WebMvcTest(PresupuestoController.class)
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
    void getPresupuestos_DebeRetornarListaYOk() throws Exception {
        given(presupuestoService.getPresupuestos()).willReturn(List.of(presupuestoMock));

        mockMvc.perform(get("/presupuestos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].numero").value(100))
                .andExpect(jsonPath("$[0].cliente.nombre").value("Juan"));
    }

    // --- POST ---
    @Test
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
    void cambiarEstado_DebeRetornarOk() throws Exception {
        doNothing().when(presupuestoService).cambiarEstado(100L, "FACTURADO");

        mockMvc.perform(patch("/presupuestos/100/estado")
                .param("nuevoEstado", "FACTURADO"))
                .andExpect(status().isOk())
                .andExpect(content().string("Estado actualizado a FACTURADO"));
    }

    // --- BUSCAR CON MULTIPLES PARAMS ---
    @Test
    void search_DebeRetornarListaSegunFiltros() throws Exception {
        given(presupuestoService.findByClienteNombreContainingIgnoreCaseOrBicicletaMarcaContainingIgnoreCase("Juan", "Vairo"))
                .willReturn(List.of(presupuestoMock));

        mockMvc.perform(get("/presupuestos/buscar")
                .param("cliente", "Juan")
                .param("bicicleta", "Vairo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].numero").value(100));
    }

    // --- TESTS DE VALIDACIÓN (Bean Validation) ---

    @Test
    void savePresupuesto_DebeRetornarBadRequest_CuandoFechaEsFutura() throws Exception {
        presupuestoMock.setFecha(LocalDate.now().plusDays(1)); // Mañana

        mockMvc.perform(post("/presupuestos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(presupuestoMock)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void savePresupuesto_DebeRetornarBadRequest_CuandoEstadoEsInvalido() throws Exception {
        // Forzamos un estado que no cumple con el @Pattern
        presupuestoMock.setEstado("TERMINADO"); // Solo acepta PENDIENTE, FACTURADO, ANULADO

        mockMvc.perform(post("/presupuestos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(presupuestoMock)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void savePresupuesto_DebeRetornarBadRequest_CuandoValorTotalEsNegativo() throws Exception {
        presupuestoMock.setValorTotal(-1.0);

        mockMvc.perform(post("/presupuestos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(presupuestoMock)))
                .andExpect(status().isBadRequest());
    }
}
