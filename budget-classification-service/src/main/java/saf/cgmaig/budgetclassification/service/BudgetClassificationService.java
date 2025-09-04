package saf.cgmaig.budgetclassification.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import saf.cgmaig.budgetclassification.dto.*;
import saf.cgmaig.budgetclassification.entity.BudgetClassification;
import saf.cgmaig.budgetclassification.entity.BudgetLevel;
import saf.cgmaig.budgetclassification.repository.BudgetClassificationRepository;

import java.util.List;
import java.util.UUID;

@Service
@Transactional("transactionManager")
public class BudgetClassificationService {

    private static final Logger logger = LoggerFactory.getLogger(BudgetClassificationService.class);

    @Autowired
    private BudgetClassificationRepository repository;

    @Autowired
    private BudgetClassificationMapper mapper;

    /**
     * Crear nueva clasificación presupuestaria
     */
    @CacheEvict(value = {"budgetClassifications", "hierarchies"}, allEntries = true)
    public BudgetClassificationResponse create(BudgetClassificationCreateRequest request, String userCurp) {
        logger.info("Creando clasificación presupuestaria: {}", request.getCodigo());

        // Validar que el código no exista
        if (repository.existsByCodigo(request.getCodigo())) {
            throw new IllegalArgumentException("Ya existe una clasificación con el código: " + request.getCodigo());
        }

        // Determinar nivel automáticamente
        BudgetLevel nivel = BudgetLevel.fromCode(request.getCodigo());
        String padreCodigo = nivel.getParentCode(request.getCodigo());

        // Validar que el padre existe (excepto para capítulos)
        if (padreCodigo != null && !repository.existsActivePadre(padreCodigo)) {
            throw new IllegalArgumentException(
                String.format("No existe el código padre %s para el código %s", 
                            padreCodigo, request.getCodigo()));
        }

        BudgetClassification entity = mapper.toEntity(request, userCurp);
        BudgetClassification saved = repository.save(entity);

        logger.info("Clasificación presupuestaria creada exitosamente: {}", saved.getCodigo());
        return mapper.toResponse(saved);
    }

    /**
     * Actualizar clasificación existente
     */
    @CacheEvict(value = {"budgetClassifications", "hierarchies"}, allEntries = true)
    public BudgetClassificationResponse update(String codigo, BudgetClassificationUpdateRequest request, String userCurp) {
        logger.info("Actualizando clasificación presupuestaria: {}", codigo);

        BudgetClassification existing = findByCodigo(codigo);
        
        mapper.updateEntity(existing, request, userCurp);
        BudgetClassification updated = repository.save(existing);

        logger.info("Clasificación presupuestaria actualizada exitosamente: {}", updated.getCodigo());
        return mapper.toResponse(updated);
    }

    /**
     * Activar/Inactivar clasificación
     */
    @CacheEvict(value = {"budgetClassifications", "hierarchies"}, allEntries = true)
    public BudgetClassificationResponse toggleActive(String codigo, boolean activo, String userCurp) {
        logger.info("Cambiando estado activo de {} a {}", codigo, activo);

        BudgetClassification existing = findByCodigo(codigo);

        // Si se quiere inactivar, verificar que no tenga hijos activos
        if (!activo && repository.countHijosActivos(codigo) > 0) {
            throw new IllegalStateException(
                "No se puede inactivar el código " + codigo + " porque tiene elementos hijos activos");
        }

        if (activo) {
            existing.activar(userCurp);
        } else {
            existing.inactivar(userCurp);
        }

        BudgetClassification updated = repository.save(existing);
        logger.info("Estado actualizado exitosamente para: {}", codigo);
        return mapper.toResponse(updated);
    }

    /**
     * Obtener por código
     */
    @Cacheable(value = "budgetClassifications", key = "#codigo")
    @Transactional(value = "transactionManager", readOnly = true)
    public BudgetClassificationResponse getByCodigo(String codigo) {
        BudgetClassification entity = findByCodigo(codigo);
        return mapper.toResponse(entity);
    }

    /**
     * Obtener todos los capítulos
     */
    @Cacheable(value = "budgetClassifications", key = "'capitulos'")
    @Transactional(value = "transactionManager", readOnly = true)
    public List<BudgetClassificationResponse> getCapitulos() {
        logger.debug("Obteniendo todos los capítulos");
        List<BudgetClassification> capitulos = repository.findByNivelAndActivoTrueOrderByCodigoAsc(BudgetLevel.CAPITULO);
        return mapper.toResponseList(capitulos);
    }

    /**
     * Obtener hijos directos de un código
     */
    @Cacheable(value = "budgetClassifications", key = "'hijos-' + #padreCodigo")
    @Transactional(value = "transactionManager", readOnly = true)
    public List<BudgetClassificationResponse> getHijos(String padreCodigo) {
        logger.debug("Obteniendo hijos del código: {}", padreCodigo);
        
        // Verificar que el padre existe
        if (!repository.existsByCodigo(padreCodigo)) {
            throw new IllegalArgumentException("No existe el código padre: " + padreCodigo);
        }

        List<BudgetClassification> hijos = repository.findByPadreCodigoAndActivoTrueOrderByOrdenAscCodigoAsc(padreCodigo);
        return mapper.toResponseList(hijos);
    }

