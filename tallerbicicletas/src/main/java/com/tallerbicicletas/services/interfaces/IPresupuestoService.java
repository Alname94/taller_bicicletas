package com.tallerbicicletas.services.interfaces;

import java.util.List;

import org.springframework.data.domain.Page;

import com.tallerbicicletas.dtos.presupuestos.PresupuestoListDTO;
import com.tallerbicicletas.models.entities.Presupuesto;

public interface IPresupuestoService {

    public List<Presupuesto> getPresupuestos();

    public Presupuesto savePresupuesto(Presupuesto presupuesto);

    public Presupuesto findPresupuesto(Long numero);

    public void deletePresupuesto(Long numero);

    public Presupuesto editPresupuesto(Presupuesto presupuesto);

    public Page<PresupuestoListDTO> buscarPresupuestosDTO(String query, int page, int size);

    public void cambiarEstado (Long numero, String nuevoEstado);

    public void asignarServicio(Long presupuestoId, Long servicioId);

    public Page<Presupuesto> listarPresupuestosPaginados(int page, int size);

    public Page<PresupuestoListDTO> listarPresupuestosPaginadosDTO(int page, int size);

}
