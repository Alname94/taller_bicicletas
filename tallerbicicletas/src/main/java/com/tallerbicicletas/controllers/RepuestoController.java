package com.tallerbicicletas.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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

import com.tallerbicicletas.models.entities.Repuesto;
import com.tallerbicicletas.services.interfaces.IRepuestoService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/repuestos")
public class RepuestoController {

    @Autowired
    private IRepuestoService repuestoService;

    @Operation(summary = "Obtener todos los repuestos", description = "Devuelve una lista de todos los repuestos registrados en el sistema.")
    @GetMapping
    public ResponseEntity<List<Repuesto>> getRepuestos() {
        return new ResponseEntity<>(repuestoService.getRepuestos(), HttpStatus.OK);
    }
    
    @Operation(summary = "Crear un nuevo repuesto", description = "Permite crear un nuevo repuesto en el sistema, incluyendo información como el código, producto, marca, precio y stock disponible.")
    @PostMapping
    public ResponseEntity<Repuesto> saveRepuesto(@Valid @RequestBody Repuesto repuesto) {
        return new ResponseEntity<>(repuestoService.saveRepuesto(repuesto), HttpStatus.CREATED);
    }

    @Operation(summary = "Obtener un repuesto por ID", description = "Devuelve los detalles de un repuesto específico según su código único.")
    @GetMapping("/{codigo}")
    public ResponseEntity<Repuesto> findRepuesto(@PathVariable String codigo) {
        return new ResponseEntity<>(repuestoService.findRepuesto(codigo), HttpStatus.OK);
    }

    @Operation(summary = "Eliminar un repuesto", description = "Permite eliminar un repuesto del sistema utilizando su código único.")
    @DeleteMapping("/borrar/{codigo}")
    public ResponseEntity<String> deleteRepuesto(@PathVariable String codigo) {
        repuestoService.deleteRepuesto(codigo);
        return new ResponseEntity<>("Repuesto eliminado correctamente", HttpStatus.OK);
    }

    @Operation(summary = "Editar un repuesto", description = "Permite editar los detalles de un repuesto existente utilizando su código único.")
    @PutMapping("/{codigo}")
    public ResponseEntity<Repuesto> editRepuesto(@PathVariable String codigo, @Valid @RequestBody Repuesto repuesto) {
        repuesto.setCodigo(codigo);
        return new ResponseEntity<>(repuestoService.editRepuesto(repuesto), HttpStatus.OK);
    }

    @Operation(summary = "Buscar repuestos por producto o marca", description = "Permite buscar repuestos filtrando por el nombre del producto o la marca. Ambos parámetros son opcionales y se pueden usar de forma combinada para refinar la búsqueda.")
    @GetMapping("/buscar")
    public ResponseEntity<List<Repuesto>> searchRepuestos(@RequestParam String query) {
        return new ResponseEntity<>(repuestoService.findByProductoContainingIgnoreCaseOrMarcaContainingIgnoreCaseOrCodigoContainingIgnoreCase(query, query, query), HttpStatus.OK);
    }

    @Operation(summary = "Obtener repuestos con stock disponible", description = "Devuelve una lista de repuestos que tienen stock disponible (stock mayor a 0).")
    @GetMapping("/disponibles")
    public ResponseEntity<List<Repuesto>> getRepuestosDisponibles() {
        return new ResponseEntity<>(repuestoService.findByStockGreaterThan(), HttpStatus.OK);
    }

    @GetMapping("/paginado")
    public ResponseEntity<Page<Repuesto>> getRepuestos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(repuestoService.listarRepuestosPaginados(page, size));
    }
}
