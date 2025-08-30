package saf.cgmaig.technicalconcept.service;

import org.springframework.stereotype.Component;
import saf.cgmaig.technicalconcept.dto.TechnicalConceptCreateRequest;
import saf.cgmaig.technicalconcept.dto.TechnicalConceptResponse;
import saf.cgmaig.technicalconcept.dto.TechnicalConceptUpdateRequest;
import saf.cgmaig.technicalconcept.entity.AreaFacultada;
import saf.cgmaig.technicalconcept.entity.ConceptStatus;
import saf.cgmaig.technicalconcept.entity.TechnicalConcept;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TechnicalConceptMapperImpl implements TechnicalConceptMapper {

    @Override
    public TechnicalConcept toEntity(TechnicalConceptCreateRequest request, String userCurp) {
        if (request == null) {
            return null;
        }

        TechnicalConcept concept = new TechnicalConcept();
        concept.setNombre(request.getNombre());
        concept.setDescripcionDetallada(request.getDescripcionDetallada());
        concept.setCapitulo(request.getCapitulo());
        concept.setPartidasPermitidas(request.getPartidasPermitidas());
        concept.setAreaFacultada(request.getAreaFacultada());
        concept.setEstado(ConceptStatus.ACTIVO);
        concept.setActivo(true);
        concept.setVersion(1);
        concept.setCreadoPor(userCurp);
        concept.setActualizadoPor(userCurp);
        concept.setMotivoCambio(request.getMotivoCreacion());
        concept.setFechaCreacion(LocalDateTime.now());
        concept.setFechaActualizacion(LocalDateTime.now());

        return concept;
    }

    @Override
    public TechnicalConceptResponse toResponse(TechnicalConcept entity) {
        if (entity == null) {
            return null;
        }

        TechnicalConceptResponse response = new TechnicalConceptResponse();
        response.setId(entity.getId());
        response.setNombre(entity.getNombre());
        response.setDescripcionDetallada(entity.getDescripcionDetallada());
        response.setCapitulo(entity.getCapitulo());
        // Safe handling of potentially lazy-loaded collection
        try {
            response.setPartidasPermitidas(entity.getPartidasPermitidas());
        } catch (org.hibernate.LazyInitializationException e) {
            response.setPartidasPermitidas(new java.util.ArrayList<>());
        }
        response.setAreaFacultada(entity.getAreaFacultada());
        response.setAreaFacultadaDescripcion(areaToDescription(entity.getAreaFacultada()));
        response.setEstado(entity.getEstado());
        response.setEstadoDescripcion(statusToDescription(entity.getEstado()));
        response.setVersion(entity.getVersion());
        response.setActivo(entity.getActivo());
        response.setFechaCreacion(entity.getFechaCreacion());
        response.setFechaActualizacion(entity.getFechaActualizacion());
        response.setCreadoPor(entity.getCreadoPor());
        response.setActualizadoPor(entity.getActualizadoPor());
        response.setMotivoCambio(entity.getMotivoCambio());

        return response;
    }

    @Override
    public List<TechnicalConceptResponse> toResponseList(List<TechnicalConcept> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void updateEntity(TechnicalConcept target, TechnicalConceptUpdateRequest request, String userCurp) {
        if (target == null || request == null) {
            return;
        }

        target.setNombre(request.getNombre());
        target.setDescripcionDetallada(request.getDescripcionDetallada());
        target.setPartidasPermitidas(request.getPartidasPermitidas());
        target.setMotivoCambio(request.getMotivoCambio());
        target.setActualizadoPor(userCurp);
        target.setVersion(target.getVersion() + 1);
        target.setFechaActualizacion(LocalDateTime.now());
    }

    @Override
    public String areaToDescription(AreaFacultada area) {
        return area != null ? area.getDescripcion() : null;
    }

    @Override
    public String statusToDescription(ConceptStatus status) {
        return status != null ? status.getDescripcion() : null;
    }
}