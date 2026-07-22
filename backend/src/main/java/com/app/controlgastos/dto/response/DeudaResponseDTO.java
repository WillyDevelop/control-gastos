package com.app.controlgastos.dto.response;

import java.math.BigDecimal;

public class DeudaResponseDTO {
    private Long id;
    private String gastoDescripcion;
    private String deudorEmail;
    private String deudorNombre;
    private BigDecimal montoDeuda;
    private boolean liquidada;

    public DeudaResponseDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getGastoDescripcion() { return gastoDescripcion; }
    public void setGastoDescripcion(String gastoDescripcion) { this.gastoDescripcion = gastoDescripcion; }
    public String getDeudorEmail() { return deudorEmail; }
    public void setDeudorEmail(String deudorEmail) { this.deudorEmail = deudorEmail; }
    public String getDeudorNombre() { return deudorNombre; }
    public void setDeudorNombre(String deudorNombre) { this.deudorNombre = deudorNombre; }
    public BigDecimal getMontoDeuda() { return montoDeuda; }
    public void setMontoDeuda(BigDecimal montoDeuda) { this.montoDeuda = montoDeuda; }
    public boolean isLiquidada() { return liquidada; }
    public void setLiquidada(boolean liquidada) { this.liquidada = liquidada; }
}
