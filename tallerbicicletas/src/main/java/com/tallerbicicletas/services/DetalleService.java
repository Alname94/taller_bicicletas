package com.tallerbicicletas.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.tallerbicicletas.exceptions.BadRequestException;
import com.tallerbicicletas.exceptions.ResourceNotFoundException;
import com.tallerbicicletas.models.entities.Detalle;
import com.tallerbicicletas.models.entities.DetalleId;
import com.tallerbicicletas.models.entities.Presupuesto;
import com.tallerbicicletas.models.entities.Repuesto;
import com.tallerbicicletas.repositories.IDetalleRepository;
import com.tallerbicicletas.repositories.IPresupuestoRepository;
import com.tallerbicicletas.services.interfaces.IDetalleService;
import com.tallerbicicletas.services.interfaces.IRepuestoService;

public class DetalleService implements IDetalleService {

    @Autowired
    private IDetalleRepository detalleRepository;

    @Autowired
    private IRepuestoService repuestoService;

    @Autowired
    private IPresupuestoRepository presupuestoRepository;

    @Override
    public List<Detalle> getDetalles() {
        return detalleRepository.findAll();
    }

    @Override
    @Transactional
    public Detalle saveDetalle(Detalle detalle) {
        Presupuesto presupuesto = presupuestoRepository.findById(detalle.getId().getPresupuestoNumero())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "El presupuesto " + detalle.getId().getPresupuestoNumero() + " no existe."));

        if (!"PENDIENTE".equalsIgnoreCase(presupuesto.getEstado())) {
            throw new BadRequestException(
                    "No se pueden agregar repuestos a un presupuesto con estado: " + presupuesto.getEstado());
        }

        Repuesto repuesto = repuestoService.findRepuesto(detalle.getId().getRepuestoCodigo());

        if (repuesto.getStock() < detalle.getCantidadAgregada()) {
            throw new BadRequestException("Stock insuficiente para '" + repuesto.getProducto() +
                    "'. Disponible: " + repuesto.getStock());
        }

        repuesto.setStock(repuesto.getStock() - detalle.getCantidadAgregada());
        repuestoService.editRepuesto(repuesto);

        detalle.setRepuesto(repuesto);
        detalle.setPresupuesto(presupuesto);

        return detalleRepository.save(detalle);
    }

    @Override
    @Transactional
    public void deleteDetalle(Long presupuestoNumero, String repuestoCodigo) {
       
        DetalleId id = new DetalleId(presupuestoNumero, repuestoCodigo);

        Detalle detalle = detalleRepository.findById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException("El detalle no existe para ese presupuesto y repuesto."));

        if (!"PENDIENTE".equalsIgnoreCase(detalle.getPresupuesto().getEstado())) {
            throw new BadRequestException("No se puede eliminar el repuesto porque el presupuesto ya está "
                    + detalle.getPresupuesto().getEstado());
        }

        // Devolver stock
        Repuesto repuesto = detalle.getRepuesto();
        repuesto.setStock(repuesto.getStock() + detalle.getCantidadAgregada());
        repuestoService.editRepuesto(repuesto);

        detalleRepository.delete(detalle);

    }

    @Override
    public Detalle findDetalle(Long presupuestoNumero, String repuestoCodigo) {
        DetalleId id = new DetalleId(presupuestoNumero, repuestoCodigo);

        return detalleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el detalle solicitado."));
    }

    @Override
    public List<Detalle> findByIdPresupuestoNumero(Long presupuestoNumero) {
        List<Detalle> detalles = detalleRepository.findByIdPresupuestoNumero(presupuestoNumero);
        if (detalles.isEmpty()) {
            throw new ResourceNotFoundException(
                    "No hay detalles que coincidan con el presupuesto N°: " + presupuestoNumero);
        }
        return detalles;
    }

    @Override
    @Transactional
    public void devolverStockPorAnulacion(Long presupuestoNumero) {
        List<Detalle> detalles = detalleRepository.findByIdPresupuestoNumero(presupuestoNumero);

        for (Detalle d : detalles) {
            Repuesto r = d.getRepuesto();
            r.setStock(r.getStock() + d.getCantidadAgregada());
            repuestoService.editRepuesto(r);
        }
    }
}
