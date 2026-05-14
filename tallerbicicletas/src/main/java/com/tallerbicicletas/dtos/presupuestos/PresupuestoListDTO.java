package com.tallerbicicletas.dtos.presupuestos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PresupuestoListDTO {

    private Long numero;
    private String fecha;
    private String cliente;
    private String bicicleta;
    private Double valorTotal;
    private String estado;
}
