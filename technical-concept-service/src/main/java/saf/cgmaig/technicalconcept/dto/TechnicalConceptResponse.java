package saf.cgmaig.technicalconcept.dto;

import saf.cgmaig.technicalconcept.entity.AreaFacultada;
import saf.cgmaig.technicalconcept.entity.ConceptStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class TechnicalConceptResponse {

    private UUID id;
    private String nombre;
    private String descripcionDetallada;
    private Integer capitulo;
    private List<String> partidasPermitidas;
    private AreaFacultada areaFacultada;
    private String areaFacultadaDescripcion;
    private ConceptStatus estado;
    private String estadoDescripcion;
    private Integer version;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
    private String creadoPor;
    private String actualizadoPor;
    private String motivoCambio;

    // Constructores
    public TechnicalConceptResponse() {}

    // Getters y Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

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
        this.areaFacultadaDescripcion = areaFacultada != null ? areaFacultada.getDescripcion() : null;
    }

    public String getAreaFacultadaDescripcion() {
        return areaFacultadaDescripcion;
    }

    public void setAreaFacultadaDescripcion(String areaFacultadaDescripcion) {
        this.areaFacultadaDescripcion = areaFacultadaDescripcion;
    }

    public ConceptStatus getEstado() {
        return estado;
    }

    public void setEstado(ConceptStatus estado) {
        this.estado = estado;
        this.estadoDescripcion = estado != null ? estado.getDescripcion() : null;
    }

    public String getEstadoDescripcion() {
        return estadoDescripcion;
    }

    public void setEstadoDescripcion(String estadoDescripcion) {
        this.estadoDescripcion = estadoDescripcion;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    public String getCreadoPor() {
        return creadoPor;
    }

    public void setCreadoPor(String creadoPor) {
        this.creadoPor = creadoPor;
    }

    public String getActualizadoPor() {
        return actualizadoPor;
    }

    public void setActualizadoPor(String actualizadoPor) {
        this.actualizadoPor = actualizadoPor;
    }

    public String getMotivoCambio() {
        return motivoCambio;
    }

    public void setMotivoCambio(String motivoCambio) {
        this.motivoCambio = motivoCambio;
    }

    // Método helper para capturistas
    public boolean isDisponibleParaCaptura() {
        return ConceptStatus.ACTIVO.equals(this.estado) && Boolean.TRUE.equals(this.activo);
    }
}