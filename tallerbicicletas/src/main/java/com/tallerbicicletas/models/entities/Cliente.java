package com.tallerbicicletas.models.entities;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.GenerationType;

@Entity
@Table (name = "clientes")
@Data
@NoArgsConstructor
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cliente_id")
    @Schema(description = "Identificador único del cliente", example = "1")
    private Long id;

    @Column(name = "nombre", nullable = false, length = 50)
    @NotBlank(message = "El nombre del cliente no puede estar vacío")
    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
    @Schema(description = "Nombre del cliente", example = "Juan")
    private String nombre;

    @Column(name = "apellido", nullable = false, length = 50)
    @NotBlank(message = "El apellido del cliente no puede estar vacío")
    @Size(min = 2, max = 50, message = "El apellido debe tener entre 2 y 50 caracteres")
    @Schema(description = "Apellido del cliente", example = "Pérez")
    private String apellido;

    @Column(name = "dni", nullable = false, unique = true, length = 8)
    @NotBlank(message = "El dni no puede estar vacío")
    @Pattern(regexp = "^[0-9]{8}$", message = "El DNI debe ser de 8 dígitos numéricos")
    @Schema(description = "DNI del cliente", example = "12345678")
    private String dni;

    @Column(name = "telefono", nullable = false, length = 15)
    @NotBlank(message = "El telefono no puede estar vacío")
    @Pattern(regexp = "^\\+?[0-9]{8,15}$",
    message = "El teléfono debe contener solo dígitos, puede empezar con '+', y tener entre 8 y 15 caracteres")
    @Schema(description = "Teléfono del cliente", example = "+5491112345678")
    private String telefono;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    @NotBlank(message = "El email no puede estar vacío")
    @Email(message = "Formato de email inválido")
    @Size(max = 100, message = "El email no puede exceder 100 caracteres")
    @Schema(description = "Email del cliente", example = "juan.perez@example.com")
    private String email;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnoreProperties("cliente")
    @Schema(description = "Lista de bicicletas del cliente")
    private List<Bicicleta> bicicletas = new ArrayList<>();


    //Constructor con todos los parámetros excepto la lista de bicicletas
    public Cliente(Long id, String nombre, String apellido, String dni, String telefono, String email) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
        this.telefono = telefono;
        this.email = email;
    }

    public void agregarBicicleta(Bicicleta bicicleta) {
        this.bicicletas.add(bicicleta);
        bicicleta.setCliente(this);
    }

    public void setNombre(String nombre) {
        this.nombre = nombre == null ? null : nombre.trim();
    }

    public void setApellido(String apellido) {
        this.apellido = apellido == null ? null : apellido.trim();
    }

    public void setDni(String dni) {
        this.dni = dni == null ? null : dni.trim();
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono == null ? null : telefono.trim();
    }

    public void setEmail(String email) {
        this.email = email == null ? null : email.trim();
    }
}
