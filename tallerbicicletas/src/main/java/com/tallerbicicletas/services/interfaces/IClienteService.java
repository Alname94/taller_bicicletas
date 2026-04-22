package com.tallerbicicletas.services.interfaces;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;

import com.tallerbicicletas.models.entities.Cliente;

public interface IClienteService {

    public List<Cliente> getClientes();

    public Cliente saveCliente(Cliente cliente);

    public void deleteCliente(Long id);

    public Cliente findCliente(Long id);

    public Cliente editCliente(Cliente cliente);

    public List<Cliente> findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCase(String nombre, String apellido);

    public Optional<Cliente> findByDni(String dni);

    public Page<Cliente> listarClientesPaginados(int pagina, int tamaño);
}
