package com.tallerbicicletas.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.tallerbicicletas.exceptions.BadRequestException;
import com.tallerbicicletas.exceptions.ResourceNotFoundException;
import com.tallerbicicletas.models.entities.Cliente;
import com.tallerbicicletas.repositories.IClienteRepository;
import com.tallerbicicletas.services.interfaces.IClienteService;

/**
 * Servicio para la gestión integral de Clientes.
 * Implementa validaciones de unicidad de datos (DNI, Email, Teléfono) 
 * y protege la integridad referencial con Bicicletas.
 */
@Service
public class ClienteService implements IClienteService {

    @Autowired
    private IClienteRepository clienteRepository;

    @Override
    public List<Cliente> getClientes() {
        return clienteRepository.findAll();
    }

    @Override
    public Cliente saveCliente(Cliente cliente) {
        // Validar unicidad de DNI, Email y Teléfono antes de guardar.
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

        // Impedir el borrado si el cliente tiene bicicletas asociadas.
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


    @Override
    public Cliente editCliente(Cliente cliente) {
        // Validar que el cliente exista antes de intentar editarlo.
        Cliente clienteExistente = findCliente(cliente.getId());
        
        // Validar unicidad de DNI, Email y Teléfono para el cliente editado, excluyendo su propio registro.
        clienteRepository.findByDni(cliente.getDni())
                .filter(c -> !c.getId().equals(cliente.getId()))
                .ifPresent(c -> {
                    throw new BadRequestException("El DNI ya está registrado por otro cliente.");
                });

        
        clienteRepository.findByEmail(cliente.getEmail())
                .filter(c -> !c.getId().equals(cliente.getId()))
                .ifPresent(c -> {
                    throw new BadRequestException("El Email ya está registrado por otro cliente.");
                });

        
        clienteRepository.findByTelefono(cliente.getTelefono())
                .filter(c -> !c.getId().equals(cliente.getId()))
                .ifPresent(c -> {
                    throw new BadRequestException("El teléfono ya está registrado por otro cliente.");
                });

        
        clienteExistente.setNombre(cliente.getNombre());
        clienteExistente.setApellido(cliente.getApellido());
        clienteExistente.setDni(cliente.getDni());
        clienteExistente.setTelefono(cliente.getTelefono());
        clienteExistente.setEmail(cliente.getEmail());

        return clienteRepository.save(clienteExistente);
    }

    
    @Override
    public List<Cliente> findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCase(String nombre, String apellido) {
        return clienteRepository.findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCase(nombre, apellido);
    }

    
    @Override
    public Optional<Cliente> findByDni(String dni) {
        return clienteRepository.findByDni(dni);
    }

    @Override
    public Page<Cliente> listarClientesPaginados(int page, int size) {
        // Paginación configurada por ID descendente para ver ingresos recientes primero.
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return clienteRepository.findAll(pageable);
    }   
}
