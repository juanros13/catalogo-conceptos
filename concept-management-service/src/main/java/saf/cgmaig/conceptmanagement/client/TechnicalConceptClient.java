package saf.cgmaig.conceptmanagement.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import saf.cgmaig.conceptmanagement.client.dto.TechnicalConceptDto;

import java.util.List;

/**
 * Feign Client para comunicación con Technical Concept Service
 * 
 * Permite consultar conceptos técnicos base que servirán como
 * punto de partida para crear conceptos específicos por áreas.
 */
@FeignClient(name = "technical-concept-service")
public interface TechnicalConceptClient {

    /**
     * Obtener conceptos base por área
     * Solo conceptos creados por validadores técnicos
     */
    @GetMapping("/api/concepts/base/{area}")
    List<TechnicalConceptDto> getBaseConceptsByArea(@PathVariable("area") String area);

    /**
     * Obtener concepto base específico por ID
     */
    @GetMapping("/api/concepts/{id}")
    TechnicalConceptDto getBaseConceptById(@PathVariable("id") Long id);

    /**
     * Verificar si un concepto base existe y está activo
     */
    @GetMapping("/api/concepts/{id}/exists")
    boolean baseConceptExists(@PathVariable("id") Long id);

    /**
     * Obtener información básica de concepto para cache
     */
    @GetMapping("/api/concepts/{id}/summary")
    TechnicalConceptSummary getConceptSummary(@PathVariable("id") Long id);

    /**
     * Obtener todos los conceptos base (para validadores)
     */
    @GetMapping("/api/concepts/base")
    List<TechnicalConceptDto> getAllBaseConcepts();

    /**
     * DTO para información resumida de concepto técnico
     */
    record TechnicalConceptSummary(
        Long id,
        String name,
        String area,
        String chapter,
        String status,
        String createdBy
    ) {}
}