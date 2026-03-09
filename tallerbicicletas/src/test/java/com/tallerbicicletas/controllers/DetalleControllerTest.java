package com.tallerbicicletas.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.tallerbicicletas.config.SecurityConfig;
import com.tallerbicicletas.models.entities.Detalle;
import com.tallerbicicletas.models.entities.DetalleId;
import com.tallerbicicletas.models.entities.Repuesto;
import com.tallerbicicletas.services.interfaces.IDetalleService;

import tools.jackson.databind.ObjectMapper;

@WebMvcTest(DetalleController.class)
@Import(SecurityConfig.class)
@WithMockUser(username = "admin", roles = {"ADMIN"})
public class DetalleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IDetalleService detalleService;

    @Autowired
    private ObjectMapper objectMapper;

    // --- OBJETOS PARA LOS TESTS ---
    private DetalleId detalleId;
    private Detalle detalleMock;
    private Repuesto repuestoMock;

    @BeforeEach
    void setUp() {
        detalleId = new DetalleId(100L, "CAD-KMC-9");
        
        repuestoMock = new Repuesto();
        repuestoMock.setCodigo("CAD-KMC-9");
        repuestoMock.setProducto("Cadena KMC");
        repuestoMock.setPrecioVenta(15000.0);

        detalleMock = new Detalle();
        detalleMock.setId(detalleId);
        detalleMock.setRepuesto(repuestoMock);
        detalleMock.setCantidadAgregada(2);
    }

    // --- GET ALL ---
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getDetalles_DebeRetornarListaYOk() throws Exception {
        given(detalleService.getDetalles()).willReturn(List.of(detalleMock));

        mockMvc.perform(get("/detalles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id.presupuestoNumero").value(100))
                .andExpect(jsonPath("$[0].repuesto.codigo").value("CAD-KMC-9"));
    }

    // --- POST ---
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void saveDetalle_DebeRetornarCreated_CuandoEsValido() throws Exception {
        given(detalleService.saveDetalle(any(Detalle.class))).willReturn(detalleMock);

        mockMvc.perform(post("/detalles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(detalleMock)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.cantidadAgregada").value(2));
    }

    // --- GET FIND DETALLE ---
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void findDetalle_DebeRetornarDetalle_CuandoExiste() throws Exception {
        given(detalleService.findDetalle(100L, "CAD-KMC-9")).willReturn(detalleMock);

        mockMvc.perform(get("/detalles/presupuesto/100/repuesto/CAD-KMC-9"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id.repuestoCodigo").value("CAD-KMC-9"));
    }

    // --- DELETE ---
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deleteDetalle_DebeRetornarOk() throws Exception {
        doNothing().when(detalleService).deleteDetalle(100L, "CAD-KMC-9");

        mockMvc.perform(delete("/detalles/presupuesto/100/repuesto/CAD-KMC-9"))
                .andExpect(status().isOk())
                .andExpect(content().string("Repuesto eliminado y stock restaurado"));
    }

    // --- GET BY PRESUPUESTO ---
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void findByPresupuesto_DebeRetornarLista() throws Exception {
        given(detalleService.findByIdPresupuestoNumero(100L)).willReturn(List.of(detalleMock));

        mockMvc.perform(get("/detalles/presupuesto/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    // --- TESTS DE VALIDACIÓN ---

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void saveDetalle_DebeRetornarBadRequest_CuandoCantidadEsCero() throws Exception {
        // Forzamos cantidad 0
        detalleMock.setCantidadAgregada(0);

        mockMvc.perform(post("/detalles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(detalleMock)))
                .andExpect(status().isBadRequest());
    }
}
