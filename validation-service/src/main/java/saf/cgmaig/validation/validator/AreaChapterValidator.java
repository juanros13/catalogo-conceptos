package saf.cgmaig.validation.validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import saf.cgmaig.validation.model.ValidationRequest;
import saf.cgmaig.validation.model.ValidationResult;
import saf.cgmaig.validation.model.ValidationType;

import java.util.Map;
import java.util.Set;

/**
 * Validador de relaciones área-capítulo para conceptos técnicos del CUBS
 * 
 * REGLAS DE NEGOCIO:
 * - Cada área tiene capítulos específicos permitidos
 * - La combinación área-capítulo debe ser válida según el catálogo CUBS
 * - Algunas combinaciones pueden estar temporalmente deshabilitadas
 * 
 * ÁREAS Y CAPÍTULOS VÁLIDOS CUBS:
 * - CGMAIG: SOFTWARE, HARDWARE, SERVICIOS_TI, CONSULTORIA
 * - CGRM: MOBILIARIO, EQUIPO_OFICINA, VEHICULOS
 * - CGSG: MANTENIMIENTO, LIMPIEZA, VIGILANCIA
 * - PATRIMONIO: INMUEBLES, TERRENOS, INFRAESTRUCTURA
 * 
 * CONFIGURACIÓN:
 * - cubs.validation.area-chapter.enabled: true/false
 * - cubs.validation.area-chapter.strict-validation: true/false
 */
@Component
public class AreaChapterValidator {

    private static final Logger logger = LoggerFactory.getLogger(AreaChapterValidator.class);

    @Value("${cubs.validation.area-chapter.enabled:true}")
    private boolean areaChapterValidationEnabled;

    @Value("${cubs.validation.area-chapter.strict-validation:true}")
    private boolean strictValidation;

    // Combinaciones válidas área-capítulo según CUBS
    private static final Map<String, Set<String>> VALID_AREA_CHAPTER_COMBINATIONS = Map.of(
        "CGMAIG", Set.of("SOFTWARE", "HARDWARE", "SERVICIOS_TI", "CONSULTORIA", "TELECOMUNICACIONES"),
        "CGRM", Set.of("MOBILIARIO", "EQUIPO_OFICINA", "VEHICULOS", "COMBUSTIBLES", "REFACCIONES"),
        "CGSG", Set.of("MANTENIMIENTO", "LIMPIEZA", "VIGILANCIA", "JARDINERIA", "FUMIGACION"),
        "PATRIMONIO", Set.of("INMUEBLES", "TERRENOS", "INFRAESTRUCTURA", "CONSTRUCCION", "REMODELACION")
    );

    // Combinaciones temporalmente deshabilitadas
    private static final Set<String> DISABLED_COMBINATIONS = Set.of(
        "CGMAIG:TELECOMUNICACIONES", // Temporalmente deshabilitado
        "CGRM:REFACCIONES" // En revisión
    );