    /**
     * Cargar hijos para una entidad (método utilitario)
     */
    private void loadChildren(BudgetClassification entity) {
        if (entity != null && entity.getCodigo() != null) {
            List<BudgetClassification> hijos = repository.findByPadreCodigoAndActivoTrueOrderByOrdenAscCodigoAsc(entity.getCodigo());
            entity.setHijos(hijos);
        }
    }

    /**
     * Obtener jerarquía completa desde un código
     */
    @Cacheable(value = "hierarchies", key = "#codigoRaiz")
    @Transactional(value = "transactionManager", readOnly = true)
    public HierarchyResponse getJerarquiaCompleta(String codigoRaiz) {
        logger.debug("Obteniendo jerarquía completa desde: {}", codigoRaiz);

        BudgetClassification raiz = findByCodigo(codigoRaiz);
        List<BudgetClassification> jerarquiaCompleta = repository.findJerarquiaCompleta(codigoRaiz);
        
        logger.debug("Elementos encontrados en jerarquía completa: {}", jerarquiaCompleta.size());
        if (jerarquiaCompleta.isEmpty()) {
            logger.warn("No se encontraron elementos en la jerarquía para código: {}", codigoRaiz);
        }
        
        List<BudgetClassificationResponse> jerarquiaResponse = mapper.buildHierarchy(jerarquiaCompleta, codigoRaiz);
        
        HierarchyResponse response = new HierarchyResponse(codigoRaiz, raiz.getNombre(), jerarquiaResponse);
        logger.debug("Jerarquía construida con {} nodos raíz", jerarquiaResponse.size());
        
        return response;
    }

    /**
     * Obtener por nivel específico
     */
    @Cacheable(value = "budgetClassifications", key = "'nivel-' + #nivel.name()")
    @Transactional(value = "transactionManager", readOnly = true)
    public Page<BudgetClassificationResponse> getByNivel(BudgetLevel nivel, Pageable pageable) {
        logger.debug("Obteniendo clasificaciones por nivel: {}", nivel);
        
        Page<BudgetClassification> page = repository.findByNivelAndActivoTrueOrderByCodigoAsc(nivel, pageable);
        return page.map(mapper::toResponse);
    }

    /**
     * Búsqueda avanzada con filtros
     */
    @Transactional(value = "transactionManager", readOnly = true)
    public Page<BudgetClassificationResponse> search(BudgetLevel nivel, String padreCodigo, 
                                                   Boolean activo, String searchTerm, Pageable pageable) {
        logger.debug("Búsqueda con filtros - nivel: {}, padre: {}, activo: {}, término: {}", 
                    nivel, padreCodigo, activo, searchTerm);
        
        Page<BudgetClassification> page = repository.findWithFilters(nivel, padreCodigo, activo, searchTerm, pageable);
        return page.map(mapper::toResponse);
    }

    /**
     * Búsqueda por texto libre
     */
    @Transactional(value = "transactionManager", readOnly = true)
    public List<BudgetClassificationResponse> searchByText(String searchTerm) {
        logger.debug("Búsqueda por texto: {}", searchTerm);
        
        List<BudgetClassification> results = repository.findBySearchTerm(searchTerm);
        return mapper.toResponseList(results);
    }

    /**
     * Obtener breadcrumb (ruta hacia arriba)
     */
    @Cacheable(value = "budgetClassifications", key = "'breadcrumb-' + #codigo")
    @Transactional(value = "transactionManager", readOnly = true)
    public List<BudgetClassificationResponse> getBreadcrumb(String codigo) {
        logger.debug("Obteniendo breadcrumb para: {}", codigo);
        
        List<Object[]> breadcrumbData = repository.getBreadcrumb(codigo);
        return mapper.buildBreadcrumb(breadcrumbData);
    }

    /**
     * Validar si un código puede ser eliminado
     */
    @Transactional(value = "transactionManager", readOnly = true)
    public boolean canDelete(String codigo) {
        return repository.canBeDeleted(codigo);
    }

    /**
     * Obtener estadísticas por nivel
     */
    @Cacheable(value = "budgetClassifications", key = "'stats'")
    @Transactional(value = "transactionManager", readOnly = true)
    public java.util.Map<BudgetLevel, Long> getStatistics() {
        logger.debug("Obteniendo estadísticas por nivel");
        
        List<Object[]> stats = repository.countByNivel();
        java.util.Map<BudgetLevel, Long> result = new java.util.HashMap<>();
        
        for (Object[] stat : stats) {
            BudgetLevel nivel = (BudgetLevel) stat[0];
            Long count = (Long) stat[1];
            result.put(nivel, count);
        }
        
        return result;
    }

    /**
     * Buscar entidad por código con validación
     */
    private BudgetClassification findByCodigo(String codigo) {
        return repository.findByCodigo(codigo)
            .orElseThrow(() -> new IllegalArgumentException(
                "No se encontró clasificación presupuestaria con código: " + codigo));
    }
}