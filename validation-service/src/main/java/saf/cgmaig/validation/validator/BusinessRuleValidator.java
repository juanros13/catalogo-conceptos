package saf.cgmaig.validation.validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import saf.cgmaig.validation.model.ValidationRequest;
import saf.cgmaig.validation.model.ValidationResult;
import saf.cgmaig.validation.model.ValidationType;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Validador de reglas de negocio específicas del CUBS
 * 
 * REGLAS DE NEGOCIO CUBS:
 * - Conceptos de software deben incluir especificaciones técnicas mínimas
 * - Conceptos de hardware requieren marca y modelo
 * - Servicios de consultoría requieren duración y alcance
 * - Conceptos de mantenimiento deben especificar periodicidad
 * - Valores monetarios deben estar dentro de rangos permitidos por área
 * 
 * VALIDACIONES TEMPORALES:
 * - Algunos conceptos pueden tener restricciones de horario de creación
 * - Conceptos de alto valor requieren aprobación adicional
 * - Conceptos temporales tienen fechas de vigencia
 * 
 * CONFIGURACIÓN:
 * - cubs.validation.business-rules.enabled: true/false
 * - cubs.validation.business-rules.high-value-threshold: Umbral de alto valor
 * - cubs.validation.business-rules.restricted-hours-enabled: true/false
 */
@Component
public class BusinessRuleValidator {

    private static final Logger logger = LoggerFactory.getLogger(BusinessRuleValidator.class);

    @Value("${cubs.validation.business-rules.enabled:true}")
    private boolean businessRulesEnabled;

    @Value("${cubs.validation.business-rules.high-value-threshold:100000.00}")
    private BigDecimal highValueThreshold;

    @Value("${cubs.validation.business-rules.restricted-hours-enabled:false}")
    private boolean restrictedHoursEnabled;

    @Value("${cubs.validation.business-rules.work-start-hour:8}")
    private int workStartHour;

    @Value("${cubs.validation.business-rules.work-end-hour:18}")
    private int workEndHour;

    // Especificaciones técnicas requeridas por capítulo
    private static final Map<String, Set<String>> REQUIRED_TECH_SPECS = Map.of(
        "SOFTWARE", Set.of("version", "licencia", "sistema operativo", "requisitos"),
        "HARDWARE", Set.of("marca", "modelo", "especificaciones técnicas"),
        "SERVICIOS_TI", Set.of("alcance", "duración", "entregables"),
        "CONSULTORIA", Set.of("perfil consultor", "duración", "metodología"),
        "MANTENIMIENTO", Set.of("periodicidad", "tipo mantenimiento", "alcance")
    );

    // Patrones para validar especificaciones
    private static final Pattern VERSION_PATTERN = Pattern.compile(".*v?\\d+\\.\\d+.*", Pattern.CASE_INSENSITIVE);
    private static final Pattern DURATION_PATTERN = Pattern.compile(".*(\\d+\\s*(día|días|mes|meses|año|años|hora|horas)).*", Pattern.CASE_INSENSITIVE);
    private static final Pattern BRAND_MODEL_PATTERN = Pattern.compile(".*(marca|modelo).*", Pattern.CASE_INSENSITIVE);

    // Límites de valor por área (en pesos mexicanos)
    private static final Map<String, BigDecimal> AREA_VALUE_LIMITS = Map.of(
        "CGMAIG", new BigDecimal("500000.00"),
        "CGRM", new BigDecimal("200000.00"),
        "CGSG", new BigDecimal("150000.00"),
        "PATRIMONIO", new BigDecimal("1000000.00")
    );

    // Conceptos que requieren aprobación especial
    private static final Set<String> SPECIAL_APPROVAL_CHAPTERS = Set.of(
        "TELECOMUNICACIONES", "INFRAESTRUCTURA", "VEHICULOS", "CONSULTORIA"
    );

