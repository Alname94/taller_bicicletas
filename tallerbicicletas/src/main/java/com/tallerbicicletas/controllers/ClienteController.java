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

import com.tallerbicicletas.dtos.clientes.ClienteListDTO;
import com.tallerbicicletas.models.entities.Cliente;
import com.tallerbicicletas.services.interfaces.IClienteService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    @Autowired
    private IClienteService clienteService;

    @Operation(summary = "Obtener todos los clientes", description = "Retorna una lista completa de los clientes registrados en el sistema.")
    @GetMapping
    public ResponseEntity<List<Cliente>> getClientes() {
        return new ResponseEntity<>(clienteService.getClientes(), HttpStatus.OK);
    }

    @Operation(summary = "Registrar un nuevo cliente", description = "Permite agregar un nuevo cliente al sistema. Se requiere proporcionar los detalles del cliente en el cuerpo de la solicitud.")
    @PostMapping
    public ResponseEntity<Cliente> saveCliente(@Valid @RequestBody Cliente cliente) {
        return new ResponseEntity<>(clienteService.saveCliente(cliente), HttpStatus.CREATED);
    }

    @Operation(summary = "Obtener un cliente por ID", description = "Retorna los detalles de un cliente específico según su ID.")
    @GetMapping("/{id}")
    public ResponseEntity<Cliente> findCliente(@PathVariable Long id) {
        return new ResponseEntity<>(clienteService.findCliente(id), HttpStatus.OK);
    }

    @Operation(summary = "Eliminar un cliente", description = "Permite eliminar un cliente del sistema utilizando su ID.")
    @DeleteMapping("/borrar/{id}")
    public ResponseEntity<String> deleteCliente(@PathVariable Long id) {
        clienteService.deleteCliente(id);
        return new ResponseEntity<>("Cliente eliminado correctamente", HttpStatus.OK);
    }

    @Operation(summary = "Actualizar un cliente", description = "Permite modificar los detalles de un cliente existente utilizando su ID. Se requiere proporcionar los nuevos detalles del cliente en el cuerpo de la solicitud.")
    @PutMapping("/{id}")
    public ResponseEntity<Cliente> editCliente(@PathVariable Long id, @Valid @RequestBody Cliente cliente) {
        cliente.setId(id);
        return new ResponseEntity<>(clienteService.editCliente(cliente), HttpStatus.OK);
    }

    @Operation(summary = "Buscar clientes por nombre o apellido", description = "Permite buscar clientes cuyo nombre o apellido contenga el término de búsqueda proporcionado. Se requiere proporcionar el término de búsqueda como parámetro de consulta.")
    @GetMapping("/buscar")
    public ResponseEntity<Page<ClienteListDTO>> buscarClientes(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return new ResponseEntity<>(clienteService.buscarClientesDTO(query, page, size), HttpStatus.OK);
    }

    @GetMapping("/paginado")
    public ResponseEntity<Page<ClienteListDTO>> getClientes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(clienteService.listarClientesPaginadosDTO(page, size));
    }
}
