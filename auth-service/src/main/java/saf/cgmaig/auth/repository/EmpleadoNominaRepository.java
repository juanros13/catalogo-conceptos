package saf.cgmaig.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import saf.cgmaig.auth.entity.EmpleadoNomina;
import saf.cgmaig.auth.entity.StatusNomina;

import java.util.List;
import java.util.Optional;

/**
 * Repository para acceso a la vista de empleados de nómina.
 * 
 * Proporciona métodos para:
 * - Buscar empleados por CURP
 * - Validar status activo en nómina
 * - Buscar por dependencia
 * - Consultas personalizadas para autenticación
 */
@Repository
public interface EmpleadoNominaRepository extends JpaRepository<EmpleadoNomina, String> {

    /**
     * Busca un empleado por CURP exacto
     */
    Optional<EmpleadoNomina> findByCurp(String curp);

    /**
     * Busca un empleado por CURP ignorando mayúsculas/minúsculas
     */
    Optional<EmpleadoNomina> findByCurpIgnoreCase(String curp);

    /**
     * Verifica si existe un empleado con CURP y status ACTIVO
     */
    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM EmpleadoNomina e WHERE e.curp = :curp AND e.statusNomina = :status")
    boolean existsByCurpAndStatusNomina(@Param("curp") String curp, @Param("status") StatusNomina status);

    /**
     * Busca un empleado activo por CURP para autenticación
     */
    @Query("SELECT e FROM EmpleadoNomina e WHERE e.curp = :curp AND e.statusNomina = 'ACTIVO'")
    Optional<EmpleadoNomina> findActiveByCurp(@Param("curp") String curp);

    /**
     * Busca empleados por dependencia y status
     */
    List<EmpleadoNomina> findByDependenciaAndStatusNomina(String dependencia, StatusNomina status);

    /**
     * Busca empleados por email (para casos donde se use email como usuario)
     */
    Optional<EmpleadoNomina> findByEmailIgnoreCase(String email);

    /**
     * Busca empleados activos por email
     */
    @Query("SELECT e FROM EmpleadoNomina e WHERE LOWER(e.email) = LOWER(:email) AND e.statusNomina = 'ACTIVO'")
    Optional<EmpleadoNomina> findActiveByEmail(@Param("email") String email);

    /**
     * Cuenta empleados activos por dependencia
     */
    @Query("SELECT COUNT(e) FROM EmpleadoNomina e WHERE e.dependencia = :dependencia AND e.statusNomina = 'ACTIVO'")
    long countActiveByDependencia(@Param("dependencia") String dependencia);

    /**
     * Busca empleados por nombres y apellidos (búsqueda flexible)
     */
    @Query("SELECT e FROM EmpleadoNomina e WHERE " +
           "LOWER(CONCAT(e.nombres, ' ', COALESCE(e.apellidoPaterno, ''), ' ', COALESCE(e.apellidoMaterno, ''))) " +
           "LIKE LOWER(CONCAT('%', :nombre, '%')) " +
           "AND e.statusNomina = 'ACTIVO'")
    List<EmpleadoNomina> findActiveByNombreContaining(@Param("nombre") String nombre);

    /**
     * Obtiene todos los empleados activos de una dependencia específica
     */
    @Query("SELECT e FROM EmpleadoNomina e WHERE e.dependencia = :dependencia AND e.statusNomina = 'ACTIVO' ORDER BY e.apellidoPaterno, e.apellidoMaterno, e.nombres")
    List<EmpleadoNomina> findActiveByDependenciaOrderedByName(@Param("dependencia") String dependencia);
}