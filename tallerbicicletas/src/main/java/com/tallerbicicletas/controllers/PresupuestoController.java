package com.tallerbicicletas.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/presupuestos")
public class PresupuestoController {

    @Autowired
    private IPresupuestoService presupuestoService;

    @Operation(summary = "Obtener todos los presupuestos", description = "Devuelve una lista de todos los presupuestos registrados en el sistema.")
    @GetMapping
    public ResponseEntity<List<Presupuesto>> getPresupuestos() {
        return new ResponseEntity<>(presupuestoService.getPresupuestos(), HttpStatus.OK);
    }

    @Operation(summary = "Crear un nuevo presupuesto", description = "Permite crear un nuevo presupuesto para una bicicleta, incluyendo información del cliente, la bicicleta y el estado inicial del presupuesto.")
    @PostMapping
    public ResponseEntity<Presupuesto> savePresupuesto(@Valid @RequestBody Presupuesto presupuesto) {
        return new ResponseEntity<>(presupuestoService.savePresupuesto(presupuesto), HttpStatus.CREATED);
    }

    @Operation(summary = "Obtener un presupuesto por ID", description = "Devuelve los detalles de un presupuesto específico según su número de presupuesto.")
    @GetMapping("/{numero}")
    public ResponseEntity<Presupuesto> findPresupuesto(@PathVariable Long numero) {
        return new ResponseEntity<>(presupuestoService.findPresupuesto(numero), HttpStatus.OK);
    }

    @Operation(summary = "Eliminar un presupuesto", description = "Permite eliminar un presupuesto del sistema utilizando su número de presupuesto.")
    @DeleteMapping("/borrar/{numero}")
    public ResponseEntity<String> deletePresupuesto(@PathVariable Long numero) {
        presupuestoService.deletePresupuesto(numero);
        return new ResponseEntity<>("Presupuesto eliminado con éxito", HttpStatus.OK);
    }

    @Operation(summary = "Editar un presupuesto", description = "Permite editar los detalles de un presupuesto existente utilizando su número de presupuesto.")
    @PutMapping("/{numero}")
    public ResponseEntity<Presupuesto> editPresupuesto(@PathVariable Long numero, @Valid @RequestBody Presupuesto presupuesto) {
        presupuesto.setNumero(numero);
        return new ResponseEntity<>(presupuestoService.editPresupuesto(presupuesto), HttpStatus.OK);
    }

    @Operation(summary = "Cambiar el estado de un presupuesto", description = "Permite cambiar el estado de un presupuesto existente utilizando su número de presupuesto y el nuevo estado deseado.")
    @PatchMapping("/{numero}/estado")
    public ResponseEntity<String> cambiarEstado(@PathVariable Long numero, @RequestParam String nuevoEstado) {
        presupuestoService.cambiarEstado(numero, nuevoEstado);
        return new ResponseEntity<>("Estado actualizado a " + nuevoEstado.toUpperCase(), HttpStatus.OK);
    }

    @Operation(summary = "Buscar presupuestos por cliente o bicicleta", description = "Permite buscar presupuestos filtrando por el nombre del cliente o la marca de la bicicleta. Ambos parámetros son opcionales y se pueden usar de forma combinada para refinar la búsqueda.")
    @GetMapping("/buscar")
    public ResponseEntity<List<Presupuesto>> search(@RequestParam(required = false) String cliente, @RequestParam(required = false) String bicicleta) {
        return new ResponseEntity<>(presupuestoService.findByClienteNombreContainingIgnoreCaseOrBicicletaMarcaContainingIgnoreCase(cliente, bicicleta), HttpStatus.OK);
    }

    @Operation(summary = "Asignar un servicio a un presupuesto", description = "Permite asignar un servicio a un presupuesto existente utilizando su número de presupuesto y el ID del servicio.")
    @PatchMapping("/{numero}/servicio")
    public ResponseEntity<Presupuesto> asignarServicio(@PathVariable Long numero, @RequestParam Long servicioId) {
        presupuestoService.asignarServicio(numero, servicioId);
        Presupuesto actualizado = presupuestoService.findPresupuesto(numero);
        return ResponseEntity.ok(actualizado);
    }

    @GetMapping("/paginado")
    public ResponseEntity<Page<Presupuesto>> getPresupuestos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(presupuestoService.listarPresupuestosPaginados(page, size));
    }
}
