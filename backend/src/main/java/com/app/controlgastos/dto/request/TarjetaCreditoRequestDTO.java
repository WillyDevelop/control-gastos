package com.app.controlgastos.dto.request;

public class TarjetaCreditoRequestDTO {
    private String nombre;
    private int diaCierre;
    private int diaVencimiento;

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public int getDiaCierre() { return diaCierre; }
    public void setDiaCierre(int diaCierre) { this.diaCierre = diaCierre; }

    public int getDiaVencimiento() { return diaVencimiento; }
    public void setDiaVencimiento(int diaVencimiento) { this.diaVencimiento = diaVencimiento; }
}
