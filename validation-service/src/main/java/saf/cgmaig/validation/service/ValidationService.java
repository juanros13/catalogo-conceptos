package saf.cgmaig.validation.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import saf.cgmaig.validation.model.ValidationRequest;
import saf.cgmaig.validation.model.ValidationResult;
import saf.cgmaig.validation.model.ValidationType;
import saf.cgmaig.validation.validator.AreaChapterValidator;
import saf.cgmaig.validation.validator.BusinessRuleValidator;
import saf.cgmaig.validation.validator.FormatValidator;
import saf.cgmaig.validation.validator.UniquenessValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Servicio principal de validación de conceptos técnicos
 * 
 * Coordina y ejecuta todas las validaciones necesarias para conceptos del CUBS.
 * Integra los validadores individuales y proporciona un resultado consolidado.
 */
@Service
public class ValidationService {

    private static final Logger logger = LoggerFactory.getLogger(ValidationService.class);

    private final UniquenessValidator uniquenessValidator;
    private final AreaChapterValidator areaChapterValidator;
    private final FormatValidator formatValidator;
    private final BusinessRuleValidator businessRuleValidator;

    // Configuración del servicio
    @Value("${cubs.validation.service.enabled:true}")
    private boolean validationServiceEnabled;

    @Value("${cubs.validation.service.parallel-execution:false}")
    private boolean parallelExecution;

    @Value("${cubs.validation.service.stop-on-first-error:false}")
    private boolean stopOnFirstError;

    // Estadísticas internas
    private final AtomicLong totalValidations = new AtomicLong(0);
    private final AtomicLong successfulValidations = new AtomicLong(0);
    private final AtomicLong failedValidations = new AtomicLong(0);

    @Autowired
    public ValidationService(UniquenessValidator uniquenessValidator,
                           AreaChapterValidator areaChapterValidator,
                           FormatValidator formatValidator,
                           BusinessRuleValidator businessRuleValidator) {
        this.uniquenessValidator = uniquenessValidator;
        this.areaChapterValidator = areaChapterValidator;
        this.formatValidator = formatValidator;
        this.businessRuleValidator = businessRuleValidator;
    }

    /**
     * Valida un concepto técnico aplicando todas las reglas de negocio
     */
    public ValidationResult validateConcept(ValidationRequest request, String validatedBy) {
        logger.info("Iniciando validación completa para concepto: {} por usuario: {}", 
                   request.getName(), validatedBy);
        
        totalValidations.incrementAndGet();

        // Verificar si el servicio está habilitado
        if (!validationServiceEnabled) {
            logger.warn("Servicio de validación deshabilitado por configuración");
            ValidationResult disabledResult = new ValidationResult(ValidationType.COMPLETE_VALIDATION, validatedBy);
            disabledResult.addWarning("service", "Servicio de validación deshabilitado", "SERVICE_DISABLED");
            return disabledResult;
        }

        try {
            ValidationResult result = new ValidationResult(ValidationType.COMPLETE_VALIDATION, validatedBy);

            // Ejecutar validaciones en orden
            List<ValidationResult> individualResults = new ArrayList<>();

            // 1. Validación de formato (fundamental)
            if (formatValidator.isEnabled()) {
                logger.debug("Ejecutando validación de formato");
                ValidationResult formatResult = formatValidator.validate(request, validatedBy);
                individualResults.add(formatResult);
                mergeValidationResult(result, formatResult);
                
                if (stopOnFirstError && !formatResult.isValid()) {
                    logger.warn("Deteniendo validación por errores de formato");
                    return finalizeResult(result, individualResults, false);
                }
            }

            // 2. Validación de relaciones área-capítulo (estructura)
            if (areaChapterValidator.isEnabled()) {
                logger.debug("Ejecutando validación área-capítulo");
                ValidationResult areaChapterResult = areaChapterValidator.validate(request, validatedBy);
                individualResults.add(areaChapterResult);
                mergeValidationResult(result, areaChapterResult);
                
                if (stopOnFirstError && !areaChapterResult.isValid()) {
                    logger.warn("Deteniendo validación por errores de área-capítulo");
                    return finalizeResult(result, individualResults, false);
                }
            }

            // 3. Validación de unicidad (base de datos)
            if (uniquenessValidator.isEnabled()) {
                logger.debug("Ejecutando validación de unicidad");
                ValidationResult uniquenessResult = uniquenessValidator.validate(request, validatedBy);
                individualResults.add(uniquenessResult);
                mergeValidationResult(result, uniquenessResult);
                
                if (stopOnFirstError && !uniquenessResult.isValid()) {
                    logger.warn("Deteniendo validación por errores de unicidad");
                    return finalizeResult(result, individualResults, false);
                }
            }

            // 4. Validación de reglas de negocio (específicas CUBS)
            if (businessRuleValidator.isEnabled()) {
                logger.debug("Ejecutando validación de reglas de negocio");
                ValidationResult businessRuleResult = businessRuleValidator.validate(request, validatedBy);
                individualResults.add(businessRuleResult);
                mergeValidationResult(result, businessRuleResult);
            }

            return finalizeResult(result, individualResults, true);

        } catch (Exception e) {
            logger.error("Error crítico durante validación completa para concepto: {}", request.getName(), e);
            failedValidations.incrementAndGet();
            
            ValidationResult errorResult = new ValidationResult(ValidationType.COMPLETE_VALIDATION, validatedBy);
            errorResult.addError("system", "Error crítico durante validación: " + e.getMessage(), "CRITICAL_ERROR");
            return errorResult;
        }
    }

