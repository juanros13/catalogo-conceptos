package saf.cgmaig.validation.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Request para validación de conceptos técnicos
 * 
 * Contiene toda la información necesaria para realizar las validaciones
 * de reglas de negocio y integridad de datos para conceptos del CUBS.
 */
public class ValidationRequest {

    @NotBlank(message = "El nombre del concepto es obligatorio")
    @Size(max = 255, message = "El nombre no puede exceder 255 caracteres")
    private String name;

    @NotBlank(message = "El área es obligatoria")
    @Size(max = 100, message = "El área no puede exceder 100 caracteres") 
    private String area;

    @NotBlank(message = "El capítulo es obligatorio")
    @Size(max = 100, message = "El capítulo no puede exceder 100 caracteres")
    private String chapter;

    @Size(max = 1000, message = "La descripción no puede exceder 1000 caracteres")
    private String description;

    @Size(max = 50, message = "La unidad de medida no puede exceder 50 caracteres")
    private String unitMeasure;

    @Size(max = 2000, message = "Las especificaciones no pueden exceder 2000 caracteres")
    private String specifications;

    @NotBlank(message = "El usuario que crea/modifica es obligatorio")
    private String createdBy;

    // ID del concepto (null para creación, presente para actualización)
    private Long conceptId;

    // Constructor por defecto
    public ValidationRequest() {}

    // Constructor completo
    public ValidationRequest(String name, String area, String chapter, String description, 
                           String unitMeasure, String specifications, String createdBy, Long conceptId) {
        this.name = name;
        this.area = area;
        this.chapter = chapter;
        this.description = description;
        this.unitMeasure = unitMeasure;
        this.specifications = specifications;
        this.createdBy = createdBy;
        this.conceptId = conceptId;
    }

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

    public Long getConceptId() {
        return conceptId;
    }

    public void setConceptId(Long conceptId) {
        this.conceptId = conceptId;
    }

    @Override
    public String toString() {
        return "ValidationRequest{" +
                "name='" + name + '\'' +
                ", area='" + area + '\'' +
                ", chapter='" + chapter + '\'' +
                ", description='" + description + '\'' +
                ", unitMeasure='" + unitMeasure + '\'' +
                ", specifications='" + specifications + '\'' +
                ", createdBy='" + createdBy + '\'' +
                ", conceptId=" + conceptId +
                '}';
    }
}