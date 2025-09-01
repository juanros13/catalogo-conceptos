package saf.cgmaig.validation.model;

import java.util.List;

/**
 * Modelo para campos de template de capítulos
 * 
 * Define la configuración específica de cada campo dentro de un template,
 * incluyendo validaciones, tipos y restricciones.
 */
public class TemplateField {

    private String fieldKey;
    private String fieldName;
    private String fieldType;
    private boolean required;
    private int maxLength;
    private String placeholder;
    private List<String> validationRules;

    // Constructor por defecto
    public TemplateField() {}

    // Constructor completo
    public TemplateField(String fieldKey, String fieldName, String fieldType, 
                        boolean required, int maxLength, String placeholder, 
                        List<String> validationRules) {
        this.fieldKey = fieldKey;
        this.fieldName = fieldName;
        this.fieldType = fieldType;
        this.required = required;
        this.maxLength = maxLength;
        this.placeholder = placeholder;
        this.validationRules = validationRules;
    }

    // Getters y Setters
    public String getFieldKey() {
        return fieldKey;
    }

    public void setFieldKey(String fieldKey) {
        this.fieldKey = fieldKey;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }

    public List<String> getValidationRules() {
        return validationRules;
    }

    public void setValidationRules(List<String> validationRules) {
        this.validationRules = validationRules;
    }

    /**
     * Verifica si el campo tiene una regla específica
     */
    public boolean hasValidationRule(String rule) {
        return validationRules != null && validationRules.contains(rule);
    }

    /**
     * Obtiene el tipo de input HTML sugerido
     */
    public String getHtmlInputType() {
        return switch (fieldType.toLowerCase()) {
            case "textarea" -> "textarea";
            case "text" -> "text";
            case "number" -> "number";
            case "email" -> "email";
            case "date" -> "date";
            default -> "text";
        };
    }

    @Override
    public String toString() {
        return "TemplateField{" +
                "fieldKey='" + fieldKey + '\'' +
                ", fieldName='" + fieldName + '\'' +
                ", fieldType='" + fieldType + '\'' +
                ", required=" + required +
                ", maxLength=" + maxLength +
                '}';
    }
}