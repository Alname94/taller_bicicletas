package com.tallerbicicletas.models.entities;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "repuestos")
public class Repuesto {

    @Id
    @Column(name = "repuesto_codigo")
    @NotBlank(message = "El codigo del producto es obligatorio")
    @Size(min = 2, max = 20, message = "El codigo debe tener entre 2 y 20 caracteres")
    @Schema(description = "Código único del repuesto", example = "REP12345")
    private String codigo;

    @Column(name = "producto", nullable = false, length = 100)
    @NotBlank(message = "El nombre del producto no puede estar vacío")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    @Schema(description = "Nombre del producto", example = "Frenos de disco")
    private String producto;

    @Column(name = "marca", nullable = false, length = 100)
    @NotBlank(message = "La marca del repuesto no puede estar vacía")
    @Size(min = 2, max = 100, message = "La marca debe tener entre 2 y 100 caracteres")
    @Schema(description = "Marca del repuesto", example = "Shimano")
    private String marca;

    @Column(name = "color", nullable = false, length = 50)
    @NotBlank(message = "El color no puede estar vacío")
    @Size(min = 2, max = 50, message = "El color debe tener entre 2 y 50 caracteres")
    @Schema(description = "Color del repuesto")
    private String color;

    @Column(name = "precio_venta", nullable = false)
    @NotNull(message = "El precio de venta es obligatorio")
    @PositiveOrZero(message = "El precio de venta no puede ser negativo")
    @Schema(description = "Precio de venta del repuesto", example = "500.00")
    private Double precioVenta;

    @Column(name = "precio_costo", nullable = false)
    @NotNull(message = "El precio de costo no puede estar vacío")
    @PositiveOrZero(message = "El precio de costo no puede ser negativo")
    @Schema(description = "Precio de costo del repuesto", example = "300.00")
    private Double precioCosto;

    @Column(name = "stock", nullable = false)
    @NotNull(message = "El stock no puede estar vacío")
    @Min(value = 0, message = "El stock no puede ser negativo")
    @Schema(description = "Stock del repuesto", example = "10")
    private Integer stock;

    @OneToMany(mappedBy = "repuesto")
    @JsonIgnore
    @Schema(description = "Lista de detalles que incluyen este repuesto")
    private List<Detalle> detalles;

    public void setCodigo(String codigo) {
        this.codigo = (codigo != null) ? codigo.trim() : null;
    }

    public void setProducto(String producto) {
        this.producto = (producto != null) ? producto.trim() : null;
    }

    public void setPrecioVenta(Double precioVenta) {
        if (precioVenta != null) {
            this.precioVenta = Math.round(precioVenta * 100.0) / 100.0;
        }
    }

    public void setPrecioCosto(Double precioCosto) {
        if (precioCosto != null) {
            this.precioCosto = Math.round(precioCosto * 100.0) / 100.0;
        }
    }
}
