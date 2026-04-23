package com.tallerbicicletas.models.entities;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "bicicletas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Bicicleta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bicicleta_id")
    @Schema(description = "Identificador único de la bicicleta", example = "1")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_cliente", nullable = false)
    @JsonIgnoreProperties("bicicletas") // Ignora la lista de bicicletas de cada cliente para evitar referencias circulares
    @NotNull(message = "El cliente no puede estar vacío")
    @Schema(description = "Cliente propietario de la bicicleta")
    private Cliente cliente;

    @Column(name = "marca", nullable = false, length = 100)
    @NotBlank(message = "La marca de la bicicleta no puede estar vacía")
    @Size(min = 2, max = 100, message = "La marca debe tener entre 2 y 100 caracteres")
    @Schema(description = "Marca de la bicicleta", example = "Trek")
    private String marca;

    @Column(name = "modelo", nullable = false, length = 100)
    @NotBlank(message = "El modelo de la bicicleta no puede estar vacía")
    @Size(min = 2, max = 100, message = "El modelo debe tener entre 2 y 100 caracteres")
    @Schema(description = "Modelo de la bicicleta", example = "Domane SL 7")
    private String modelo;

    @Column(name = "color", nullable = false, length = 50)
    @NotBlank(message = "El color no puede estar vacío")
    @Size(min = 2, max = 50, message = "El color debe tener entre 2 y 50 caracteres")
    @Schema(description = "Color de la bicicleta")
    private String color;

    @Column(name = "rodado", nullable = false)
    @NotNull(message = "El rodado no puede estar vacío")
    @Schema(description = "Rodado de la bicicleta")
    private String rodado;

    @Column(name = "fecha_ingreso", nullable = false)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @NotNull(message = "Ingrese una fecha valida")
    @PastOrPresent(message = "La fecha de ingreso no puede ser futura")
    @Schema(description = "Fecha de ingreso de la bicicleta al taller", example = "2024-06-01")
    private LocalDate fechaIngreso;

    @OneToMany(mappedBy = "bicicleta")
    @JsonIgnore
    @Schema(description = "Lista de presupuestos asociados a la bicicleta")
    private List<Presupuesto> presupuestos;

    public Bicicleta (Long id) {
        this.id = id;
    }

    public void setMarca(String marca) {
        this.marca = marca == null ? null : marca.trim();
    }

    public void setModelo(String modelo) {
        this.modelo = modelo == null ? null : modelo.trim();
    }

    public void setColor(String color) {
        this.color = color == null ? null : color.trim();
    }
}
