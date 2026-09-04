package com.app.controlgastos.dto.response;

import com.app.controlgastos.model.Rol;

public class AdminUsuarioDTO {
    private Long id;
    private String email;
    private String nombre;
    private boolean activo;
    private Rol rol;
    private long totalGastos;
    private long totalTarjetas;

    public AdminUsuarioDTO() {}

    public AdminUsuarioDTO(Long id, String email, String nombre, boolean activo, Rol rol, long totalGastos, long totalTarjetas) {
        this.id = id;
        this.email = email;
        this.nombre = nombre;
        this.activo = activo;
        this.rol = rol;
        this.totalGastos = totalGastos;
        this.totalTarjetas = totalTarjetas;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }

    public long getTotalGastos() { return totalGastos; }
    public void setTotalGastos(long totalGastos) { this.totalGastos = totalGastos; }

    public long getTotalTarjetas() { return totalTarjetas; }
    public void setTotalTarjetas(long totalTarjetas) { this.totalTarjetas = totalTarjetas; }
}
