package saf.cgmaig.conceptmanagement.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import saf.cgmaig.conceptmanagement.client.TechnicalConceptClient;
import saf.cgmaig.conceptmanagement.client.ValidationServiceClient;
import saf.cgmaig.conceptmanagement.client.dto.TechnicalConceptDto;
import saf.cgmaig.conceptmanagement.client.dto.TemplateStructure;
import saf.cgmaig.conceptmanagement.client.dto.ValidationRequest;
import saf.cgmaig.conceptmanagement.client.dto.ValidationResult;
import saf.cgmaig.conceptmanagement.model.AreaConcept;
import saf.cgmaig.conceptmanagement.model.ConceptStatus;
import saf.cgmaig.conceptmanagement.repository.AreaConceptRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Servicio de negocio para gestión de conceptos específicos por áreas
 * 
 * Maneja el flujo completo desde creación hasta validación de conceptos
 * basados en templates de capítulo CUBS.
 */
@Service
public class AreaConceptService {

    private static final Logger logger = LoggerFactory.getLogger(AreaConceptService.class);

    private final AreaConceptRepository areaConceptRepository;
    private final TechnicalConceptClient technicalConceptClient;
    private final ValidationServiceClient validationServiceClient;

    @Autowired
    public AreaConceptService(AreaConceptRepository areaConceptRepository,
                             TechnicalConceptClient technicalConceptClient,
                             ValidationServiceClient validationServiceClient) {
        this.areaConceptRepository = areaConceptRepository;
        this.technicalConceptClient = technicalConceptClient;
        this.validationServiceClient = validationServiceClient;
    }

    /**
     * Obtener conceptos técnicos base disponibles para un área
     */
    public List<TechnicalConceptDto> getAvailableBaseConcepts(String area) {
        logger.debug("Obteniendo conceptos base disponibles para área: {}", area);
        
        try {
            List<TechnicalConceptDto> baseConcepts = technicalConceptClient.getBaseConceptsByArea(area);
            logger.debug("Encontrados {} conceptos base para área: {}", baseConcepts.size(), area);
            return baseConcepts;
        } catch (Exception e) {
            logger.error("Error al obtener conceptos base para área: {}", area, e);
            throw new ServiceException("Error al consultar conceptos base: " + e.getMessage());
        }
    }

    /**
     * Obtener estructura de template por capítulo
     */
    public TemplateStructure getChapterTemplate(String chapterKey) {
        logger.debug("Obteniendo template para capítulo: {}", chapterKey);
        
        try {
            TemplateStructure template = validationServiceClient.getTemplateStructure(chapterKey);
            logger.debug("Template obtenido: {} con {} campos", template.getName(), template.getRequiredFieldsCount());
            return template;
        } catch (Exception e) {
            logger.error("Error al obtener template para capítulo: {}", chapterKey, e);
            throw new ServiceException("Error al obtener template: " + e.getMessage());
        }
    }

    /**
     * Crear nuevo concepto específico
     */
    @Transactional
    public AreaConcept createAreaConcept(AreaConceptCreationRequest request, String createdBy) {
        logger.info("Creando concepto específico: {} por usuario: {}", request.getSpecificName(), createdBy);

        // Validar que el concepto base existe
        if (!technicalConceptClient.baseConceptExists(request.getBaseConceptId())) {
            throw new ServiceException("El concepto base no existe o no está activo");
        }

        // Verificar unicidad del nombre específico en el área
        if (areaConceptRepository.existsBySpecificNameAndArea(request.getSpecificName(), request.getArea())) {
            throw new ServiceException("Ya existe un concepto con ese nombre en el área");
        }

        // Obtener información del concepto base para cache
        TechnicalConceptDto baseConcept = technicalConceptClient.getBaseConceptById(request.getBaseConceptId());

        // Crear concepto específico
        AreaConcept areaConcept = new AreaConcept();
        areaConcept.setBaseConceptId(request.getBaseConceptId());
        areaConcept.setBaseConceptName(baseConcept.getName());
        areaConcept.setSpecificName(request.getSpecificName());
        areaConcept.setArea(request.getArea());
        areaConcept.setChapter(request.getChapter());
        areaConcept.setChapterTemplate(request.getChapterTemplate());
        areaConcept.setUnitMeasure(request.getUnitMeasure());
        areaConcept.setEstimatedValue(request.getEstimatedValue());
        areaConcept.setCreatedBy(createdBy);

        // Asignar campos específicos del template
        setTemplateFields(areaConcept, request);

        AreaConcept savedConcept = areaConceptRepository.save(areaConcept);
        logger.info("Concepto específico creado con ID: {}", savedConcept.getId());

        return savedConcept;
    }

