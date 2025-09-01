package saf.cgmaig.validation.validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import saf.cgmaig.validation.model.ValidationRequest;
import saf.cgmaig.validation.model.ValidationResult;
import saf.cgmaig.validation.model.ValidationType;

import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Validador de formatos y especificaciones para conceptos técnicos del CUBS
 * 
 * REGLAS DE FORMATO:
 * - Nombre: máximo 255 caracteres, solo letras, números, espacios y caracteres especiales permitidos
 * - Descripción: máximo 2000 caracteres
 * - Especificaciones: máximo 5000 caracteres
 * - Unidad de medida: debe ser de las permitidas por CUBS
 * - Campos no deben contener solo espacios en blanco
 * 
 * UNIDADES DE MEDIDA VÁLIDAS CUBS:
 * - PIEZA, METRO, KILOGRAMO, LITRO, SERVICIO, LOTE, M2, M3, HORA, MES, AÑO
 * 
 * CONFIGURACIÓN:
 * - cubs.validation.format.enabled: true/false
 * - cubs.validation.format.max-name-length: 255
 * - cubs.validation.format.max-description-length: 2000
 * - cubs.validation.format.max-specifications-length: 5000
 */
@Component
public class FormatValidator {

    private static final Logger logger = LoggerFactory.getLogger(FormatValidator.class);

    @Value("${cubs.validation.format.enabled:true}")
    private boolean formatValidationEnabled;

    @Value("${cubs.validation.format.max-name-length:255}")
    private int maxNameLength;

    @Value("${cubs.validation.format.max-description-length:2000}")
    private int maxDescriptionLength;

    @Value("${cubs.validation.format.max-specifications-length:5000}")
    private int maxSpecificationsLength;

    @Value("#{'${cubs.validation.format.allowed-unit-measures:PIEZA,METRO,KILOGRAMO,LITRO,SERVICIO,LOTE,M2,M3,HORA,MES,AÑO}'.split(',')}")
    private List<String> allowedUnitMeasures;

