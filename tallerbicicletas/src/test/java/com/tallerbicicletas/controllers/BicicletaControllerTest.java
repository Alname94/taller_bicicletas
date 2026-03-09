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

import java.time.LocalDate;
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
import com.tallerbicicletas.models.entities.Bicicleta;
import com.tallerbicicletas.models.entities.Cliente;
import com.tallerbicicletas.services.interfaces.IBicicletaService;

import tools.jackson.databind.ObjectMapper;

@WebMvcTest(BicicletaController.class)
@Import(SecurityConfig.class)
@WithMockUser(username = "admin", roles = {"ADMIN"})
public class BicicletaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IBicicletaService bicicletaService;

    @Autowired
    private ObjectMapper objectMapper;

    private Cliente crearClienteMock() {
        return new Cliente(1L, "Juan", "Perez", "12345678", "11223344", "juan@mail.com");
    }

    // --- GET ALL ---
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getBicicletas_DebeRetornarListaYOk() throws Exception {
        Bicicleta b = new Bicicleta(1L, crearClienteMock(), "Vairo", "XR 3.5", "Negro", "29", LocalDate.now(), null);
        given(bicicletaService.getBicicletas()).willReturn(List.of(b));

        mockMvc.perform(get("/bicicletas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].marca").value("Vairo"))
                .andExpect(jsonPath("$[0].cliente.id").value(1));
    }

    // --- POST ---
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void saveBicicleta_DebeRetornarCreated_CuandoEsValido() throws Exception {
        Bicicleta b = new Bicicleta(null, crearClienteMock(), "TopMega", "Sunshine", "Blanco", "26", LocalDate.now(), null);
        given(bicicletaService.saveBicicleta(any(Bicicleta.class))).willReturn(b);

        mockMvc.perform(post("/bicicletas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(b)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.marca").value("TopMega"));
    }

    // --- GET BY CLIENTE ID ---
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void findByCliente_DebeRetornarListaDeBicicletas() throws Exception {
        Bicicleta b = new Bicicleta(1L, crearClienteMock(), "Venzo", "Skyline", "Azul", "29", LocalDate.now(), null);
        given(bicicletaService.findByClienteId(1L)).willReturn(List.of(b));

        mockMvc.perform(get("/bicicletas/cliente/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].marca").value("Venzo"))
                .andExpect(jsonPath("$[0].cliente.id").value(1));
    }

    // --- GET BUSCAR POR MARCA ---
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void findByMarca_DebeRetornarListaFiltrada() throws Exception {
        Bicicleta b = new Bicicleta(1L, crearClienteMock(), "Scott", "Aspect", "Gris", "29", LocalDate.now(), null);
        given(bicicletaService.findByMarcaContainingIgnoreCase("Sco")).willReturn(List.of(b));

        mockMvc.perform(get("/bicicletas/buscar")
                .param("marca", "Sco"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].marca").value("Scott"));
    }

    // --- DELETE ---
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deleteBicicleta_DebeRetornarOk() throws Exception {
        doNothing().when(bicicletaService).deleteBicicleta(1L);

        mockMvc.perform(delete("/bicicletas/borrar/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Bicicleta eliminada con éxito"));
    }

    // --- TESTS DE VALIDACIÓN (BEAN VALIDATION) ---

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void saveBicicleta_DebeRetornarBadRequest_CuandoFechaEsFutura() throws Exception {
        // Fecha en el 2030
        Bicicleta b = new Bicicleta(null, crearClienteMock(), "Marca", "Modelo", "Color", "29", LocalDate.of(2030, 1, 1), null);

        mockMvc.perform(post("/bicicletas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(b)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void saveBicicleta_DebeRetornarBadRequest_CuandoClienteEsNulo() throws Exception {
        // Cliente null
        Bicicleta b = new Bicicleta(null, null, "Marca", "Modelo", "Color", "29", LocalDate.now(), null);

        mockMvc.perform(post("/bicicletas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(b)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void saveBicicleta_DebeRetornarBadRequest_CuandoMarcaEsVacia() throws Exception {
        // Marca vacía
        Bicicleta b = new Bicicleta(null, crearClienteMock(), "", "Modelo", "Color", "29", LocalDate.now(), null);

        mockMvc.perform(post("/bicicletas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(b)))
                .andExpect(status().isBadRequest());
    }
}
