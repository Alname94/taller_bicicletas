package com.tallerbicicletas.models.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
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
    private DetalleId id;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("presupuestoNumero")  
    @JoinColumn(name = "presupuesto_numero", nullable = false)
    @JsonIgnore
    private Presupuesto presupuesto;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("repuestoCodigo")
    @JoinColumn(name = "repuesto_codigo", nullable = false)
    @JsonIgnoreProperties({"stock", "precioCosto"})
    private Repuesto repuesto;

    @Column(name = "cantidad_agregada", nullable = false)
    private int cantidadAgregada;

    public void setCantidadAgregada(int cantidadAgregada) {
        if(cantidadAgregada<0) {
            throw new IllegalArgumentException("La cantidad no puede ser negativa");
        }
        this.cantidadAgregada = cantidadAgregada;
    }
}
