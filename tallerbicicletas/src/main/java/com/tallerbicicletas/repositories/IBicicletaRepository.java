package com.tallerbicicletas.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tallerbicicletas.models.entities.Bicicleta;

@Repository
public interface IBicicletaRepository extends JpaRepository<Bicicleta, Long>{
    
    List<Bicicleta> findByMarcaContainingIgnoreCase(String marca);

    List<Bicicleta> findByClienteId(Long clienteId);
    
}
