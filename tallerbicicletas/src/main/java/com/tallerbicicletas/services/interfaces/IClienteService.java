package com.tallerbicicletas.services.interfaces;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;

import com.tallerbicicletas.dtos.clientes.ClienteListDTO;
import com.tallerbicicletas.models.entities.Cliente;

public interface IClienteService {

    public List<Cliente> getClientes();

    public Cliente saveCliente(Cliente cliente);

    public void deleteCliente(Long id);

    public Cliente findCliente(Long id);

    public Cliente editCliente(Cliente cliente);

    public Page <ClienteListDTO> buscarClientesDTO(String query, int page, int size);

    public Optional<Cliente> findByDni(String dni);

    public Page<ClienteListDTO> listarClientesPaginadosDTO(int page, int size);
}
