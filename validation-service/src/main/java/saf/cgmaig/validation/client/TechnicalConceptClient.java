package saf.cgmaig.validation.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import saf.cgmaig.validation.client.dto.TechnicalConceptDto;

import java.util.List;

/**
 * Feign Client para comunicación con Technical Concept Service
 * 
 * Permite al validation-service consultar conceptos técnicos existentes
 * para realizar validaciones de unicidad y verificaciones de datos.
 */
@FeignClient(name = "technical-concept-service")
public interface TechnicalConceptClient {

    /**
     * Buscar conceptos por nombre y área (para validación de unicidad)
     */
    @GetMapping("/api/concepts/search")
    List<TechnicalConceptDto> findByNameAndArea(@RequestParam("name") String name, 
                                               @RequestParam("area") String area);

    /**
     * Verificar si existe un concepto con el mismo nombre en el área
     */
    @GetMapping("/api/concepts/exists")
    boolean existsByNameAndArea(@RequestParam("name") String name, 
                               @RequestParam("area") String area);

    /**
     * Obtener concepto por ID (para validaciones de actualización)
     */
    @GetMapping("/api/concepts/{id}")
    TechnicalConceptDto findById(@PathVariable("id") Long id);

    /**
     * Obtener todos los conceptos de un área específica
     */
    @GetMapping("/api/concepts/by-area/{area}")
    List<TechnicalConceptDto> findByArea(@PathVariable("area") String area);

    /**
     * Verificar si una combinación área-capítulo es válida
     * (basado en conceptos existentes)
     */
    @GetMapping("/api/concepts/area-chapter-combinations")
    List<AreaChapterCombination> getAreaChapterCombinations();

    /**
     * DTO interno para combinaciones área-capítulo
     */
    record AreaChapterCombination(String area, String chapter, boolean active) {}
}