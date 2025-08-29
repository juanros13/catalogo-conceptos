package saf.cgmaig.technicalconcept.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import saf.cgmaig.technicalconcept.dto.TechnicalConceptCreateRequest;
import saf.cgmaig.technicalconcept.dto.TechnicalConceptResponse;
import saf.cgmaig.technicalconcept.dto.TechnicalConceptUpdateRequest;
import saf.cgmaig.technicalconcept.entity.AreaFacultada;
import saf.cgmaig.technicalconcept.entity.ConceptStatus;
import saf.cgmaig.technicalconcept.service.TechnicalConceptService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/general-concepts")
@CrossOrigin(origins = "*", maxAge = 3600)
public class TechnicalConceptController {

    private static final Logger logger = LoggerFactory.getLogger(TechnicalConceptController.class);

    @Autowired
    private TechnicalConceptService service;

    /**
     * Obtener conceptos activos para capturistas
     * GET /api/general-concepts?status=ACTIVO&capitulo=2000
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('CAPTURISTA', 'VALIDADOR_TECNICO_CGRM', 'VALIDADOR_TECNICO_CGSG', 'VALIDADOR_TECNICO_CGMAIG', 'VALIDADOR_TECNICO_PATRIMONIO')")
    public ResponseEntity<List<TechnicalConceptResponse>> getActiveConcepts(
            @RequestParam(required = false) ConceptStatus status,
            @RequestParam(required = false) Integer capitulo) {
        
        logger.info("Obteniendo conceptos generales - status: {}, capítulo: {}", status, capitulo);

        List<TechnicalConceptResponse> concepts;
        
        if (capitulo != null) {
            concepts = service.getActiveConceptsByCapitulo(capitulo);
        } else {
            concepts = service.getActiveConceptsForCapture();
        }

        return ResponseEntity.ok(concepts);
    }

    /**
     * Crear nuevo concepto general
     * POST /api/general-concepts
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('VALIDADOR_TECNICO_CGRM', 'VALIDADOR_TECNICO_CGSG', 'VALIDADOR_TECNICO_CGMAIG', 'VALIDADOR_TECNICO_PATRIMONIO')")
    public ResponseEntity<TechnicalConceptResponse> createConcept(
            @Valid @RequestBody TechnicalConceptCreateRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        
        String userCurp = jwt.getClaimAsString("preferred_username");
        AreaFacultada userArea = extractAreaFromJwt(jwt);
        
        logger.info("Creando concepto general {} por usuario {} del área {}", 
                   request.getNombre(), userCurp, userArea);

        // Validar que el usuario puede crear conceptos para esta área
        if (!userArea.equals(request.getAreaFacultada())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        TechnicalConceptResponse response = service.create(request, userCurp);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Actualizar concepto general existente
     * PUT /api/general-concepts/{id}
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('VALIDADOR_TECNICO_CGRM', 'VALIDADOR_TECNICO_CGSG', 'VALIDADOR_TECNICO_CGMAIG', 'VALIDADOR_TECNICO_PATRIMONIO')")
    public ResponseEntity<TechnicalConceptResponse> updateConcept(
            @PathVariable UUID id,
            @Valid @RequestBody TechnicalConceptUpdateRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        
        String userCurp = jwt.getClaimAsString("preferred_username");
        AreaFacultada userArea = extractAreaFromJwt(jwt);
        
        logger.info("Actualizando concepto general {} por usuario {} del área {}", 
                   id, userCurp, userArea);

        TechnicalConceptResponse response = service.update(id, request, userArea, userCurp);
        return ResponseEntity.ok(response);
    }

    /**
     * Inactivar concepto general
     * DELETE /api/general-concepts/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('VALIDADOR_TECNICO_CGRM', 'VALIDADOR_TECNICO_CGSG', 'VALIDADOR_TECNICO_CGMAIG', 'VALIDADOR_TECNICO_PATRIMONIO')")
    public ResponseEntity<TechnicalConceptResponse> inactivateConcept(
            @PathVariable UUID id,
            @RequestParam String motivo,
            @AuthenticationPrincipal Jwt jwt) {
        
        String userCurp = jwt.getClaimAsString("preferred_username");
        AreaFacultada userArea = extractAreaFromJwt(jwt);
        
        logger.info("Inactivando concepto general {} por usuario {} del área {}", 
                   id, userCurp, userArea);

        TechnicalConceptResponse response = service.inactivate(id, motivo, userArea, userCurp);
        return ResponseEntity.ok(response);
    }

    /**
     * Reactivar concepto general
     * PATCH /api/general-concepts/{id}/reactivate
     */
    @PatchMapping("/{id}/reactivate")
    @PreAuthorize("hasAnyRole('VALIDADOR_TECNICO_CGRM', 'VALIDADOR_TECNICO_CGSG', 'VALIDADOR_TECNICO_CGMAIG', 'VALIDADOR_TECNICO_PATRIMONIO')")
    public ResponseEntity<TechnicalConceptResponse> reactivateConcept(
            @PathVariable UUID id,
            @RequestParam String motivo,
            @AuthenticationPrincipal Jwt jwt) {
        
        String userCurp = jwt.getClaimAsString("preferred_username");
        AreaFacultada userArea = extractAreaFromJwt(jwt);
        
        logger.info("Reactivando concepto general {} por usuario {} del área {}", 
                   id, userCurp, userArea);

        TechnicalConceptResponse response = service.reactivate(id, motivo, userArea, userCurp);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtener concepto por ID
     * GET /api/general-concepts/{id}
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CAPTURISTA', 'VALIDADOR_TECNICO_CGRM', 'VALIDADOR_TECNICO_CGSG', 'VALIDADOR_TECNICO_CGMAIG', 'VALIDADOR_TECNICO_PATRIMONIO')")
    public ResponseEntity<TechnicalConceptResponse> getConceptById(@PathVariable UUID id) {
        logger.debug("Obteniendo concepto general por ID: {}", id);
        
        TechnicalConceptResponse response = service.getById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Búsqueda avanzada de conceptos (para gestión por áreas)
     * GET /api/general-concepts/search
     */
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('VALIDADOR_TECNICO_CGRM', 'VALIDADOR_TECNICO_CGSG', 'VALIDADOR_TECNICO_CGMAIG', 'VALIDADOR_TECNICO_PATRIMONIO', 'ADMIN_SISTEMA')")
    public ResponseEntity<Page<TechnicalConceptResponse>> searchConcepts(
            @RequestParam(required = false) Integer capitulo,
            @RequestParam(required = false) AreaFacultada areaFacultada,
            @RequestParam(required = false) ConceptStatus estado,
            @RequestParam(required = false) Boolean activo,
            @RequestParam(required = false) String searchTerm,
            Pageable pageable,
            @AuthenticationPrincipal Jwt jwt) {
        
        AreaFacultada userArea = extractAreaFromJwt(jwt);
        
        // Si no es admin, filtrar solo por su área
        if (!hasAdminRole(jwt)) {
            areaFacultada = userArea;
        }
        
        logger.debug("Búsqueda avanzada - capítulo: {}, área: {}, estado: {}, activo: {}, término: {}", 
                    capitulo, areaFacultada, estado, activo, searchTerm);

        Page<TechnicalConceptResponse> results = service.searchConcepts(
            capitulo, areaFacultada, estado, activo, searchTerm, pageable);
        
        return ResponseEntity.ok(results);
    }

    /**
     * Obtener conceptos por área facultada (para gestión)
     * GET /api/general-concepts/my-area
     */
    @GetMapping("/my-area")
    @PreAuthorize("hasAnyRole('VALIDADOR_TECNICO_CGRM', 'VALIDADOR_TECNICO_CGSG', 'VALIDADOR_TECNICO_CGMAIG', 'VALIDADOR_TECNICO_PATRIMONIO')")
    public ResponseEntity<Page<TechnicalConceptResponse>> getConceptsByMyArea(
            Pageable pageable,
            @AuthenticationPrincipal Jwt jwt) {
        
        AreaFacultada userArea = extractAreaFromJwt(jwt);
        
        logger.debug("Obteniendo conceptos del área: {}", userArea);

        Page<TechnicalConceptResponse> concepts = service.getConceptsByArea(userArea, pageable);
        return ResponseEntity.ok(concepts);
    }

    /**
     * Extraer área facultada del JWT basado en roles
     */
    private AreaFacultada extractAreaFromJwt(Jwt jwt) {
        var realmAccess = jwt.getClaimAsMap("realm_access");
        if (realmAccess != null && realmAccess.containsKey("roles")) {
            @SuppressWarnings("unchecked")
            var roles = (List<String>) realmAccess.get("roles");
            
            if (roles.contains("ValidadorTecnico_CGRM")) {
                return AreaFacultada.CGRM;
            } else if (roles.contains("ValidadorTecnico_CGSG")) {
                return AreaFacultada.CGSG;
            } else if (roles.contains("ValidadorTecnico_CGMAIG")) {
                return AreaFacultada.CGMAIG;
            } else if (roles.contains("ValidadorTecnico_Patrimonio")) {
                return AreaFacultada.PATRIMONIO;
            }
        }
        
        throw new SecurityException("Usuario no tiene rol de área facultada válido");
    }

    /**
     * Verificar si el usuario tiene rol de administrador
     */
    private boolean hasAdminRole(Jwt jwt) {
        var realmAccess = jwt.getClaimAsMap("realm_access");
        if (realmAccess != null && realmAccess.containsKey("roles")) {
            @SuppressWarnings("unchecked")
            var roles = (List<String>) realmAccess.get("roles");
            return roles.contains("AdminSistema");
        }
        return false;
    }

    /**
     * Manejo global de excepciones
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException e) {
        logger.warn("Error de argumento: {}", e.getMessage());
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<String> handleSecurity(SecurityException e) {
        logger.warn("Error de seguridad: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> handleIllegalState(IllegalStateException e) {
        logger.warn("Error de estado: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }
}