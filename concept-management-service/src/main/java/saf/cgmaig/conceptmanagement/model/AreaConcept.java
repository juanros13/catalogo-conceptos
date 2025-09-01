package saf.cgmaig.conceptmanagement.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad para conceptos específicos creados por áreas
 * 
 * Representa conceptos completos basados en conceptos técnicos base
 * con estructura específica por capítulo según templates CUBS.
 */
@Entity
@Table(name = "area_concepts")
public class AreaConcept {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Referencia al concepto técnico base
    @Column(name = "base_concept_id", nullable = false)
    @NotNull(message = "El concepto base es obligatorio")
    private Long baseConceptId;

    @Column(name = "base_concept_name")
    private String baseConceptName; // Cache del nombre para consultas

    // Información general del concepto específico
    @Column(name = "specific_name", nullable = false)
    @NotBlank(message = "El nombre específico es obligatorio")
    @Size(max = 300, message = "El nombre no puede exceder 300 caracteres")
    private String specificName;

    @Column(name = "area", nullable = false)
    @NotBlank(message = "El área es obligatoria")
    private String area;

    @Column(name = "chapter", nullable = false)
    @NotBlank(message = "El capítulo es obligatorio")
    private String chapter;

    @Column(name = "chapter_template", nullable = false)
    @NotBlank(message = "El template de capítulo es obligatorio")
    private String chapterTemplate; // 2000_MATERIALES, 2000_SERVICIOS, 5000

    // Estado del concepto específico
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ConceptStatus status = ConceptStatus.DRAFT;

    // Campos dinámicos por template (JSON o campos específicos)
    @Column(name = "general_field", columnDefinition = "TEXT")
    private String general;

    @Column(name = "especifica_field", columnDefinition = "TEXT") 
    private String especifica;

    @Column(name = "presentacion_producto", columnDefinition = "TEXT")
    private String presentacionProducto;

    @Column(name = "composicion_materiales", columnDefinition = "TEXT")
    private String composicionMateriales;

    @Column(name = "descripcion_tecnica", columnDefinition = "TEXT")
    private String descripcionTecnica;

    @Column(name = "componentes_servicio", columnDefinition = "TEXT")
    private String componentesServicio;

    @Column(name = "accesorios_servicio", columnDefinition = "TEXT")
    private String accesoriosServicio;

    @Column(name = "caracteristica_funcionalidad", columnDefinition = "TEXT")
    private String caracteristicaFuncionalidad;

    @Column(name = "caracteristicas_fisicas", columnDefinition = "TEXT")
    private String caracteristicasFisicas;

    @Column(name = "color")
    private String color;

    @Column(name = "mayores_especificaciones", columnDefinition = "TEXT")
    private String mayoresEspecificaciones;

    // Información adicional
    @Column(name = "unit_measure")
    private String unitMeasure;

    @Column(name = "estimated_value", precision = 12, scale = 2)
    private BigDecimal estimatedValue;

    // Auditoría
    @Column(name = "created_by", nullable = false)
    @NotBlank(message = "El usuario creador es obligatorio")
    private String createdBy;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @Column(name = "validated_by")
    private String validatedBy;

    @Column(name = "validated_at")
    private LocalDateTime validatedAt;

    @Column(name = "validation_comments", columnDefinition = "TEXT")
    private String validationComments;

