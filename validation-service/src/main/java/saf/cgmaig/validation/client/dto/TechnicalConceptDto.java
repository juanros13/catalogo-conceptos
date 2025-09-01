package saf.cgmaig.validation.client.dto;

import java.time.LocalDateTime;

/**
 * DTO para representar conceptos técnicos obtenidos del technical-concept-service
 * 
 * Usado para validaciones de unicidad y verificaciones de datos existentes.
 */
public class TechnicalConceptDto {

    private Long id;
    private String name;
    private String description;
    private String area;
    private String chapter;
    private String unitMeasure;
    private String specifications;
    private String status;
    private String createdBy;
    private LocalDateTime createdAt;
    private String updatedBy;
    private LocalDateTime updatedAt;

    // Constructor por defecto
    public TechnicalConceptDto() {}

    // Constructor completo
    public TechnicalConceptDto(Long id, String name, String description, String area, 
                              String chapter, String unitMeasure, String specifications, 
                              String status, String createdBy, LocalDateTime createdAt, 
                              String updatedBy, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.area = area;
        this.chapter = chapter;
        this.unitMeasure = unitMeasure;
        this.specifications = specifications;
        this.status = status;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.updatedBy = updatedBy;
        this.updatedAt = updatedAt;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    @Override
    public String toString() {
        return "TechnicalConceptDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", area='" + area + '\'' +
                ", chapter='" + chapter + '\'' +
                ", status='" + status + '\'' +
                ", createdBy='" + createdBy + '\'' +
                '}';
    }
}