    // Patrones de validación
    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-ZáéíóúñÑ0-9\\s\\-_.,()]+$");
    private static final Pattern AREA_PATTERN = Pattern.compile("^[A-Z_]+$");
    private static final Pattern CHAPTER_PATTERN = Pattern.compile("^[A-Z_]+$");
    
    // Caracteres no permitidos en descripciones y especificaciones
    private static final Set<String> FORBIDDEN_CHARS = Set.of("<", ">", "{", "}", "[", "]", "|", "\\", "^", "~");

    /**
     * Valida los formatos y especificaciones del concepto técnico
     */
    public ValidationResult validate(ValidationRequest request, String validatedBy) {
        logger.debug("Iniciando validación de formatos para concepto: {}", request.getName());

        ValidationResult result = new ValidationResult(ValidationType.FORMAT_VALIDATION, validatedBy);

        // Verificar si la validación está habilitada
        if (!formatValidationEnabled) {
            logger.info("Validación de formatos deshabilitada por configuración");
            result.addWarning("format", "Validación de formatos deshabilitada", "VALIDATION_DISABLED");
            return result;
        }

        try {
            // Validar nombre
            validateName(request.getName(), result);

            // Validar área
            validateArea(request.getArea(), result);

            // Validar capítulo
            validateChapter(request.getChapter(), result);

            // Validar descripción
            validateDescription(request.getDescription(), result);

            // Validar especificaciones
            validateSpecifications(request.getSpecifications(), result);

            // Validar unidad de medida
            validateUnitMeasure(request.getUnitMeasure(), result);

            // Validar usuario que crea
            validateCreatedBy(request.getCreatedBy(), result);

            if (result.isValid()) {
                logger.debug("Validación de formatos exitosa para concepto: {}", request.getName());
                result.setDetails("Todos los formatos son válidos según especificaciones CUBS");
            }

        } catch (Exception e) {
            logger.error("Error durante validación de formatos para concepto: {}", request.getName(), e);
            result.addError("system", "Error interno durante validación de formatos: " + e.getMessage(), 
                          "SYSTEM_ERROR");
        }

        return result;
    }

    /**
     * Valida el formato del nombre
     */
    private void validateName(String name, ValidationResult result) {
        if (name == null || name.trim().isEmpty()) {
            result.addError("name", "El nombre del concepto es requerido", "REQUIRED_FIELD");
            return;
        }

        String trimmedName = name.trim();

        // Validar longitud
        if (trimmedName.length() > maxNameLength) {
            result.addError("name", 
                String.format("El nombre excede la longitud máxima de %d caracteres (%d)", 
                             maxNameLength, trimmedName.length()), 
                "MAX_LENGTH_EXCEEDED");
        }

        // Validar longitud mínima
        if (trimmedName.length() < 3) {
            result.addError("name", "El nombre debe tener al menos 3 caracteres", "MIN_LENGTH_NOT_REACHED");
        }

        // Validar patrón
        if (!NAME_PATTERN.matcher(trimmedName).matches()) {
            result.addError("name", 
                "El nombre contiene caracteres no permitidos. Solo se permiten letras, números, espacios y -_.,() ", 
                "INVALID_CHARACTERS");
        }

        // Validar que no sea solo espacios
        if (name.isBlank()) {
            result.addError("name", "El nombre no puede estar vacío o contener solo espacios", "BLANK_FIELD");
        }
    }

    /**
     * Valida el formato del área
     */
    private void validateArea(String area, ValidationResult result) {
        if (area == null || area.trim().isEmpty()) {
            result.addError("area", "El área es requerida", "REQUIRED_FIELD");
            return;
        }

        String trimmedArea = area.trim();

        if (!AREA_PATTERN.matcher(trimmedArea).matches()) {
            result.addError("area", 
                "El área debe estar en mayúsculas y solo contener letras y guiones bajos", 
                "INVALID_AREA_FORMAT");
        }

        if (trimmedArea.length() < 2 || trimmedArea.length() > 20) {
            result.addError("area", "El área debe tener entre 2 y 20 caracteres", "INVALID_AREA_LENGTH");
        }
    }

    /**
     * Valida el formato del capítulo
     */
    private void validateChapter(String chapter, ValidationResult result) {
        if (chapter == null || chapter.trim().isEmpty()) {
            result.addError("chapter", "El capítulo es requerido", "REQUIRED_FIELD");
            return;
        }

        String trimmedChapter = chapter.trim();

        if (!CHAPTER_PATTERN.matcher(trimmedChapter).matches()) {
            result.addError("chapter", 
                "El capítulo debe estar en mayúsculas y solo contener letras y guiones bajos", 
                "INVALID_CHAPTER_FORMAT");
        }

        if (trimmedChapter.length() < 2 || trimmedChapter.length() > 30) {
            result.addError("chapter", "El capítulo debe tener entre 2 y 30 caracteres", "INVALID_CHAPTER_LENGTH");
        }
    }

    /**
     * Valida el formato de la descripción
     */
    private void validateDescription(String description, ValidationResult result) {
        if (description == null) {
            return; // Descripción es opcional
        }

        String trimmedDescription = description.trim();

        if (trimmedDescription.length() > maxDescriptionLength) {
            result.addError("description", 
                String.format("La descripción excede la longitud máxima de %d caracteres (%d)", 
                             maxDescriptionLength, trimmedDescription.length()), 
                "MAX_LENGTH_EXCEEDED");
        }

        // Validar caracteres prohibidos
        for (String forbiddenChar : FORBIDDEN_CHARS) {
            if (trimmedDescription.contains(forbiddenChar)) {
                result.addError("description", 
                    String.format("La descripción contiene el carácter prohibido: %s", forbiddenChar), 
                    "FORBIDDEN_CHARACTER");
                break;
            }
        }
    }

    /**
     * Valida el formato de las especificaciones
     */
    private void validateSpecifications(String specifications, ValidationResult result) {
        if (specifications == null) {
            return; // Especificaciones son opcionales
        }

        String trimmedSpecifications = specifications.trim();

        if (trimmedSpecifications.length() > maxSpecificationsLength) {
            result.addError("specifications", 
                String.format("Las especificaciones exceden la longitud máxima de %d caracteres (%d)", 
                             maxSpecificationsLength, trimmedSpecifications.length()), 
                "MAX_LENGTH_EXCEEDED");
        }

        // Validar caracteres prohibidos
        for (String forbiddenChar : FORBIDDEN_CHARS) {
            if (trimmedSpecifications.contains(forbiddenChar)) {
                result.addError("specifications", 
                    String.format("Las especificaciones contienen el carácter prohibido: %s", forbiddenChar), 
                    "FORBIDDEN_CHARACTER");
                break;
            }
        }
    }

    /**
     * Valida la unidad de medida
     */
    private void validateUnitMeasure(String unitMeasure, ValidationResult result) {
        if (unitMeasure == null || unitMeasure.trim().isEmpty()) {
            return; // Unidad de medida es opcional
        }

        String trimmedUnitMeasure = unitMeasure.trim().toUpperCase();

        if (!allowedUnitMeasures.contains(trimmedUnitMeasure)) {
            result.addError("unitMeasure", 
                String.format("Unidad de medida '%s' no es válida. Unidades permitidas: %s", 
                             trimmedUnitMeasure, allowedUnitMeasures), 
                "INVALID_UNIT_MEASURE");
        }
    }

    /**
     * Valida el campo createdBy
     */
    private void validateCreatedBy(String createdBy, ValidationResult result) {
        if (createdBy == null || createdBy.trim().isEmpty()) {
            result.addError("createdBy", "El campo 'createdBy' es requerido", "REQUIRED_FIELD");
            return;
        }

        String trimmedCreatedBy = createdBy.trim();

        if (trimmedCreatedBy.length() < 3 || trimmedCreatedBy.length() > 100) {
            result.addError("createdBy", "El campo 'createdBy' debe tener entre 3 y 100 caracteres", 
                          "INVALID_CREATED_BY_LENGTH");
        }
    }

    /**
     * Verifica si la validación está habilitada
     */
    public boolean isEnabled() {
        return formatValidationEnabled;
    }

    /**
     * Obtiene las unidades de medida permitidas
     */
    public List<String> getAllowedUnitMeasures() {
        return List.copyOf(allowedUnitMeasures);
    }

    /**
     * Obtiene la configuración actual del validador
     */
    public FormatValidatorConfig getConfig() {
        return new FormatValidatorConfig(
            formatValidationEnabled,
            maxNameLength,
            maxDescriptionLength,
            maxSpecificationsLength,
            allowedUnitMeasures.size()
        );
    }

    /**
     * Record para exponer la configuración del validador
     */
    public record FormatValidatorConfig(
        boolean enabled,
        int maxNameLength,
        int maxDescriptionLength,
        int maxSpecificationsLength,
        int allowedUnitMeasuresCount
    ) {}
}