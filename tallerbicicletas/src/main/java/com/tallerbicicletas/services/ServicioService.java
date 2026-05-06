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
import com.tallerbicicletas.models.entities.Servicio;
import com.tallerbicicletas.repositories.IPresupuestoRepository;
import com.tallerbicicletas.repositories.IServicioRepository;
import com.tallerbicicletas.services.interfaces.IServicioService;

/**
 * Servicio para la gestión integral de Servicios.
 * Implementa validaciones de unicidad de nombre y valor positivo,
 * y protege la integridad referencial con Presupuestos.
 */
@Service
public class ServicioService implements IServicioService {

    @Autowired
    private IServicioRepository servicioRepository;

    @Autowired
    private IPresupuestoRepository presupuestoRepository;

    @Override
    public List<Servicio> getServicios() {
        return servicioRepository.findAll();
    }

    @Override
    public Servicio saveServicio(Servicio servicio) {
        // Validar unicidad de nombre y valor positivo antes de guardar.
        if (servicioRepository.existsByNombreIgnoreCase(servicio.getNombre())) {
            throw new BadRequestException("El Servicio '" + servicio.getNombre() + "' ya está registrado.");
        }

        if (servicio.getValor() <= 0) {
            throw new BadRequestException("El valor del servicio debe ser mayor a 0.");
        }

        return servicioRepository.save(servicio);
    }

    @Override
    public Servicio findServicio(Long id) {
        return servicioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("El servicio con id " + id + " no existe."));
    }

    @Override
    public void deleteServicio(Long id) {
        Servicio servicio = this.findServicio(id);

        // Impedir el borrado si el servicio ya figura en presupuestos para proteger la integridad referencial.
        // Si el servicio está asociado a presupuestos, en lugar de eliminarlo, se marca como inactivo.
        if (presupuestoRepository.existsByServicioId(id)) {
            servicio.setActivo(false);
            servicioRepository.save(servicio);
        } else {
            servicioRepository.deleteById(id);
        }
    }

    @Override
    public Servicio editServicio(Servicio servicio) {
        Servicio existente = this.findServicio(servicio.getId());

        // Validar unicidad de nombre y valor positivo antes de editar.
        if (!existente.getNombre().equalsIgnoreCase(servicio.getNombre())) {
            if (servicioRepository.existsByNombreIgnoreCase(servicio.getNombre())) {
                throw new BadRequestException("Ya existe otro servicio con ese nombre.");
            }
        }

        if (servicio.getValor() <= 0) {
            throw new BadRequestException("El valor del servicio debe ser mayor a 0.");
        }

        existente.setNombre(servicio.getNombre());
        existente.setDescripcion(servicio.getDescripcion());
        existente.setValor(servicio.getValor());
        existente.setActivo(servicio.getActivo());

        return servicioRepository.save(existente);
    }

    // Método adicional para obtener solo los servicios activos, útil para listados en el frontend.
    @Override
    public List<Servicio> getServiciosActivos() {
        return servicioRepository.findByActivoTrue();
    }

    @Override
    public List<Servicio> findByNombreContainingIgnoreCase(String nombre) {
        return servicioRepository.findByNombreContainingIgnoreCase(nombre);
    }

    @Override
    public Page<Servicio> listarServiciosPaginados(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        return servicioRepository.findAll(pageable);
    }    
}
