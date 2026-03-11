package com.tallerbicicletas.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI tallerOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Sistema Taller de Bicicletas")
                        .description(
                                "Documentación de los servicios REST para la gestión de clientes, bicicletas, presupuestos, repuestos, servicios y detalles.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Soporte Taller")
                                .email("soporte@tallerbicicletas.com")));                
    }
}
