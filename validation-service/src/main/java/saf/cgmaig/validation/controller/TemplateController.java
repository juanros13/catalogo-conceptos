package saf.cgmaig.validation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import saf.cgmaig.validation.model.ChapterTemplate;
import saf.cgmaig.validation.service.ChapterTemplateService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Controller para gestionar templates de capítulos CUBS
 * 
 * Proporciona endpoints para obtener estructura de templates
 * de forma consumible para frontends y otros servicios.
 */
@RestController
@RequestMapping("/api/templates")
public class TemplateController {

    private final ChapterTemplateService templateService;

    @Autowired
    public TemplateController(ChapterTemplateService templateService) {
        this.templateService = templateService;
    }

    /**
     * Obtiene todos los templates disponibles
     */
    @GetMapping
    public ResponseEntity<Map<String, ChapterTemplate>> getAllTemplates() {
        Map<String, ChapterTemplate> templates = templateService.getAllTemplates();
        return ResponseEntity.ok(templates);
    }

    /**
     * Obtiene template específico por clave de capítulo
     */
    @GetMapping("/{chapterKey}")
    public ResponseEntity<ChapterTemplate> getTemplate(@PathVariable String chapterKey) {
        Optional<ChapterTemplate> template = templateService.getTemplate(chapterKey);
        return template.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Obtiene estructura completa del template para frontend
     */
    @GetMapping("/{chapterKey}/structure")
    public ResponseEntity<ChapterTemplateService.TemplateStructure> getTemplateStructure(
            @PathVariable String chapterKey) {
        ChapterTemplateService.TemplateStructure structure = templateService.getTemplateStructure(chapterKey);
        return ResponseEntity.ok(structure);
    }

    /**
     * Obtiene templates por capítulo base (ej: "2000" devuelve MATERIALES y SERVICIOS)
     */
    @GetMapping("/chapter/{chapter}")
    public ResponseEntity<List<ChapterTemplate>> getTemplatesByChapter(@PathVariable String chapter) {
        List<ChapterTemplate> templates = templateService.getTemplatesByChapter(chapter);
        return ResponseEntity.ok(templates);
    }

    /**
     * Obtiene lista de capítulos disponibles
     */
    @GetMapping("/chapters")
    public ResponseEntity<List<String>> getAvailableChapters() {
        List<String> chapters = templateService.getAvailableChapters();
        return ResponseEntity.ok(chapters);
    }

    /**
     * Valida un campo específico del template
     */
    @PostMapping("/{chapterKey}/validate-field")
    public ResponseEntity<ChapterTemplateService.ValidationFieldResult> validateField(
            @PathVariable String chapterKey,
            @RequestParam String fieldKey,
            @RequestBody String value) {
        
        ChapterTemplateService.ValidationFieldResult result = 
            templateService.validateFieldValue(chapterKey, fieldKey, value);
        return ResponseEntity.ok(result);
    }

    /**
     * Verifica si un campo es requerido
     */
    @GetMapping("/{chapterKey}/fields/{fieldKey}/required")
    public ResponseEntity<Boolean> isFieldRequired(
            @PathVariable String chapterKey,
            @PathVariable String fieldKey) {
        boolean required = templateService.isFieldRequired(chapterKey, fieldKey);
        return ResponseEntity.ok(required);
    }
}