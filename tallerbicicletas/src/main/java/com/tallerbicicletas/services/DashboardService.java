package com.tallerbicicletas.services;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tallerbicicletas.repositories.IClienteRepository;
import com.tallerbicicletas.repositories.IPresupuestoRepository;
import com.tallerbicicletas.repositories.IPresupuestoRepository.ResumenMensual;
import com.tallerbicicletas.services.interfaces.IDashboardService;

@Service
public class DashboardService implements IDashboardService {

    @Autowired
    private IPresupuestoRepository presupuestoRepo;
    @Autowired
    private IClienteRepository clienteRepo;

    @Override
    public Map<String, Object> getSummaryStats() {
        Map<String, Object> stats = new HashMap<>();
        LocalDate inicioMes = LocalDate.now().withDayOfMonth(1);

        long pendientes = presupuestoRepo.countByEstado("PENDIENTE");

        ResumenMensual resumen = presupuestoRepo.getResumenMensual(inicioMes);

        Long totalClientes = clienteRepo.count();

        stats.put("presupuestosPendientes", pendientes);
        stats.put("cantidadPresupuestosMes", resumen != null ? resumen.getCantidad() : 0L);
        stats.put("montoTotalMes", resumen != null ? resumen.getMonto() : 0.0);
        stats.put("totalClientes", totalClientes);

        return stats;
    }
}
