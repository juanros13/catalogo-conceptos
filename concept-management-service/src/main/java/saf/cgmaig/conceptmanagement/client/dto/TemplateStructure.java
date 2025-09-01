package saf.cgmaig.conceptmanagement.client.dto;

import java.util.List;

/**
 * DTO para estructura de templates obtenida del validation-service
 */
public class TemplateStructure {

    private String chapterKey;
    private String name;
    private List<TemplateField> fields;
    private int requiredFieldsCount;

    // Constructor por defecto
    public TemplateStructure() {}

    // Constructor completo
    public TemplateStructure(String chapterKey, String name, List<TemplateField> fields, int requiredFieldsCount) {
        this.chapterKey = chapterKey;
        this.name = name;
        this.fields = fields;
        this.requiredFieldsCount = requiredFieldsCount;
    }

    // Getters y Setters
    public String getChapterKey() {
        return chapterKey;
    }

    public void setChapterKey(String chapterKey) {
        this.chapterKey = chapterKey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<TemplateField> getFields() {
        return fields;
    }

    public void setFields(List<TemplateField> fields) {
        this.fields = fields;
    }

    public int getRequiredFieldsCount() {
        return requiredFieldsCount;
    }

    public void setRequiredFieldsCount(int requiredFieldsCount) {
        this.requiredFieldsCount = requiredFieldsCount;
    }

    /**
     * DTO para campos de template
     */
    public static class TemplateField {
        private String fieldKey;
        private String fieldName;
        private String fieldType;
        private boolean required;
        private int maxLength;
        private String placeholder;
        private List<String> validationRules;

        // Constructor por defecto
        public TemplateField() {}

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

        @Override
        public String toString() {
            return "TemplateField{" +
                    "fieldKey='" + fieldKey + '\'' +
                    ", fieldName='" + fieldName + '\'' +
                    ", required=" + required +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "TemplateStructure{" +
                "chapterKey='" + chapterKey + '\'' +
                ", name='" + name + '\'' +
                ", fieldsCount=" + (fields != null ? fields.size() : 0) +
                ", requiredFieldsCount=" + requiredFieldsCount +
                '}';
    }
}