package com.tallerbicicletas.services.interfaces;

import java.util.List;

import com.tallerbicicletas.models.entities.Servicio;

public interface IServicioService {

    public List<Servicio> getServicios();

    public Servicio saveServicio(Servicio servicio);

    public Servicio findServicio(Long id);

    public void deleteServicio(Long id);
    
    public Servicio editServicio(Servicio servicio);

    public List<Servicio> getServiciosActivos();

    public List<Servicio> findByNombreContainingIgnoreCase(String nombre);
}
