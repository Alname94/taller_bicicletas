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

import com.tallerbicicletas.models.entities.Cliente;
import com.tallerbicicletas.services.interfaces.IClienteService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/clientes")
@CrossOrigin(origins = "*")
public class ClienteController {

    @Autowired
    private IClienteService clienteService;

    @GetMapping
    public ResponseEntity<List<Cliente>> getClientes() {
        return new ResponseEntity<>(clienteService.getClientes(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Cliente> saveCliente(@Valid @RequestBody Cliente cliente) {
        return new ResponseEntity<>(clienteService.saveCliente(cliente), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cliente> findCliente(@PathVariable Long id) {
        return new ResponseEntity<>(clienteService.findCliente(id), HttpStatus.OK);
    }

    @DeleteMapping("/borrar/{id}")
    public ResponseEntity<String> deleteCliente(@PathVariable Long id) {
        clienteService.deleteCliente(id);
        return new ResponseEntity<>("Cliente eliminado correctamente", HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cliente> editCliente(@PathVariable Long id, @Valid @RequestBody Cliente cliente) {
        cliente.setId(id);
        return new ResponseEntity<>(clienteService.editCliente(cliente), HttpStatus.OK);
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<Cliente>> findByNombreOrApellido(@RequestParam String nombre) {
        return new ResponseEntity<>(clienteService.findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCase(nombre, nombre), HttpStatus.OK);
    }
}
