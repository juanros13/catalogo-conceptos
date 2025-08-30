package saf.cgmaig.technicalconcept.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "technical_concepts", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"nombre", "area_facultada"})
})
public class TechnicalConcept {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_concepto_general")
    private UUID id;

    @Column(name = "nombre", length = 200, nullable = false)
    @NotBlank(message = "Nombre del concepto general es obligatorio")
    @Size(max = 200, message = "Nombre no puede exceder 200 caracteres")
    private String nombre;

    @Column(name = "descripcion_detallada", columnDefinition = "TEXT")
    @Size(max = 1000, message = "Descripción no puede exceder 1000 caracteres")
    private String descripcionDetallada;

    @Column(name = "capitulo", nullable = false)
    @NotNull(message = "Capítulo es obligatorio")
    private Integer capitulo;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "concept_partidas", joinColumns = @JoinColumn(name = "concept_id"))
    @Column(name = "partida")
    private List<String> partidasPermitidas;

    @Enumerated(EnumType.STRING)
    @Column(name = "area_facultada", length = 20, nullable = false)
    @NotNull(message = "Área facultada es obligatoria")
    private AreaFacultada areaFacultada;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", length = 20, nullable = false)
    private ConceptStatus estado = ConceptStatus.ACTIVO;

    @Column(name = "version", nullable = false)
    private Integer version = 1;

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

    @Column(name = "motivo_cambio", length = 500)
    private String motivoCambio;

    // Constructores
    public TechnicalConcept() {}

    public TechnicalConcept(String nombre, String descripcionDetallada, Integer capitulo, 
                           AreaFacultada areaFacultada, String creadoPor) {
        this.nombre = nombre;
        this.descripcionDetallada = descripcionDetallada;
        this.capitulo = capitulo;
        this.areaFacultada = areaFacultada;
        this.creadoPor = creadoPor;
        this.actualizadoPor = creadoPor;
        this.validateAreaCapitulo();
    }

    // Métodos de negocio
    public void inactivar(String motivoCambio, String actualizadoPor) {
        this.estado = ConceptStatus.INACTIVO;
        this.activo = false;
        this.motivoCambio = motivoCambio;
        this.actualizadoPor = actualizadoPor;
        this.version++;
    }

    public void reactivar(String motivoCambio, String actualizadoPor) {
        this.estado = ConceptStatus.ACTIVO;
        this.activo = true;
        this.motivoCambio = motivoCambio;
        this.actualizadoPor = actualizadoPor;
        this.version++;
    }

    public boolean isActivo() {
        return ConceptStatus.ACTIVO.equals(this.estado) && Boolean.TRUE.equals(this.activo);
    }

    public boolean canBeEditedBy(AreaFacultada area) {
        return this.areaFacultada.equals(area);
    }

    private void validateAreaCapitulo() {
        if (this.areaFacultada != null && this.capitulo != null) {
            if (!this.areaFacultada.getCapitulo().equals(this.capitulo)) {
                throw new IllegalArgumentException(
                    String.format("Área facultada %s no puede gestionar capítulo %d", 
                                this.areaFacultada, this.capitulo)
                );
            }
        }
    }

    @PrePersist
    @PreUpdate
    private void validate() {
        validateAreaCapitulo();
    }

    // Getters y Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcionDetallada() {
        return descripcionDetallada;
    }

    public void setDescripcionDetallada(String descripcionDetallada) {
        this.descripcionDetallada = descripcionDetallada;
    }

    public Integer getCapitulo() {
        return capitulo;
    }

    public void setCapitulo(Integer capitulo) {
        this.capitulo = capitulo;
        validateAreaCapitulo();
    }

    public List<String> getPartidasPermitidas() {
        return partidasPermitidas;
    }

    public void setPartidasPermitidas(List<String> partidasPermitidas) {
        this.partidasPermitidas = partidasPermitidas;
    }

    public AreaFacultada getAreaFacultada() {
        return areaFacultada;
    }

    public void setAreaFacultada(AreaFacultada areaFacultada) {
        this.areaFacultada = areaFacultada;
        validateAreaCapitulo();
    }

    public ConceptStatus getEstado() {
        return estado;
    }

    public void setEstado(ConceptStatus estado) {
        this.estado = estado;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
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

    public String getMotivoCambio() {
        return motivoCambio;
    }

    public void setMotivoCambio(String motivoCambio) {
        this.motivoCambio = motivoCambio;
    }

    @Override
    public String toString() {
        return "TechnicalConcept{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", capitulo=" + capitulo +
                ", areaFacultada=" + areaFacultada +
                ", estado=" + estado +
                ", activo=" + activo +
                '}';
    }
}