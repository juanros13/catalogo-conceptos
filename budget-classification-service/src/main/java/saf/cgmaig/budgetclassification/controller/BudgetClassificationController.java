package saf.cgmaig.budgetclassification.controller;

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
import saf.cgmaig.budgetclassification.dto.*;
import saf.cgmaig.budgetclassification.entity.BudgetLevel;
import saf.cgmaig.budgetclassification.service.BudgetClassificationService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/budget-classifications")
@CrossOrigin(origins = "*", maxAge = 3600)
public class BudgetClassificationController {

    private static final Logger logger = LoggerFactory.getLogger(BudgetClassificationController.class);

    @Autowired
    private BudgetClassificationService service;

    /**
     * Health check específico del servicio
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "service", "budget-classification-service",
            "message", "Servicio de clasificación presupuestaria funcionando correctamente"
        ));
    }

    /**
     * Crear nueva clasificación presupuestaria
     * Solo administradores del sistema
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN_SISTEMA', 'CONFIGURADOR_PRESUPUESTO')")
    public ResponseEntity<BudgetClassificationResponse> create(
            @Valid @RequestBody BudgetClassificationCreateRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        
        String userCurp = jwt.getClaimAsString("preferred_username");
        logger.info("Creando clasificación presupuestaria {} por usuario {}", 
                   request.getCodigo(), userCurp);

        BudgetClassificationResponse response = service.create(request, userCurp);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Actualizar clasificación existente
     * Solo administradores del sistema
     */
    @PutMapping("/{codigo}")
    @PreAuthorize("hasAnyRole('ADMIN_SISTEMA', 'CONFIGURADOR_PRESUPUESTO')")
    public ResponseEntity<BudgetClassificationResponse> update(
            @PathVariable String codigo,
            @Valid @RequestBody BudgetClassificationUpdateRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        
        String userCurp = jwt.getClaimAsString("preferred_username");
        logger.info("Actualizando clasificación presupuestaria {} por usuario {}", 
                   codigo, userCurp);

        BudgetClassificationResponse response = service.update(codigo, request, userCurp);
        return ResponseEntity.ok(response);
    }

