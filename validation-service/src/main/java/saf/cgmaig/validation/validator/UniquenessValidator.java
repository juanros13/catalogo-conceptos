package saf.cgmaig.validation.validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import saf.cgmaig.validation.client.TechnicalConceptClient;
import saf.cgmaig.validation.client.dto.TechnicalConceptDto;
import saf.cgmaig.validation.model.ValidationRequest;
import saf.cgmaig.validation.model.ValidationResult;
import saf.cgmaig.validation.model.ValidationType;

import java.util.List;

/**
 * Validador de unicidad de conceptos técnicos por área
 * 
 * REGLAS DE NEGOCIO:
 * - El nombre del concepto debe ser único dentro de cada área
 * - La validación puede ser case-sensitive o case-insensitive según configuración
 * - Se excluyen conceptos eliminados de la validación
 * - Para actualizaciones, se excluye el propio concepto de la validación
 * 
 * CONFIGURACIÓN:
 * - cubs.validation.uniqueness.enabled: true/false
 * - cubs.validation.uniqueness.case-sensitive: true/false
 * - cubs.validation.uniqueness.check-deleted-concepts: true/false
 */
@Component
public class UniquenessValidator {

    private static final Logger logger = LoggerFactory.getLogger(UniquenessValidator.class);

    private final TechnicalConceptClient technicalConceptClient;

    @Value("${cubs.validation.uniqueness.enabled:true}")
    private boolean uniquenessValidationEnabled;

    @Value("${cubs.validation.uniqueness.case-sensitive:false}")
    private boolean caseSensitive;

    @Value("${cubs.validation.uniqueness.check-deleted-concepts:false}")
    private boolean checkDeletedConcepts;

    @Autowired
    public UniquenessValidator(TechnicalConceptClient technicalConceptClient) {
        this.technicalConceptClient = technicalConceptClient;
    }

    /**
     * Valida la unicidad del concepto técnico por área
     */
    public ValidationResult validate(ValidationRequest request, String validatedBy) {
        logger.debug("Iniciando validación de unicidad para concepto: {} en área: {}", 
                    request.getName(), request.getArea());

        ValidationResult result = new ValidationResult(ValidationType.UNIQUENESS_VALIDATION, validatedBy);

        // Verificar si la validación está habilitada
        if (!uniquenessValidationEnabled) {
            logger.info("Validación de unicidad deshabilitada por configuración");
            result.addWarning("uniqueness", "Validación de unicidad deshabilitada", "VALIDATION_DISABLED");
            return result;
        }

        // Validar parámetros requeridos
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            result.addError("name", "El nombre del concepto es requerido para validación de unicidad", "REQUIRED_FIELD");
            return result;
        }

        if (request.getArea() == null || request.getArea().trim().isEmpty()) {
            result.addError("area", "El área es requerida para validación de unicidad", "REQUIRED_FIELD");
            return result;
        }

        try {
            // Buscar conceptos existentes con el mismo nombre y área
            List<TechnicalConceptDto> existingConcepts = findExistingConcepts(request);

            // Filtrar conceptos según las reglas de negocio
            List<TechnicalConceptDto> conflictingConcepts = filterConflictingConcepts(existingConcepts, request);

            // Evaluar conflictos
            if (!conflictingConcepts.isEmpty()) {
                handleUniquenessConflict(conflictingConcepts, request, result);
            } else {
                logger.debug("Validación de unicidad exitosa para concepto: {} en área: {}", 
                           request.getName(), request.getArea());
                result.setDetails("Concepto único en el área especificada");
            }

        } catch (Exception e) {
            logger.error("Error durante validación de unicidad para concepto: {} en área: {}", 
                        request.getName(), request.getArea(), e);
            result.addError("system", "Error interno durante validación de unicidad: " + e.getMessage(), 
                          "SYSTEM_ERROR");
        }

        return result;
    }

    /**
     * Busca conceptos existentes que puedan entrar en conflicto
     */
    private List<TechnicalConceptDto> findExistingConcepts(ValidationRequest request) {
        String searchName = caseSensitive ? request.getName() : request.getName().toLowerCase();
        String searchArea = caseSensitive ? request.getArea() : request.getArea().toLowerCase();
        
        logger.debug("Buscando conceptos existentes con nombre: {} en área: {} (case-sensitive: {})", 
                    searchName, searchArea, caseSensitive);

        return technicalConceptClient.findByNameAndArea(searchName, searchArea);
    }

    /**
     * Filtra conceptos que realmente entran en conflicto según las reglas de negocio
     */
    private List<TechnicalConceptDto> filterConflictingConcepts(List<TechnicalConceptDto> existingConcepts, 
                                                               ValidationRequest request) {
        return existingConcepts.stream()
                .filter(concept -> {
                    // Excluir conceptos eliminados si está configurado
                    if (!checkDeletedConcepts && "INACTIVO".equals(concept.getStatus())) {
                        return false;
                    }

                    // Para actualizaciones, excluir el propio concepto
                    if (request.getConceptId() != null && request.getConceptId().equals(concept.getId())) {
                        return false;
                    }

                    // Aplicar comparación case-sensitive/insensitive
                    if (caseSensitive) {
                        return concept.getName().equals(request.getName()) && 
                               concept.getArea().equals(request.getArea());
                    } else {
                        return concept.getName().equalsIgnoreCase(request.getName()) && 
                               concept.getArea().equalsIgnoreCase(request.getArea());
                    }
                })
                .toList();
    }

    /**
     * Maneja conflictos de unicidad encontrados
     */
    private void handleUniquenessConflict(List<TechnicalConceptDto> conflictingConcepts, 
                                        ValidationRequest request, ValidationResult result) {
        
        TechnicalConceptDto conflictingConcept = conflictingConcepts.get(0);
        
        logger.warn("Conflicto de unicidad detectado para concepto: {} en área: {}. " +
                   "Concepto existente ID: {}, creado por: {}", 
                   request.getName(), request.getArea(), 
                   conflictingConcept.getId(), conflictingConcept.getCreatedBy());

        String errorMessage = String.format(
            "Ya existe un concepto con el nombre '%s' en el área '%s'. " +
            "Concepto existente: ID %d, creado por %s el %s",
            conflictingConcept.getName(),
            conflictingConcept.getArea(),
            conflictingConcept.getId(),
            conflictingConcept.getCreatedBy(),
            conflictingConcept.getCreatedAt()
        );

        result.addError("name", errorMessage, "UNIQUENESS_VIOLATION");

        // Información adicional para debugging
        result.setDetails(String.format("Conflictos encontrados: %d", conflictingConcepts.size()));
    }

    /**
     * Verifica si la validación de unicidad está habilitada
     */
    public boolean isEnabled() {
        return uniquenessValidationEnabled;
    }

    /**
     * Obtiene la configuración actual del validador
     */
    public UniquenessValidatorConfig getConfig() {
        return new UniquenessValidatorConfig(uniquenessValidationEnabled, caseSensitive, checkDeletedConcepts);
    }

    /**
     * Record para exponer la configuración del validador
     */
    public record UniquenessValidatorConfig(
        boolean enabled,
        boolean caseSensitive,
        boolean checkDeletedConcepts
    ) {}
}