package saf.cgmaig.validation.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import saf.cgmaig.validation.model.ValidationRequest;
import saf.cgmaig.validation.model.ValidationResult;
import saf.cgmaig.validation.model.ValidationType;
import saf.cgmaig.validation.service.ValidationService;

import java.util.List;
import java.util.Map;

/**
 * Controlador REST para el Validation Service
 * 
 * Proporciona endpoints para la validación de conceptos técnicos del CUBS,
 * incluyendo validaciones específicas y consulta de reglas de validación.
 * 
 * ENDPOINTS PRINCIPALES:
 * - POST /api/validation/concept - Validación completa de concepto
 * - POST /api/validation/uniqueness - Validación de unicidad
 * - POST /api/validation/area-chapter - Validación área-capítulo
 * - POST /api/validation/format - Validación de formatos
 * - GET /api/validation/rules - Consulta de reglas activas
 */
@RestController
@RequestMapping("/api/validation")
@CrossOrigin(origins = "*")
public class ValidationController {

    private static final Logger logger = LoggerFactory.getLogger(ValidationController.class);

    private final ValidationService validationService;

    @Autowired
    public ValidationController(ValidationService validationService) {
        this.validationService = validationService;
    }

    /**
     * Endpoint para validación completa de un concepto técnico
     * Ejecuta todas las validaciones disponibles
     */
    @PostMapping("/concept")
    @PreAuthorize("hasAnyRole('VALIDADOR_TECNICO_CGRM', 'VALIDADOR_TECNICO_CGSG', 'VALIDADOR_TECNICO_CGMAIG', 'VALIDADOR_TECNICO_PATRIMONIO')")
    public ResponseEntity<ValidationResult> validateConcept(
            @Valid @RequestBody ValidationRequest request,
            Authentication authentication) {
        
        try {
            logger.info("Iniciando validación completa de concepto: {} por usuario: {}", 
                       request.getName(), authentication.getName());
            
            ValidationResult result = validationService.validateConcept(request, authentication.getName());
            
            HttpStatus status = result.isValid() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
            logger.info("Validación completa finalizada - Válido: {} para concepto: {}", 
                       result.isValid(), request.getName());
            
            return new ResponseEntity<>(result, status);
            
        } catch (Exception e) {
            logger.error("Error durante validación completa de concepto: {}", request.getName(), e);
            ValidationResult errorResult = new ValidationResult(ValidationType.FULL_VALIDATION, authentication.getName());
            errorResult.addError("system", "Error interno durante la validación: " + e.getMessage(), "SYSTEM_ERROR");
            return new ResponseEntity<>(errorResult, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Endpoint para validación específica de unicidad
     * Verifica que el nombre sea único dentro del área
     */
    @PostMapping("/uniqueness")
    @PreAuthorize("hasAnyRole('VALIDADOR_TECNICO_CGRM', 'VALIDADOR_TECNICO_CGSG', 'VALIDADOR_TECNICO_CGMAIG', 'VALIDADOR_TECNICO_PATRIMONIO')")
    public ResponseEntity<ValidationResult> validateUniqueness(
            @Valid @RequestBody ValidationRequest request,
            Authentication authentication) {
        
        try {
            logger.debug("Validando unicidad para concepto: {} en área: {}", request.getName(), request.getArea());
            
            ValidationResult result = validationService.validateUniqueness(request, authentication.getName());
            
            HttpStatus status = result.isValid() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
            return new ResponseEntity<>(result, status);
            
        } catch (Exception e) {
            logger.error("Error durante validación de unicidad para concepto: {}", request.getName(), e);
            ValidationResult errorResult = new ValidationResult(ValidationType.UNIQUENESS_VALIDATION, authentication.getName());
            errorResult.addError("system", "Error interno durante validación de unicidad: " + e.getMessage(), "SYSTEM_ERROR");
            return new ResponseEntity<>(errorResult, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Endpoint para validación de relación área-capítulo
     * Verifica que la combinación área-capítulo sea válida según las reglas del CUBS
     */
    @PostMapping("/area-chapter")
    @PreAuthorize("hasAnyRole('VALIDADOR_TECNICO_CGRM', 'VALIDADOR_TECNICO_CGSG', 'VALIDADOR_TECNICO_CGMAIG', 'VALIDADOR_TECNICO_PATRIMONIO')")
    public ResponseEntity<ValidationResult> validateAreaChapter(
            @Valid @RequestBody ValidationRequest request,
            Authentication authentication) {
        
        try {
            logger.debug("Validando relación área-capítulo: {} - {}", request.getArea(), request.getChapter());
            
            ValidationResult result = validationService.validateAreaChapter(request, authentication.getName());
            
            HttpStatus status = result.isValid() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
            return new ResponseEntity<>(result, status);
            
        } catch (Exception e) {
            logger.error("Error durante validación área-capítulo: {} - {}", request.getArea(), request.getChapter(), e);
            ValidationResult errorResult = new ValidationResult(ValidationType.AREA_CHAPTER_VALIDATION, authentication.getName());
            errorResult.addError("system", "Error interno durante validación área-capítulo: " + e.getMessage(), "SYSTEM_ERROR");
            return new ResponseEntity<>(errorResult, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Endpoint para validación de formatos y especificaciones
     * Verifica formatos de campos, longitudes y valores permitidos
     */
    @PostMapping("/format")
    @PreAuthorize("hasAnyRole('VALIDADOR_TECNICO_CGRM', 'VALIDADOR_TECNICO_CGSG', 'VALIDADOR_TECNICO_CGMAIG', 'VALIDADOR_TECNICO_PATRIMONIO')")
    public ResponseEntity<ValidationResult> validateFormat(
            @Valid @RequestBody ValidationRequest request,
            Authentication authentication) {
        
        try {
            logger.debug("Validando formatos para concepto: {}", request.getName());
            
            ValidationResult result = validationService.validateFormat(request, authentication.getName());
            
            HttpStatus status = result.isValid() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
            return new ResponseEntity<>(result, status);
            
        } catch (Exception e) {
            logger.error("Error durante validación de formato para concepto: {}", request.getName(), e);
            ValidationResult errorResult = new ValidationResult(ValidationType.FORMAT_VALIDATION, authentication.getName());
            errorResult.addError("system", "Error interno durante validación de formato: " + e.getMessage(), "SYSTEM_ERROR");
            return new ResponseEntity<>(errorResult, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Endpoint para obtener las reglas de validación activas
     * Útil para que el frontend muestre las reglas al usuario
     */
    @GetMapping("/rules")
    @PreAuthorize("hasAnyRole('VALIDADOR_TECNICO_CGRM', 'VALIDADOR_TECNICO_CGSG', 'VALIDADOR_TECNICO_CGMAIG', 'VALIDADOR_TECNICO_PATRIMONIO')")
    public ResponseEntity<Map<String, Object>> getValidationRules() {
        
        try {
            logger.debug("Consultando reglas de validación activas");
            
            Map<String, Object> rules = validationService.getValidationRules();
            return ResponseEntity.ok(rules);
            
        } catch (Exception e) {
            logger.error("Error al consultar reglas de validación", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Endpoint para obtener reglas específicas por tipo de validación
     */
    @GetMapping("/rules/{validationType}")
    @PreAuthorize("hasAnyRole('VALIDADOR_TECNICO_CGRM', 'VALIDADOR_TECNICO_CGSG', 'VALIDADOR_TECNICO_CGMAIG', 'VALIDADOR_TECNICO_PATRIMONIO')")
    public ResponseEntity<Map<String, Object>> getValidationRulesByType(@PathVariable ValidationType validationType) {
        
        try {
            logger.debug("Consultando reglas para tipo de validación: {}", validationType);
            
            Map<String, Object> rules = validationService.getValidationRulesByType(validationType);
            return ResponseEntity.ok(rules);
            
        } catch (Exception e) {
            logger.error("Error al consultar reglas para tipo: {}", validationType, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Endpoint de salud específico para el servicio de validación
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> health = Map.of(
            "status", "UP",
            "service", "validation-service",
            "timestamp", System.currentTimeMillis(),
            "validatorsEnabled", validationService.getEnabledValidators()
        );
        
        return ResponseEntity.ok(health);
    }
}