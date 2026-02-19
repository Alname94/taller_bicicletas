package com.tallerbicicletas.services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tallerbicicletas.exceptions.BadRequestException;
import com.tallerbicicletas.exceptions.ResourceNotFoundException;
import com.tallerbicicletas.models.entities.Bicicleta;
import com.tallerbicicletas.models.entities.Cliente;
import com.tallerbicicletas.models.entities.Presupuesto;
import com.tallerbicicletas.repositories.IPresupuestoRepository;
import com.tallerbicicletas.services.interfaces.IBicicletaService;
import com.tallerbicicletas.services.interfaces.IClienteService;
import com.tallerbicicletas.services.interfaces.IDetalleService;
import com.tallerbicicletas.services.interfaces.IPresupuestoService;

@Service
public class PresupuestoService implements IPresupuestoService {

    @Autowired
    private IPresupuestoRepository presupuestoRepository;

    @Autowired
    private IClienteService clienteService;

    @Autowired
    private IBicicletaService bicicletaService;

    @Autowired
    private IDetalleService detalleService;

    @Override
    public List<Presupuesto> getPresupuestos() {
        return presupuestoRepository.findAll();
    }

    @Override
    public Presupuesto savePresupuesto(Presupuesto presupuesto) {

        if (presupuesto.getCliente() == null || presupuesto.getCliente().getId() == null ||
                presupuesto.getBicicleta() == null || presupuesto.getBicicleta().getId() == null) {
            throw new BadRequestException("El presupuesto debe incluir un Cliente y una Bicicleta válidos.");
        }

        Cliente cliente = clienteService.findCliente(presupuesto.getCliente().getId());
        Bicicleta bicicleta = bicicletaService.findBicicleta(presupuesto.getBicicleta().getId());

        if (!bicicleta.getCliente().getId().equals(cliente.getId())) {
            throw new BadRequestException("La bicicleta con ID " + bicicleta.getId() +
                    " no pertenece al cliente " + cliente.getNombre() + " " + cliente.getApellido());
        }

        presupuesto.setCliente(cliente);
        presupuesto.setBicicleta(bicicleta);

        if (presupuesto.getFecha().isAfter(LocalDate.now())) {
            throw new BadRequestException("La fecha del presupuesto no puede ser futura.");
        }

        return presupuestoRepository.save(presupuesto);
    }

    @Override
    public Presupuesto findPresupuesto(Long numero) {
        return presupuestoRepository.findById(numero)
                .orElseThrow(() -> new ResourceNotFoundException("El presupuesto número " + numero + " no existe."));
    }

    @Override
    public void deletePresupuesto(Long numero) {
        Presupuesto presupuesto = findPresupuesto(numero);

        if (presupuesto.getEstado().equalsIgnoreCase("FACTURADO")) {
            throw new BadRequestException("No se puede eliminar un presupuesto FACTURADO.");
        }

        if (presupuesto.getDetalles() != null && !presupuesto.getDetalles().isEmpty()) {
            throw new BadRequestException("Debe eliminar los repuestos asociados antes de borrar el presupuesto.");
        }

        presupuestoRepository.delete(presupuesto);
    }

    @Override
    public Presupuesto editPresupuesto(Presupuesto presupuesto) {
        Presupuesto presupuestoExistente = findPresupuesto(presupuesto.getNumero());

        if (!presupuestoExistente.getEstado().equalsIgnoreCase("PENDIENTE")) {
            throw new BadRequestException("Solo se pueden modificar presupuestos en estado PENDIENTE. " +
                    "El estado actual es: " + presupuestoExistente.getEstado());
        }

        presupuestoExistente.setFecha(presupuesto.getFecha());
        presupuestoExistente.setDescripcion(presupuesto.getDescripcion());
        presupuestoExistente.setValorTotal(presupuesto.getValorTotal());

        return presupuestoRepository.save(presupuestoExistente);
    }

    @Override
    public List<Presupuesto> findByClienteNombreContainingIgnoreCaseOrBicicletaMarcaContainingIgnoreCase(String query,
            String query2) {
        List<Presupuesto> presupuestos = presupuestoRepository
                .findByClienteNombreContainingIgnoreCaseOrBicicletaMarcaContainingIgnoreCase(query, query2);
        if (presupuestos.isEmpty()) {
            throw new ResourceNotFoundException("No hay presupuestos que coincidan con: " + query);
        }
        return presupuestos;
    }

    @Override
    public List<Presupuesto> findByBicicletaId(Long bicicletaId) {
        List<Presupuesto> presupuestos = presupuestoRepository.findByBicicletaId(bicicletaId);
        if (presupuestos.isEmpty()) {
            throw new ResourceNotFoundException("No hay presupuestos que coincidan con la bicicleta: " + bicicletaId);
        }
        return presupuestos;
    }

    @Override
    @Transactional
    public void cambiarEstado(Long presupuestoNumero, String nuevoEstado) {
        Presupuesto presupuesto = findPresupuesto(presupuestoNumero);

        if (presupuesto.getEstado().equalsIgnoreCase("ANULADO")) {
            throw new BadRequestException("No se puede cambiar el estado de un presupuesto ya ANULADO.");
        }

        if (nuevoEstado.equalsIgnoreCase("ANULADO")) {
            detalleService.devolverStockPorAnulacion(presupuestoNumero);
        }

        presupuesto.setEstado(nuevoEstado.toUpperCase());
        presupuestoRepository.save(presupuesto);
    }
}
