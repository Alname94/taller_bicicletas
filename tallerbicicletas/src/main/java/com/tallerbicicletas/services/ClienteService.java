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


    @Override
    public Cliente editCliente(Cliente cliente) {
        
        Cliente clienteExistente = findCliente(cliente.getId());

        
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
    public Page<Cliente> listarClientesPaginados(int pagina, int tamaño) {
        Pageable pageable = PageRequest.of(pagina, tamaño, Sort.by("id").descending());
        return clienteRepository.findAll(pageable);
    }   
}
