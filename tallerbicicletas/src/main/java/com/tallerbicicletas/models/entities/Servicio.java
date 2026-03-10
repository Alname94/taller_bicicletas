package com.tallerbicicletas.models.entities;

import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description = "Identificador único del servicio", example = "1")
    private Long id;

    @Column(name = "nombre_servicio", nullable = false, unique = true)
    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(min = 3, max = 50, message = "El nombre debe tener entre 3 y 50 caracteres")
    @Schema(description = "Nombre del servicio", example = "Cambio de cadena")
    private String nombre;

    @Column(name = "descripcion_servicio", length = 300)
    @Size(max = 300, message = "La descripción no puede superar los 300 caracteres")
    @Schema(description = "Descripción del servicio", example = "Incluye cambio de cadena, ajuste de cambios y lubricación")
    private String descripcion;

    @Column(name = "valor_servicio", nullable = false)
    @NotNull(message = "El valor del servicio no puede estar vacío")
    @Positive(message = "El valor del servicio debe ser mayor a cero")
    @Schema(description = "Valor del servicio", example = "800.00")
    private Double valor;

    @Column(name = "activo")
    @Schema(description = "Indica si el servicio está activo o inactivo", example = "true")
    private boolean activo = true;
}
