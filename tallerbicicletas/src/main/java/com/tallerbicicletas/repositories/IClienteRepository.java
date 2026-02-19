package com.tallerbicicletas.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tallerbicicletas.models.entities.Cliente;

@Repository
public interface IClienteRepository extends JpaRepository<Cliente, Long> {

    List<Cliente> findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCase(String nombre, String apellido);

    Optional<Cliente> findByDni(String dni);

    Optional<Cliente> findByEmail(String email);

    Optional<Cliente> findByTelefono(String telefono);

    boolean existsByDni(String dni);

    boolean existsByEmail(String email);
    
    boolean existsByTelefono(String telefono);
}
