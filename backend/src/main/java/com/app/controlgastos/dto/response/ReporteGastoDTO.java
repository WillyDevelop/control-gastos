package com.app.controlgastos.dto.response;

import java.math.BigDecimal;

public class ReporteGastoDTO {
    private String fecha;
    private BigDecimal total;

    public ReporteGastoDTO() {}

    public ReporteGastoDTO(String fecha, BigDecimal total) {
        this.fecha = fecha;
        this.total = total;
    }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
}
