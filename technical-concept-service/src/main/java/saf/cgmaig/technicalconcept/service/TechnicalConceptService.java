package saf.cgmaig.technicalconcept.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import saf.cgmaig.technicalconcept.dto.TechnicalConceptCreateRequest;
import saf.cgmaig.technicalconcept.dto.TechnicalConceptResponse;
import saf.cgmaig.technicalconcept.dto.TechnicalConceptUpdateRequest;
import saf.cgmaig.technicalconcept.entity.AreaFacultada;
import saf.cgmaig.technicalconcept.entity.ConceptStatus;
import saf.cgmaig.technicalconcept.entity.TechnicalConcept;
import saf.cgmaig.technicalconcept.repository.TechnicalConceptRepository;

import java.util.List;
import java.util.UUID;

@Service
@Transactional("transactionManager")
public class TechnicalConceptService {

    private static final Logger logger = LoggerFactory.getLogger(TechnicalConceptService.class);

    @Autowired
    private TechnicalConceptRepository repository;

    @Autowired
    private TechnicalConceptMapper mapper;

    /**
     * Crear un nuevo concepto general
     */
    public TechnicalConceptResponse create(TechnicalConceptCreateRequest request, String userCurp) {
        logger.info("Creando concepto general: {} para área: {}", request.getNombre(), request.getAreaFacultada());

        // Validar que el área facultada puede gestionar el capítulo
        validateAreaCapitulo(request.getAreaFacultada(), request.getCapitulo());

        // Validar unicidad del nombre en el área
        if (repository.findByNombreAndAreaFacultada(request.getNombre(), request.getAreaFacultada()).isPresent()) {
            throw new IllegalArgumentException(
                String.format("Ya existe un concepto general con el nombre '%s' en el área %s", 
                            request.getNombre(), request.getAreaFacultada()));
        }

        TechnicalConcept concept = mapper.toEntity(request, userCurp);
        TechnicalConcept saved = repository.save(concept);

        logger.info("Concepto general creado exitosamente con ID: {}", saved.getId());
        return mapper.toResponse(saved);
    }

    /**
     * Actualizar un concepto general existente
     */
    public TechnicalConceptResponse update(UUID id, TechnicalConceptUpdateRequest request, 
                                         AreaFacultada userArea, String userCurp) {
        logger.info("Actualizando concepto general ID: {} por área: {}", id, userArea);

        TechnicalConcept existing = findConceptById(id);

        // Validar que el usuario puede editar este concepto
        if (!existing.canBeEditedBy(userArea)) {
            throw new SecurityException(
                String.format("Área %s no puede editar conceptos del área %s", 
                            userArea, existing.getAreaFacultada()));
        }

        // Validar unicidad del nombre si cambió
        if (!existing.getNombre().equals(request.getNombre()) &&
            repository.existsByNombreAndAreaFacultadaAndIdNot(
                request.getNombre(), existing.getAreaFacultada(), id)) {
            throw new IllegalArgumentException(
                String.format("Ya existe un concepto general con el nombre '%s' en el área %s", 
                            request.getNombre(), existing.getAreaFacultada()));
        }

        // Aplicar cambios
        mapper.updateEntity(existing, request, userCurp);
        TechnicalConcept updated = repository.save(existing);

        logger.info("Concepto general actualizado exitosamente: {}", updated.getId());
        return mapper.toResponse(updated);
    }

    /**
     * Inactivar un concepto general (baja lógica)
     */
    public TechnicalConceptResponse inactivate(UUID id, String motivo, 
                                             AreaFacultada userArea, String userCurp) {
        logger.info("Inactivando concepto general ID: {} por área: {}", id, userArea);

        TechnicalConcept existing = findConceptById(id);

        // Validar permisos
        if (!existing.canBeEditedBy(userArea)) {
            throw new SecurityException(
                String.format("Área %s no puede inactivar conceptos del área %s", 
                            userArea, existing.getAreaFacultada()));
        }

        if (!existing.isActivo()) {
            throw new IllegalStateException("El concepto ya está inactivo");
        }

        existing.inactivar(motivo, userCurp);
        TechnicalConcept updated = repository.save(existing);

        logger.info("Concepto general inactivado exitosamente: {}", updated.getId());
        return mapper.toResponse(updated);
    }

