package saf.cgmaig.validation.model;

import java.util.List;

/**
 * Modelo para templates de capítulos CUBS
 * 
 * Define la estructura dinámica de campos por capítulo,
 * permitiendo diferentes configuraciones según el tipo de concepto.
 */
public class ChapterTemplate {

    private String name;
    private String description;
    private int requiredFields;
    private List<TemplateField> fields;

    // Constructor por defecto
    public ChapterTemplate() {}

    // Constructor completo
    public ChapterTemplate(String name, String description, int requiredFields, List<TemplateField> fields) {
        this.name = name;
        this.description = description;
        this.requiredFields = requiredFields;
        this.fields = fields;
    }

    // Getters y Setters
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

    public int getRequiredFields() {
        return requiredFields;
    }

    public void setRequiredFields(int requiredFields) {
        this.requiredFields = requiredFields;
    }

    public List<TemplateField> getFields() {
        return fields;
    }

    public void setFields(List<TemplateField> fields) {
        this.fields = fields;
    }

    /**
     * Obtiene campo por clave
     */
    public TemplateField getFieldByKey(String fieldKey) {
        return fields.stream()
                .filter(field -> field.getFieldKey().equals(fieldKey))
                .findFirst()
                .orElse(null);
    }

    /**
     * Obtiene solo campos requeridos
     */
    public List<TemplateField> getRequiredFieldsList() {
        return fields.stream()
                .filter(TemplateField::isRequired)
                .toList();
    }

    /**
     * Obtiene solo campos opcionales
     */
    public List<TemplateField> getOptionalFields() {
        return fields.stream()
                .filter(field -> !field.isRequired())
                .toList();
    }

    @Override
    public String toString() {
        return "ChapterTemplate{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", requiredFields=" + requiredFields +
                ", fields=" + fields.size() +
                '}';
    }
}