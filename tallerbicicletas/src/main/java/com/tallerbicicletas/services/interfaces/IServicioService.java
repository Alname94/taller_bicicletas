package com.tallerbicicletas.services.interfaces;

import java.util.List;

import org.springframework.data.domain.Page;

import com.tallerbicicletas.models.entities.Servicio;

public interface IServicioService {

    public List<Servicio> getServicios();

    public Servicio saveServicio(Servicio servicio);

    public Servicio findServicio(Long id);

    public void deleteServicio(Long id);
    
    public Servicio editServicio(Servicio servicio);

    public List<Servicio> getServiciosActivos();

    public List<Servicio> findByNombreContainingIgnoreCase(String nombre);

    public Page<Servicio> listarServiciosPaginados(int pagina, int tamaño);
}
