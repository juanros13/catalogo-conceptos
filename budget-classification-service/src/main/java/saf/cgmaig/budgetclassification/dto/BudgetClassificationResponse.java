package saf.cgmaig.budgetclassification.dto;

import saf.cgmaig.budgetclassification.entity.BudgetLevel;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BudgetClassificationResponse {

    private UUID id;
    private String codigo;
    private String nombre;
    private String descripcion;
    private BudgetLevel nivel;
    private String nivelDescripcion;
    private String padreCodigo;
    private Integer orden;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
    private String creadoPor;
    private String actualizadoPor;
    private List<BudgetClassificationResponse> hijos = new ArrayList<>();
    private boolean tieneHijos;

    // Constructor vacío
    public BudgetClassificationResponse() {}

    // Getters y Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BudgetLevel getNivel() {
        return nivel;
    }

    public void setNivel(BudgetLevel nivel) {
        this.nivel = nivel;
        this.nivelDescripcion = nivel != null ? nivel.getDescripcion() : null;
    }

    public String getNivelDescripcion() {
        return nivelDescripcion;
    }

    public void setNivelDescripcion(String nivelDescripcion) {
        this.nivelDescripcion = nivelDescripcion;
    }

    public String getPadreCodigo() {
        return padreCodigo;
    }

    public void setPadreCodigo(String padreCodigo) {
        this.padreCodigo = padreCodigo;
    }

    public Integer getOrden() {
        return orden;
    }

    public void setOrden(Integer orden) {
        this.orden = orden;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    public String getCreadoPor() {
        return creadoPor;
    }

    public void setCreadoPor(String creadoPor) {
        this.creadoPor = creadoPor;
    }

    public String getActualizadoPor() {
        return actualizadoPor;
    }

    public void setActualizadoPor(String actualizadoPor) {
        this.actualizadoPor = actualizadoPor;
    }

    public List<BudgetClassificationResponse> getHijos() {
        return hijos;
    }

    public void setHijos(List<BudgetClassificationResponse> hijos) {
        this.hijos = hijos;
        this.tieneHijos = hijos != null && !hijos.isEmpty();
    }

    public boolean isTieneHijos() {
        return tieneHijos;
    }

    public void setTieneHijos(boolean tieneHijos) {
        this.tieneHijos = tieneHijos;
    }

    // Métodos helper
    public boolean isCapitulo() {
        return BudgetLevel.CAPITULO.equals(this.nivel);
    }

    public boolean isPartidaGenerica() {
        return BudgetLevel.PARTIDA_GENERICA.equals(this.nivel);
    }

    public boolean isPartidaEspecifica() {
        return BudgetLevel.PARTIDA_ESPECIFICA.equals(this.nivel);
    }

    public boolean isPartida() {
        return BudgetLevel.PARTIDA.equals(this.nivel);
    }
}