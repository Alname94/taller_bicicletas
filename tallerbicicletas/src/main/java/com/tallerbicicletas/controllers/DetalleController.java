package com.tallerbicicletas.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tallerbicicletas.models.entities.Detalle;
import com.tallerbicicletas.services.interfaces.IDetalleService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/detalles")
public class DetalleController {

    @Autowired
    private  IDetalleService detalleService;

    @Operation(summary = "Obtener todos los detalles", description = "Devuelve una lista de todos los detalles registrados en el sistema.")
    @GetMapping
    public ResponseEntity<List<Detalle>> getDetalles() {
        return new ResponseEntity<>(detalleService.getDetalles(), HttpStatus.OK);
    }

    @Operation(summary = "Crear un nuevo detalle", description = "Permite crear un nuevo detalle asociado a un presupuesto y un repuesto. El detalle incluye la cantidad de repuestos utilizados y el precio total.")
    @PostMapping
    public ResponseEntity<Detalle> saveDetalle(@Valid @RequestBody Detalle detalle) {
        return new ResponseEntity<>(detalleService.saveDetalle(detalle), HttpStatus.CREATED);
    }

    @Operation(summary = "Obtener un detalle por ID", description = "Devuelve los detalles de un detalle específico según su ID.")
    @GetMapping("/presupuesto/{presupuestoNumero}/repuesto/{repuestoCodigo}")
    public ResponseEntity<Detalle> findDetalle(
            @PathVariable Long presupuestoNumero,
            @PathVariable String repuestoCodigo) {
        return new ResponseEntity<>(detalleService.findDetalle(presupuestoNumero, repuestoCodigo), HttpStatus.OK);
    }

    @Operation(summary = "Eliminar un detalle", description = "Permite eliminar un detalle del sistema utilizando su ID.")
    @DeleteMapping("/presupuesto/{presupuestoNumero}/repuesto/{repuestoCodigo}")
    public ResponseEntity<String> deleteDetalle(
            @PathVariable Long presupuestoNumero,
            @PathVariable String repuestoCodigo) {
        detalleService.deleteDetalle(presupuestoNumero, repuestoCodigo);
        return new ResponseEntity<>("Repuesto eliminado y stock restaurado", HttpStatus.OK);
    }

    @Operation(summary = "Buscar detalles por número de presupuesto", description = "Devuelve una lista de detalles asociados a un número de presupuesto específico.")
    @GetMapping("/presupuesto/{presupuestoNumero}")
    public ResponseEntity<List<Detalle>> findByPresupuesto(@PathVariable Long presupuestoNumero) {
        return new ResponseEntity<>(detalleService.findByIdPresupuestoNumero(presupuestoNumero), HttpStatus.OK);
    }
}
