package com.tallerbicicletas.services.interfaces;

import java.util.List;
import java.util.Optional;

import com.tallerbicicletas.models.entities.Cliente;

public interface IClienteService {

    public List<Cliente> getClientes();

    public Cliente saveCliente(Cliente cliente);

    public void deleteCliente(Long id);

    public Cliente findCliente(Long id);

    public Cliente editCliente(Cliente cliente);

    public List<Cliente> findByNombreContainingIgnoreCase(String nombre);

    public Optional<Cliente> findByDni(String dni);
}
