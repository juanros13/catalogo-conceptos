package saf.cgmaig.validation.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import saf.cgmaig.validation.config.ChapterTemplateConfig;
import saf.cgmaig.validation.model.ChapterTemplate;
import saf.cgmaig.validation.model.TemplateField;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Servicio para gestionar templates de capítulos CUBS
 * 
 * Proporciona estructura dinámica y consumible para diferentes capítulos,
 * permitiendo validaciones específicas por tipo de concepto.
 */
@Service
public class ChapterTemplateService {

    private static final Logger logger = LoggerFactory.getLogger(ChapterTemplateService.class);

    private final ChapterTemplateConfig templateConfig;

    @Autowired
    public ChapterTemplateService(ChapterTemplateConfig templateConfig) {
        this.templateConfig = templateConfig;
    }

    /**
     * Obtiene template por clave de capítulo
     */
    public Optional<ChapterTemplate> getTemplate(String chapterKey) {
        logger.debug("Obteniendo template para capítulo: {}", chapterKey);
        
        Map<String, ChapterTemplate> templates = templateConfig.getChapterTemplates();
        ChapterTemplate template = templates.get(chapterKey);
        
        if (template != null) {
            logger.debug("Template encontrado: {} con {} campos", template.getName(), template.getRequiredFields());
        } else {
            logger.warn("Template no encontrado para capítulo: {}", chapterKey);
        }
        
        return Optional.ofNullable(template);
    }

    /**
     * Obtiene todos los templates disponibles
     */
    public Map<String, ChapterTemplate> getAllTemplates() {
        return templateConfig.getChapterTemplates();
    }

    /**
     * Obtiene templates por capítulo base (ej: "2000" devuelve MATERIALES y SERVICIOS)
     */
    public List<ChapterTemplate> getTemplatesByChapter(String chapter) {
        return templateConfig.getChapterTemplates().entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(chapter))
                .map(Map.Entry::getValue)
                .toList();
    }

    /**
     * Valida si un campo es requerido en el template
     */
    public boolean isFieldRequired(String chapterKey, String fieldKey) {
        return getTemplate(chapterKey)
                .map(template -> template.getFields().stream()
                        .anyMatch(field -> field.getFieldKey().equals(fieldKey) && field.isRequired()))
                .orElse(false);
    }

    /**
     * Obtiene configuración de un campo específico
     */
    public Optional<TemplateField> getFieldConfig(String chapterKey, String fieldKey) {
        return getTemplate(chapterKey)
                .flatMap(template -> template.getFields().stream()
                        .filter(field -> field.getFieldKey().equals(fieldKey))
                        .findFirst());
    }

    /**
     * Valida un valor contra las reglas del campo
     */
    public ValidationFieldResult validateFieldValue(String chapterKey, String fieldKey, String value) {
        Optional<TemplateField> fieldConfig = getFieldConfig(chapterKey, fieldKey);
        
        if (fieldConfig.isEmpty()) {
            return new ValidationFieldResult(false, "Campo no encontrado en template");
        }

        TemplateField field = fieldConfig.get();
        
        // Validar campo requerido
        if (field.isRequired() && (value == null || value.trim().isEmpty())) {
            return new ValidationFieldResult(false, 
                String.format("El campo '%s' es requerido", field.getFieldName()));
        }

        // Validar longitud máxima
        if (value != null && value.length() > field.getMaxLength()) {
            return new ValidationFieldResult(false, 
                String.format("El campo '%s' excede la longitud máxima de %d caracteres", 
                             field.getFieldName(), field.getMaxLength()));
        }

        // Validar reglas específicas
        if (value != null && !value.trim().isEmpty()) {
            for (String rule : field.getValidationRules()) {
                if (!validateRule(rule, value)) {
                    return new ValidationFieldResult(false, 
                        String.format("El campo '%s' no cumple con la regla: %s", 
                                     field.getFieldName(), rule));
                }
            }
        }

        return new ValidationFieldResult(true, "Campo válido");
    }

    /**
     * Valida una regla específica
     */
    private boolean validateRule(String rule, String value) {
        Map<String, String> patterns = templateConfig.getValidationPatterns();
        
        if (rule.equals("not_empty")) {
            return !value.trim().isEmpty();
        }
        
        if (rule.startsWith("min_length:")) {
            int minLength = Integer.parseInt(rule.split(":")[1]);
            return value.trim().length() >= minLength;
        }
        
        // Validaciones con patrones regex
        String pattern = patterns.get(rule);
        if (pattern != null) {
            return Pattern.compile(pattern, Pattern.CASE_INSENSITIVE).matcher(value).find();
        }
        
        logger.warn("Regla de validación desconocida: {}", rule);
        return true; // Si no conocemos la regla, la pasamos
    }

    /**
     * Obtiene estructura completa del template para frontend
     */
    public TemplateStructure getTemplateStructure(String chapterKey) {
        Optional<ChapterTemplate> template = getTemplate(chapterKey);
        
        if (template.isEmpty()) {
            return new TemplateStructure(chapterKey, "Template no encontrado", List.of(), 0);
        }

        ChapterTemplate t = template.get();
        return new TemplateStructure(
            chapterKey,
            t.getName(),
            t.getFields(),
            t.getRequiredFields()
        );
    }

    /**
     * Obtiene lista de capítulos disponibles
     */
    public List<String> getAvailableChapters() {
        return templateConfig.getChapterTemplates().keySet().stream()
                .map(key -> key.contains("_") ? key.split("_")[0] : key)
                .distinct()
                .sorted()
                .toList();
    }

    /**
     * Record para resultado de validación de campo
     */
    public record ValidationFieldResult(boolean isValid, String message) {}

    /**
     * Record para estructura de template (consumible por frontend)
     */
    public record TemplateStructure(
        String chapterKey,
        String name,
        List<TemplateField> fields,
        int requiredFieldsCount
    ) {}
}