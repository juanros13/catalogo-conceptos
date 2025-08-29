package saf.cgmaig.technicalconcept.entity;

public enum ConceptStatus {
    ACTIVO("Activo - Disponible para captura"),
    INACTIVO("Inactivo - No disponible para nuevos conceptos"),
    PENDIENTE("Pendiente de aprobación"),
    REVISION("En revisión");

    private final String descripcion;

    ConceptStatus(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}