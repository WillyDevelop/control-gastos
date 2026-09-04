package com.app.controlgastos.dto;

import com.app.controlgastos.model.Rol;

public class AuthResponse {
    private String token;
    private String email;
    private String nombre;
    private Rol rol;

    public AuthResponse(String token, String email, String nombre, Rol rol) {
        this.token = token;
        this.email = email;
        this.nombre = nombre;
        this.rol = rol;
    }

    public String getToken() { return token; }
    public String getEmail() { return email; }
    public String getNombre() { return nombre; }
    public Rol getRol() { return rol; }
}
