package com.tallerbicicletas.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tallerbicicletas.models.entities.Servicio;
import com.tallerbicicletas.services.interfaces.IServicioService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/servicios")
@CrossOrigin(origins = "*")
public class ServicioController {

    @Autowired
    private IServicioService servicioService;

    @GetMapping("/activos")
    public ResponseEntity<List<Servicio>> getServiciosActivos() {
        return new ResponseEntity<>(servicioService.getServiciosActivos(), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<Servicio>> getAllServicios() {
        return new ResponseEntity<>(servicioService.getServicios(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Servicio> getServicioById(@PathVariable Long id) {
        return new ResponseEntity<>(servicioService.findServicio(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Servicio> createServicio(@Valid @RequestBody Servicio servicio) {
        return new ResponseEntity<>(servicioService.saveServicio(servicio), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Servicio> editServicio(@PathVariable Long id, @RequestBody Servicio servicio) {
        servicio.setId(id);
        return new ResponseEntity<>(servicioService.editServicio(servicio), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteServicio(@PathVariable Long id) {
        servicioService.deleteServicio(id);
        return new ResponseEntity<>("Servicio eliminado/desactivado correctamente", HttpStatus.OK);
    }
}
