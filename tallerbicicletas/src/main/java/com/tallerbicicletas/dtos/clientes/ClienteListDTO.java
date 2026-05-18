package com.tallerbicicletas.dtos.clientes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClienteListDTO {

    private Long id;
    private String nombre;
    private String apellido;
    private String dni;
}
