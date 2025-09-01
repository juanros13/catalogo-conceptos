package saf.cgmaig.conceptmanagement.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import saf.cgmaig.conceptmanagement.model.AreaConcept;
import saf.cgmaig.conceptmanagement.model.ConceptStatus;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para conceptos específicos de áreas
 */
@Repository
public interface AreaConceptRepository extends JpaRepository<AreaConcept, Long> {

    /**
     * Buscar conceptos por área
     */
    List<AreaConcept> findByAreaOrderByCreatedAtDesc(String area);

    /**
     * Buscar conceptos por área con paginación
     */
    Page<AreaConcept> findByArea(String area, Pageable pageable);

    /**
     * Buscar conceptos por estado
     */
    List<AreaConcept> findByStatus(ConceptStatus status);

    /**
     * Buscar conceptos por área y estado
     */
    List<AreaConcept> findByAreaAndStatus(String area, ConceptStatus status);

    /**
     * Buscar conceptos por usuario creador
     */
    List<AreaConcept> findByCreatedBy(String createdBy);

    /**
     * Buscar conceptos basados en concepto técnico base
     */
    List<AreaConcept> findByBaseConceptId(Long baseConceptId);

    /**
     * Buscar conceptos por template de capítulo
     */
    List<AreaConcept> findByChapterTemplate(String chapterTemplate);

    /**
     * Buscar conceptos por área y template
     */
    List<AreaConcept> findByAreaAndChapterTemplate(String area, String chapterTemplate);

    /**
     * Verificar si existe concepto con nombre específico en área
     */
    boolean existsBySpecificNameAndArea(String specificName, String area);

    /**
     * Verificar si existe concepto con nombre específico en área excluyendo uno específico
     */
    @Query("SELECT COUNT(c) > 0 FROM AreaConcept c WHERE c.specificName = :name AND c.area = :area AND c.id != :excludeId")
    boolean existsBySpecificNameAndAreaExcludingId(@Param("name") String specificName, 
                                                   @Param("area") String area, 
                                                   @Param("excludeId") Long excludeId);

    /**
     * Obtener conceptos pendientes de validación por área
     */
    @Query("SELECT c FROM AreaConcept c WHERE c.area = :area AND c.status IN ('SUBMITTED', 'IN_REVIEW') ORDER BY c.submittedAt ASC")
    List<AreaConcept> findPendingValidationByArea(@Param("area") String area);

    /**
     * Obtener estadísticas por área
     */
    @Query("SELECT c.status, COUNT(c) FROM AreaConcept c WHERE c.area = :area GROUP BY c.status")
    List<Object[]> getStatusStatisticsByArea(@Param("area") String area);

    /**
     * Buscar conceptos por validador técnico
     */
    List<AreaConcept> findByValidatedBy(String validatedBy);

    /**
     * Obtener conceptos activos por área
     */
    @Query("SELECT c FROM AreaConcept c WHERE c.area = :area AND c.status = 'APPROVED' ORDER BY c.specificName ASC")
    List<AreaConcept> findActiveConceptsByArea(@Param("area") String area);

    /**
     * Buscar conceptos por texto en nombre o descripción general
     */
    @Query("SELECT c FROM AreaConcept c WHERE c.area = :area AND " +
           "(LOWER(c.specificName) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
           "LOWER(c.general) LIKE LOWER(CONCAT('%', :searchText, '%')))")
    List<AreaConcept> searchConceptsByText(@Param("area") String area, @Param("searchText") String searchText);
}