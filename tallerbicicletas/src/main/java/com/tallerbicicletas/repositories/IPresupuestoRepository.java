package com.tallerbicicletas.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tallerbicicletas.models.entities.Presupuesto;

public interface IPresupuestoRepository extends JpaRepository<Presupuesto, Long>{

    List<Presupuesto> findByClienteNombreContainingIgnoreCaseOrBicicletaMarcaContainingIgnoreCase(String nombre, String marca);

    List<Presupuesto> findByBicicletaId(Long bicicletaId);
}
