package saf.cgmaig.conceptmanagement.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import saf.cgmaig.conceptmanagement.client.dto.TechnicalConceptDto;
import saf.cgmaig.conceptmanagement.client.dto.TemplateStructure;
import saf.cgmaig.conceptmanagement.model.AreaConcept;
import saf.cgmaig.conceptmanagement.service.AreaConceptCreationRequest;
import saf.cgmaig.conceptmanagement.service.AreaConceptService;
import saf.cgmaig.conceptmanagement.service.AreaConceptUpdateRequest;

import java.util.List;
import java.util.Map;

/**
 * Controller para gestión de conceptos específicos por áreas
 * 
 * Proporciona endpoints para el flujo completo de conceptos específicos:
 * 1. Consultar conceptos base disponibles
 * 2. Obtener templates por capítulo
 * 3. Crear conceptos específicos
 * 4. Enviar para validación
 * 5. Gestión por validadores técnicos
 */
@RestController
@RequestMapping("/api/area-concepts")
public class AreaConceptController {

    private final AreaConceptService areaConceptService;

    @Autowired
    public AreaConceptController(AreaConceptService areaConceptService) {
        this.areaConceptService = areaConceptService;
    }

    /**
     * Obtener conceptos técnicos base disponibles por área
     * Acceso: Usuarios de área y validadores técnicos
     */
    @GetMapping("/base-concepts/{area}")
    @PreAuthorize("hasAnyRole('USER_' + #area, 'VALIDADOR_TECNICO_' + #area)")
    public ResponseEntity<List<TechnicalConceptDto>> getBaseConceptsByArea(@PathVariable String area) {
        List<TechnicalConceptDto> baseConcepts = areaConceptService.getAvailableBaseConcepts(area);
        return ResponseEntity.ok(baseConcepts);
    }

    /**
     * Obtener estructura de template por capítulo
     * Acceso: Cualquier usuario autenticado
     */
    @GetMapping("/templates/{chapterKey}")
    public ResponseEntity<TemplateStructure> getChapterTemplate(@PathVariable String chapterKey) {
        TemplateStructure template = areaConceptService.getChapterTemplate(chapterKey);
        return ResponseEntity.ok(template);
    }

