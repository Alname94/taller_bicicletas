package com.tallerbicicletas.models.entities;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    @NotBlank(message = "El codigo del producto no puede estar vacío")
    private String codigo;

    @Column(name = "producto", nullable = false, length = 100)
    @NotBlank(message = "El nombre del producto no puede estar vacío")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String producto;

    @Column(name = "marca", nullable = false, length = 100)
    @NotBlank(message = "La marca del repuesto no puede estar vacía")
    @Size(min = 2, max = 100, message = "La marca debe tener entre 2 y 100 caracteres")
    private String marca;

    @Column(name = "color", nullable = false, length = 50)
    @NotBlank(message = "El color no puede estar vacío")
    @Size(min = 2, max = 50, message = "El color debe tener entre 2 y 50 caracteres")
    private String color;

    @Column(name = "precio_venta", nullable = false)
    @NotNull(message = "El precio de venta no puede estar vacío")
    private Double precioVenta;

    @Column(name = "precio_costo", nullable = false)
    @NotNull(message = "El precio de costo no puede estar vacío")
    private Double precioCosto;

    @Column(name = "stock", nullable = false)
    @NotNull(message = "El stock no puede estar vacío")
    private int stock;

    @OneToMany(mappedBy = "repuesto")
    @JsonIgnore
    private List<Detalle> detalles;

    public void setCodigo(String codigo) {
        this.codigo = codigo == null ? null : codigo.trim();
    }

    public void setProducto(String producto) {
        this.producto = producto == null ? null : producto.trim();
    }

    public void setMarca(String marca) {
        this.marca = marca == null ? null : marca.trim();
    }

    public void setColor(String color) {
        this.color = color == null ? null : color.trim();
    }

    // para los precios se valida que no sean negativos y se aplica el redondeo
    public void setPrecioVenta(Double precioVenta) {
        if (precioVenta < 0) {
            throw new IllegalArgumentException("El precio no puede ser negativo");
        }
        double valorRedondeado = Math.round(precioVenta * 100.0) / 100.0;
        this.precioVenta = valorRedondeado;
    }

    public void setPrecioCosto(Double precioCosto) {
        if (precioCosto < 0) {
            throw new IllegalArgumentException("El precio no puede ser negativo");
        }
        double valorRedondeado = Math.round(precioCosto * 100.0) / 100.0;
        this.precioCosto = valorRedondeado;
    }

    public void setStock(int stock) {
        if (stock < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo");
        }
        this.stock = stock;
    }
}
