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

import com.tallerbicicletas.models.entities.Bicicleta;
import com.tallerbicicletas.services.interfaces.IBicicletaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/bicicletas")
@Tag(name = "Bicicletas", description = "Operaciones relacionadas con el inventario de bicicletas")
public class BicicletaController {

    @Autowired
    private IBicicletaService bicicletaService;

    @Operation(summary = "Obtener todas las bicicletas", description = "Retorna una lista completa de las bicicletas registradas en el sistema.")
    @GetMapping
    public ResponseEntity<List<Bicicleta>> getBicicletas() {
        return new ResponseEntity<>(bicicletaService.getBicicletas(), HttpStatus.OK);
    }

    @Operation(summary = "Registrar una nueva bicicleta", description = "Permite agregar una nueva bicicleta al inventario del taller. Se requiere proporcionar los detalles de la bicicleta en el cuerpo de la solicitud.")
    @PostMapping
    public ResponseEntity<Bicicleta> saveBicicleta(@Valid @RequestBody Bicicleta bicicleta) {
        return new ResponseEntity<>(bicicletaService.saveBicicleta(bicicleta), HttpStatus.CREATED);
    }

    @Operation(summary = "Obtener detalles de una bicicleta", description = "Permite obtener la información detallada de una bicicleta específica utilizando su ID.")
    @GetMapping("/{id}")
    public ResponseEntity<Bicicleta> findBicicleta(@PathVariable Long id) {
        return new ResponseEntity<>(bicicletaService.findBicicleta(id), HttpStatus.OK);
    }

    @Operation(summary = "Eliminar una bicicleta", description = "Permite eliminar una bicicleta del inventario utilizando su ID.")
    @DeleteMapping("/borrar/{id}")
    public ResponseEntity<String> deleteBicicleta(@PathVariable Long id) {
        bicicletaService.deleteBicicleta(id);
        return new ResponseEntity<>("Bicicleta eliminada con éxito", HttpStatus.OK);
    }

    @Operation(summary = "Actualizar información de una bicicleta", description = "Permite modificar los detalles de una bicicleta específica utilizando su ID.")
    @PutMapping("/{id}")
    public ResponseEntity<Bicicleta> editBicicleta(@PathVariable Long id, @Valid @RequestBody Bicicleta bicicleta) {
        bicicleta.setId(id);
        return new ResponseEntity<>(bicicletaService.editBicicleta(bicicleta), HttpStatus.OK);
    }

    @Operation(summary = "Buscar bicicletas por marca", description = "Permite buscar bicicletas por su marca.")
    @GetMapping("/buscar")
    public ResponseEntity<List<Bicicleta>> findByMarca(@RequestParam String marca) {
        return new ResponseEntity<>(bicicletaService.findByMarcaContainingIgnoreCase(marca), HttpStatus.OK);
    }

    @Operation(summary = "Buscar bicicletas por cliente", description = "Permite buscar bicicletas asociadas a un cliente específico utilizando su ID.")
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<Bicicleta>> findByCliente(@PathVariable Long clienteId) {
        return new ResponseEntity<>(bicicletaService.findByClienteId(clienteId), HttpStatus.OK);
    }

    @GetMapping("/paginado")
    public ResponseEntity<Page<Bicicleta>> getBicicletas(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(bicicletaService.listarBicicletasPaginadas(page, size));
    }
}
