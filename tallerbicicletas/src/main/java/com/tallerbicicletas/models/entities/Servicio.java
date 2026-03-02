package com.tallerbicicletas.models.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
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

    @Column(name = "nombre_servicio", nullable = false, unique = true)
    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(min = 3, max = 50, message = "El nombre debe tener entre 3 y 50 caracteres")
    private String nombre;

    @Column(name = "descripcion_servicio", length = 300)
    @Size(max = 300, message = "La descripción no puede superar los 300 caracteres")
    private String descripcion;

    @Column(name = "valor_servicio", nullable = false)
    @NotNull(message = "El valor del servicio no puede estar vacío")
    @Positive(message = "El valor del servicio debe ser mayor a cero")
    private Double valor;

    @Column(name = "activo")
    private boolean activo = true;
}