    /**
     * Reactivar un concepto general
     */
    public TechnicalConceptResponse reactivate(UUID id, String motivo, 
                                             AreaFacultada userArea, String userCurp) {
        logger.info("Reactivando concepto general ID: {} por área: {}", id, userArea);

        TechnicalConcept existing = findConceptById(id);

        // Validar permisos
        if (!existing.canBeEditedBy(userArea)) {
            throw new SecurityException(
                String.format("Área %s no puede reactivar conceptos del área %s", 
                            userArea, existing.getAreaFacultada()));
        }

        if (existing.isActivo()) {
            throw new IllegalStateException("El concepto ya está activo");
        }

        existing.reactivar(motivo, userCurp);
        TechnicalConcept updated = repository.save(existing);

        logger.info("Concepto general reactivado exitosamente: {}", updated.getId());
        return mapper.toResponse(updated);
    }

    /**
     * Obtener conceptos activos para capturistas
     */
    @Transactional(value = "transactionManager", readOnly = true)
    public List<TechnicalConceptResponse> getActiveConceptsForCapture() {
        logger.debug("Obteniendo conceptos activos para captura");
        
        List<TechnicalConcept> concepts = repository.findByEstadoAndActivoTrueOrderByNombreAsc(ConceptStatus.ACTIVO);
        return mapper.toResponseList(concepts);
    }

    /**
     * Obtener conceptos activos por capítulo para capturistas
     */
    @Transactional(value = "transactionManager", readOnly = true)
    public List<TechnicalConceptResponse> getActiveConceptsByCapitulo(Integer capitulo) {
        logger.debug("Obteniendo conceptos activos para captura del capítulo: {}", capitulo);
        
        List<TechnicalConcept> concepts = repository.findByCapituloAndEstadoAndActivoTrueOrderByNombreAsc(
            capitulo, ConceptStatus.ACTIVO);
        return mapper.toResponseList(concepts);
    }

    /**
     * Obtener conceptos por área facultada (para gestión)
     */
    @Transactional(value = "transactionManager", readOnly = true)
    public Page<TechnicalConceptResponse> getConceptsByArea(AreaFacultada area, Pageable pageable) {
        logger.debug("Obteniendo conceptos del área: {}", area);
        
        Page<TechnicalConcept> concepts = repository.findByAreaFacultadaOrderByFechaCreacionDesc(area, pageable);
        return concepts.map(mapper::toResponse);
    }

    /**
     * Búsqueda avanzada con filtros
     */
    @Transactional(value = "transactionManager", readOnly = true)
    public Page<TechnicalConceptResponse> searchConcepts(Integer capitulo, AreaFacultada areaFacultada,
                                                       ConceptStatus estado, Boolean activo, 
                                                       String searchTerm, Pageable pageable) {
        logger.debug("Búsqueda con filtros - capítulo: {}, área: {}, estado: {}, activo: {}, búsqueda: {}", 
                    capitulo, areaFacultada, estado, activo, searchTerm);
        
        Page<TechnicalConcept> concepts = repository.findConceptsWithFilters(
            capitulo, areaFacultada, estado, activo, searchTerm, pageable);
        return concepts.map(mapper::toResponse);
    }

    /**
     * Obtener concepto por ID
     */
    @Transactional(value = "transactionManager", readOnly = true)
    public TechnicalConceptResponse getById(UUID id) {
        TechnicalConcept concept = findConceptById(id);
        return mapper.toResponse(concept);
    }

    /**
     * Validar que área facultada puede gestionar el capítulo
     */
    private void validateAreaCapitulo(AreaFacultada area, Integer capitulo) {
        if (!area.getCapitulo().equals(capitulo)) {
            throw new IllegalArgumentException(
                String.format("Área facultada %s (%s) no puede gestionar conceptos del capítulo %d. " +
                            "Su competencia es el capítulo %d", 
                            area, area.getDescripcion(), capitulo, area.getCapitulo()));
        }
    }

    /**
     * Buscar concepto por ID con validación
     */
    private TechnicalConcept findConceptById(UUID id) {
        return repository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException(
                "No se encontró concepto general con ID: " + id));
    }
}