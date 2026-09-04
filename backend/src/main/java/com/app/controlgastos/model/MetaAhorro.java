package com.app.controlgastos.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "metas_ahorro")
public class MetaAhorro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(name = "monto_objetivo", nullable = false, precision = 12, scale = 2)
    private BigDecimal montoObjetivo;

    @Column(name = "monto_actual", nullable = false, precision = 12, scale = 2)
    private BigDecimal montoActual = BigDecimal.ZERO;

    @Column(name = "activa_para_redondeo", nullable = false)
    private Boolean activaParaRedondeo = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    public MetaAhorro() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public BigDecimal getMontoObjetivo() { return montoObjetivo; }
    public void setMontoObjetivo(BigDecimal montoObjetivo) { this.montoObjetivo = montoObjetivo; }

    public BigDecimal getMontoActual() { return montoActual; }
    public void setMontoActual(BigDecimal montoActual) { this.montoActual = montoActual; }

    public boolean isActivaParaRedondeo() { return Boolean.TRUE.equals(activaParaRedondeo); }
    public void setActivaParaRedondeo(Boolean activaParaRedondeo) { this.activaParaRedondeo = activaParaRedondeo != null ? activaParaRedondeo : false; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
}
