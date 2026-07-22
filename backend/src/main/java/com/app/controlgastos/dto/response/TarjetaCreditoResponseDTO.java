package com.app.controlgastos.dto.response;

public class TarjetaCreditoResponseDTO {
    private Long id;
    private String nombre;
    private int diaCierre;
    private int diaVencimiento;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public int getDiaCierre() { return diaCierre; }
    public void setDiaCierre(int diaCierre) { this.diaCierre = diaCierre; }

    public int getDiaVencimiento() { return diaVencimiento; }
    public void setDiaVencimiento(int diaVencimiento) { this.diaVencimiento = diaVencimiento; }
}
