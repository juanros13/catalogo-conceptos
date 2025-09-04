package saf.cgmaig.budgetclassification.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "budget_classifications", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"codigo"}),
       indexes = {
           @Index(name = "idx_codigo", columnList = "codigo"),
           @Index(name = "idx_nivel", columnList = "nivel"),
           @Index(name = "idx_padre_codigo", columnList = "padre_codigo"),
           @Index(name = "idx_activo", columnList = "activo")
       })
public class BudgetClassification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "codigo", length = 4, nullable = false, unique = true)
    @NotBlank(message = "Código es obligatorio")
    @Pattern(regexp = "^[0-9]{4}$", message = "Código debe tener exactamente 4 dígitos")
    private String codigo;

    @Column(name = "nombre", length = 200, nullable = false)
    @NotBlank(message = "Nombre es obligatorio")
    @Size(max = 200, message = "Nombre no puede exceder 200 caracteres")
    private String nombre;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    @Size(max = 1000, message = "Descripción no puede exceder 1000 caracteres")
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(name = "nivel", length = 20, nullable = false)
    @NotNull(message = "Nivel es obligatorio")
    private BudgetLevel nivel;

    @Column(name = "padre_codigo", length = 4)
    private String padreCodigo;

    @Column(name = "orden", nullable = false)
    private Integer orden = 0;

    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    @CreationTimestamp
    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @Column(name = "creado_por", length = 100)
    private String creadoPor;

    @Column(name = "actualizado_por", length = 100)
    private String actualizadoPor;

    // Relación con conceptos hijos (lazy loading)
    @OneToMany(mappedBy = "padreCodigo", fetch = FetchType.LAZY)
    @OrderBy("codigo ASC")
    private List<BudgetClassification> hijos = new ArrayList<>();

    // Constructor vacío
    public BudgetClassification() {}

    // Constructor con parámetros esenciales
    public BudgetClassification(String codigo, String nombre, String descripcion, String creadoPor) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.nivel = BudgetLevel.fromCode(codigo);
        this.padreCodigo = this.nivel.getParentCode(codigo);
        this.creadoPor = creadoPor;
        this.actualizadoPor = creadoPor;
    }

    // Métodos de negocio
    public void activar(String usuario) {
        this.activo = true;
        this.actualizadoPor = usuario;
    }

    public void inactivar(String usuario) {
        this.activo = false;
        this.actualizadoPor = usuario;
    }

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

    public boolean tieneHijos() {
        return hijos != null && !hijos.isEmpty();
    }

    /**
     * Valida la consistencia jerárquica
     */
    public void validarJerarquia() {
        if (codigo == null || codigo.length() != 4) {
            throw new IllegalArgumentException("Código debe tener exactamente 4 dígitos");
        }

        BudgetLevel nivelCalculado = BudgetLevel.fromCode(codigo);
        if (!nivelCalculado.equals(this.nivel)) {
            throw new IllegalArgumentException(
                String.format("Código %s no corresponde al nivel %s", codigo, nivel));
        }

        String padreEsperado = nivel.getParentCode(codigo);
        if (!java.util.Objects.equals(padreEsperado, this.padreCodigo)) {
            throw new IllegalArgumentException(
                String.format("Código padre incorrecto. Esperado: %s, Actual: %s", 
                            padreEsperado, this.padreCodigo));
        }
    }

    @PrePersist
    @PreUpdate
    private void validate() {
        validarJerarquia();
    }

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
        if (codigo != null) {
            this.nivel = BudgetLevel.fromCode(codigo);
            this.padreCodigo = this.nivel.getParentCode(codigo);
        }
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

    public List<BudgetClassification> getHijos() {
        return hijos;
    }

    public void setHijos(List<BudgetClassification> hijos) {
        this.hijos = hijos;
    }

    @Override
    public String toString() {
        return "BudgetClassification{" +
                "id=" + id +
                ", codigo='" + codigo + '\'' +
                ", nombre='" + nombre + '\'' +
                ", nivel=" + nivel +
                ", padreCodigo='" + padreCodigo + '\'' +
                ", activo=" + activo +
                '}';
    }
}