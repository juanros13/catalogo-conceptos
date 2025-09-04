package saf.cgmaig.budgetclassification.service;

import org.springframework.stereotype.Component;
import saf.cgmaig.budgetclassification.dto.BudgetClassificationCreateRequest;
import saf.cgmaig.budgetclassification.dto.BudgetClassificationResponse;
import saf.cgmaig.budgetclassification.dto.BudgetClassificationUpdateRequest;
import saf.cgmaig.budgetclassification.entity.BudgetClassification;
import saf.cgmaig.budgetclassification.entity.BudgetLevel;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class BudgetClassificationMapper {

    public BudgetClassification toEntity(BudgetClassificationCreateRequest request, String userCurp) {
        if (request == null) {
            return null;
        }

        BudgetClassification entity = new BudgetClassification();
        entity.setCodigo(request.getCodigo());
        entity.setNombre(request.getNombre());
        entity.setDescripcion(request.getDescripcion());
        entity.setOrden(request.getOrden() != null ? request.getOrden() : 0);
        entity.setActivo(true);
        entity.setCreadoPor(userCurp);
        entity.setActualizadoPor(userCurp);
        
        // El nivel y padreCodigo se calculan automáticamente en el setter del código
        
        return entity;
    }

    public BudgetClassificationResponse toResponse(BudgetClassification entity) {
        if (entity == null) {
            return null;
        }

        BudgetClassificationResponse response = new BudgetClassificationResponse();
        response.setId(entity.getId());
        response.setCodigo(entity.getCodigo());
        response.setNombre(entity.getNombre());
        response.setDescripcion(entity.getDescripcion());
        response.setNivel(entity.getNivel());
        response.setNivelDescripcion(entity.getNivel() != null ? entity.getNivel().getDescripcion() : null);
        response.setPadreCodigo(entity.getPadreCodigo());
        response.setOrden(entity.getOrden());
        response.setActivo(entity.getActivo());
        response.setFechaCreacion(entity.getFechaCreacion());
        response.setFechaActualizacion(entity.getFechaActualizacion());
        response.setCreadoPor(entity.getCreadoPor());
        response.setActualizadoPor(entity.getActualizadoPor());
        
        // Mapear hijos (sin cargar lazy loading)
        response.setTieneHijos(entity.tieneHijos());
        
        return response;
    }

    public List<BudgetClassificationResponse> toResponseList(List<BudgetClassification> entities) {
        if (entities == null) {
            return null;
        }
        
        return entities.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public void updateEntity(BudgetClassification target, BudgetClassificationUpdateRequest request, String userCurp) {
        if (target == null || request == null) {
            return;
        }

        target.setNombre(request.getNombre());
        target.setDescripcion(request.getDescripcion());
        if (request.getOrden() != null) {
            target.setOrden(request.getOrden());
        }
        target.setActualizadoPor(userCurp);
        target.setFechaActualizacion(LocalDateTime.now());
    }

    /**
     * Construir jerarquía completa a partir de una lista plana
     */
    public List<BudgetClassificationResponse> buildHierarchy(List<BudgetClassification> flatList, String codigoRaiz) {
        if (flatList == null || flatList.isEmpty()) {
            return new ArrayList<>();
        }

        // Convertir a responses
        List<BudgetClassificationResponse> responses = toResponseList(flatList);
        
        // Crear mapa por código para acceso rápido
        Map<String, BudgetClassificationResponse> responseMap = new HashMap<>();
        for (BudgetClassificationResponse response : responses) {
            responseMap.put(response.getCodigo(), response);
        }

        // Encontrar el nodo raíz
        BudgetClassificationResponse raiz = responseMap.get(codigoRaiz);
        if (raiz == null) {
            return new ArrayList<>();
        }

        // Construir jerarquía recursivamente
        buildChildrenRecursive(raiz, responseMap);
        
        return List.of(raiz);
    }

    private void buildChildrenRecursive(BudgetClassificationResponse parent, 
                                       Map<String, BudgetClassificationResponse> allNodes) {
        List<BudgetClassificationResponse> children = new ArrayList<>();
        
        for (BudgetClassificationResponse node : allNodes.values()) {
            if (parent.getCodigo().equals(node.getPadreCodigo())) {
                children.add(node);
                // Recursivamente construir los hijos de este nodo
                buildChildrenRecursive(node, allNodes);
            }
        }
        
        // Ordenar hijos por código
        children.sort((a, b) -> a.getCodigo().compareTo(b.getCodigo()));
        parent.setHijos(children);
    }

    /**
     * Construir breadcrumb a partir de datos de consulta nativa
     */
    public List<BudgetClassificationResponse> buildBreadcrumb(List<Object[]> breadcrumbData) {
        if (breadcrumbData == null || breadcrumbData.isEmpty()) {
            return new ArrayList<>();
        }

        List<BudgetClassificationResponse> breadcrumb = new ArrayList<>();
        
        for (Object[] data : breadcrumbData) {
            BudgetClassificationResponse item = new BudgetClassificationResponse();
            item.setCodigo((String) data[0]);
            item.setNombre((String) data[1]);
            item.setPadreCodigo((String) data[2]);
            // Determinar nivel desde el código
            if (item.getCodigo() != null) {
                item.setNivel(BudgetLevel.fromCode(item.getCodigo()));
            }
            breadcrumb.add(item);
        }
        
        return breadcrumb;
    }

    /**
     * Mapear response con hijos cargados (para APIs que requieren jerarquía completa)
     */
    public BudgetClassificationResponse toResponseWithChildren(BudgetClassification entity) {
        BudgetClassificationResponse response = toResponse(entity);
        
        if (entity.getHijos() != null && !entity.getHijos().isEmpty()) {
            List<BudgetClassificationResponse> hijosResponse = entity.getHijos().stream()
                    .map(this::toResponseWithChildren) // Recursivo
                    .collect(Collectors.toList());
            response.setHijos(hijosResponse);
        }
        
        return response;
    }
}