package com.tallerbicicletas.exceptions;

import java.time.LocalDateTime;

// Clase para representar la estructura de la respuesta de error
public record ErrorResponse(int status, String message, LocalDateTime timestamp) {

}