    /**
     * Actualizar concepto específico (solo en estado DRAFT o REJECTED)
     */
    @Transactional
    public AreaConcept updateAreaConcept(Long conceptId, AreaConceptUpdateRequest request, String updatedBy) {
        logger.info("Actualizando concepto específico ID: {} por usuario: {}", conceptId, updatedBy);

        AreaConcept existingConcept = areaConceptRepository.findById(conceptId)
                .orElseThrow(() -> new ServiceException("Concepto no encontrado"));

        if (!existingConcept.getStatus().isEditable()) {
            throw new ServiceException("El concepto no puede ser editado en su estado actual: " + 
                                     existingConcept.getStatus().getDisplayName());
        }

        // Verificar unicidad si cambió el nombre
        if (!existingConcept.getSpecificName().equals(request.getSpecificName())) {
            if (areaConceptRepository.existsBySpecificNameAndAreaExcludingId(
                    request.getSpecificName(), existingConcept.getArea(), conceptId)) {
                throw new ServiceException("Ya existe un concepto con ese nombre en el área");
            }
        }

        // Actualizar campos
        existingConcept.setSpecificName(request.getSpecificName());
        existingConcept.setUnitMeasure(request.getUnitMeasure());
        existingConcept.setEstimatedValue(request.getEstimatedValue());
        existingConcept.setUpdatedBy(updatedBy);

        // Actualizar campos específicos del template
        setTemplateFields(existingConcept, request);

        // Si había sido rechazado, volver a DRAFT
        if (existingConcept.getStatus() == ConceptStatus.REJECTED) {
            existingConcept.setStatus(ConceptStatus.DRAFT);
            existingConcept.setValidatedBy(null);
            existingConcept.setValidatedAt(null);
            existingConcept.setValidationComments(null);
        }

        AreaConcept updatedConcept = areaConceptRepository.save(existingConcept);
        logger.info("Concepto específico actualizado: {}", updatedConcept.getId());

        return updatedConcept;
    }

    /**
     * Enviar concepto para validación
     */
    @Transactional
    public AreaConcept submitForValidation(Long conceptId, String authorizationHeader) {
        logger.info("Enviando concepto ID: {} para validación", conceptId);

        AreaConcept concept = areaConceptRepository.findById(conceptId)
                .orElseThrow(() -> new ServiceException("Concepto no encontrado"));

        if (!concept.getStatus().canBeSubmitted()) {
            throw new ServiceException("El concepto no puede ser enviado para validación en su estado actual");
        }

        // Crear request de validación
        ValidationRequest validationRequest = createValidationRequest(concept);

        try {
            // Validar concepto
            ValidationResult validationResult = validationServiceClient.validateConcept(
                    validationRequest, authorizationHeader);

            if (validationResult.hasErrors()) {
                // Si hay errores de validación, rechazar automáticamente
                concept.reject("SYSTEM_VALIDATION", 
                              "Errores de validación: " + validationResult.getErrors().toString());
                logger.warn("Concepto ID: {} rechazado automáticamente por errores de validación", conceptId);
            } else {
                // Enviar para revisión manual
                concept.submitForValidation();
                logger.info("Concepto ID: {} enviado exitosamente para validación", conceptId);
            }

        } catch (Exception e) {
            logger.error("Error durante validación de concepto ID: {}", conceptId, e);
            throw new ServiceException("Error durante validación: " + e.getMessage());
        }

        return areaConceptRepository.save(concept);
    }

    /**
     * Aprobar concepto (solo validadores técnicos)
     */
    @Transactional
    public AreaConcept approveConcept(Long conceptId, String validatedBy, String comments) {
        logger.info("Aprobando concepto ID: {} por validador: {}", conceptId, validatedBy);

        AreaConcept concept = areaConceptRepository.findById(conceptId)
                .orElseThrow(() -> new ServiceException("Concepto no encontrado"));

        if (concept.getStatus() != ConceptStatus.SUBMITTED && concept.getStatus() != ConceptStatus.IN_REVIEW) {
            throw new ServiceException("El concepto no está en estado de validación");
        }

        concept.approve(validatedBy, comments);
        AreaConcept approvedConcept = areaConceptRepository.save(concept);

        logger.info("Concepto ID: {} aprobado exitosamente", conceptId);
        return approvedConcept;
    }

