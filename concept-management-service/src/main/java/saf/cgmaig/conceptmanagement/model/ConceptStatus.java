package saf.cgmaig.conceptmanagement.model;

/**
 * Estados del ciclo de vida de conceptos específicos por áreas
 * 
 * Define el flujo completo desde creación hasta aprobación/rechazo
 * por validadores técnicos.
 */
public enum ConceptStatus {
    
    /**
     * Concepto en borrador - Usuario trabajando en él
     */
    DRAFT("Borrador", "El concepto está siendo creado o editado"),
    
    /**
     * Concepto enviado para validación
     */
    SUBMITTED("Enviado", "El concepto ha sido enviado para validación técnica"),
    
    /**
     * Concepto en revisión por validador técnico
     */
    IN_REVIEW("En Revisión", "El concepto está siendo revisado por un validador técnico"),
    
    /**
     * Concepto aprobado por validador técnico
     */
    APPROVED("Aprobado", "El concepto ha sido aprobado y está activo en el catálogo"),
    
    /**
     * Concepto rechazado por validador técnico
     */
    REJECTED("Rechazado", "El concepto ha sido rechazado y requiere correcciones"),
    
    /**
     * Concepto inactivo (no disponible para uso)
     */
    INACTIVE("Inactivo", "El concepto ha sido desactivado del catálogo");

    private final String displayName;
    private final String description;

    ConceptStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Verifica si el concepto puede ser editado
     */
    public boolean isEditable() {
        return this == DRAFT || this == REJECTED;
    }

    /**
     * Verifica si el concepto puede ser enviado para validación
     */
    public boolean canBeSubmitted() {
        return this == DRAFT || this == REJECTED;
    }

    /**
     * Verifica si el concepto está disponible para uso
     */
    public boolean isActive() {
        return this == APPROVED;
    }

    /**
     * Obtiene estados que requieren acción del usuario
     */
    public static ConceptStatus[] getUserActionStates() {
        return new ConceptStatus[]{DRAFT, REJECTED};
    }

    /**
     * Obtiene estados que requieren acción del validador
     */
    public static ConceptStatus[] getValidatorActionStates() {
        return new ConceptStatus[]{SUBMITTED, IN_REVIEW};
    }

    @Override
    public String toString() {
        return displayName;
    }
}