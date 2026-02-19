package com.tallerbicicletas.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tallerbicicletas.models.entities.Presupuesto;
import com.tallerbicicletas.services.interfaces.IPresupuestoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/presupuestos")
@CrossOrigin(origins = "*")
public class PresupuestoController {

    @Autowired
    private IPresupuestoService presupuestoService;

    @GetMapping
    public ResponseEntity<List<Presupuesto>> getPresupuestos() {
        return new ResponseEntity<>(presupuestoService.getPresupuestos(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Presupuesto> savePresupuesto(@Valid @RequestBody Presupuesto presupuesto) {
        return new ResponseEntity<>(presupuestoService.savePresupuesto(presupuesto), HttpStatus.CREATED);
    }

    @GetMapping("/{numero}")
    public ResponseEntity<Presupuesto> findPresupuesto(@PathVariable Long numero) {
        return new ResponseEntity<>(presupuestoService.findPresupuesto(numero), HttpStatus.OK);
    }

    @DeleteMapping("/borrar/{numero}")
    public ResponseEntity<String> deletePresupuesto(@PathVariable Long numero) {
        presupuestoService.deletePresupuesto(numero);
        return new ResponseEntity<>("Presupuesto eliminado con éxito", HttpStatus.OK);
    }

    @PutMapping("/{numero}")
    public ResponseEntity<Presupuesto> editPresupuesto(@PathVariable Long numero, @Valid @RequestBody Presupuesto presupuesto) {
        presupuesto.setNumero(numero);
        return new ResponseEntity<>(presupuestoService.editPresupuesto(presupuesto), HttpStatus.OK);
    }

    @PatchMapping("/{numero}/estado")
    public ResponseEntity<String> cambiarEstado(@PathVariable Long numero, @RequestParam String nuevoEstado) {
        presupuestoService.cambiarEstado(numero, nuevoEstado);
        return new ResponseEntity<>("Estado actualizado a " + nuevoEstado.toUpperCase(), HttpStatus.OK);
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<Presupuesto>> search(@RequestParam(required = false) String cliente, @RequestParam(required = false) String bicicleta) {
        return new ResponseEntity<>(
            presupuestoService.findByClienteNombreContainingIgnoreCaseOrBicicletaMarcaContainingIgnoreCase(cliente, bicicleta), 
            HttpStatus.OK);
    }
}