    /**
     * Rechazar concepto (solo validadores técnicos)
     */
    @Transactional
    public AreaConcept rejectConcept(Long conceptId, String validatedBy, String comments) {
        logger.info("Rechazando concepto ID: {} por validador: {}", conceptId, validatedBy);

        AreaConcept concept = areaConceptRepository.findById(conceptId)
                .orElseThrow(() -> new ServiceException("Concepto no encontrado"));

        if (concept.getStatus() != ConceptStatus.SUBMITTED && concept.getStatus() != ConceptStatus.IN_REVIEW) {
            throw new ServiceException("El concepto no está en estado de validación");
        }

        concept.reject(validatedBy, comments);
        AreaConcept rejectedConcept = areaConceptRepository.save(concept);

        logger.info("Concepto ID: {} rechazado", conceptId);
        return rejectedConcept;
    }

    /**
     * Obtener conceptos por área
     */
    public List<AreaConcept> getConceptsByArea(String area) {
        return areaConceptRepository.findByAreaOrderByCreatedAtDesc(area);
    }

    /**
     * Obtener conceptos por área con paginación
     */
    public Page<AreaConcept> getConceptsByArea(String area, Pageable pageable) {
        return areaConceptRepository.findByArea(area, pageable);
    }

    /**
     * Obtener conceptos pendientes de validación por área
     */
    public List<AreaConcept> getPendingValidationByArea(String area) {
        return areaConceptRepository.findPendingValidationByArea(area);
    }

    /**
     * Buscar conceptos por texto
     */
    public List<AreaConcept> searchConcepts(String area, String searchText) {
        return areaConceptRepository.searchConceptsByText(area, searchText);
    }

    /**
     * Asignar campos específicos del template al concepto
     */
    private void setTemplateFields(AreaConcept concept, TemplateFieldsProvider fieldsProvider) {
        concept.setGeneral(fieldsProvider.getGeneral());
        concept.setEspecifica(fieldsProvider.getEspecifica());
        concept.setPresentacionProducto(fieldsProvider.getPresentacionProducto());
        concept.setComposicionMateriales(fieldsProvider.getComposicionMateriales());
        concept.setDescripcionTecnica(fieldsProvider.getDescripcionTecnica());
        concept.setComponentesServicio(fieldsProvider.getComponentesServicio());
        concept.setAccesoriosServicio(fieldsProvider.getAccesoriosServicio());
        concept.setCaracteristicaFuncionalidad(fieldsProvider.getCaracteristicaFuncionalidad());
        concept.setCaracteristicasFisicas(fieldsProvider.getCaracteristicasFisicas());
        concept.setColor(fieldsProvider.getColor());
        concept.setMayoresEspecificaciones(fieldsProvider.getMayoresEspecificaciones());
    }

    /**
     * Crear request de validación desde concepto de área
     */
    private ValidationRequest createValidationRequest(AreaConcept concept) {
        ValidationRequest request = new ValidationRequest();
        
        request.setName(concept.getSpecificName());
        request.setArea(concept.getArea());
        request.setChapter(concept.getChapter());
        request.setUnitMeasure(concept.getUnitMeasure());
        request.setCreatedBy(concept.getCreatedBy());
        request.setBaseConceptId(concept.getBaseConceptId());
        request.setChapterTemplate(concept.getChapterTemplate());
        request.setEstimatedValue(concept.getEstimatedValue());

        // Campos específicos del template
        request.setGeneral(concept.getGeneral());
        request.setEspecifica(concept.getEspecifica());
        request.setPresentacionProducto(concept.getPresentacionProducto());
        request.setComposicionMateriales(concept.getComposicionMateriales());
        request.setDescripcionTecnica(concept.getDescripcionTecnica());
        request.setComponentesServicio(concept.getComponentesServicio());
        request.setAccesoriosServicio(concept.getAccesoriosServicio());
        request.setCaracteristicaFuncionalidad(concept.getCaracteristicaFuncionalidad());
        request.setCaracteristicasFisicas(concept.getCaracteristicasFisicas());
        request.setColor(concept.getColor());
        request.setMayoresEspecificaciones(concept.getMayoresEspecificaciones());

        return request;
    }

    /**
     * Interface común para objetos que proporcionan campos de template
     */
    public interface TemplateFieldsProvider {
        String getGeneral();
        String getEspecifica();
        String getPresentacionProducto();
        String getComposicionMateriales();
        String getDescripcionTecnica();
        String getComponentesServicio();
        String getAccesoriosServicio();
        String getCaracteristicaFuncionalidad();
        String getCaracteristicasFisicas();
        String getColor();
        String getMayoresEspecificaciones();
    }

    /**
     * Excepción personalizada para errores del servicio
     */
    public static class ServiceException extends RuntimeException {
        public ServiceException(String message) {
            super(message);
        }
    }
}