    /**
     * Valida las reglas de negocio específicas del CUBS
     */
    public ValidationResult validate(ValidationRequest request, String validatedBy) {
        logger.debug("Iniciando validación de reglas de negocio para concepto: {}", request.getName());

        ValidationResult result = new ValidationResult(ValidationType.BUSINESS_RULE_VALIDATION, validatedBy);

        // Verificar si la validación está habilitada
        if (!businessRulesEnabled) {
            logger.info("Validación de reglas de negocio deshabilitada por configuración");
            result.addWarning("business-rules", "Validación de reglas de negocio deshabilitada", "VALIDATION_DISABLED");
            return result;
        }

        try {
            // Validar horario de trabajo si está habilitado
            validateWorkingHours(result);

            // Validar especificaciones técnicas por capítulo
            validateTechnicalSpecifications(request, result);

            // Validar valores monetarios
            validateMonetaryValues(request, result);

            // Validar aprobaciones especiales
            validateSpecialApprovals(request, result);

            // Validar coherencia entre área y especificaciones
            validateAreaSpecificationCoherence(request, result);

            // Validar formato de especificaciones técnicas
            validateSpecificationFormat(request, result);

            if (result.isValid()) {
                logger.debug("Validación de reglas de negocio exitosa para concepto: {}", request.getName());
                result.setDetails("Todas las reglas de negocio CUBS han sido validadas exitosamente");
            }

        } catch (Exception e) {
            logger.error("Error durante validación de reglas de negocio para concepto: {}", request.getName(), e);
            result.addError("system", "Error interno durante validación de reglas de negocio: " + e.getMessage(), 
                          "SYSTEM_ERROR");
        }

        return result;
    }

    /**
     * Valida horario de trabajo para creación de conceptos
     */
    private void validateWorkingHours(ValidationResult result) {
        if (!restrictedHoursEnabled) {
            return;
        }

        LocalTime now = LocalTime.now();
        LocalTime workStart = LocalTime.of(workStartHour, 0);
        LocalTime workEnd = LocalTime.of(workEndHour, 0);

        if (now.isBefore(workStart) || now.isAfter(workEnd)) {
            result.addWarning("schedule", 
                String.format("Concepto creado fuera de horario laboral (%02d:00 - %02d:00)", 
                             workStartHour, workEndHour), 
                "OUT_OF_WORKING_HOURS");
        }
    }

    /**
     * Valida especificaciones técnicas requeridas por capítulo
     */
    private void validateTechnicalSpecifications(ValidationRequest request, ValidationResult result) {
        if (request.getChapter() == null || request.getSpecifications() == null) {
            return;
        }

        String chapter = request.getChapter().toUpperCase().trim();
        String specifications = request.getSpecifications().toLowerCase();

        Set<String> requiredSpecs = REQUIRED_TECH_SPECS.get(chapter);
        if (requiredSpecs != null) {
            for (String requiredSpec : requiredSpecs) {
                if (!specifications.contains(requiredSpec.toLowerCase())) {
                    result.addError("specifications", 
                        String.format("Las especificaciones deben incluir información sobre: %s", requiredSpec), 
                        "MISSING_TECH_SPEC");
                }
            }
        }
    }

    /**
     * Valida valores monetarios según límites por área
     */
    private void validateMonetaryValues(ValidationRequest request, ValidationResult result) {
        if (request.getEstimatedValue() == null || request.getArea() == null) {
            return;
        }

        String area = request.getArea().toUpperCase().trim();
        BigDecimal estimatedValue = request.getEstimatedValue();

        // Validar que el valor no sea negativo o cero
        if (estimatedValue.compareTo(BigDecimal.ZERO) <= 0) {
            result.addError("estimatedValue", "El valor estimado debe ser mayor a cero", "INVALID_VALUE");
            return;
        }

        // Validar límites por área
        BigDecimal areaLimit = AREA_VALUE_LIMITS.get(area);
        if (areaLimit != null && estimatedValue.compareTo(areaLimit) > 0) {
            result.addError("estimatedValue", 
                String.format("El valor estimado (%.2f) excede el límite para el área %s (%.2f)", 
                             estimatedValue, area, areaLimit), 
                "VALUE_EXCEEDS_AREA_LIMIT");
        }

        // Validar conceptos de alto valor
        if (estimatedValue.compareTo(highValueThreshold) > 0) {
            result.addWarning("estimatedValue", 
                String.format("Concepto de alto valor (%.2f) requiere aprobación adicional", estimatedValue), 
                "HIGH_VALUE_CONCEPT");
        }
    }

    /**
     * Valida si el concepto requiere aprobaciones especiales
     */
    private void validateSpecialApprovals(ValidationRequest request, ValidationResult result) {
        if (request.getChapter() == null) {
            return;
        }

        String chapter = request.getChapter().toUpperCase().trim();
        if (SPECIAL_APPROVAL_CHAPTERS.contains(chapter)) {
            result.addWarning("chapter", 
                String.format("Conceptos de %s requieren aprobación especial antes de su activación", chapter), 
                "SPECIAL_APPROVAL_REQUIRED");
        }
    }

