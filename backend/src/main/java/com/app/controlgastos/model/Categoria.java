package com.app.controlgastos.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "categorias", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"nombre", "usuario_id"})
})
public class Categoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String nombre;
    
    private String icono;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoCategoria tipo;
    
    @Column(name = "limite_mensual")
    private BigDecimal limiteMensual;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    public Categoria() {}

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

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
}
