package com.tallerbicicletas.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.tallerbicicletas.exceptions.BadRequestException;
import com.tallerbicicletas.exceptions.ResourceNotFoundException;
import com.tallerbicicletas.models.entities.Bicicleta;
import com.tallerbicicletas.models.entities.Cliente;
import com.tallerbicicletas.repositories.IBicicletaRepository;
import com.tallerbicicletas.services.interfaces.IBicicletaService;
import com.tallerbicicletas.services.interfaces.IClienteService;

/**
 * Servicio encargado de la lógica de negocio para la gestión de Bicicletas.
 * Maneja la relación con Clientes y valida la integridad con Presupuestos.
 */
@Service
public class BicicletaService implements IBicicletaService {

    @Autowired
    private IBicicletaRepository bicicletaRepository;

    @Autowired
    private IClienteService clienteService;

    @Override
    public List<Bicicleta> getBicicletas() {
        return bicicletaRepository.findAll();
    }

    @Override
    public Bicicleta saveBicicleta(Bicicleta bicicleta) {
        // Validar que se haya seleccionado un cliente válido
        if (bicicleta.getCliente() == null || bicicleta.getCliente().getId() == null) {
            throw new BadRequestException("Debe seleccionar un cliente válido para la bicicleta.");
        }
        // Verificar que el cliente exista en la base de datos
        Cliente cliente = clienteService.findCliente(bicicleta.getCliente().getId());
        bicicleta.setCliente(cliente);
        return bicicletaRepository.save(bicicleta);
    }

    @Override
    public void deleteBicicleta(Long id) {
        Bicicleta bicicleta = findBicicleta(id);

        // Impedir el borrado si hay historial de presupuestos.
        // Esto evita que queden presupuestos "huérfanos" en el sistema.
        if (bicicleta.getPresupuestos() != null && !bicicleta.getPresupuestos().isEmpty()) {
            throw new BadRequestException("No se puede eliminar la bicicleta porque tiene presupuestos asociados.");
        }

        bicicletaRepository.delete(bicicleta);
    }

    @Override
    public Bicicleta findBicicleta(Long id) {
        return bicicletaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("La bicicleta con id " + id + " no existe."));
    }

    @Override
    public Bicicleta editBicicleta(Bicicleta bicicleta) {
        // Recuperar la instancia persistida para actualizar solo los campos permitidos.
        Bicicleta bicicletaExistente = findBicicleta(bicicleta.getId());

        bicicletaExistente.setMarca(bicicleta.getMarca());
        bicicletaExistente.setModelo(bicicleta.getModelo());
        bicicletaExistente.setColor(bicicleta.getColor());
        bicicletaExistente.setRodado(bicicleta.getRodado());
        bicicletaExistente.setFechaIngreso(bicicleta.getFechaIngreso());

        return bicicletaRepository.save(bicicletaExistente);
    }

    @Override
    public List<Bicicleta> findByMarcaContainingIgnoreCase(String marca) {
        List<Bicicleta> bicicletas = bicicletaRepository.findByMarcaContainingIgnoreCase(marca);
        if (bicicletas.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron bicicletas con la marca: " + marca);
        }
        return bicicletas;
    }

    @Override
    public List<Bicicleta> findByClienteId(Long clienteId) {
        // Validar primero la existencia del cliente para dar un mensaje de error más preciso.
        clienteService.findCliente(clienteId);

        List<Bicicleta> bicicletas = bicicletaRepository.findByClienteId(clienteId);
        if (bicicletas.isEmpty()) {
            throw new ResourceNotFoundException("El cliente existe, pero aún no tiene bicicletas registradas.");
        }
        return bicicletas;
    }

    @Override
    public Page<Bicicleta> listarBicicletasPaginadas(int page, int size) {
        // Paginación configurada por ID descendente para ver ingresos recientes primero.
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return bicicletaRepository.findAll(pageable);
    }   
}
