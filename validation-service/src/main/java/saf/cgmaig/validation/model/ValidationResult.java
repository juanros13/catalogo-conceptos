package saf.cgmaig.validation.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Resultado de la validación de un concepto técnico
 * 
 * Contiene el resultado de todas las validaciones aplicadas,
 * incluyendo errores encontrados y detalles de la validación.
 */
public class ValidationResult {

    // Indica si la validación fue exitosa (true) o falló (false)
    private boolean valid;

    // Lista de errores encontrados durante la validación
    private List<ValidationError> errors;

    // Lista de advertencias (validaciones que pasaron pero con observaciones)
    private List<ValidationWarning> warnings;

    // Timestamp de cuándo se realizó la validación
    private LocalDateTime validationTimestamp;

    // Usuario que solicitó la validación
    private String validatedBy;

    // Tipo de validación realizada
    private ValidationType validationType;

    // Detalles adicionales de la validación
    private String details;

    // Constructor por defecto
    public ValidationResult() {
        this.errors = new ArrayList<>();
        this.warnings = new ArrayList<>();
        this.validationTimestamp = LocalDateTime.now();
        this.valid = true; // Por defecto es válido, se cambia si hay errores
    }

    // Constructor con tipo de validación
    public ValidationResult(ValidationType validationType, String validatedBy) {
        this();
        this.validationType = validationType;
        this.validatedBy = validatedBy;
    }

    // Método para agregar un error (automáticamente marca como inválido)
    public void addError(String field, String message, String code) {
        this.errors.add(new ValidationError(field, message, code));
        this.valid = false;
    }

    // Método para agregar una advertencia
    public void addWarning(String field, String message, String code) {
        this.warnings.add(new ValidationWarning(field, message, code));
    }

    // Método para verificar si hay errores
    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    // Método para verificar si hay advertencias
    public boolean hasWarnings() {
        return !warnings.isEmpty();
    }

    // Getters y Setters
    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public List<ValidationError> getErrors() {
        return errors;
    }

    public void setErrors(List<ValidationError> errors) {
        this.errors = errors;
        // Si hay errores, marcar como inválido
        if (!errors.isEmpty()) {
            this.valid = false;
        }
    }

    public List<ValidationWarning> getWarnings() {
        return warnings;
    }

    public void setWarnings(List<ValidationWarning> warnings) {
        this.warnings = warnings;
    }

    public LocalDateTime getValidationTimestamp() {
        return validationTimestamp;
    }

    public void setValidationTimestamp(LocalDateTime validationTimestamp) {
        this.validationTimestamp = validationTimestamp;
    }

    public String getValidatedBy() {
        return validatedBy;
    }

    public void setValidatedBy(String validatedBy) {
        this.validatedBy = validatedBy;
    }

    public ValidationType getValidationType() {
        return validationType;
    }

    public void setValidationType(ValidationType validationType) {
        this.validationType = validationType;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    @Override
    public String toString() {
        return "ValidationResult{" +
                "valid=" + valid +
                ", errors=" + errors.size() +
                ", warnings=" + warnings.size() +
                ", validationTimestamp=" + validationTimestamp +
                ", validatedBy='" + validatedBy + '\'' +
                ", validationType=" + validationType +
                ", details='" + details + '\'' +
                '}';
    }

    // Clase interna para errores de validación
    public static class ValidationError {
        private String field;
        private String message;
        private String code;

        public ValidationError(String field, String message, String code) {
            this.field = field;
            this.message = message;
            this.code = code;
        }

        // Getters y Setters
        public String getField() { return field; }
        public void setField(String field) { this.field = field; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }

        @Override
        public String toString() {
            return "ValidationError{field='" + field + "', message='" + message + "', code='" + code + "'}";
        }
    }

    // Clase interna para advertencias de validación
    public static class ValidationWarning {
        private String field;
        private String message;
        private String code;

        public ValidationWarning(String field, String message, String code) {
            this.field = field;
            this.message = message;
            this.code = code;
        }

        // Getters y Setters
        public String getField() { return field; }
        public void setField(String field) { this.field = field; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }

        @Override
        public String toString() {
            return "ValidationWarning{field='" + field + "', message='" + message + "', code='" + code + "'}";
        }
    }
}