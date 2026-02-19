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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tallerbicicletas.models.entities.Detalle;
import com.tallerbicicletas.services.interfaces.IDetalleService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/detalles")
@CrossOrigin(origins = "*")
public class DetalleController {

    @Autowired
    private  IDetalleService detalleService;

    @GetMapping
    public ResponseEntity<List<Detalle>> getDetalles() {
        return new ResponseEntity<>(detalleService.getDetalles(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Detalle> saveDetalle(@Valid @RequestBody Detalle detalle) {
        return new ResponseEntity<>(detalleService.saveDetalle(detalle), HttpStatus.CREATED);
    }

    @GetMapping("/presupuesto/{presupuestoNumero}/repuesto/{repuestoCodigo}")
    public ResponseEntity<Detalle> findDetalle(
            @PathVariable Long presupuestoNumero,
            @PathVariable String repuestoCodigo) {
        return new ResponseEntity<>(detalleService.findDetalle(presupuestoNumero, repuestoCodigo), HttpStatus.OK);
    }

    @DeleteMapping("/presupuesto/{presupuestoNumero}/repuesto/{repuestoCodigo}")
    public ResponseEntity<String> deleteDetalle(
            @PathVariable Long presupuestoNumero,
            @PathVariable String repuestoCodigo) {
        detalleService.deleteDetalle(presupuestoNumero, repuestoCodigo);
        return new ResponseEntity<>("Repuesto eliminado y stock restaurado", HttpStatus.OK);
    }

    @GetMapping("/presupuesto/{presupuestoNumero}")
    public ResponseEntity<List<Detalle>> findByPresupuesto(@PathVariable Long presupuestoNumero) {
        return new ResponseEntity<>(detalleService.findByIdPresupuestoNumero(presupuestoNumero), HttpStatus.OK);
    }
}
