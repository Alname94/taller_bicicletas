package com.tallerbicicletas.models.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "servicios")
public class Servicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "servicio_id")
    private Long id;

    @Column(name = "nombre_servicio", nullable = false)
    private String nombre;

    @Column(name = "descripcion_servicio", length = 300)
    private String descripcion;

    @Column(name = "valor_servicio", nullable = false)
    @NotNull(message = "El valor del servicio no puede estar vacío")
    private Double valor;

    @Column(name = "activo")
    private boolean activo = true;

    public void setValor(Double valor) {
        if (valor != null && valor < 0) {
            throw new IllegalArgumentException("El valor del servicio no puede ser negativo");
        }
        this.valor = valor;
    }
}
