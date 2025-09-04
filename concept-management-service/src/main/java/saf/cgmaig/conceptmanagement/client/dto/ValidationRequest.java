package saf.cgmaig.conceptmanagement.client.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * DTO para enviar solicitudes de validación al validation-service
 * 
 * Contiene toda la información necesaria para validar conceptos específicos
 * con estructura de template por capítulo.
 */
public class ValidationRequest {

    // Información básica
    @NotBlank(message = "El nombre del concepto es obligatorio")
    @Size(max = 300, message = "El nombre no puede exceder 300 caracteres")
    private String name;

    @NotBlank(message = "El área es obligatoria")
    private String area;

    @NotBlank(message = "El capítulo es obligatorio")
    private String chapter;

    @Size(max = 1000, message = "La descripción no puede exceder 1000 caracteres")
    private String description;

    @Size(max = 50, message = "La unidad de medida no puede exceder 50 caracteres")
    private String unitMeasure;

    @Size(max = 2000, message = "Las especificaciones no pueden exceder 2000 caracteres")
    private String specifications;

    @NotBlank(message = "El usuario que crea/modifica es obligatorio")
    private String createdBy;

    // Información específica para conceptos de área
    private Long baseConceptId; // Referencia al concepto técnico base
    private String conceptType = "SPECIFIC"; // Siempre específico para concept-management
    private String chapterTemplate; // 2000_MATERIALES, 2000_SERVICIOS, 5000

    @DecimalMin(value = "0.0", inclusive = false, message = "El valor estimado debe ser mayor a cero")
    private BigDecimal estimatedValue;

    // Campos específicos de templates por capítulo
    private String general;
    private String especifica;
    private String presentacionProducto;
    private String composicionMateriales;
    private String descripcionTecnica;
    private String componentesServicio;
    private String accesoriosServicio;
    private String caracteristicaFuncionalidad;
    private String caracteristicasFisicas;
    private String color;
    private String mayoresEspecificaciones;

    // Constructor por defecto
    public ValidationRequest() {}

    // Getters y Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUnitMeasure() {
        return unitMeasure;
    }

    public void setUnitMeasure(String unitMeasure) {
        this.unitMeasure = unitMeasure;
    }

    public String getSpecifications() {
        return specifications;
    }

    public void setSpecifications(String specifications) {
        this.specifications = specifications;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Long getBaseConceptId() {
        return baseConceptId;
    }

    public void setBaseConceptId(Long baseConceptId) {
        this.baseConceptId = baseConceptId;
    }

    public String getConceptType() {
        return conceptType;
    }

    public void setConceptType(String conceptType) {
        this.conceptType = conceptType;
    }

    public String getChapterTemplate() {
        return chapterTemplate;
    }

    public void setChapterTemplate(String chapterTemplate) {
        this.chapterTemplate = chapterTemplate;
    }

    public BigDecimal getEstimatedValue() {
        return estimatedValue;
    }

    public void setEstimatedValue(BigDecimal estimatedValue) {
        this.estimatedValue = estimatedValue;
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

    @Override
    public String toString() {
        return "ValidationRequest{" +
                "name='" + name + '\'' +
                ", area='" + area + '\'' +
                ", chapter='" + chapter + '\'' +
                ", chapterTemplate='" + chapterTemplate + '\'' +
                ", baseConceptId=" + baseConceptId +
                ", createdBy='" + createdBy + '\'' +
                '}';
    }
}