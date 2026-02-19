package com.tallerbicicletas.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tallerbicicletas.models.entities.Repuesto;

@Repository
public interface IRepuestoRepository extends JpaRepository<Repuesto, String>{

    List<Repuesto> findByProductoContainingIgnoreCaseOrMarcaContainingIgnoreCase(String query, String query2);

    boolean existsById(String codigo);
}
