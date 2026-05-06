package com.tallerbicicletas.services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tallerbicicletas.dtos.presupuestos.PresupuestoListDTO;
import com.tallerbicicletas.exceptions.BadRequestException;
import com.tallerbicicletas.exceptions.ResourceNotFoundException;
import com.tallerbicicletas.models.entities.Bicicleta;
import com.tallerbicicletas.models.entities.Cliente;
import com.tallerbicicletas.models.entities.Presupuesto;
import com.tallerbicicletas.models.entities.Servicio;
import com.tallerbicicletas.repositories.IPresupuestoRepository;
import com.tallerbicicletas.services.interfaces.IBicicletaService;
import com.tallerbicicletas.services.interfaces.IClienteService;
import com.tallerbicicletas.services.interfaces.IDetalleService;
import com.tallerbicicletas.services.interfaces.IPresupuestoService;
import com.tallerbicicletas.services.interfaces.IServicioService;

/**
 * Servicio principal que orquestra la gestión de presupuestos.
 * Maneja el ciclo de vida completo: creación, asignación de servicios, 
 * cambios de estado y cálculos de montos finales.
 */
@Service
public class PresupuestoService implements IPresupuestoService {

    @Autowired
    private IPresupuestoRepository presupuestoRepository;

    @Autowired
    private IClienteService clienteService;

    @Autowired
    private IBicicletaService bicicletaService;

    @Autowired
    private IDetalleService detalleService;

    @Autowired
    private IServicioService servicioService;

    @Override
    public List<Presupuesto> getPresupuestos() {
        return presupuestoRepository.findAll();
    }

    @Override
    public Presupuesto savePresupuesto(Presupuesto presupuesto) {

        // Validar que el presupuesto tenga un cliente y una bicicleta
        if (presupuesto.getCliente() == null || presupuesto.getCliente().getId() == null ||
                presupuesto.getBicicleta() == null || presupuesto.getBicicleta().getId() == null) {
            throw new BadRequestException("El presupuesto debe incluir un Cliente y una Bicicleta válidos.");
        }

        Cliente cliente = clienteService.findCliente(presupuesto.getCliente().getId());
        Bicicleta bicicleta = bicicletaService.findBicicleta(presupuesto.getBicicleta().getId());

        // Validar que la bicicleta pertenezca al cliente indicado para mantener la integridad referencial.
        if (!bicicleta.getCliente().getId().equals(cliente.getId())) {
            throw new BadRequestException("La bicicleta con ID " + bicicleta.getId() +
                    " no pertenece al cliente " + cliente.getNombre() + " " + cliente.getApellido());
        }

        presupuesto.setCliente(cliente);
        presupuesto.setBicicleta(bicicleta);

        // Inicialización de valores por defecto para nuevos presupuestos.
        if (presupuesto.getEstado() == null) {
            presupuesto.setEstado("PENDIENTE");
        }

        if (presupuesto.getDescripcion() == null) {
            presupuesto.setDescripcion("");
        }

        // Si se asigna un servicio al presupuesto, se valida que exista y se asigna su
        // valor al presupuesto para evitar que cambios futuros en el precio del
        // servicio afecten el valor total del presupuesto
        if (presupuesto.getServicio() != null && presupuesto.getServicio().getId() != null) {
            Servicio s = servicioService.findServicio(presupuesto.getServicio().getId());
            presupuesto.setServicio(s);
            presupuesto.setValorServicioAplicado(s.getValor());
        } else {
            presupuesto.setValorServicioAplicado(0.0);
        }

        // Cálculo dinámico del total (Servicio + sumatoria de Repuestos).
        presupuesto.setValorTotal(presupuesto.calcularTotalFinal());

        if (presupuesto.getFecha().isAfter(LocalDate.now())) {
            throw new BadRequestException("La fecha del presupuesto no puede ser futura.");
        }

        return presupuestoRepository.save(presupuesto);
    }

    @Override
    public Presupuesto findPresupuesto(Long numero) {
        return presupuestoRepository.findById(numero)
                .orElseThrow(() -> new ResourceNotFoundException("El presupuesto número " + numero + " no existe."));
    }

    @Override
    public void deletePresupuesto(Long numero) {
        Presupuesto presupuesto = findPresupuesto(numero);

        // Solo se permiten eliminar presupuestos que estén en estado PENDIENTE y no tengan detalles asociados.
        if (presupuesto.getEstado().equalsIgnoreCase("FACTURADO")
                || presupuesto.getEstado().equalsIgnoreCase("ANULADO")) {
            throw new BadRequestException("No se puede eliminar un presupuesto en estado " + presupuesto.getEstado());
        }

        if (presupuesto.getDetalles() != null && !presupuesto.getDetalles().isEmpty()) {
            throw new BadRequestException("Debe eliminar los repuestos asociados antes de borrar el presupuesto.");
        }

        presupuestoRepository.delete(presupuesto);
    }

