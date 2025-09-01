package saf.cgmaig.conceptmanagement.service;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;

/**
 * Request para actualización de conceptos específicos por áreas
 * 
 * Solo permite actualizar campos editables, no información estructural
 * como concepto base o área.
 */
public class AreaConceptUpdateRequest implements AreaConceptService.TemplateFieldsProvider {

    // Información básica editable
    @NotBlank(message = "El nombre específico es obligatorio")
    @Size(max = 300, message = "El nombre no puede exceder 300 caracteres")
    private String specificName;

    @Size(max = 50, message = "La unidad de medida no puede exceder 50 caracteres")
    private String unitMeasure;

    @DecimalMin(value = "0.0", inclusive = false, message = "El valor estimado debe ser mayor a cero")
    private BigDecimal estimatedValue;

    // Campos específicos por template
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
    public AreaConceptUpdateRequest() {}

    // Getters y Setters
    public String getSpecificName() {
        return specificName;
    }

    public void setSpecificName(String specificName) {
        this.specificName = specificName;
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

    // Implementación de TemplateFieldsProvider
    @Override
    public String getGeneral() {
        return general;
    }

    public void setGeneral(String general) {
        this.general = general;
    }

    @Override
    public String getEspecifica() {
        return especifica;
    }

    public void setEspecifica(String especifica) {
        this.especifica = especifica;
    }

    @Override
    public String getPresentacionProducto() {
        return presentacionProducto;
    }

    public void setPresentacionProducto(String presentacionProducto) {
        this.presentacionProducto = presentacionProducto;
    }

    @Override
    public String getComposicionMateriales() {
        return composicionMateriales;
    }

    public void setComposicionMateriales(String composicionMateriales) {
        this.composicionMateriales = composicionMateriales;
    }

    @Override
    public String getDescripcionTecnica() {
        return descripcionTecnica;
    }

    public void setDescripcionTecnica(String descripcionTecnica) {
        this.descripcionTecnica = descripcionTecnica;
    }

    @Override
    public String getComponentesServicio() {
        return componentesServicio;
    }

    public void setComponentesServicio(String componentesServicio) {
        this.componentesServicio = componentesServicio;
    }

    @Override
    public String getAccesoriosServicio() {
        return accesoriosServicio;
    }

    public void setAccesoriosServicio(String accesoriosServicio) {
        this.accesoriosServicio = accesoriosServicio;
    }

    @Override
    public String getCaracteristicaFuncionalidad() {
        return caracteristicaFuncionalidad;
    }

    public void setCaracteristicaFuncionalidad(String caracteristicaFuncionalidad) {
        this.caracteristicaFuncionalidad = caracteristicaFuncionalidad;
    }

    @Override
    public String getCaracteristicasFisicas() {
        return caracteristicasFisicas;
    }

    public void setCaracteristicasFisicas(String caracteristicasFisicas) {
        this.caracteristicasFisicas = caracteristicasFisicas;
    }

    @Override
    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public String getMayoresEspecificaciones() {
        return mayoresEspecificaciones;
    }

    public void setMayoresEspecificaciones(String mayoresEspecificaciones) {
        this.mayoresEspecificaciones = mayoresEspecificaciones;
    }

    @Override
    public String toString() {
        return "AreaConceptUpdateRequest{" +
                "specificName='" + specificName + '\'' +
                ", unitMeasure='" + unitMeasure + '\'' +
                ", estimatedValue=" + estimatedValue +
                '}';
    }
}