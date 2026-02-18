package com.tallerbicicletas.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.tallerbicicletas.exceptions.BadRequestException;
import com.tallerbicicletas.exceptions.ResourceNotFoundException;
import com.tallerbicicletas.models.entities.Repuesto;
import com.tallerbicicletas.repositories.IRepuestoRepository;
import com.tallerbicicletas.services.interfaces.IRepuestoService;

public class RepuestoService implements IRepuestoService {

    @Autowired
    private IRepuestoRepository repuestoRepository;

    @Override
    public List<Repuesto> getRepuestos() {
        return repuestoRepository.findAll();
    }

    @Override
    public Repuesto saveRepuesto(Repuesto repuesto) {
        if (repuestoRepository.existsById(repuesto.getCodigo())) {
            throw new BadRequestException("El código de repuesto '" + repuesto.getCodigo() + "' ya existe.");
        }

        if (repuesto.getPrecioVenta() < repuesto.getPrecioCosto()) {
            throw new BadRequestException("El precio de venta no puede ser menor al precio de costo.");
        }

        return repuestoRepository.save(repuesto);
    }

    @Override
    public void deleteRepuesto(String codigo) {
        Repuesto repuesto = findRepuesto(codigo);

        if (repuesto.getDetalles() != null && !repuesto.getDetalles().isEmpty()) {
            throw new BadRequestException(
                    "No se puede eliminar el repuesto porque ya figura en presupuestos existentes.");
        }

        repuestoRepository.delete(repuesto);
    }

    @Override
    public Repuesto findRepuesto(String codigo) {
        return repuestoRepository.findById(codigo)
                .orElseThrow(() -> new ResourceNotFoundException("El repuesto con código '" + codigo + "' no existe."));
    }

    @Override
    public Repuesto editRepuesto(Repuesto repuesto) {
        Repuesto repuestoExistente = findRepuesto(repuesto.getCodigo());

        if (repuesto.getPrecioVenta() < repuesto.getPrecioCosto()) {
            throw new BadRequestException("El precio de venta no puede ser menor al precio de costo.");
        }

        repuestoExistente.setProducto(repuesto.getProducto());
        repuestoExistente.setMarca(repuesto.getMarca());
        repuestoExistente.setColor(repuesto.getColor());
        repuestoExistente.setPrecioVenta(repuesto.getPrecioVenta());
        repuestoExistente.setPrecioCosto(repuesto.getPrecioCosto());
        repuestoExistente.setStock(repuesto.getStock());

        return repuestoRepository.save(repuestoExistente);
    }

    @Override
    public List<Repuesto> findByProductoContainingIgnoreCaseOrMarcaContainingIgnoreCase(String producto, String marca) {
        List<Repuesto> repuestos = repuestoRepository
                .findByProductoContainingIgnoreCaseOrMarcaContainingIgnoreCase(producto, marca);
        if (repuestos.isEmpty()) {
            throw new ResourceNotFoundException("No hay repuestos que coincidan con: " + producto);
        }
        return repuestos;
    }
}
