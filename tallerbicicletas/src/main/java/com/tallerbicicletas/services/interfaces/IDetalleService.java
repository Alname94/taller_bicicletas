package com.tallerbicicletas.services.interfaces;

import java.util.List;

import com.tallerbicicletas.models.entities.Detalle;

public interface IDetalleService {

    public List<Detalle> getDetalles();

    public Detalle saveDetalle(Detalle detalle);

    public void deleteDetalle(Long presupuestoNumero, String repuestoCodigo);

    public Detalle findDetalle(Long presupuestoNumero, String repuestoCodigo);

    public List<Detalle> findByPresupuestoNumero(Long presupuestoNumero);
}
