package saf.cgmaig.budgetclassification.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class BudgetClassificationUpdateRequest {

    @NotBlank(message = "Nombre es obligatorio")
    @Size(max = 200, message = "Nombre no puede exceder 200 caracteres")
    private String nombre;

    @Size(max = 1000, message = "Descripción no puede exceder 1000 caracteres")
    private String descripcion;

    private Integer orden;

    // Constructor vacío
    public BudgetClassificationUpdateRequest() {}

    // Constructor con parámetros
    public BudgetClassificationUpdateRequest(String nombre, String descripcion, Integer orden) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.orden = orden;
    }

    // Getters y Setters
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