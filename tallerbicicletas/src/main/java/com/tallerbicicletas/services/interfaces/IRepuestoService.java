package com.tallerbicicletas.services.interfaces;

import java.util.List;

import org.springframework.data.domain.Page;

import com.tallerbicicletas.models.entities.Repuesto;

public interface IRepuestoService {

    public List<Repuesto> getRepuestos();

    public Repuesto saveRepuesto(Repuesto repuesto);

    public void deleteRepuesto(String codigo);

    public Repuesto findRepuesto(String codigo);

    public Repuesto editRepuesto(Repuesto repuesto);

    List<Repuesto> findByProductoContainingIgnoreCaseOrMarcaContainingIgnoreCaseOrCodigoContainingIgnoreCase(String producto, String marca, String codigo);

    List<Repuesto> findByStockGreaterThan();

    public Page<Repuesto> listarRepuestosPaginados(int pagina, int tamaño);
}
