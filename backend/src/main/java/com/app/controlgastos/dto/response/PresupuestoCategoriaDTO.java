package com.app.controlgastos.dto.response;

import java.math.BigDecimal;

public class PresupuestoCategoriaDTO {
    private Long categoriaId;
    private String nombreCategoria;
    private BigDecimal limiteMensual;
    private BigDecimal montoGastado;
    private double porcentajeConsumido;

    public PresupuestoCategoriaDTO() {}

    public Long getCategoriaId() { return categoriaId; }
    public void setCategoriaId(Long categoriaId) { this.categoriaId = categoriaId; }

    public String getNombreCategoria() { return nombreCategoria; }
    public void setNombreCategoria(String nombreCategoria) { this.nombreCategoria = nombreCategoria; }

    public BigDecimal getLimiteMensual() { return limiteMensual; }
    public void setLimiteMensual(BigDecimal limiteMensual) { this.limiteMensual = limiteMensual; }

    public BigDecimal getMontoGastado() { return montoGastado; }
    public void setMontoGastado(BigDecimal montoGastado) { this.montoGastado = montoGastado; }

    public double getPorcentajeConsumido() { return porcentajeConsumido; }
    public void setPorcentajeConsumido(double porcentajeConsumido) { this.porcentajeConsumido = porcentajeConsumido; }
}
