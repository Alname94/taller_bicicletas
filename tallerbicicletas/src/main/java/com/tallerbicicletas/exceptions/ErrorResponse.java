package com.tallerbicicletas.exceptions;

import java.time.LocalDate;

public record ErrorResponse(int status, String message, LocalDate timestamp) {

}
