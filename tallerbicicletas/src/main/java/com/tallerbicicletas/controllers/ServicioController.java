package com.tallerbicicletas.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tallerbicicletas.models.entities.Servicio;
import com.tallerbicicletas.services.interfaces.IServicioService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/servicios")
public class ServicioController {

    @Autowired
    private IServicioService servicioService;

    @Operation(summary = "Obtener todos los servicios activos", description = "Devuelve una lista de todos los servicios activos registrados en el sistema.")
    @GetMapping("/activos")
    public ResponseEntity<List<Servicio>> getServiciosActivos() {
        return new ResponseEntity<>(servicioService.getServiciosActivos(), HttpStatus.OK);
    }

    @Operation(summary = "Obtener todos los servicios", description = "Devuelve una lista de todos los servicios registrados en el sistema, incluyendo tanto activos como inactivos.")
    @GetMapping
    public ResponseEntity<List<Servicio>> getAllServicios() {
        return new ResponseEntity<>(servicioService.getServicios(), HttpStatus.OK);
    }

    @Operation(summary = "Obtener un servicio por ID", description = "Devuelve los detalles de un servicio específico según su ID.")
    @GetMapping("/{id}")
    public ResponseEntity<Servicio> getServicioById(@PathVariable Long id) {
        return new ResponseEntity<>(servicioService.findServicio(id), HttpStatus.OK);
    }

    @Operation(summary = "Crear un nuevo servicio", description = "Permite crear un nuevo servicio en el sistema, incluyendo información como el nombre del servicio, la descripción y el precio.")
    @PostMapping
    public ResponseEntity<Servicio> createServicio(@Valid @RequestBody Servicio servicio) {
        return new ResponseEntity<>(servicioService.saveServicio(servicio), HttpStatus.CREATED);
    }

    @Operation(summary = "Editar un servicio", description = "Permite editar los detalles de un servicio existente utilizando su ID.")
    @PutMapping("/{id}")
    public ResponseEntity<Servicio> editServicio(@PathVariable Long id, @RequestBody Servicio servicio) {
        servicio.setId(id);
        return new ResponseEntity<>(servicioService.editServicio(servicio), HttpStatus.OK);
    }

    @Operation(summary = "Eliminar o desactivar un servicio", description = "Permite eliminar o desactivar un servicio del sistema utilizando su ID. La eliminación puede ser lógica (desactivación) para mantener el historial de servicios realizados.")
    @DeleteMapping("/borrar/{id}")
    public ResponseEntity<String> deleteServicio(@PathVariable Long id) {
        servicioService.deleteServicio(id);
        return new ResponseEntity<>("Servicio eliminado/desactivado correctamente", HttpStatus.OK);
    }

    @Operation(summary = "Buscar servicios por nombre", description = "Permite buscar servicios cuyo nombre contenga el término de búsqueda proporcionado. Se requiere proporcionar el término de búsqueda como parámetro de consulta.")
    @GetMapping("/buscar")
    public ResponseEntity<List<Servicio>> findByNombre(@RequestParam String nombre) {
        return new ResponseEntity<>(servicioService.findByNombreContainingIgnoreCase(nombre), HttpStatus.OK);
    }
}
