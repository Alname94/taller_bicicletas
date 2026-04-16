package com.tallerbicicletas.models.entities;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
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
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
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
    @Schema(description = "Número único del presupuesto", example = "1")
    private Long numero;

    @Column(name = "fecha", nullable = false)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @NotNull(message = "Ingrese una fecha valida")
    @PastOrPresent(message = "La fecha no puede ser futura")
    @Schema(description = "Fecha del presupuesto", example = "2024-06-15")
    private LocalDate fecha;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cliente_id", nullable = false)
    @NotNull(message = "El cliente no puede estar vacío")
    @JsonIgnoreProperties("bicicletas")
    @Schema(description = "Cliente asociado al presupuesto")
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "bicicleta_id", nullable = false)
    @NotNull(message = "La bicicleta no puede estar vacía")
    @JsonIgnoreProperties("cliente")
    @Schema(description = "Bicicleta asociada al presupuesto")
    private Bicicleta bicicleta;

    @Column(name = "valor_total", nullable = false)
    @NotNull(message = "El valor total no puede estar vacío")
    @PositiveOrZero(message = "El valor total no puede ser negativo")
    @Schema(description = "Valor total del presupuesto", example = "1500.00")
    private Double valorTotal= 0.0;

    @Column(name = "descripcion", length = 300)
    @Size(max = 300, message = "La descripción no puede superar los 300 caracteres")
    @Schema(description = "Descripción del presupuesto")
    private String descripcion;

    @Column(name = "estado", nullable = false, length = 20)
    @Pattern(regexp = "PENDIENTE|FACTURADO|ANULADO", message = "Estado no válido")
    @Schema(description = "Estado del presupuesto", example = "PENDIENTE")
    private String estado = "PENDIENTE";

    @OneToMany(mappedBy = "presupuesto")
    @Schema(description = "Lista de detalles del presupuesto")
    private List<Detalle> detalles;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "servicio_id")
    @Schema(description = "Servicio asociado al presupuesto")
    private Servicio servicio;

    // Nuevo campo para almacenar el valor del servicio aplicado al presupuesto y evitar que el valor total se vea afectado por cambios futuros en el precio del servicio
    @Column(name = "valor_servicio_aplicado")
    @PositiveOrZero(message = "El valor del servicio no puede ser negativo")
    @Schema(description = "Valor del servicio aplicado al presupuesto", example = "200.00")
    private Double valorServicioAplicado = 0.0;

    public void setValorTotal(Double valorTotal) {
        if (valorTotal != null) {
            this.valorTotal = Math.round(valorTotal * 100.0) / 100.0;
        }
    }

    // Método para calcular el valor total del presupuesto sumando el precio unitario de los repuestos y el valor del servicio aplicado
    public double calcularTotalFinal() {
    double subtotalRepuestos = (this.detalles == null) ? 0.0 : 
        this.detalles.stream()
            .mapToDouble(d -> {
                double precio = (d.getPrecioUnitario() != null) ? d.getPrecioUnitario() : 0.0;
                return d.getCantidadAgregada() * precio;
            })
            .sum();

    double manoDeObra = (this.valorServicioAplicado == null) ? 0.0 : this.valorServicioAplicado;    
    return subtotalRepuestos + manoDeObra;
}
}