    @Override
    public Presupuesto editPresupuesto(Presupuesto presupuesto) {
        Presupuesto presupuestoExistente = findPresupuesto(presupuesto.getNumero());

        // Solo se permiten editar presupuestos que estén en estado PENDIENTE para evitar inconsistencias en presupuestos ya facturados o anulados.
        if (!presupuestoExistente.getEstado().equalsIgnoreCase("PENDIENTE")) {
            throw new BadRequestException("Solo se pueden modificar presupuestos en estado PENDIENTE. " +
                    "El estado actual es: " + presupuestoExistente.getEstado());
        }

        if (presupuesto.getServicio() != null) {
            Servicio nuevoServicio = servicioService.findServicio(presupuesto.getServicio().getId());
            presupuestoExistente.setServicio(nuevoServicio);
            presupuestoExistente.setValorServicioAplicado(nuevoServicio.getValor());
        }

        presupuestoExistente.setFecha(presupuesto.getFecha());
        presupuestoExistente.setDescripcion(presupuesto.getDescripcion());
        presupuestoExistente.setValorTotal(presupuestoExistente.calcularTotalFinal());

        return presupuestoRepository.save(presupuestoExistente);
    }

    @Override
    public List<Presupuesto> findByClienteNombreContainingIgnoreCaseOrBicicletaMarcaContainingIgnoreCase(String query,
            String query2) {
        List<Presupuesto> presupuestos = presupuestoRepository
                .findByClienteNombreContainingIgnoreCaseOrBicicletaMarcaContainingIgnoreCase(query, query2);
        if (presupuestos.isEmpty()) {
            throw new ResourceNotFoundException("No hay presupuestos que coincidan con: " + query);
        }
        return presupuestos;
    }

    @Override
    public List<Presupuesto> findByBicicletaId(Long bicicletaId) {
        List<Presupuesto> presupuestos = presupuestoRepository.findByBicicletaId(bicicletaId);
        if (presupuestos.isEmpty()) {
            throw new ResourceNotFoundException("No hay presupuestos que coincidan con la bicicleta: " + bicicletaId);
        }
        return presupuestos;
    }

    // Método para cambiar el estado de un presupuesto, 
    // con validaciones para evitar cambios no permitidos y devolver el stock
    // de los repuestos al anular un presupuesto
    @Override
    @Transactional
    public void cambiarEstado(Long presupuestoNumero, String nuevoEstado) {
        Presupuesto presupuesto = findPresupuesto(presupuestoNumero);

        if (presupuesto.getEstado().equalsIgnoreCase("ANULADO")) {
            throw new BadRequestException("No se puede cambiar el estado de un presupuesto ya ANULADO.");
        }

        if (nuevoEstado.equalsIgnoreCase("ANULADO")) {
            detalleService.devolverStockPorAnulacion(presupuestoNumero);
        }

        presupuesto.setEstado(nuevoEstado.toUpperCase());
        presupuestoRepository.save(presupuesto);
    }


    // Método para asignar un servicio a un presupuesto existente y actualizar su valor total
    @Override
    @Transactional
    public void asignarServicio(Long presupuestoId, Long servicioId) {
        Presupuesto p = findPresupuesto(presupuestoId);

        if (!p.getEstado().equalsIgnoreCase("PENDIENTE")) {
            throw new BadRequestException("No se puede cambiar el servicio de un presupuesto " + p.getEstado());
        }

        Servicio s = servicioService.findServicio(servicioId);

        p.setServicio(s);
        p.setValorServicioAplicado(s.getValor());
        p.setValorTotal(p.calcularTotalFinal());

        presupuestoRepository.save(p);
    }

    @Override
    public Page<Presupuesto> listarPresupuestosPaginados(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("numero").descending());
        return presupuestoRepository.findAll(pageable);
    }

    /**
     * Retorna una página de DTOs simplificados para la visualización en tablas.
     * Concatena información de cliente y bicicleta en un solo String desde el servidor.
     */
    @Override
    public Page<PresupuestoListDTO> listarPresupuestosPaginadosDTO(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("numero").descending());
        Page<Presupuesto> presupuestosPage = presupuestoRepository.findAll(pageable);

        return presupuestosPage.map(p -> new PresupuestoListDTO(
                p.getNumero(),
                p.getFecha().toString(),
                String.format("#%d - %s %s", p.getCliente().getId(), p.getCliente().getNombre(),
                        p.getCliente().getApellido()),
                String.format("#%d - %s %s", p.getBicicleta().getId(), p.getBicicleta().getMarca(),
                        p.getBicicleta().getModelo()),
                p.getValorTotal(),
                p.getEstado()));
    }
}