    /**
     * Activar/Inactivar clasificación
     * Solo administradores del sistema
     */
    @PatchMapping("/{codigo}/toggle-active")
    @PreAuthorize("hasAnyRole('ADMIN_SISTEMA', 'CONFIGURADOR_PRESUPUESTO')")
    public ResponseEntity<BudgetClassificationResponse> toggleActive(
            @PathVariable String codigo,
            @RequestParam boolean activo,
            @AuthenticationPrincipal Jwt jwt) {
        
        String userCurp = jwt.getClaimAsString("preferred_username");
        logger.info("Cambiando estado de {} a {} por usuario {}", 
                   codigo, activo, userCurp);

        BudgetClassificationResponse response = service.toggleActive(codigo, activo, userCurp);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtener clasificación por código
     * Acceso público para consulta
     */
    @GetMapping("/{codigo}")
    public ResponseEntity<BudgetClassificationResponse> getByCodigo(@PathVariable String codigo) {
        logger.debug("Obteniendo clasificación por código: {}", codigo);
        
        BudgetClassificationResponse response = service.getByCodigo(codigo);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtener todos los capítulos (nivel raíz)
     * Acceso público para consulta
     */
    @GetMapping("/capitulos")
    public ResponseEntity<List<BudgetClassificationResponse>> getCapitulos() {
        logger.debug("Obteniendo todos los capítulos");
        
        List<BudgetClassificationResponse> capitulos = service.getCapitulos();
        return ResponseEntity.ok(capitulos);
    }

    /**
     * Obtener hijos directos de un código
     * Acceso público para consulta
     */
    @GetMapping("/{padreCodigo}/hijos")
    public ResponseEntity<List<BudgetClassificationResponse>> getHijos(@PathVariable String padreCodigo) {
        logger.debug("Obteniendo hijos del código: {}", padreCodigo);
        
        List<BudgetClassificationResponse> hijos = service.getHijos(padreCodigo);
        return ResponseEntity.ok(hijos);
    }

    /**
     * Obtener jerarquía completa desde un código
     * Acceso público para consulta
     */
    @GetMapping("/{codigoRaiz}/jerarquia")
    public ResponseEntity<HierarchyResponse> getJerarquiaCompleta(@PathVariable String codigoRaiz) {
        logger.debug("Obteniendo jerarquía completa desde: {}", codigoRaiz);
        
        HierarchyResponse jerarquia = service.getJerarquiaCompleta(codigoRaiz);
        return ResponseEntity.ok(jerarquia);
    }

    /**
     * Obtener por nivel específico (paginado)
     * Acceso público para consulta
     */
    @GetMapping("/nivel/{nivel}")
    public ResponseEntity<Page<BudgetClassificationResponse>> getByNivel(
            @PathVariable BudgetLevel nivel,
            Pageable pageable) {
        
        logger.debug("Obteniendo clasificaciones por nivel: {}", nivel);
        
        Page<BudgetClassificationResponse> page = service.getByNivel(nivel, pageable);
        return ResponseEntity.ok(page);
    }

    /**
     * Búsqueda avanzada con múltiples filtros
     * Acceso público para consulta
     */
    @GetMapping("/search")
    public ResponseEntity<Page<BudgetClassificationResponse>> search(
            @RequestParam(required = false) BudgetLevel nivel,
            @RequestParam(required = false) String padreCodigo,
            @RequestParam(required = false) Boolean activo,
            @RequestParam(required = false) String searchTerm,
            Pageable pageable) {
        
        logger.debug("Búsqueda avanzada - nivel: {}, padre: {}, activo: {}, término: {}", 
                    nivel, padreCodigo, activo, searchTerm);
        
        Page<BudgetClassificationResponse> results = service.search(
            nivel, padreCodigo, activo, searchTerm, pageable);
        return ResponseEntity.ok(results);
    }

    /**
     * Búsqueda por texto libre
     * Acceso público para consulta
     */
    @GetMapping("/search/text")
    public ResponseEntity<List<BudgetClassificationResponse>> searchByText(
            @RequestParam String searchTerm) {
        
        logger.debug("Búsqueda por texto: {}", searchTerm);
        
        List<BudgetClassificationResponse> results = service.searchByText(searchTerm);
        return ResponseEntity.ok(results);
    }

    /**
     * Obtener breadcrumb (ruta hacia arriba)
     * Acceso público para consulta
     */
    @GetMapping("/{codigo}/breadcrumb")
    public ResponseEntity<List<BudgetClassificationResponse>> getBreadcrumb(@PathVariable String codigo) {
        logger.debug("Obteniendo breadcrumb para: {}", codigo);
        
        List<BudgetClassificationResponse> breadcrumb = service.getBreadcrumb(codigo);
        return ResponseEntity.ok(breadcrumb);
    }

    /**
     * Validar si un código puede ser eliminado
     * Solo administradores del sistema
     */
    @GetMapping("/{codigo}/can-delete")
    @PreAuthorize("hasAnyRole('ADMIN_SISTEMA', 'CONFIGURADOR_PRESUPUESTO')")
    public ResponseEntity<Map<String, Object>> canDelete(@PathVariable String codigo) {
        logger.debug("Verificando si se puede eliminar: {}", codigo);
        
        boolean canDelete = service.canDelete(codigo);
        return ResponseEntity.ok(Map.of(
            "codigo", codigo,
            "canDelete", canDelete,
            "message", canDelete ? "Código puede ser eliminado" : "Código tiene dependencias activas"
        ));
    }

    /**
     * Obtener estadísticas por nivel
     * Acceso para usuarios autenticados
     */
    @GetMapping("/statistics")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        logger.debug("Obteniendo estadísticas del sistema");
        
        Map<BudgetLevel, Long> stats = service.getStatistics();
        
        Map<String, Object> response = Map.of(
            "statistics", stats,
            "total", stats.values().stream().mapToLong(Long::longValue).sum(),
            "generatedAt", java.time.LocalDateTime.now()
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * Obtener información del servicio
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> info() {
        return ResponseEntity.ok(Map.of(
            "service", "budget-classification-service",
            "version", "1.0.0",
            "description", "Servicio de Clasificación Presupuestaria CUBS - 4 Niveles Jerárquicos",
            "levels", Map.of(
                "CAPITULO", "Nivel 1 - Termina en 000 (ej: 2000)",
                "PARTIDA_GENERICA", "Nivel 2 - Termina en 00 (ej: 2100)",
                "PARTIDA_ESPECIFICA", "Nivel 3 - Termina en 0 (ej: 2110)",
                "PARTIDA", "Nivel 4 - No termina en 0 (ej: 2111)"
            ),
            "features", List.of(
                "Gestión jerárquica de 4 niveles",
                "Validación automática de códigos",
                "Consultas optimizadas con cache",
                "Búsqueda avanzada y por texto",
                "APIs públicas para consulta",
                "Administración granular por roles"
            )
        ));
    }

    /**
     * Manejo de excepciones
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException e) {
        logger.warn("Error de argumento: {}", e.getMessage());
        return ResponseEntity.badRequest().body(Map.of(
            "error", "Argumento inválido",
            "message", e.getMessage()
        ));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> handleIllegalState(IllegalStateException e) {
        logger.warn("Error de estado: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
            "error", "Estado inválido",
            "message", e.getMessage()
        ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception e) {
        logger.error("Error inesperado: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
            "error", "Error interno del servidor",
            "message", "Ocurrió un error inesperado"
        ));
    }
}