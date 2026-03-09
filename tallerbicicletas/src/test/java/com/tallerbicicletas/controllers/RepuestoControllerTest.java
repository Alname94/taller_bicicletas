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

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.tallerbicicletas.config.SecurityConfig;
import com.tallerbicicletas.models.entities.Repuesto;
import com.tallerbicicletas.services.interfaces.IRepuestoService;

import tools.jackson.databind.ObjectMapper;

@WebMvcTest(RepuestoController.class)
@Import(SecurityConfig.class)
@WithMockUser(username = "admin", roles = {"ADMIN"})
public class RepuestoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IRepuestoService repuestoService;

    @Autowired
    private ObjectMapper objectMapper;

    // --- GET ALL ---
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getRepuestos_DebeRetornarListaYOk() throws Exception {
        Repuesto r = new Repuesto("CUB-29-MAX", "Cubierta 29", "Maxxis", "Negro", 45000.0, 30000.0, 10, null);
        given(repuestoService.getRepuestos()).willReturn(List.of(r));

        mockMvc.perform(get("/repuestos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].codigo").value("CUB-29-MAX"))
                .andExpect(jsonPath("$[0].precioVenta").value(45000.0));
    }

    // --- POST ---
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void saveRepuesto_DebeRetornarCreated_CuandoEsValido() throws Exception {
        Repuesto r = new Repuesto("CAD-KMC-9", "Cadena 9v", "KMC", "Plateado", 15000.0, 8000.0, 20, null);
        given(repuestoService.saveRepuesto(any(Repuesto.class))).willReturn(r);

        mockMvc.perform(post("/repuestos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(r)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.codigo").value("CAD-KMC-9"));
    }

    // --- GET POR CODIGO ---
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void findRepuesto_DebeRetornarRepuesto_CuandoExiste() throws Exception {
        Repuesto r = new Repuesto("FRE-SHI-MT200", "Freno Hidraulico", "Shimano", "Negro", 60000.0, 40000.0, 5, null);
        given(repuestoService.findRepuesto("FRE-SHI-MT200")).willReturn(r);

        mockMvc.perform(get("/repuestos/FRE-SHI-MT200"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.producto").value("Freno Hidraulico"));
    }

    // --- DELETE ---
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deleteRepuesto_DebeRetornarOk() throws Exception {
        doNothing().when(repuestoService).deleteRepuesto("CUB-29-MAX");

        mockMvc.perform(delete("/repuestos/borrar/CUB-29-MAX"))
                .andExpect(status().isOk())
                .andExpect(content().string("Repuesto eliminado correctamente"));
    }

    // --- BUSQUEDA POR QUERY PARAM ---
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void searchRepuestos_DebeRetornarListaFiltrada() throws Exception {
        Repuesto r = new Repuesto("CUB-29", "Cubierta", "Maxxis", "Negro", 45000.0, 30000.0, 10, null);
        given(repuestoService.findByProductoContainingIgnoreCaseOrMarcaContainingIgnoreCase("Maxxis", "Maxxis"))
                .willReturn(List.of(r));

        mockMvc.perform(get("/repuestos/buscar")
                .param("query", "Maxxis"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].marca").value("Maxxis"));
    }

    // --- TESTS DE VALIDACIÓN (BEAN VALIDATION) ---

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void saveRepuesto_DebeRetornarBadRequest_CuandoPrecioVentaEsNegativo() throws Exception {
        // precioVenta -100.0
        Repuesto r = new Repuesto("TEST", "Producto", "Marca", "Color", -100.0, 50.0, 10, null);

        mockMvc.perform(post("/repuestos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(r)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void saveRepuesto_DebeRetornarBadRequest_CuandoStockEsNegativo() throws Exception {
        // stock -1
        Repuesto r = new Repuesto("TEST", "Producto", "Marca", "Color", 100.0, 50.0, -1, null);

        mockMvc.perform(post("/repuestos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(r)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void saveRepuesto_DebeRetornarBadRequest_CuandoCodigoEstaVacio() throws Exception {
        // codigo ""
        Repuesto r = new Repuesto("", "Producto", "Marca", "Color", 100.0, 50.0, 10, null);

        mockMvc.perform(post("/repuestos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(r)))
                .andExpect(status().isBadRequest());
    }
}
