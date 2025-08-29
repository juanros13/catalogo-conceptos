package saf.cgmaig.technicalconcept.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import saf.cgmaig.technicalconcept.entity.AreaFacultada;
import saf.cgmaig.technicalconcept.entity.ConceptStatus;
import saf.cgmaig.technicalconcept.entity.TechnicalConcept;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TechnicalConceptRepository extends JpaRepository<TechnicalConcept, UUID> {

    // Búsquedas para capturistas (conceptos activos)
    List<TechnicalConcept> findByEstadoAndActivoTrueOrderByNombreAsc(ConceptStatus estado);
    
    List<TechnicalConcept> findByCapituloAndEstadoAndActivoTrueOrderByNombreAsc(
        Integer capitulo, ConceptStatus estado);

    // Búsquedas por área facultada
    Page<TechnicalConcept> findByAreaFacultadaOrderByFechaCreacionDesc(
        AreaFacultada areaFacultada, Pageable pageable);

    List<TechnicalConcept> findByAreaFacultadaAndCapituloOrderByNombreAsc(
        AreaFacultada areaFacultada, Integer capitulo);

    // Búsqueda por nombre (para validar unicidad)
    Optional<TechnicalConcept> findByNombreAndAreaFacultada(String nombre, AreaFacultada areaFacultada);

    // Verificar si existe concepto con el mismo nombre (excluyendo un ID específico)
    boolean existsByNombreAndAreaFacultadaAndIdNot(String nombre, AreaFacultada areaFacultada, UUID id);

    // Búsquedas avanzadas
    @Query("SELECT tc FROM TechnicalConcept tc WHERE " +
           "(:capitulo IS NULL OR tc.capitulo = :capitulo) AND " +
           "(:areaFacultada IS NULL OR tc.areaFacultada = :areaFacultada) AND " +
           "(:estado IS NULL OR tc.estado = :estado) AND " +
           "(:activo IS NULL OR tc.activo = :activo) AND " +
           "(:searchTerm IS NULL OR LOWER(tc.nombre) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(tc.descripcionDetallada) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<TechnicalConcept> findConceptsWithFilters(
        @Param("capitulo") Integer capitulo,
        @Param("areaFacultada") AreaFacultada areaFacultada,
        @Param("estado") ConceptStatus estado,
        @Param("activo") Boolean activo,
        @Param("searchTerm") String searchTerm,
        Pageable pageable);

    // Estadísticas
    @Query("SELECT tc.areaFacultada, COUNT(tc) FROM TechnicalConcept tc " +
           "WHERE tc.estado = :estado GROUP BY tc.areaFacultada")
    List<Object[]> countByAreaFacultadaAndEstado(@Param("estado") ConceptStatus estado);

    @Query("SELECT tc.capitulo, COUNT(tc) FROM TechnicalConcept tc " +
           "WHERE tc.estado = :estado GROUP BY tc.capitulo")
    List<Object[]> countByCapituloAndEstado(@Param("estado") ConceptStatus estado);

    // Para auditoría - conceptos modificados recientemente
    @Query("SELECT tc FROM TechnicalConcept tc WHERE " +
           "tc.fechaActualizacion >= CURRENT_DATE - :days " +
           "ORDER BY tc.fechaActualizacion DESC")
    List<TechnicalConcept> findRecentlyModified(@Param("days") int days);

    // Validaciones de negocio
    @Query("SELECT COUNT(tc) > 0 FROM TechnicalConcept tc WHERE " +
           "tc.areaFacultada = :areaFacultada AND tc.capitulo != :expectedCapitulo")
    boolean existsInvalidAreaCapituloMapping(
        @Param("areaFacultada") AreaFacultada areaFacultada, 
        @Param("expectedCapitulo") Integer expectedCapitulo);
}