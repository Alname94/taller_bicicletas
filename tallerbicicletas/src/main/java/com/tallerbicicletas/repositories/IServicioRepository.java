package com.tallerbicicletas.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tallerbicicletas.models.entities.Servicio;

@Repository
public interface IServicioRepository extends JpaRepository<Servicio, Long> {

    Optional<Servicio> findByNombreContainingIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCase(String nombre);

    List<Servicio> findByActivoTrue();
}
