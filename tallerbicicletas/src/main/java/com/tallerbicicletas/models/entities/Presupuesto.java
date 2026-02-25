package com.tallerbicicletas.models.entities;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "presupuestos")
public class Presupuesto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "presupuesto_numero")
    private Long numero;

    @Column(name = "fecha", nullable = false)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @NotNull(message = "Ingrese una fecha valida")
    private LocalDate fecha;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cliente_id", nullable = false)
    @NotNull(message = "El cliente no puede estar vacío")
    @JsonIgnoreProperties("bicicletas")
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "bicicleta_id", nullable = false)
    @NotNull(message = "La bicicleta no puede estar vacía")
    @JsonIgnoreProperties("cliente")
    private Bicicleta bicicleta;

    @Column(name = "valor_total", nullable = false)
    @NotNull(message = "El valor total no puede estar vacío")
    private Double valorTotal;

    @Column(name = "descripcion", length = 300)
    private String descripcion;

    @Column(name = "estado", nullable = false, length = 20)
    private String estado = "PENDIENTE"; // PENDIENTE, FACTURADO, ANULADO

    @OneToMany(mappedBy = "presupuesto")
    private List<Detalle> detalles;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "servicio_id")
    private Servicio servicio;

    @Column(name = "valor_servicio_aplicado")
    private Double valorServicioAplicado = 0.0;

    public void setValorTotal(Double valorTotal) {
        if (valorTotal < 0) {
            throw new IllegalArgumentException("El valor total no puede ser negativo");
        }
        double valorRedondeado = Math.round(valorTotal * 100.0) / 100.0;
        this.valorTotal = valorRedondeado;
    }

    public double calcularTotalFinal() {
        double subtotalRepuestos = (this.detalles == null) ? 0.0 : 
            this.detalles.stream()
                .mapToDouble(d -> d.getCantidadAgregada() * d.getRepuesto().getPrecioVenta())
                .sum();
        
        double manoDeObra = (this.valorServicioAplicado == null) ? 0.0 : this.valorServicioAplicado;
        
        return subtotalRepuestos + manoDeObra;
    }
}
