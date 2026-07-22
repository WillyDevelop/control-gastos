package com.app.controlgastos.dto.response;

import java.math.BigDecimal;

public class MetaAhorroResponseDTO {
    private Long id;
    private String nombre;
    private BigDecimal montoObjetivo;
    private BigDecimal montoActual;
    private boolean activaParaRedondeo;

    public MetaAhorroResponseDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public BigDecimal getMontoObjetivo() { return montoObjetivo; }
    public void setMontoObjetivo(BigDecimal montoObjetivo) { this.montoObjetivo = montoObjetivo; }

    public BigDecimal getMontoActual() { return montoActual; }
    public void setMontoActual(BigDecimal montoActual) { this.montoActual = montoActual; }

    public boolean isActivaParaRedondeo() { return activaParaRedondeo; }
    public void setActivaParaRedondeo(boolean activaParaRedondeo) { this.activaParaRedondeo = activaParaRedondeo; }
}
