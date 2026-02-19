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

import com.tallerbicicletas.models.entities.Bicicleta;
import com.tallerbicicletas.services.interfaces.IBicicletaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/bicicletas")
@CrossOrigin(origins = "*")
public class BicicletaController {

    @Autowired
    private IBicicletaService bicicletaService;

    @GetMapping
    public ResponseEntity<List<Bicicleta>> getBicicletas() {
        return new ResponseEntity<>(bicicletaService.getBicicletas(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Bicicleta> saveBicicleta(@Valid @RequestBody Bicicleta bicicleta) {
        return new ResponseEntity<>(bicicletaService.saveBicicleta(bicicleta), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Bicicleta> findBicicleta(@PathVariable Long id) {
        return new ResponseEntity<>(bicicletaService.findBicicleta(id), HttpStatus.OK);
    }

    @DeleteMapping("/borrar/{id}")
    public ResponseEntity<String> deleteBicicleta(@PathVariable Long id) {
        bicicletaService.deleteBicicleta(id);
        return new ResponseEntity<>("Bicicleta eliminada con éxito", HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Bicicleta> editBicicleta(@PathVariable Long id, @Valid @RequestBody Bicicleta bicicleta) {
        bicicleta.setId(id);
        return new ResponseEntity<>(bicicletaService.editBicicleta(bicicleta), HttpStatus.OK);
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<Bicicleta>> findByMarca(@RequestParam String marca) {
        return new ResponseEntity<>(bicicletaService.findByMarcaContainingIgnoreCase(marca), HttpStatus.OK);
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<Bicicleta>> findByCliente(@PathVariable Long clienteId) {
        return new ResponseEntity<>(bicicletaService.findByClienteId(clienteId), HttpStatus.OK);
    }
}
