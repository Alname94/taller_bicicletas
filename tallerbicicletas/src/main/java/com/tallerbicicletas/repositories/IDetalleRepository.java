package com.tallerbicicletas.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tallerbicicletas.models.entities.Detalle;
import com.tallerbicicletas.models.entities.DetalleId;

public interface IDetalleRepository extends JpaRepository<Detalle, DetalleId>{
    
    List<Detalle> findByIdPresupuestoNumero(Long presupuestoNumero);
}
