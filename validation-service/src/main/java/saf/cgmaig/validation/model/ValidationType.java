package saf.cgmaig.validation.model;

/**
 * Tipos de validación disponibles en el sistema CUBS
 * 
 * Define los diferentes tipos de validación que se pueden aplicar
 * a los conceptos técnicos del catálogo.
 */
public enum ValidationType {
    
    /**
     * Validación completa - Ejecuta todas las validaciones disponibles
     */
    FULL_VALIDATION("Validación completa de concepto técnico"),
    
    /**
     * Validación completa - Alias para compatibilidad
     */
    COMPLETE_VALIDATION("Validación completa de concepto técnico"),
    
    /**
     * Validación de unicidad - Verifica que el nombre sea único por área
     */
    UNIQUENESS_VALIDATION("Validación de unicidad por área"),
    
    /**
     * Validación de área-capítulo - Verifica relación válida entre área y capítulo
     */
    AREA_CHAPTER_VALIDATION("Validación de relación área-capítulo"),
    
    /**
     * Validación de formato - Verifica formatos de campos y especificaciones
     */
    FORMAT_VALIDATION("Validación de formatos y especificaciones"),
    
    /**
     * Validación de reglas de negocio - Aplica reglas específicas del CUBS
     */
    BUSINESS_RULES_VALIDATION("Validación de reglas de negocio CUBS"),
    
    /**
     * Validación de reglas de negocio - Alias para compatibilidad
     */
    BUSINESS_RULE_VALIDATION("Validación de reglas de negocio CUBS"),
    
    /**
     * Validación antes de actualización - Verifica cambios permitidos
     */
    UPDATE_VALIDATION("Validación para actualización de concepto"),
    
    /**
     * Validación antes de eliminación - Verifica si se puede eliminar
     */
    DELETION_VALIDATION("Validación para eliminación de concepto");

    private final String description;

    ValidationType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return this.name() + " - " + this.description;
    }
}