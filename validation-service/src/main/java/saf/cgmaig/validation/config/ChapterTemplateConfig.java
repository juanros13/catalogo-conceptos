package saf.cgmaig.validation.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import saf.cgmaig.validation.model.ChapterTemplate;

import java.util.Map;

/**
 * Configuración de templates de capítulos CUBS
 * 
 * Carga la configuración desde chapter-templates.yml para definir
 * dinámicamente la estructura de campos por capítulo.
 */
@Configuration
@ConfigurationProperties(prefix = "cubs.validation.templates")
public class ChapterTemplateConfig {

    private Map<String, ChapterTemplate> chapterTemplates;
    private Map<String, String> validationPatterns;

    // Getters y Setters
    public Map<String, ChapterTemplate> getChapterTemplates() {
        return chapterTemplates;
    }

    public void setChapterTemplates(Map<String, ChapterTemplate> chapterTemplates) {
        this.chapterTemplates = chapterTemplates;
    }

    public Map<String, String> getValidationPatterns() {
        return validationPatterns;
    }

    public void setValidationPatterns(Map<String, String> validationPatterns) {
        this.validationPatterns = validationPatterns;
    }
}