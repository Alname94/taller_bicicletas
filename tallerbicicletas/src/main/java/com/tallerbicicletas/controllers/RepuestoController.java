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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tallerbicicletas.models.entities.Repuesto;
import com.tallerbicicletas.services.interfaces.IRepuestoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/repuestos")
@CrossOrigin(origins = "*")
public class RepuestoController {

    @Autowired
    private IRepuestoService repuestoService;

    @GetMapping
    public ResponseEntity<List<Repuesto>> getRepuestos() {
        return new ResponseEntity<>(repuestoService.getRepuestos(), HttpStatus.OK);
    }
    
    @PostMapping
    public ResponseEntity<Repuesto> saveRepuesto(@Valid @RequestBody Repuesto repuesto) {
        return new ResponseEntity<>(repuestoService.saveRepuesto(repuesto), HttpStatus.CREATED);
    }

    @GetMapping("/{codigo}")
    public ResponseEntity<Repuesto> findRepuesto(@PathVariable String codigo) {
        return new ResponseEntity<>(repuestoService.findRepuesto(codigo), HttpStatus.OK);
    }

    @DeleteMapping("/borrar/{codigo}")
    public ResponseEntity<String> deleteRepuesto(@PathVariable String codigo) {
        repuestoService.deleteRepuesto(codigo);
        return new ResponseEntity<>("Repuesto eliminado correctamente", HttpStatus.OK);
    }

    @PutMapping("/{codigo}")
    public ResponseEntity<Repuesto> editRepuesto(@RequestParam String codigo, @Valid @RequestBody Repuesto repuesto) {
        repuesto.setCodigo(codigo);
        return new ResponseEntity<>(repuestoService.editRepuesto(repuesto), HttpStatus.OK);
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<Repuesto>> searchRepuestos(@RequestParam String query) {
        return new ResponseEntity<>(repuestoService.findByProductoContainingIgnoreCaseOrMarcaContainingIgnoreCase(query, query), HttpStatus.OK);
    }
}
