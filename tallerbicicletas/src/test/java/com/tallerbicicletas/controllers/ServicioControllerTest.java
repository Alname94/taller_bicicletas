package com.tallerbicicletas.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.tallerbicicletas.exceptions.BadRequestException;
import com.tallerbicicletas.exceptions.ResourceNotFoundException;
import com.tallerbicicletas.models.entities.Servicio;
import com.tallerbicicletas.services.interfaces.IServicioService;

import tools.jackson.databind.ObjectMapper;

@WebMvcTest(ServicioController.class)
public class ServicioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IServicioService servicioService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getServiciosActivos_DebeRetornarListaYOk() throws Exception {
        Servicio s = new Servicio(1L, "Lavado", "Desc", 5000.0, true);
        given(servicioService.getServiciosActivos()).willReturn(List.of(s));

        mockMvc.perform(get("/servicios/activos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Lavado"));
    }

    @Test
    void getServicioById_DebeRetornarServicio_CuandoExiste() throws Exception {
        Servicio s = new Servicio(1L, "Ajuste", "Desc", 3000.0, true);
        given(servicioService.findServicio(1L)).willReturn(s);

        mockMvc.perform(get("/servicios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Ajuste"));
    }

    @Test
    void getServicioById_DebeRetornarNotFound_CuandoNoExiste() throws Exception {
        given(servicioService.findServicio(99L))
                .willThrow(new ResourceNotFoundException("El servicio con id 99 no existe."));

        mockMvc.perform(get("/servicios/99"))
                .andExpect(status().isNotFound());
    }

    // --- TESTS POST ---

    @Test
    void createServicio_DebeRetornarCreated_CuandoEsValido() throws Exception {
        Servicio s = new Servicio(null, "Nuevo", "Desc", 1000.0, true);
        given(servicioService.saveServicio(any(Servicio.class))).willReturn(s);

        mockMvc.perform(post("/servicios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(s)))
                .andExpect(status().isCreated());
    }

    @Test
    void createServicio_DebeRetornarBadRequest_CuandoNombreDuplicado() throws Exception {
        Servicio s = new Servicio(null, "Repetido", "Desc", 1000.0, true);
        given(servicioService.saveServicio(any(Servicio.class)))
                .willThrow(new BadRequestException("El Servicio 'Repetido' ya está registrado."));

        mockMvc.perform(post("/servicios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(s)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("El Servicio 'Repetido' ya está registrado."));
    }

    // --- TESTS PUT ---

    @Test
    void editServicio_DebeRetornarOk_CuandoEsExitoso() throws Exception {
        Servicio s = new Servicio(1L, "Editado", "Desc", 2000.0, true);
        given(servicioService.editServicio(any(Servicio.class))).willReturn(s);

        mockMvc.perform(put("/servicios/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(s)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Editado"));
    }

    // --- TESTS DELETE ---

    @Test
    void deleteServicio_DebeRetornarOk_CuandoSeElimina() throws Exception {
        doNothing().when(servicioService).deleteServicio(1L);

        mockMvc.perform(delete("/servicios/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Servicio eliminado/desactivado correctamente"));
    }

    // --- TESTS DE VALIDACIÓN (BEAN VALIDATION) ---

    @Test
    void createServicio_DebeRetornarBadRequest_CuandoNombreEstaVacio() throws Exception {
        Servicio s = new Servicio(null, "", "Descripción válida", 1000.0, true);

        mockMvc.perform(post("/servicios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(s)))
                .andExpect(status().isBadRequest());
        
        verify(servicioService, times(0)).saveServicio(any());
    }

    @Test
    void createServicio_DebeRetornarBadRequest_CuandoValorEsNegativo() throws Exception {
        Servicio s = new Servicio(null, "Servicio Test", "Desc", -50.0, true);

        mockMvc.perform(post("/servicios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(s)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createServicio_DebeRetornarBadRequest_CuandoValorEsNulo() throws Exception {
        Servicio s = new Servicio(null, "Servicio Test", "Desc", null, true);

        mockMvc.perform(post("/servicios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(s)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createServicio_DebeRetornarBadRequest_CuandoNombreEsMuyLargo() throws Exception {
        String nombreLargo = "A".repeat(51);
        Servicio s = new Servicio(null, nombreLargo, "Desc", 1000.0, true);

        mockMvc.perform(post("/servicios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(s)))
                .andExpect(status().isBadRequest());
    }
}
