package com.tallerbicicletas.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tallerbicicletas.models.entities.Repuesto;

public interface IRepuestoRepository extends JpaRepository<Repuesto, String>{

    List<Repuesto> findByProductoContainingIgnoreCaseOrMarcaContainingIgnoreCase(String query, String query2);

    boolean existsById(String codigo);
}
