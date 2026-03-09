package com.tallerbicicletas;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.tallerbicicletas.config.SecurityConfig;
import com.tallerbicicletas.models.entities.Bicicleta;
import com.tallerbicicletas.models.entities.Cliente;
import com.tallerbicicletas.models.entities.Detalle;
import com.tallerbicicletas.models.entities.DetalleId;
import com.tallerbicicletas.models.entities.Presupuesto;
import com.tallerbicicletas.models.entities.Repuesto;
import com.tallerbicicletas.services.interfaces.IBicicletaService;
import com.tallerbicicletas.services.interfaces.IClienteService;
import com.tallerbicicletas.services.interfaces.IDetalleService;
import com.tallerbicicletas.services.interfaces.IPresupuestoService;
import com.tallerbicicletas.services.interfaces.IRepuestoService;

@SpringBootTest
@ActiveProfiles("test") // Indica que use application-test.properties
@Transactional // Revierte los cambios en la DB al finalizar cada test
@Import(SecurityConfig.class)
@WithMockUser(username = "admin", roles = {"ADMIN"})
public class PresupuestoIntegrationTest {

    @Autowired
    private IPresupuestoService presupuestoService;

    @Autowired
    private IClienteService clienteService;

    @Autowired
    private IRepuestoService repuestoService;

    @Autowired
    private IBicicletaService bicicletaService;

    @Autowired
    private IDetalleService detalleService;

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void flujoCompleto_DebeAfectarStockYTotales_EnBaseDeDatosReal() {
        // --- 1. Preparación de datos reales ---

        // Guardamos un Cliente
        Cliente cliente = new Cliente(null, "Carlos", "Integracion", "44555666", "112234567", "carlos@test.com");
        cliente = clienteService.saveCliente(cliente);

        // Guardamos una Bicicleta vinculada
        Bicicleta bici = new Bicicleta(null, cliente, "Vairo", "XR", "Negro", "29", LocalDate.now(), new ArrayList<>());
        bici = bicicletaService.saveBicicleta(bici);

        // Guardamos un Repuesto con stock inicial de 10
        Repuesto repuesto = new Repuesto();
        repuesto.setCodigo("CAD-TEST");
        repuesto.setProducto("Cadena Shimano");
        repuesto.setMarca("Shimano");
        repuesto.setColor("Plata");
        repuesto.setPrecioCosto(5000.0);
        repuesto.setPrecioVenta(8000.0);
        repuesto.setStock(10);
        repuestoService.saveRepuesto(repuesto);

        // --- 2. Creamos un Presupuesto y agregamos un Repuesto(detalle) ---

        // Creamos el Presupuesto base
        Presupuesto p = new Presupuesto();
        p.setFecha(LocalDate.now());
        p.setCliente(cliente);
        p.setBicicleta(bici);
        p.setEstado("PENDIENTE");
        p = presupuestoService.savePresupuesto(p);

        // Creamos el Detalle
        DetalleId detalleId = new DetalleId(p.getNumero(), repuesto.getCodigo());
        Detalle detalle = new Detalle();
        detalle.setId(detalleId);
        detalle.setCantidadAgregada(3); // Vamos a llevar 3 cadenas

        detalleService.saveDetalle(detalle);

        // --- 3. VERIFICACIONES ---
        Presupuesto presupuestoEnDB = presupuestoService.findPresupuesto(p.getNumero());

        // A. Verificamos stock
        Repuesto repuestoEnDB = repuestoService.findRepuesto("CAD-TEST");
        assertEquals(7, repuestoEnDB.getStock());

        // B. Verificamos el total
        assertEquals(24000.0, presupuestoEnDB.getValorTotal(), "El valor total debería ser 24000.0");
    }
}
