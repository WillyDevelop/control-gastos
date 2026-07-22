package com.app.controlgastos.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "deudas")
public class Deuda {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gasto_id", nullable = false)
    private Gasto gasto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "acreedor_id", nullable = false)
    private Usuario acreedor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deudor_id", nullable = false)
    private Usuario deudor;

    @Column(name = "monto_deuda", nullable = false, precision = 12, scale = 2)
    private BigDecimal montoDeuda;

    @Column(nullable = false)
    private boolean liquidada = false;

    public Deuda() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Gasto getGasto() { return gasto; }
    public void setGasto(Gasto gasto) { this.gasto = gasto; }
    public Usuario getAcreedor() { return acreedor; }
    public void setAcreedor(Usuario acreedor) { this.acreedor = acreedor; }
    public Usuario getDeudor() { return deudor; }
    public void setDeudor(Usuario deudor) { this.deudor = deudor; }
    public BigDecimal getMontoDeuda() { return montoDeuda; }
    public void setMontoDeuda(BigDecimal montoDeuda) { this.montoDeuda = montoDeuda; }
    public boolean isLiquidada() { return liquidada; }
    public void setLiquidada(boolean liquidada) { this.liquidada = liquidada; }
}
