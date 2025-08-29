package saf.cgmaig.auth.entity;

/**
 * Enum para los posibles estados de un empleado en la nómina del Gobierno de Tabasco.
 * 
 * Estados:
 * - ACTIVO: Empleado activo con acceso a sistemas
 * - INACTIVO: Empleado dado de baja temporal
 * - SUSPENDIDO: Empleado suspendido administrativamente
 */
public enum StatusNomina {
    ACTIVO("Activo"),
    INACTIVO("Inactivo"), 
    SUSPENDIDO("Suspendido");

    private final String descripcion;

    StatusNomina(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Verifica si el status permite acceso a los sistemas
     */
    public boolean permiteAcceso() {
        return this == ACTIVO;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}