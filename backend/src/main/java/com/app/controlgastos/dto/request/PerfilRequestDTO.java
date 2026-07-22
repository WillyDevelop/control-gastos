package com.app.controlgastos.dto.request;

import jakarta.validation.constraints.NotBlank;

public class PerfilRequestDTO {
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
}
