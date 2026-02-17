package com.tallerbicicletas.services.interfaces;

import java.util.List;

import com.tallerbicicletas.models.entities.Presupuesto;

public interface IPresupuestoService {

    public List<Presupuesto> getPresupuestos();

    public Presupuesto savePresupuesto(Presupuesto presupuesto);

    public Presupuesto findPresupuestoById(Long numero);

    public void deletePresupuesto(Long numero);

    public Presupuesto editPresupuesto(Presupuesto presupuesto);

    public List<Presupuesto> findByClienteNombreContainingIgnoreCaseOrBicicletaMarcaContainingIgnoreCase(String query, String query2);

    public List<Presupuesto> findByBicicletaId(Long bicicletaId);
}