    /**
     * Crear nuevo concepto específico
     * Acceso: Solo usuarios del área correspondiente
     */
    @PostMapping
    @PreAuthorize("hasRole('USER_' + #request.area)")
    public ResponseEntity<AreaConcept> createAreaConcept(
            @Valid @RequestBody AreaConceptCreationRequest request,
            Authentication authentication) {
        
        String createdBy = authentication.getName();
        AreaConcept createdConcept = areaConceptService.createAreaConcept(request, createdBy);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdConcept);
    }

    /**
     * Actualizar concepto específico existente
     * Acceso: Solo el usuario que lo creó o usuarios del área
     */
    @PutMapping("/{conceptId}")
    @PreAuthorize("@areaConceptSecurityService.canEditConcept(#conceptId, authentication)")
    public ResponseEntity<AreaConcept> updateAreaConcept(
            @PathVariable Long conceptId,
            @Valid @RequestBody AreaConceptUpdateRequest request,
            Authentication authentication) {
        
        String updatedBy = authentication.getName();
        AreaConcept updatedConcept = areaConceptService.updateAreaConcept(conceptId, request, updatedBy);
        return ResponseEntity.ok(updatedConcept);
    }

    /**
     * Obtener concepto específico por ID
     * Acceso: Usuarios del área o validadores técnicos
     */
    @GetMapping("/{conceptId}")
    @PreAuthorize("@areaConceptSecurityService.canViewConcept(#conceptId, authentication)")
    public ResponseEntity<AreaConcept> getAreaConcept(@PathVariable Long conceptId) {
        // Se implementará con findById en el servicio
        return ResponseEntity.ok().build(); // TODO: Implementar
    }

    /**
     * Enviar concepto para validación
     * Acceso: Solo el usuario que lo creó o usuarios del área
     */
    @PutMapping("/{conceptId}/submit")
    @PreAuthorize("@areaConceptSecurityService.canSubmitConcept(#conceptId, authentication)")
    public ResponseEntity<AreaConcept> submitForValidation(
            @PathVariable Long conceptId,
            @RequestHeader("Authorization") String authorizationHeader) {
        
        AreaConcept submittedConcept = areaConceptService.submitForValidation(conceptId, authorizationHeader);
        return ResponseEntity.ok(submittedConcept);
    }

    /**
     * Obtener conceptos por área
     * Acceso: Usuarios del área o validadores técnicos
     */
    @GetMapping("/area/{area}")
    @PreAuthorize("hasAnyRole('USER_' + #area, 'VALIDADOR_TECNICO_' + #area)")
    public ResponseEntity<List<AreaConcept>> getConceptsByArea(
            @PathVariable String area,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        if (size == 0) {
            // Sin paginación
            List<AreaConcept> concepts = areaConceptService.getConceptsByArea(area);
            return ResponseEntity.ok(concepts);
        } else {
            // Con paginación
            Pageable pageable = PageRequest.of(page, size);
            Page<AreaConcept> conceptsPage = areaConceptService.getConceptsByArea(area, pageable);
            return ResponseEntity.ok()
                    .header("X-Total-Elements", String.valueOf(conceptsPage.getTotalElements()))
                    .header("X-Total-Pages", String.valueOf(conceptsPage.getTotalPages()))
                    .body(conceptsPage.getContent());
        }
    }

    /**
     * Obtener conceptos pendientes de validación por área
     * Acceso: Solo validadores técnicos del área
     */
    @GetMapping("/area/{area}/pending-validation")
    @PreAuthorize("hasRole('VALIDADOR_TECNICO_' + #area)")
    public ResponseEntity<List<AreaConcept>> getPendingValidationByArea(@PathVariable String area) {
        List<AreaConcept> pendingConcepts = areaConceptService.getPendingValidationByArea(area);
        return ResponseEntity.ok(pendingConcepts);
    }

    /**
     * Buscar conceptos por texto
     * Acceso: Usuarios del área o validadores técnicos
     */
    @GetMapping("/area/{area}/search")
    @PreAuthorize("hasAnyRole('USER_' + #area, 'VALIDADOR_TECNICO_' + #area)")
    public ResponseEntity<List<AreaConcept>> searchConcepts(
            @PathVariable String area,
            @RequestParam String q) {
        
        List<AreaConcept> concepts = areaConceptService.searchConcepts(area, q);
        return ResponseEntity.ok(concepts);
    }

    // ==================== ENDPOINTS PARA VALIDADORES TÉCNICOS ====================

    /**
     * Aprobar concepto específico
     * Acceso: Solo validadores técnicos del área correspondiente
     */
    @PutMapping("/{conceptId}/approve")
    @PreAuthorize("@areaConceptSecurityService.canValidateConcept(#conceptId, authentication)")
    public ResponseEntity<AreaConcept> approveConcept(
            @PathVariable Long conceptId,
            @RequestBody(required = false) Map<String, String> requestBody,
            Authentication authentication) {
        
        String validatedBy = authentication.getName();
        String comments = requestBody != null ? requestBody.getOrDefault("comments", "") : "";
        
        AreaConcept approvedConcept = areaConceptService.approveConcept(conceptId, validatedBy, comments);
        return ResponseEntity.ok(approvedConcept);
    }

    /**
     * Rechazar concepto específico
     * Acceso: Solo validadores técnicos del área correspondiente
     */
    @PutMapping("/{conceptId}/reject")
    @PreAuthorize("@areaConceptSecurityService.canValidateConcept(#conceptId, authentication)")
    public ResponseEntity<AreaConcept> rejectConcept(
            @PathVariable Long conceptId,
            @RequestBody Map<String, String> requestBody,
            Authentication authentication) {
        
        String validatedBy = authentication.getName();
        String comments = requestBody.getOrDefault("comments", "");
        
        if (comments.trim().isEmpty()) {
            return ResponseEntity.badRequest().build(); // Comentarios obligatorios para rechazo
        }
        
        AreaConcept rejectedConcept = areaConceptService.rejectConcept(conceptId, validatedBy, comments);
        return ResponseEntity.ok(rejectedConcept);
    }

    /**
     * Obtener todos los conceptos pendientes de validación
     * Acceso: Solo validadores técnicos de cualquier área
     */
    @GetMapping("/pending-validation")
    @PreAuthorize("hasAnyRole('VALIDADOR_TECNICO_CGRM', 'VALIDADOR_TECNICO_CGSG', 'VALIDADOR_TECNICO_CGMAIG', 'VALIDADOR_TECNICO_PATRIMONIO')")
    public ResponseEntity<Map<String, List<AreaConcept>>> getAllPendingValidation() {
        // Obtener conceptos pendientes agrupados por área
        Map<String, List<AreaConcept>> pendingByArea = Map.of(
            "CGRM", areaConceptService.getPendingValidationByArea("CGRM"),
            "CGSG", areaConceptService.getPendingValidationByArea("CGSG"), 
            "CGMAIG", areaConceptService.getPendingValidationByArea("CGMAIG"),
            "PATRIMONIO", areaConceptService.getPendingValidationByArea("PATRIMONIO")
        );
        
        return ResponseEntity.ok(pendingByArea);
    }

    /**
     * Endpoint de salud del servicio
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> getHealth() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "service", "concept-management-service",
            "description", "Gestión de conceptos específicos por áreas del CUBS"
        ));
    }
}