    /**
     * Valida solo la unicidad del concepto
     */
    public ValidationResult validateUniqueness(ValidationRequest request, String validatedBy) {
        logger.debug("Ejecutando validación de unicidad independiente para concepto: {}", request.getName());
        
        if (!uniquenessValidator.isEnabled()) {
            ValidationResult result = new ValidationResult(ValidationType.UNIQUENESS_VALIDATION, validatedBy);
            result.addWarning("uniqueness", "Validación de unicidad deshabilitada", "VALIDATION_DISABLED");
            return result;
        }

        return uniquenessValidator.validate(request, validatedBy);
    }

    /**
     * Valida solo las relaciones área-capítulo
     */
    public ValidationResult validateAreaChapter(ValidationRequest request, String validatedBy) {
        logger.debug("Ejecutando validación área-capítulo independiente para concepto: {}", request.getName());
        
        if (!areaChapterValidator.isEnabled()) {
            ValidationResult result = new ValidationResult(ValidationType.AREA_CHAPTER_VALIDATION, validatedBy);
            result.addWarning("area-chapter", "Validación área-capítulo deshabilitada", "VALIDATION_DISABLED");
            return result;
        }

        return areaChapterValidator.validate(request, validatedBy);
    }

    /**
     * Valida solo los formatos
     */
    public ValidationResult validateFormat(ValidationRequest request, String validatedBy) {
        logger.debug("Ejecutando validación de formato independiente para concepto: {}", request.getName());
        
        if (!formatValidator.isEnabled()) {
            ValidationResult result = new ValidationResult(ValidationType.FORMAT_VALIDATION, validatedBy);
            result.addWarning("format", "Validación de formato deshabilitada", "VALIDATION_DISABLED");
            return result;
        }

        return formatValidator.validate(request, validatedBy);
    }

    /**
     * Combina los resultados de validaciones individuales en el resultado principal
     */
    private void mergeValidationResult(ValidationResult mainResult, ValidationResult individualResult) {
        // Copiar errores
        if (individualResult.getErrors() != null) {
            individualResult.getErrors().forEach(error -> 
                mainResult.addError(error.field(), error.message(), error.code()));
        }

        // Copiar warnings
        if (individualResult.getWarnings() != null) {
            individualResult.getWarnings().forEach(warning -> 
                mainResult.addWarning(warning.field(), warning.message(), warning.code()));
        }
    }

    /**
     * Finaliza el resultado de validación con estadísticas y detalles
     */
    private ValidationResult finalizeResult(ValidationResult result, List<ValidationResult> individualResults, boolean completed) {
        if (result.isValid() && completed) {
            successfulValidations.incrementAndGet();
            result.setDetails(String.format("Validación completa exitosa. Ejecutadas %d validaciones individuales", 
                                           individualResults.size()));
            logger.info("Validación completa exitosa para concepto");
        } else {
            failedValidations.incrementAndGet();
            long totalErrors = individualResults.stream().mapToLong(r -> r.getErrors() != null ? r.getErrors().size() : 0).sum();
            long totalWarnings = individualResults.stream().mapToLong(r -> r.getWarnings() != null ? r.getWarnings().size() : 0).sum();
            
            result.setDetails(String.format("Validación completada con %d errores y %d advertencias en %d validaciones", 
                                           totalErrors, totalWarnings, individualResults.size()));
            logger.warn("Validación completada con errores para concepto");
        }

        return result;
    }

