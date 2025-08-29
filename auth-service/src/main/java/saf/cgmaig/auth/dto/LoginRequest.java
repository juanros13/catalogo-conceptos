package saf.cgmaig.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO para solicitudes de login al sistema de acceso del Gobierno de Tabasco.
 * 
 * Campos:
 * - curp: CURP del empleado (se validará en nómina)
 * - password: Password del usuario en Keycloak
 */
public class LoginRequest {

    @NotBlank(message = "CURP es requerido")
    @Size(min = 18, max = 18, message = "CURP debe tener exactamente 18 caracteres")
    private String curp;

    @NotBlank(message = "Password es requerido")
    @Size(min = 6, message = "Password debe tener al menos 6 caracteres")
    private String password;

    // Constructors
    public LoginRequest() {}

    public LoginRequest(String curp, String password) {
        this.curp = curp;
        this.password = password;
    }

    // Getters and Setters
    public String getCurp() {
        return curp;
    }

    public void setCurp(String curp) {
        this.curp = curp != null ? curp.toUpperCase() : null;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "LoginRequest{" +
                "curp='" + curp + '\'' +
                ", password='[PROTECTED]'" +
                '}';
    }
}