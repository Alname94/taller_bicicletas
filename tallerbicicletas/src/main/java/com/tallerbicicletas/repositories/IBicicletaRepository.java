package com.tallerbicicletas.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tallerbicicletas.models.entities.Bicicleta;

public interface IBicicletaRepository extends JpaRepository<Bicicleta, Long>{
    
    List<Bicicleta> findByMarcaContainingIgnoreCase(String marca);

    List<Bicicleta> findByClienteId(Long clienteId);
    
}