    /**
     * Valida la relación área-capítulo del concepto técnico
     */
    public ValidationResult validate(ValidationRequest request, String validatedBy) {
        logger.debug("Iniciando validación área-capítulo para: {} - {}", 
                    request.getArea(), request.getChapter());

        ValidationResult result = new ValidationResult(ValidationType.AREA_CHAPTER_VALIDATION, validatedBy);

        // Verificar si la validación está habilitada
        if (!areaChapterValidationEnabled) {
            logger.info("Validación área-capítulo deshabilitada por configuración");
            result.addWarning("area-chapter", "Validación área-capítulo deshabilitada", "VALIDATION_DISABLED");
            return result;
        }

        // Validar parámetros requeridos
        if (request.getArea() == null || request.getArea().trim().isEmpty()) {
            result.addError("area", "El área es requerida para validación área-capítulo", "REQUIRED_FIELD");
            return result;
        }

        if (request.getChapter() == null || request.getChapter().trim().isEmpty()) {
            result.addError("chapter", "El capítulo es requerido para validación área-capítulo", "REQUIRED_FIELD");
            return result;
        }

        try {
            String area = request.getArea().toUpperCase().trim();
            String chapter = request.getChapter().toUpperCase().trim();

            // Validar que el área existe
            if (!VALID_AREA_CHAPTER_COMBINATIONS.containsKey(area)) {
                result.addError("area", 
                    String.format("Área '%s' no es válida. Áreas válidas: %s", 
                                 area, VALID_AREA_CHAPTER_COMBINATIONS.keySet()), 
                    "INVALID_AREA");
                return result;
            }

            // Validar que el capítulo es válido para el área
            Set<String> validChapters = VALID_AREA_CHAPTER_COMBINATIONS.get(area);
            if (!validChapters.contains(chapter)) {
                result.addError("chapter", 
                    String.format("Capítulo '%s' no es válido para el área '%s'. Capítulos válidos: %s", 
                                 chapter, area, validChapters), 
                    "INVALID_CHAPTER_FOR_AREA");
                return result;
            }

            // Verificar si la combinación está temporalmente deshabilitada
            String combination = area + ":" + chapter;
            if (DISABLED_COMBINATIONS.contains(combination)) {
                if (strictValidation) {
                    result.addError("area-chapter", 
                        String.format("La combinación %s está temporalmente deshabilitada", combination), 
                        "COMBINATION_DISABLED");
                } else {
                    result.addWarning("area-chapter", 
                        String.format("La combinación %s está temporalmente deshabilitada pero se permite por configuración", combination), 
                        "COMBINATION_DISABLED_WARNING");
                }
                return result;
            }

            // Validación exitosa
            logger.debug("Validación área-capítulo exitosa para: {} - {}", area, chapter);
            result.setDetails(String.format("Combinación área-capítulo válida: %s - %s", area, chapter));

        } catch (Exception e) {
            logger.error("Error durante validación área-capítulo para: {} - {}", 
                        request.getArea(), request.getChapter(), e);
            result.addError("system", "Error interno durante validación área-capítulo: " + e.getMessage(), 
                          "SYSTEM_ERROR");
        }

        return result;
    }

    /**
     * Obtiene todas las combinaciones válidas área-capítulo
     */
    public Map<String, Set<String>> getValidCombinations() {
        return Map.copyOf(VALID_AREA_CHAPTER_COMBINATIONS);
    }

    /**
     * Obtiene los capítulos válidos para un área específica
     */
    public Set<String> getValidChaptersForArea(String area) {
        if (area == null) {
            return Set.of();
        }
        return VALID_AREA_CHAPTER_COMBINATIONS.getOrDefault(area.toUpperCase().trim(), Set.of());
    }

    /**
     * Verifica si una combinación área-capítulo es válida
     */
    public boolean isValidCombination(String area, String chapter) {
        if (area == null || chapter == null) {
            return false;
        }
        
        String normalizedArea = area.toUpperCase().trim();
        String normalizedChapter = chapter.toUpperCase().trim();
        
        Set<String> validChapters = VALID_AREA_CHAPTER_COMBINATIONS.get(normalizedArea);
        return validChapters != null && validChapters.contains(normalizedChapter);
    }

    /**
     * Verifica si una combinación está deshabilitada
     */
    public boolean isCombinationDisabled(String area, String chapter) {
        if (area == null || chapter == null) {
            return false;
        }
        
        String combination = area.toUpperCase().trim() + ":" + chapter.toUpperCase().trim();
        return DISABLED_COMBINATIONS.contains(combination);
    }

    /**
     * Verifica si la validación está habilitada
     */
    public boolean isEnabled() {
        return areaChapterValidationEnabled;
    }

    /**
     * Obtiene la configuración actual del validador
     */
    public AreaChapterValidatorConfig getConfig() {
        return new AreaChapterValidatorConfig(
            areaChapterValidationEnabled, 
            strictValidation, 
            VALID_AREA_CHAPTER_COMBINATIONS.size(),
            DISABLED_COMBINATIONS.size()
        );
    }

    /**
     * Record para exponer la configuración del validador
     */
    public record AreaChapterValidatorConfig(
        boolean enabled,
        boolean strictValidation,
        int totalCombinations,
        int disabledCombinations
    ) {}
}