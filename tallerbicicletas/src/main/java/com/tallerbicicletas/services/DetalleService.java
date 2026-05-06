package com.tallerbicicletas.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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

// Servicio para la gestión integral de Detalles de Presupuestos.
// Implementa validaciones de stock, protección de integridad referencial con Repuestos y Presupuestos,
// y actualiza automáticamente el valor total del presupuesto al agregar o eliminar detalles.
@Service
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

    /**
     * Registra un nuevo repuesto en el presupuesto o incrementa la cantidad si ya existe.
     * Realiza validaciones de estado de presupuesto y suficiencia de stock.
     */
    @Override
    @Transactional
    public Detalle saveDetalle(Detalle detalle) {
        // Validar que el presupuesto exista y esté en estado "PENDIENTE" para permitir agregar detalles.
        Presupuesto presupuesto = presupuestoRepository.findById(detalle.getId().getPresupuestoNumero())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "El presupuesto " + detalle.getId().getPresupuestoNumero() + " no existe."));

        if (!"PENDIENTE".equalsIgnoreCase(presupuesto.getEstado())) {
            throw new BadRequestException(
                    "No se pueden agregar repuestos a un presupuesto con estado: " + presupuesto.getEstado());
        }

        Repuesto repuesto = repuestoService.findRepuesto(detalle.getId().getRepuestoCodigo());

        // Validar que el stock sea suficiente para la cantidad que se desea agregar.
        if (repuesto.getStock() < detalle.getCantidadAgregada()) {
            throw new BadRequestException("Stock insuficiente para '" + repuesto.getProducto() +
                    "'. Disponible: " + repuesto.getStock());
        }

        Optional<Detalle> detalleExistente = detalleRepository.findById(detalle.getId());
        Detalle detalleFinal;

        // Si el detalle ya existe para ese presupuesto y repuesto, se incrementa la cantidad y se actualiza el precio unitario.
        // Si no existe, se crea un nuevo detalle y se asocia al presupuesto.
        if (detalleExistente.isPresent()) {
            detalleFinal = detalleExistente.get();
            detalleFinal.setCantidadAgregada(detalleFinal.getCantidadAgregada() + detalle.getCantidadAgregada());

            detalleFinal.setPrecioUnitario(repuesto.getPrecioVenta());
        } else {
            detalle.setRepuesto(repuesto);
            detalle.setPresupuesto(presupuesto);
            detalle.setPrecioUnitario(repuesto.getPrecioVenta());
            detalleFinal = detalle;

            if (presupuesto.getDetalles() == null) {
                presupuesto.setDetalles(new ArrayList<>());
            }
            presupuesto.getDetalles().add(detalleFinal);
        }

        detalleFinal.calcularSubtotal();

        // Actualizar el stock del repuesto restando la cantidad agregada al detalle.
        repuesto.setStock(repuesto.getStock() - detalle.getCantidadAgregada());
        repuestoService.editRepuesto(repuesto);

        Detalle detalleGuardado = detalleRepository.save(detalleFinal);

        // Actualizar el valor total del presupuesto después de agregar o actualizar el detalle.
        actualizarTotal(presupuesto.getNumero());

        return detalleGuardado;
    }

    // Elimina un detalle específico de un presupuesto, devuelve el stock del repuesto y actualiza el total del presupuesto.
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

        // Devolver el stock del repuesto sumando la cantidad que se estaba utilizando en el detalle.
        Repuesto repuesto = detalle.getRepuesto();
        repuesto.setStock(repuesto.getStock() + detalle.getCantidadAgregada());
        repuestoService.editRepuesto(repuesto);

        Presupuesto presupuesto = detalle.getPresupuesto();
        presupuesto.getDetalles().remove(detalle);

        detalleRepository.delete(detalle);
        actualizarTotal(presupuestoNumero);
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

    // Método para devolver el stock de los repuestos al anular un presupuesto
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

    // Método privado para actualizar el valor total del presupuesto después de
    // agregar o eliminar un detalle
    private void actualizarTotal(Long numero) {
        Presupuesto p = presupuestoRepository.findById(numero).get();
        p.setValorTotal(p.calcularTotalFinal());
        presupuestoRepository.save(p);
    }
}
