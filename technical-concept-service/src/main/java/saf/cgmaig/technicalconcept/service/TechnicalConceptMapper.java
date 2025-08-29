package saf.cgmaig.technicalconcept.service;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import saf.cgmaig.technicalconcept.dto.TechnicalConceptCreateRequest;
import saf.cgmaig.technicalconcept.dto.TechnicalConceptResponse;
import saf.cgmaig.technicalconcept.dto.TechnicalConceptUpdateRequest;
import saf.cgmaig.technicalconcept.entity.TechnicalConcept;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TechnicalConceptMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "estado", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "activo", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaActualizacion", ignore = true)
    @Mapping(target = "creadoPor", source = "userCurp")
    @Mapping(target = "actualizadoPor", source = "userCurp")
    @Mapping(target = "motivoCambio", source = "request.motivoCreacion")
    TechnicalConcept toEntity(TechnicalConceptCreateRequest request, String userCurp);

    @Mapping(target = "areaFacultadaDescripcion", source = "areaFacultada.descripcion")
    @Mapping(target = "estadoDescripcion", source = "estado.descripcion")
    TechnicalConceptResponse toResponse(TechnicalConcept entity);

    List<TechnicalConceptResponse> toResponseList(List<TechnicalConcept> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "capitulo", ignore = true)
    @Mapping(target = "areaFacultada", ignore = true)
    @Mapping(target = "estado", ignore = true)
    @Mapping(target = "activo", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaActualizacion", ignore = true)
    @Mapping(target = "creadoPor", ignore = true)
    @Mapping(target = "actualizadoPor", source = "userCurp")
    @Mapping(target = "version", expression = "java(target.getVersion() + 1)")
    void updateEntity(@MappingTarget TechnicalConcept target, TechnicalConceptUpdateRequest request, String userCurp);
}