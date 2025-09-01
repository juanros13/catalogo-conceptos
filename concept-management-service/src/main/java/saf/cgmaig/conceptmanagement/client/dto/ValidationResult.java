package saf.cgmaig.conceptmanagement.client.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO para resultados de validación del validation-service
 */
public class ValidationResult {

    private String validationType;
    private String validatedBy;
    private boolean isValid;
    private String details;
    private LocalDateTime validatedAt;
    private List<ValidationError> errors;
    private List<ValidationWarning> warnings;

    // Constructor por defecto
    public ValidationResult() {
        this.validatedAt = LocalDateTime.now();
    }

    // Getters y Setters
    public String getValidationType() {
        return validationType;
    }

    public void setValidationType(String validationType) {
        this.validationType = validationType;
    }

    public String getValidatedBy() {
        return validatedBy;
    }

    public void setValidatedBy(String validatedBy) {
        this.validatedBy = validatedBy;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public LocalDateTime getValidatedAt() {
        return validatedAt;
    }

    public void setValidatedAt(LocalDateTime validatedAt) {
        this.validatedAt = validatedAt;
    }

    public List<ValidationError> getErrors() {
        return errors;
    }

    public void setErrors(List<ValidationError> errors) {
        this.errors = errors;
    }

    public List<ValidationWarning> getWarnings() {
        return warnings;
    }

    public void setWarnings(List<ValidationWarning> warnings) {
        this.warnings = warnings;
    }

    public boolean hasErrors() {
        return errors != null && !errors.isEmpty();
    }

    public boolean hasWarnings() {
        return warnings != null && !warnings.isEmpty();
    }

    // Records para errores y warnings
    public record ValidationError(String field, String message, String code) {}
    public record ValidationWarning(String field, String message, String code) {}

    @Override
    public String toString() {
        return "ValidationResult{" +
                "validationType='" + validationType + '\'' +
                ", isValid=" + isValid +
                ", errors=" + (errors != null ? errors.size() : 0) +
                ", warnings=" + (warnings != null ? warnings.size() : 0) +
                '}';
    }
}