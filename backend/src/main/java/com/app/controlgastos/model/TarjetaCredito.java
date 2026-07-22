package com.app.controlgastos.model;

import jakarta.persistence.*;

@Entity
@Table(name = "tarjetas_credito")
public class TarjetaCredito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(name = "dia_cierre", nullable = false)
    private int diaCierre;

    @Column(name = "dia_vencimiento", nullable = false)
    private int diaVencimiento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    public TarjetaCredito() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public int getDiaCierre() { return diaCierre; }
    public void setDiaCierre(int diaCierre) { this.diaCierre = diaCierre; }

    public int getDiaVencimiento() { return diaVencimiento; }
    public void setDiaVencimiento(int diaVencimiento) { this.diaVencimiento = diaVencimiento; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
}