    // Constructor por defecto
    public AreaConcept() {
        this.createdAt = LocalDateTime.now();
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBaseConceptId() {
        return baseConceptId;
    }

    public void setBaseConceptId(Long baseConceptId) {
        this.baseConceptId = baseConceptId;
    }

    public String getBaseConceptName() {
        return baseConceptName;
    }

    public void setBaseConceptName(String baseConceptName) {
        this.baseConceptName = baseConceptName;
    }

    public String getSpecificName() {
        return specificName;
    }

    public void setSpecificName(String specificName) {
        this.specificName = specificName;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getChapter() {
        return chapter;
    }

    public void setChapter(String chapter) {
        this.chapter = chapter;
    }

    public String getChapterTemplate() {
        return chapterTemplate;
    }

    public void setChapterTemplate(String chapterTemplate) {
        this.chapterTemplate = chapterTemplate;
    }

    public ConceptStatus getStatus() {
        return status;
    }

    public void setStatus(ConceptStatus status) {
        this.status = status;
    }

    // Getters y setters para campos de template
    public String getGeneral() {
        return general;
    }

    public void setGeneral(String general) {
        this.general = general;
    }

    public String getEspecifica() {
        return especifica;
    }

    public void setEspecifica(String especifica) {
        this.especifica = especifica;
    }

    public String getPresentacionProducto() {
        return presentacionProducto;
    }

    public void setPresentacionProducto(String presentacionProducto) {
        this.presentacionProducto = presentacionProducto;
    }

    public String getComposicionMateriales() {
        return composicionMateriales;
    }

    public void setComposicionMateriales(String composicionMateriales) {
        this.composicionMateriales = composicionMateriales;
    }

    public String getDescripcionTecnica() {
        return descripcionTecnica;
    }

    public void setDescripcionTecnica(String descripcionTecnica) {
        this.descripcionTecnica = descripcionTecnica;
    }

    public String getComponentesServicio() {
        return componentesServicio;
    }

    public void setComponentesServicio(String componentesServicio) {
        this.componentesServicio = componentesServicio;
    }

    public String getAccesoriosServicio() {
        return accesoriosServicio;
    }

    public void setAccesoriosServicio(String accesoriosServicio) {
        this.accesoriosServicio = accesoriosServicio;
    }

    public String getCaracteristicaFuncionalidad() {
        return caracteristicaFuncionalidad;
    }

    public void setCaracteristicaFuncionalidad(String caracteristicaFuncionalidad) {
        this.caracteristicaFuncionalidad = caracteristicaFuncionalidad;
    }

    public String getCaracteristicasFisicas() {
        return caracteristicasFisicas;
    }

    public void setCaracteristicasFisicas(String caracteristicasFisicas) {
        this.caracteristicasFisicas = caracteristicasFisicas;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getMayoresEspecificaciones() {
        return mayoresEspecificaciones;
    }

    public void setMayoresEspecificaciones(String mayoresEspecificaciones) {
        this.mayoresEspecificaciones = mayoresEspecificaciones;
    }

    public String getUnitMeasure() {
        return unitMeasure;
    }

    public void setUnitMeasure(String unitMeasure) {
        this.unitMeasure = unitMeasure;
    }

    public BigDecimal getEstimatedValue() {
        return estimatedValue;
    }

    public void setEstimatedValue(BigDecimal estimatedValue) {
        this.estimatedValue = estimatedValue;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public String getValidatedBy() {
        return validatedBy;
    }

    public void setValidatedBy(String validatedBy) {
        this.validatedBy = validatedBy;
    }

    public LocalDateTime getValidatedAt() {
        return validatedAt;
    }

    public void setValidatedAt(LocalDateTime validatedAt) {
        this.validatedAt = validatedAt;
    }

    public String getValidationComments() {
        return validationComments;
    }

    public void setValidationComments(String validationComments) {
        this.validationComments = validationComments;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Método de utilidad para enviar a validación
     */
    public void submitForValidation() {
        this.status = ConceptStatus.SUBMITTED;
        this.submittedAt = LocalDateTime.now();
    }

    /**
     * Método de utilidad para aprobar concepto
     */
    public void approve(String validatedBy, String comments) {
        this.status = ConceptStatus.APPROVED;
        this.validatedBy = validatedBy;
        this.validatedAt = LocalDateTime.now();
        this.validationComments = comments;
    }

    /**
     * Método de utilidad para rechazar concepto
     */
    public void reject(String validatedBy, String comments) {
        this.status = ConceptStatus.REJECTED;
        this.validatedBy = validatedBy;
        this.validatedAt = LocalDateTime.now();
        this.validationComments = comments;
    }

    @Override
    public String toString() {
        return "AreaConcept{" +
                "id=" + id +
                ", baseConceptId=" + baseConceptId +
                ", specificName='" + specificName + '\'' +
                ", area='" + area + '\'' +
                ", chapter='" + chapter + '\'' +
                ", chapterTemplate='" + chapterTemplate + '\'' +
                ", status=" + status +
                ", createdBy='" + createdBy + '\'' +
                '}';
    }
}