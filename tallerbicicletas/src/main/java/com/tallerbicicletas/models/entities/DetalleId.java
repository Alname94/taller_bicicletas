package com.tallerbicicletas.models.entities;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// bloque de codigo que representa la PK compuesta.
// Embeddable para poder utilizar dentro de otra entidad 
// Serializable para que los objetos puedan ser serializados
@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DetalleId implements Serializable{

    @Column(name = "presupuesto_numero")
    private Long presupuestoNumero;

    @Column(name = "repuesto_codigo")
    private String repuestoCodigo;
}