    /**
     * Obtiene las reglas de validación disponibles
     */
    public Map<String, Object> getValidationRules() {
        List<String> enabledRules = new ArrayList<>();
        
        if (formatValidator.isEnabled()) {
            enabledRules.add("FORMAT_VALIDATION");
        }
        if (areaChapterValidator.isEnabled()) {
            enabledRules.add("AREA_CHAPTER_VALIDATION");
        }
        if (uniquenessValidator.isEnabled()) {
            enabledRules.add("UNIQUENESS_VALIDATION");
        }
        if (businessRuleValidator.isEnabled()) {
            enabledRules.add("BUSINESS_RULE_VALIDATION");
        }

        return Map.of(
            "status", "rules loaded",
            "count", enabledRules.size(),
            "enabled_rules", enabledRules,
            "total_validators", 4
        );
    }

    /**
     * Obtiene reglas de validación por tipo específico
     */
    public Map<String, Object> getValidationRulesByType(ValidationType validationType) {
        return switch (validationType) {
            case FORMAT_VALIDATION -> Map.of(
                "type", "FORMAT_VALIDATION",
                "enabled", formatValidator.isEnabled(),
                "config", formatValidator.isEnabled() ? formatValidator.getConfig() : "disabled"
            );
            case AREA_CHAPTER_VALIDATION -> Map.of(
                "type", "AREA_CHAPTER_VALIDATION", 
                "enabled", areaChapterValidator.isEnabled(),
                "config", areaChapterValidator.isEnabled() ? areaChapterValidator.getConfig() : "disabled"
            );
            case UNIQUENESS_VALIDATION -> Map.of(
                "type", "UNIQUENESS_VALIDATION",
                "enabled", uniquenessValidator.isEnabled(),
                "config", uniquenessValidator.isEnabled() ? uniquenessValidator.getConfig() : "disabled"
            );
            case BUSINESS_RULE_VALIDATION -> Map.of(
                "type", "BUSINESS_RULE_VALIDATION",
                "enabled", businessRuleValidator.isEnabled(),
                "config", businessRuleValidator.isEnabled() ? businessRuleValidator.getConfig() : "disabled"
            );
            default -> Map.of(
                "type", validationType.name(),
                "enabled", false,
                "message", "Validation type not supported"
            );
        };
    }

    /**
     * Obtiene lista de validadores habilitados
     */
    public List<String> getEnabledValidators() {
        List<String> enabled = new ArrayList<>();
        
        if (formatValidator.isEnabled()) enabled.add("FORMAT");
        if (areaChapterValidator.isEnabled()) enabled.add("AREA_CHAPTER");
        if (uniquenessValidator.isEnabled()) enabled.add("UNIQUENESS");
        if (businessRuleValidator.isEnabled()) enabled.add("BUSINESS_RULES");
        
        return enabled;
    }

    /**
     * Obtiene estadísticas del servicio de validación
     */
    public Map<String, Object> getValidationStatistics() {
        return Map.of(
            "total_validations", totalValidations.get(),
            "successful_validations", successfulValidations.get(),
            "failed_validations", failedValidations.get(),
            "success_rate", totalValidations.get() > 0 ? 
                (double) successfulValidations.get() / totalValidations.get() * 100 : 0.0,
            "enabled_validators_count", getEnabledValidators().size()
        );
    }

    /**
     * Obtiene la configuración completa del servicio
     */
    public ValidationServiceConfig getServiceConfig() {
        return new ValidationServiceConfig(
            validationServiceEnabled,
            parallelExecution,
            stopOnFirstError,
            formatValidator.getConfig(),
            areaChapterValidator.getConfig(),
            uniquenessValidator.getConfig(),
            businessRuleValidator.getConfig()
        );
    }

    /**
     * Record para exponer la configuración completa del servicio
     */
    public record ValidationServiceConfig(
        boolean serviceEnabled,
        boolean parallelExecution,
        boolean stopOnFirstError,
        FormatValidator.FormatValidatorConfig formatConfig,
        AreaChapterValidator.AreaChapterValidatorConfig areaChapterConfig,
        UniquenessValidator.UniquenessValidatorConfig uniquenessConfig,
        BusinessRuleValidator.BusinessRuleValidatorConfig businessRuleConfig
    ) {}
}