package com.app.controlgastos.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PatrimonioResponseDTO {
    private Long id;
    private Integer mes;
    private Integer anio;
    private BigDecimal ingresoTotal;
    private BigDecimal saldoActual;
    private BigDecimal totalPagado;
    private BigDecimal gastoMesQueViene;
    private LocalDate fechaIngreso;

    public PatrimonioResponseDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Integer getMes() { return mes; }
    public void setMes(Integer mes) { this.mes = mes; }
    public Integer getAnio() { return anio; }
    public void setAnio(Integer anio) { this.anio = anio; }
    public BigDecimal getIngresoTotal() { return ingresoTotal; }
    public void setIngresoTotal(BigDecimal ingresoTotal) { this.ingresoTotal = ingresoTotal; }
    public BigDecimal getSaldoActual() { return saldoActual; }
    public void setSaldoActual(BigDecimal saldoActual) { this.saldoActual = saldoActual; }
    public BigDecimal getTotalPagado() { return totalPagado; }
    public void setTotalPagado(BigDecimal totalPagado) { this.totalPagado = totalPagado; }
    public BigDecimal getGastoMesQueViene() { return gastoMesQueViene; }
    public void setGastoMesQueViene(BigDecimal gastoMesQueViene) { this.gastoMesQueViene = gastoMesQueViene; }

    public LocalDate getFechaIngreso() { return fechaIngreso; }
    public void setFechaIngreso(LocalDate fechaIngreso) { this.fechaIngreso = fechaIngreso; }
}
