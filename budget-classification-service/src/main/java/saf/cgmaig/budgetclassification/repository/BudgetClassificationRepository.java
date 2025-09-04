package saf.cgmaig.budgetclassification.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import saf.cgmaig.budgetclassification.entity.BudgetClassification;
import saf.cgmaig.budgetclassification.entity.BudgetLevel;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BudgetClassificationRepository extends JpaRepository<BudgetClassification, UUID> {

    // Búsqueda por código único
    Optional<BudgetClassification> findByCodigo(String codigo);
    
    boolean existsByCodigo(String codigo);

    // Búsquedas por nivel
    List<BudgetClassification> findByNivelAndActivoTrueOrderByCodigoAsc(BudgetLevel nivel);
    
    Page<BudgetClassification> findByNivelAndActivoTrueOrderByCodigoAsc(BudgetLevel nivel, Pageable pageable);

    // Búsquedas jerárquicas - obtener hijos directos
    List<BudgetClassification> findByPadreCodigoAndActivoTrueOrderByOrdenAscCodigoAsc(String padreCodigo);
    
    List<BudgetClassification> findByPadreCodigoOrderByOrdenAscCodigoAsc(String padreCodigo);

    // Obtener capítulos (nivel raíz)

    // Búsqueda por rango de códigos (para obtener toda una rama)
    @Query("SELECT bc FROM BudgetClassification bc WHERE bc.codigo LIKE :codigoPrefix% AND bc.activo = true ORDER BY bc.codigo ASC")
    List<BudgetClassification> findByCodigoStartingWithAndActivoTrue(@Param("codigoPrefix") String codigoPrefix);

    // Validar existencia de padre para integridad referencial
    @Query("SELECT COUNT(bc) > 0 FROM BudgetClassification bc WHERE bc.codigo = :padreCodigo AND bc.activo = true")
    boolean existsActivePadre(@Param("padreCodigo") String padreCodigo);

    // Contar hijos activos
    @Query("SELECT COUNT(bc) FROM BudgetClassification bc WHERE bc.padreCodigo = :padreCodigo AND bc.activo = true")
    long countHijosActivos(@Param("padreCodigo") String padreCodigo);

    // Obtener toda la jerarquía desde un nodo específico usando recursión
    @Query(value = "WITH RECURSIVE jerarquia AS ( " +
           "  SELECT codigo, nombre, descripcion, nivel, padre_codigo, orden, activo, " +
           "         fecha_creacion, fecha_actualizacion, creado_por, actualizado_por, id " +
           "  FROM budget_classifications WHERE codigo = :codigoRaiz AND activo = true " +
           "  UNION ALL " +
           "  SELECT bc.codigo, bc.nombre, bc.descripcion, bc.nivel, bc.padre_codigo, bc.orden, bc.activo, " +
           "         bc.fecha_creacion, bc.fecha_actualizacion, bc.creado_por, bc.actualizado_por, bc.id " +
           "  FROM budget_classifications bc " +
           "  INNER JOIN jerarquia j ON bc.padre_codigo = j.codigo " +
           "  WHERE bc.activo = true " +
           ") " +
           "SELECT * FROM jerarquia ORDER BY codigo ASC", 
           nativeQuery = true)
    List<BudgetClassification> findJerarquiaCompleta(@Param("codigoRaiz") String codigoRaiz);

    // Búsqueda por texto en nombre o descripción
    @Query("SELECT bc FROM BudgetClassification bc WHERE " +
           "(LOWER(bc.nombre) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(bc.descripcion) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
           "AND bc.activo = true ORDER BY bc.codigo ASC")
    List<BudgetClassification> findBySearchTerm(@Param("searchTerm") String searchTerm);

    // Búsqueda avanzada con múltiples filtros
    @Query("SELECT bc FROM BudgetClassification bc WHERE " +
           "(:nivel IS NULL OR bc.nivel = :nivel) AND " +
           "(:padreCodigo IS NULL OR bc.padreCodigo = :padreCodigo) AND " +
           "(:activo IS NULL OR bc.activo = :activo) AND " +
           "(:searchTerm IS NULL OR " +
           "LOWER(bc.nombre) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(bc.descripcion) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "bc.codigo LIKE CONCAT(:searchTerm, '%')) " +
           "ORDER BY bc.codigo ASC")
    Page<BudgetClassification> findWithFilters(
        @Param("nivel") BudgetLevel nivel,
        @Param("padreCodigo") String padreCodigo,
        @Param("activo") Boolean activo,
        @Param("searchTerm") String searchTerm,
        Pageable pageable);

    // Estadísticas por nivel
    @Query("SELECT bc.nivel, COUNT(bc) FROM BudgetClassification bc WHERE bc.activo = true GROUP BY bc.nivel")
    List<Object[]> countByNivel();

    // Verificar si un código puede ser eliminado (no tiene hijos activos)
    @Query("SELECT COUNT(bc) = 0 FROM BudgetClassification bc WHERE bc.padreCodigo = :codigo AND bc.activo = true")
    boolean canBeDeleted(@Param("codigo") String codigo);

    // Obtener ruta completa hacia arriba (breadcrumb)
    @Query(value = "WITH RECURSIVE hierarchy AS (" +
                   "  SELECT codigo, nombre, padre_codigo, nivel, 0 as depth " +
                   "  FROM budget_classifications WHERE codigo = :codigo " +
                   "  UNION ALL " +
                   "  SELECT bc.codigo, bc.nombre, bc.padre_codigo, bc.nivel, h.depth + 1 " +
                   "  FROM budget_classifications bc " +
                   "  INNER JOIN hierarchy h ON bc.codigo = h.padre_codigo " +
                   ") " +
                   "SELECT codigo, nombre, padre_codigo, nivel FROM hierarchy ORDER BY depth DESC", 
           nativeQuery = true)
    List<Object[]> getBreadcrumb(@Param("codigo") String codigo);

    // Obtener códigos que terminan con un patrón específico (útil para validaciones)
    @Query("SELECT bc FROM BudgetClassification bc WHERE bc.codigo LIKE %:suffix AND bc.activo = true")
    List<BudgetClassification> findByCodigoSuffix(@Param("suffix") String suffix);

    // Validaciones de integridad para niveles específicos
    @Query("SELECT COUNT(bc) FROM BudgetClassification bc WHERE bc.nivel = :nivel AND bc.activo = true")
    long countByNivelAndActivo(@Param("nivel") BudgetLevel nivel);
}
