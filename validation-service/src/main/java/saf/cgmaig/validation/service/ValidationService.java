package saf.cgmaig.validation.service;

import org.springframework.stereotype.Service;
import saf.cgmaig.validation.model.ValidationRequest;
import saf.cgmaig.validation.model.ValidationResult;
import saf.cgmaig.validation.model.ValidationType;

import java.util.List;
import java.util.Map;

/**
 * Stub implementation of ValidationService 
 * 
 * Este es un stub temporal para que compile el proyecto.
 * La implementación completa se agregará en el siguiente paso.
 */
@Service
public class ValidationService {

    public ValidationResult validateConcept(ValidationRequest request, String validatedBy) {
        // Stub implementation
        return new ValidationResult(ValidationType.FULL_VALIDATION, validatedBy);
    }

    public ValidationResult validateUniqueness(ValidationRequest request, String validatedBy) {
        // Stub implementation  
        return new ValidationResult(ValidationType.UNIQUENESS_VALIDATION, validatedBy);
    }

    public ValidationResult validateAreaChapter(ValidationRequest request, String validatedBy) {
        // Stub implementation
        return new ValidationResult(ValidationType.AREA_CHAPTER_VALIDATION, validatedBy);
    }

    public ValidationResult validateFormat(ValidationRequest request, String validatedBy) {
        // Stub implementation
        return new ValidationResult(ValidationType.FORMAT_VALIDATION, validatedBy);
    }

    public Map<String, Object> getValidationRules() {
        // Stub implementation
        return Map.of(
            "status", "rules loaded",
            "count", 0
        );
    }

    public Map<String, Object> getValidationRulesByType(ValidationType validationType) {
        // Stub implementation
        return Map.of(
            "type", validationType.name(),
            "rules", List.of()
        );
    }

    public List<String> getEnabledValidators() {
        // Stub implementation
        return List.of("UNIQUENESS", "AREA_CHAPTER", "FORMAT", "BUSINESS_RULES");
    }
}