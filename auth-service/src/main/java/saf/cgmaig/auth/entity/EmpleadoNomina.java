package saf.cgmaig.auth.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity para la vista de empleados de nómina del Gobierno de Tabasco.
 * 
 * Mapea la vista vw_empleados_nomina que contiene:
 * - Datos personales del empleado (CURP, nombres)
 * - Información laboral (dependencia, puesto)
 * - Status en nómina (ACTIVO, INACTIVO, SUSPENDIDO)
 */
@Entity
@Table(name = "vw_empleados_nomina")
public class EmpleadoNomina {

    @Id
    @Column(name = "curp", length = 18)
    @NotBlank(message = "CURP es requerido")
    @Pattern(regexp = "^[A-Z]{1}[AEIOUX]{1}[A-Z]{2}[0-9]{2}[0-1]{1}[0-9]{1}[0-3]{1}[0-9]{1}[HM]{1}[A-Z]{2}[BCDFGHJKLMNPQRSTVWXYZ]{3}[0-9A-Z]{1}[0-9]{1}$", 
             message = "CURP no tiene formato válido")
    private String curp;

    @Column(name = "nombres", length = 100)
    @NotBlank(message = "Nombres son requeridos")
    @Size(max = 100, message = "Nombres no puede exceder 100 caracteres")
    private String nombres;

    @Column(name = "apellido_paterno", length = 50)
    @Size(max = 50, message = "Apellido paterno no puede exceder 50 caracteres")
    private String apellidoPaterno;

    @Column(name = "apellido_materno", length = 50)
    @Size(max = 50, message = "Apellido materno no puede exceder 50 caracteres")
    private String apellidoMaterno;

    @Column(name = "email", length = 100)
    @Size(max = 100, message = "Email no puede exceder 100 caracteres")
    private String email;

    @Column(name = "dependencia", length = 100)
    @Size(max = 100, message = "Dependencia no puede exceder 100 caracteres")
    private String dependencia;

    @Column(name = "puesto", length = 100)
    @Size(max = 100, message = "Puesto no puede exceder 100 caracteres")
    private String puesto;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_nomina")
    private StatusNomina statusNomina;

    @Column(name = "fecha_ingreso")
    private LocalDate fechaIngreso;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    // Constructors
    public EmpleadoNomina() {}

    public EmpleadoNomina(String curp, String nombres, String apellidoPaterno, String apellidoMaterno) {
        this.curp = curp;
        this.nombres = nombres;
        this.apellidoPaterno = apellidoPaterno;
        this.apellidoMaterno = apellidoMaterno;
        this.statusNomina = StatusNomina.ACTIVO;
    }

    // Getters and Setters
    public String getCurp() {
        return curp;
    }

    public void setCurp(String curp) {
        this.curp = curp;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidoPaterno() {
        return apellidoPaterno;
    }

    public void setApellidoPaterno(String apellidoPaterno) {
        this.apellidoPaterno = apellidoPaterno;
    }

    public String getApellidoMaterno() {
        return apellidoMaterno;
    }

    public void setApellidoMaterno(String apellidoMaterno) {
        this.apellidoMaterno = apellidoMaterno;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDependencia() {
        return dependencia;
    }

    public void setDependencia(String dependencia) {
        this.dependencia = dependencia;
    }

    public String getPuesto() {
        return puesto;
    }

    public void setPuesto(String puesto) {
        this.puesto = puesto;
    }

    public StatusNomina getStatusNomina() {
        return statusNomina;
    }

    public void setStatusNomina(StatusNomina statusNomina) {
        this.statusNomina = statusNomina;
    }

    public LocalDate getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(LocalDate fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    /**
     * Método de utilidad para obtener el nombre completo del empleado
     */
    public String getNombreCompleto() {
        StringBuilder nombreCompleto = new StringBuilder();
        nombreCompleto.append(nombres);
        
        if (apellidoPaterno != null && !apellidoPaterno.trim().isEmpty()) {
            nombreCompleto.append(" ").append(apellidoPaterno);
        }
        
        if (apellidoMaterno != null && !apellidoMaterno.trim().isEmpty()) {
            nombreCompleto.append(" ").append(apellidoMaterno);
        }
        
        return nombreCompleto.toString();
    }

    /**
     * Verifica si el empleado está activo en nómina
     */
    public boolean isActivo() {
        return StatusNomina.ACTIVO.equals(this.statusNomina);
    }

    @Override
    public String toString() {
        return "EmpleadoNomina{" +
                "curp='" + curp + '\'' +
                ", nombreCompleto='" + getNombreCompleto() + '\'' +
                ", dependencia='" + dependencia + '\'' +
                ", statusNomina=" + statusNomina +
                '}';
    }
}