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
import com.tallerbicicletas.models.entities.Repuesto;
import com.tallerbicicletas.repositories.IRepuestoRepository;
import com.tallerbicicletas.services.interfaces.IRepuestoService;

/**
 * Servicio encargado de la gestión de inventario de repuestos.
 * Implementa reglas de validación de márgenes de ganancia y protección 
 * de integridad referencial con los detalles de presupuestos.
 */
@Service
public class RepuestoService implements IRepuestoService {

    @Autowired
    private IRepuestoRepository repuestoRepository;

    @Override
    public List<Repuesto> getRepuestos() {
        return repuestoRepository.findAll();
    }

    @Override
    public Repuesto saveRepuesto(Repuesto repuesto) {
        // Validar que el código de repuesto sea único y que el precio de venta no sea menor al precio de costo.
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

        // Impedir el borrado si el repuesto ya figura en detalles de presupuestos para proteger la integridad referencial.
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
    public List<Repuesto> findByProductoContainingIgnoreCaseOrMarcaContainingIgnoreCaseOrCodigoContainingIgnoreCase(String producto, String marca, String codigo) {
        // Búsqueda flexible por producto, marca o código, ignorando mayúsculas y minúsculas,
        List<Repuesto> repuestos = repuestoRepository
                .findByProductoContainingIgnoreCaseOrMarcaContainingIgnoreCaseOrCodigoContainingIgnoreCase(producto, marca, codigo);
        if (repuestos.isEmpty()) {
            throw new ResourceNotFoundException("No hay repuestos que coincidan con: " + producto);
        }
        return repuestos;
    }

    @Override
    public List<Repuesto> findByStockGreaterThan() {
        return repuestoRepository.findByStockGreaterThan(0);
    }

    @Override
    public Page<Repuesto> listarRepuestosPaginados(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("codigo").ascending());
        return repuestoRepository.findAll(pageable);
    }  
}
