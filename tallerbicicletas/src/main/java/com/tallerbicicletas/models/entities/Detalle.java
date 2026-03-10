package com.tallerbicicletas.models.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name="detalles")
public class Detalle {

    @EmbeddedId
    @Schema(description = "Identificador compuesto del detalle")
    private DetalleId id;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("presupuestoNumero")  
    @JoinColumn(name = "presupuesto_numero", nullable = false)
    @JsonIgnore
    @Schema(description = "Presupuesto al que pertenece el detalle")
    private Presupuesto presupuesto;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("repuestoCodigo")
    @JoinColumn(name = "repuesto_codigo", nullable = false)
    @JsonIgnoreProperties({"stock", "precioCosto"})
    @Schema(description = "Repuesto asociado al detalle")
    private Repuesto repuesto;

    @Column(name = "cantidad_agregada", nullable = false)
    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    @Schema(description = "Cantidad de repuestos agregados al detalle", example = "2")
    private Integer cantidadAgregada;
}
