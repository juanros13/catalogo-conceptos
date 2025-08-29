package saf.cgmaig.technicalconcept.entity;

public enum AreaFacultada {
    CGRM("Coordinación General de Recursos Materiales", 2000),
    CGSG("Coordinación General de Servicios Generales", 3000),
    CGMAIG("Coordinación General de Modernización Administrativa e Innovación Gubernamental", 5000),
    PATRIMONIO("Subdirección de Patrimonio", 5000);

    private final String descripcion;
    private final Integer capitulo;

    AreaFacultada(String descripcion, Integer capitulo) {
        this.descripcion = descripcion;
        this.capitulo = capitulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public Integer getCapitulo() {
        return capitulo;
    }

    public static AreaFacultada[] getByCapitulo(Integer capitulo) {
        return java.util.Arrays.stream(values())
            .filter(area -> area.getCapitulo().equals(capitulo))
            .toArray(AreaFacultada[]::new);
    }
}