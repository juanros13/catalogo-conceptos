package saf.cgmaig.technicalconcept.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public class TechnicalConceptUpdateRequest {

    @NotBlank(message = "Nombre del concepto general es obligatorio")
    @Size(max = 200, message = "Nombre no puede exceder 200 caracteres")
    private String nombre;

    @Size(max = 1000, message = "Descripción no puede exceder 1000 caracteres")
    private String descripcionDetallada;

    private List<String> partidasPermitidas;

    @NotBlank(message = "Motivo del cambio es obligatorio")
    @Size(max = 500, message = "Motivo no puede exceder 500 caracteres")
    private String motivoCambio;

    // Constructores
    public TechnicalConceptUpdateRequest() {}

    public TechnicalConceptUpdateRequest(String nombre, String descripcionDetallada, String motivoCambio) {
        this.nombre = nombre;
        this.descripcionDetallada = descripcionDetallada;
        this.motivoCambio = motivoCambio;
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

    public List<String> getPartidasPermitidas() {
        return partidasPermitidas;
    }

    public void setPartidasPermitidas(List<String> partidasPermitidas) {
        this.partidasPermitidas = partidasPermitidas;
    }

    public String getMotivoCambio() {
        return motivoCambio;
    }

    public void setMotivoCambio(String motivoCambio) {
        this.motivoCambio = motivoCambio;
    }
}