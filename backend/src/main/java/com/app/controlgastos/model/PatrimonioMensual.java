package com.app.controlgastos.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "patrimonio_mensual", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"mes", "anio", "usuario_id"})
})
public class PatrimonioMensual {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer mes;

    @Column(nullable = false)
    private Integer anio;

    @Column(name = "ingreso_total", precision = 12, scale = 2)
    private BigDecimal ingresoTotal = BigDecimal.ZERO;

    @Column(name = "saldo_actual", nullable = false, precision = 12, scale = 2)
    private BigDecimal saldoActual;

    @Column(name = "fecha_ingreso")
    private LocalDate fechaIngreso;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    public PatrimonioMensual() {}

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

    public LocalDate getFechaIngreso() { return fechaIngreso; }
    public void setFechaIngreso(LocalDate fechaIngreso) { this.fechaIngreso = fechaIngreso; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
}
