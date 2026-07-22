package com.app.controlgastos.dto.response;

import com.app.controlgastos.model.TipoCategoria;
import java.math.BigDecimal;

public class CategoriaResponseDTO {
    private Long id;
    private String nombre;
    private String icono;
    private TipoCategoria tipo;
    private BigDecimal limiteMensual;

    public CategoriaResponseDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getIcono() { return icono; }
    public void setIcono(String icono) { this.icono = icono; }
    public TipoCategoria getTipo() { return tipo; }
    public void setTipo(TipoCategoria tipo) { this.tipo = tipo; }
    public BigDecimal getLimiteMensual() { return limiteMensual; }
    public void setLimiteMensual(BigDecimal limiteMensual) { this.limiteMensual = limiteMensual; }
}
