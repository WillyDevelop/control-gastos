package com.app.controlgastos.dto;

public class RestablecerPasswordRequest {
    private String token;
    private String nuevaPassword;
    private String confirmarPassword;

    public RestablecerPasswordRequest() {}

    public RestablecerPasswordRequest(String token, String nuevaPassword, String confirmarPassword) {
        this.token = token;
        this.nuevaPassword = nuevaPassword;
        this.confirmarPassword = confirmarPassword;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getNuevaPassword() { return nuevaPassword; }
    public void setNuevaPassword(String nuevaPassword) { this.nuevaPassword = nuevaPassword; }

    public String getConfirmarPassword() { return confirmarPassword; }
    public void setConfirmarPassword(String confirmarPassword) { this.confirmarPassword = confirmarPassword; }
}
