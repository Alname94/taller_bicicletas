package com.tallerbicicletas.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tallerbicicletas.exceptions.BadRequestException;
import com.tallerbicicletas.exceptions.ResourceNotFoundException;
import com.tallerbicicletas.models.entities.Servicio;
import com.tallerbicicletas.repositories.IPresupuestoRepository;
import com.tallerbicicletas.repositories.IServicioRepository;
import com.tallerbicicletas.services.interfaces.IServicioService;

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

        if (!existente.getNombre().equalsIgnoreCase(servicio.getNombre())) {
            if (servicioRepository.existsByNombreIgnoreCase(servicio.getNombre())) {
                throw new BadRequestException("Ya existe otro servicio con ese nombre.");
            }
        }

        existente.setNombre(servicio.getNombre());
        existente.setDescripcion(servicio.getDescripcion());
        existente.setValor(servicio.getValor());

        return servicioRepository.save(existente);
    }

    @Override
    public List<Servicio> getServiciosActivos() {
        return servicioRepository.findByActivoTrue();
    }
}
