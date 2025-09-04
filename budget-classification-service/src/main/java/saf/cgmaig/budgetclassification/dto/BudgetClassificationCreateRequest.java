package saf.cgmaig.budgetclassification.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class BudgetClassificationCreateRequest {

    @NotBlank(message = "Código es obligatorio")
    @Pattern(regexp = "^[0-9]{4}$", message = "Código debe tener exactamente 4 dígitos")
    private String codigo;

    @NotBlank(message = "Nombre es obligatorio")
    @Size(max = 200, message = "Nombre no puede exceder 200 caracteres")
    private String nombre;

    @Size(max = 1000, message = "Descripción no puede exceder 1000 caracteres")
    private String descripcion;

    private Integer orden = 0;

    // Constructor vacío
    public BudgetClassificationCreateRequest() {}

    // Constructor con parámetros
    public BudgetClassificationCreateRequest(String codigo, String nombre, String descripcion) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    // Getters y Setters
    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getOrden() {
        return orden;
    }

    public void setOrden(Integer orden) {
        this.orden = orden;
    }
}