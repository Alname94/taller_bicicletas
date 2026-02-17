package com.tallerbicicletas.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.tallerbicicletas.exceptions.BadRequestException;
import com.tallerbicicletas.exceptions.ResourceNotFoundException;
import com.tallerbicicletas.models.entities.Cliente;
import com.tallerbicicletas.repositories.IClienteRepository;
import com.tallerbicicletas.services.interfaces.IClienteService;

public class ClienteService implements IClienteService {

    @Autowired
    private IClienteRepository clienteRepository;

    @Override
    public List<Cliente> getClientes() {
        return clienteRepository.findAll();
    }

    // Guarda un cliente siempre y cuando cumpla con las validaciones
    // para evitar valores repetidos de dni, email y teléfono.
    @Override
    public Cliente saveCliente(Cliente cliente) {

        if (clienteRepository.existsByDni(cliente.getDni())) {
            throw new BadRequestException("El DNI '" + cliente.getDni() + "' ya está registrado.");
        }

        if (clienteRepository.existsByEmail(cliente.getEmail())) {
            throw new BadRequestException("El Email '" + cliente.getEmail() + "' ya está registrado.");
        }

        if (clienteRepository.existsByTelefono(cliente.getTelefono())) {
            throw new BadRequestException("El Teléfono '" + cliente.getTelefono() + "' ya está registrado.");
        }

        return clienteRepository.save(cliente);
    }

    @Override
    public void deleteCliente(Long id) {
        Cliente cliente = findCliente(id);

        if (cliente.getBicicletas() != null && !cliente.getBicicletas().isEmpty()) {
            throw new BadRequestException("No se puede eliminar el cliente porque tiene bicicletas asociadas.");
        }

        clienteRepository.delete(cliente);
    }

    @Override
    public Cliente findCliente(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("El cliente con id " + id + " no existe."));
    }

    // permite editar los datos de un cliente siempre que se cumpla con las
    // validaciones para evitar valores repetidos
    @Override
    public Cliente editCliente(Cliente cliente) {
        // Verificar si el cliente a editar existe
        Cliente clienteExistente = findCliente(cliente.getId());

        // Validar DNI
        clienteRepository.findByDni(cliente.getDni())
                .filter(c -> !c.getId().equals(cliente.getId()))
                .ifPresent(c -> {
                    throw new BadRequestException("El DNI ya está registrado por otro cliente.");
                });

        // Validar Email
        clienteRepository.findByEmail(cliente.getEmail())
                .filter(c -> !c.getId().equals(cliente.getId()))
                .ifPresent(c -> {
                    throw new BadRequestException("El Email ya está registrado por otro cliente.");
                });

        // Validar Teléfono
        clienteRepository.findByTelefono(cliente.getTelefono())
                .filter(c -> !c.getId().equals(cliente.getId()))
                .ifPresent(c -> {
                    throw new BadRequestException("El teléfono ya está registrado por otro cliente.");
                });

        // Actualizar y Guardar
        clienteExistente.setNombre(cliente.getNombre());
        clienteExistente.setApellido(cliente.getApellido());
        clienteExistente.setDni(cliente.getDni());
        clienteExistente.setTelefono(cliente.getTelefono());
        clienteExistente.setEmail(cliente.getEmail());

        return clienteRepository.save(clienteExistente);
    }

    // Búsqueda por caracteres del nombre del cliente
    @Override
    public List<Cliente> findByNombreContainingIgnoreCase(String nombre) {
        List<Cliente> clientes = clienteRepository.findByNombreContainingIgnoreCase(nombre);

        if (clientes.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron clientes con el nombre: " + nombre);
        }

        return clientes;
    }

    // Búsqueda por dni
    @Override
    public Optional<Cliente> findByDni(String dni) {
        return clienteRepository.findByDni(dni);
    }
}
