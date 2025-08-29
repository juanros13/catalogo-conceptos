package saf.cgmaig.technicalconcept.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import saf.cgmaig.technicalconcept.entity.AreaFacultada;

import java.util.List;

public class TechnicalConceptCreateRequest {

    @NotBlank(message = "Nombre del concepto general es obligatorio")
    @Size(max = 200, message = "Nombre no puede exceder 200 caracteres")
    private String nombre;

    @Size(max = 1000, message = "Descripción no puede exceder 1000 caracteres")
    private String descripcionDetallada;

    @NotNull(message = "Capítulo es obligatorio")
    private Integer capitulo;

    private List<String> partidasPermitidas;

    @NotNull(message = "Área facultada es obligatoria")
    private AreaFacultada areaFacultada;

    @Size(max = 500, message = "Motivo no puede exceder 500 caracteres")
    private String motivoCreacion;

    // Constructores
    public TechnicalConceptCreateRequest() {}

    public TechnicalConceptCreateRequest(String nombre, String descripcionDetallada, 
                                       Integer capitulo, AreaFacultada areaFacultada) {
        this.nombre = nombre;
        this.descripcionDetallada = descripcionDetallada;
        this.capitulo = capitulo;
        this.areaFacultada = areaFacultada;
    }

    // Getters y Setters
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcionDetallada() {
        return descripcionDetallada;
    }

    public void setDescripcionDetallada(String descripcionDetallada) {
        this.descripcionDetallada = descripcionDetallada;
    }

    public Integer getCapitulo() {
        return capitulo;
    }

    public void setCapitulo(Integer capitulo) {
        this.capitulo = capitulo;
    }

    public List<String> getPartidasPermitidas() {
        return partidasPermitidas;
    }

    public void setPartidasPermitidas(List<String> partidasPermitidas) {
        this.partidasPermitidas = partidasPermitidas;
    }

    public AreaFacultada getAreaFacultada() {
        return areaFacultada;
    }

    public void setAreaFacultada(AreaFacultada areaFacultada) {
        this.areaFacultada = areaFacultada;
    }

    public String getMotivoCreacion() {
        return motivoCreacion;
    }

    public void setMotivoCreacion(String motivoCreacion) {
        this.motivoCreacion = motivoCreacion;
    }
}