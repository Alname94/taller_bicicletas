package com.tallerbicicletas.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.tallerbicicletas.exceptions.BadRequestException;
import com.tallerbicicletas.exceptions.ResourceNotFoundException;
import com.tallerbicicletas.models.entities.Bicicleta;
import com.tallerbicicletas.models.entities.Cliente;
import com.tallerbicicletas.repositories.IBicicletaRepository;
import com.tallerbicicletas.services.interfaces.IBicicletaService;
import com.tallerbicicletas.services.interfaces.IClienteService;

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
        if (bicicleta.getCliente() == null || bicicleta.getCliente().getId() == null) {
            throw new BadRequestException("Debe seleccionar un cliente válido para la bicicleta.");
        }
        Cliente cliente = clienteService.findCliente(bicicleta.getCliente().getId());
        bicicleta.setCliente(cliente);
        return bicicletaRepository.save(bicicleta);
    }

    @Override
    public void deleteBicicleta(Long id) {
        Bicicleta bicicleta = findBicicleta(id);

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
        clienteService.findCliente(clienteId);

        List<Bicicleta> bicicletas = bicicletaRepository.findByClienteId(clienteId);
        if (bicicletas.isEmpty()) {
            throw new ResourceNotFoundException("El cliente existe, pero aún no tiene bicicletas registradas.");
        }
        return bicicletas;
    }
}
