package saf.cgmaig.conceptmanagement.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import saf.cgmaig.conceptmanagement.client.dto.ValidationRequest;
import saf.cgmaig.conceptmanagement.client.dto.ValidationResult;
import saf.cgmaig.conceptmanagement.client.dto.TemplateStructure;

import java.util.List;
import java.util.Map;

/**
 * Feign Client para comunicación con Validation Service
 * 
 * Permite validar conceptos específicos y obtener información
 * sobre templates de capítulos CUBS.
 */
@FeignClient(name = "validation-service")
public interface ValidationServiceClient {

    /**
     * Validar concepto específico completo
     */
    @PostMapping("/api/validation/concept")
    ValidationResult validateConcept(@RequestBody ValidationRequest request, 
                                   @RequestHeader("Authorization") String authorization);

    /**
     * Obtener estructura de template por capítulo
     */
    @GetMapping("/api/templates/{chapterKey}/structure")
    TemplateStructure getTemplateStructure(@PathVariable("chapterKey") String chapterKey);

    /**
     * Obtener todos los templates disponibles
     */
    @GetMapping("/api/templates")
    Map<String, Object> getAllTemplates();

    /**
     * Obtener templates por capítulo base (ej: "2000" devuelve MATERIALES y SERVICIOS)
     */
    @GetMapping("/api/templates/chapter/{chapter}")
    List<TemplateStructure> getTemplatesByChapter(@PathVariable("chapter") String chapter);

    /**
     * Obtener lista de capítulos disponibles
     */
    @GetMapping("/api/templates/chapters")
    List<String> getAvailableChapters();

    /**
     * Validar campo específico del template
     */
    @PostMapping("/api/templates/{chapterKey}/validate-field")
    ValidationFieldResult validateTemplateField(@PathVariable("chapterKey") String chapterKey,
                                              @RequestParam("fieldKey") String fieldKey,
                                              @RequestBody String value);

    /**
     * Verificar si un campo es requerido en el template
     */
    @GetMapping("/api/templates/{chapterKey}/fields/{fieldKey}/required")
    boolean isFieldRequired(@PathVariable("chapterKey") String chapterKey,
                           @PathVariable("fieldKey") String fieldKey);

    /**
     * Record para resultado de validación de campo
     */
    record ValidationFieldResult(boolean isValid, String message) {}
}