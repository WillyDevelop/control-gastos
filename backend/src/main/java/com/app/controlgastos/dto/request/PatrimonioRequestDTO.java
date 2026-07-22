package com.app.controlgastos.dto.request;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public class PatrimonioRequestDTO {
    @NotNull(message = "El ingreso total es obligatorio")
    private BigDecimal ingresoTotal;

    private int mes;
    private int anio;
    private LocalDate fechaIngreso;

    public PatrimonioRequestDTO() {}

    public BigDecimal getIngresoTotal() { return ingresoTotal; }
    public void setIngresoTotal(BigDecimal ingresoTotal) { this.ingresoTotal = ingresoTotal; }

    public int getMes() { return mes; }
    public void setMes(int mes) { this.mes = mes; }

    public int getAnio() { return anio; }
    public void setAnio(int anio) { this.anio = anio; }

    public LocalDate getFechaIngreso() { return fechaIngreso; }
    public void setFechaIngreso(LocalDate fechaIngreso) { this.fechaIngreso = fechaIngreso; }
}
