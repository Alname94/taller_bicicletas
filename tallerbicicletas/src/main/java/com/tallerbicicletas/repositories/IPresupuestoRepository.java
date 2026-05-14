package com.tallerbicicletas.repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tallerbicicletas.models.entities.Presupuesto;

@Repository
public interface IPresupuestoRepository extends JpaRepository<Presupuesto, Long>{

    @Query("SELECT p FROM Presupuesto p WHERE " +
           "(:query IS NULL OR LOWER(p.cliente.nombre) LIKE LOWER(CONCAT('%', :query, '%'))) OR " +
           "(:query IS NULL OR LOWER(p.bicicleta.marca) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Presupuesto> findByClienteOrMarca(@Param("query") String query, Pageable pageable);


    List<Presupuesto> findByBicicletaId(Long bicicletaId);

    boolean existsByServicioId(Long servicioId);

    long countByEstado(String estado);

    @Query("SELECT COUNT(p) AS cantidad, COALESCE(SUM(p.valorTotal), 0.0) AS monto " +
       "FROM Presupuesto p WHERE p.fecha >= :inicioMes AND p.estado = 'FACTURADO'")
    ResumenMensual getResumenMensual(@Param("inicioMes") LocalDate inicioMes);  

    public interface ResumenMensual {
        Long getCantidad();
        Double getMonto();
    }
}