    /**
     * Valida coherencia entre área y especificaciones técnicas
     */
    private void validateAreaSpecificationCoherence(ValidationRequest request, ValidationResult result) {
        if (request.getArea() == null || request.getSpecifications() == null) {
            return;
        }

        String area = request.getArea().toUpperCase().trim();
        String specifications = request.getSpecifications().toLowerCase();

        switch (area) {
            case "CGMAIG":
                if (!specifications.contains("tecnolog") && !specifications.contains("sistem") && 
                    !specifications.contains("software") && !specifications.contains("hardware")) {
                    result.addWarning("specifications", 
                        "Las especificaciones de CGMAIG deberían incluir términos tecnológicos", 
                        "AREA_SPEC_MISMATCH");
                }
                break;
            case "CGRM":
                if (!specifications.contains("mobiliario") && !specifications.contains("equipo") && 
                    !specifications.contains("vehículo") && !specifications.contains("oficina")) {
                    result.addWarning("specifications", 
                        "Las especificaciones de CGRM deberían incluir términos relacionados con recursos materiales", 
                        "AREA_SPEC_MISMATCH");
                }
                break;
            case "CGSG":
                if (!specifications.contains("servicio") && !specifications.contains("mantenimiento") && 
                    !specifications.contains("limpieza") && !specifications.contains("vigilancia")) {
                    result.addWarning("specifications", 
                        "Las especificaciones de CGSG deberían incluir términos relacionados con servicios generales", 
                        "AREA_SPEC_MISMATCH");
                }
                break;
        }
    }

    /**
     * Valida formato específico de especificaciones técnicas
     */
    private void validateSpecificationFormat(ValidationRequest request, ValidationResult result) {
        if (request.getSpecifications() == null || request.getChapter() == null) {
            return;
        }

        String chapter = request.getChapter().toUpperCase().trim();
        String specifications = request.getSpecifications();

        switch (chapter) {
            case "SOFTWARE":
                if (!VERSION_PATTERN.matcher(specifications).find()) {
                    result.addWarning("specifications", 
                        "Especificaciones de software deberían incluir información de versión", 
                        "MISSING_VERSION_INFO");
                }
                break;
            case "HARDWARE":
                if (!BRAND_MODEL_PATTERN.matcher(specifications).find()) {
                    result.addWarning("specifications", 
                        "Especificaciones de hardware deberían incluir marca y modelo", 
                        "MISSING_BRAND_MODEL");
                }
                break;
            case "CONSULTORIA":
            case "SERVICIOS_TI":
                if (!DURATION_PATTERN.matcher(specifications).find()) {
                    result.addWarning("specifications", 
                        "Especificaciones de servicios deberían incluir duración estimada", 
                        "MISSING_DURATION");
                }
                break;
        }
    }

    /**
     * Verifica si la validación está habilitada
     */
    public boolean isEnabled() {
        return businessRulesEnabled;
    }

    /**
     * Obtiene especificaciones técnicas requeridas por capítulo
     */
    public Set<String> getRequiredTechSpecs(String chapter) {
        if (chapter == null) {
            return Set.of();
        }
        return REQUIRED_TECH_SPECS.getOrDefault(chapter.toUpperCase().trim(), Set.of());
    }

    /**
     * Obtiene límite de valor por área
     */
    public BigDecimal getAreaValueLimit(String area) {
        if (area == null) {
            return null;
        }
        return AREA_VALUE_LIMITS.get(area.toUpperCase().trim());
    }

    /**
     * Verifica si un capítulo requiere aprobación especial
     */
    public boolean requiresSpecialApproval(String chapter) {
        if (chapter == null) {
            return false;
        }
        return SPECIAL_APPROVAL_CHAPTERS.contains(chapter.toUpperCase().trim());
    }

    /**
     * Obtiene la configuración actual del validador
     */
    public BusinessRuleValidatorConfig getConfig() {
        return new BusinessRuleValidatorConfig(
            businessRulesEnabled,
            highValueThreshold,
            restrictedHoursEnabled,
            workStartHour,
            workEndHour,
            REQUIRED_TECH_SPECS.size(),
            SPECIAL_APPROVAL_CHAPTERS.size()
        );
    }

    /**
     * Record para exponer la configuración del validador
     */
    public record BusinessRuleValidatorConfig(
        boolean enabled,
        BigDecimal highValueThreshold,
        boolean restrictedHoursEnabled,
        int workStartHour,
        int workEndHour,
        int totalTechSpecRules,
        int specialApprovalChapters
    ) {}
}