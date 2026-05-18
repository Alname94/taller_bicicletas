package com.tallerbicicletas.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tallerbicicletas.models.entities.Cliente;

@Repository
public interface IClienteRepository extends JpaRepository<Cliente, Long> {

    @Query("SELECT c FROM Cliente c WHERE " +
       "(:query IS NULL OR LOWER(c.nombre) LIKE LOWER(CONCAT('%', :query, '%'))) OR " +
       "(:query IS NULL OR LOWER(c.apellido) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Cliente> buscarPorNombreOApellido(@Param("query") String query, Pageable pageable);

    Optional<Cliente> findByDni(String dni);

    Optional<Cliente> findByEmail(String email);

    Optional<Cliente> findByTelefono(String telefono);

    boolean existsByDni(String dni);

    boolean existsByEmail(String email);
    
    boolean existsByTelefono(String telefono);

    long count();
}
