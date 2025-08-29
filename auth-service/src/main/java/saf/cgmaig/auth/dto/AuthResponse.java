package saf.cgmaig.auth.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * DTO para respuestas de autenticación del sistema de acceso.
 * 
 * Campos:
 * - success: Indica si la operación fue exitosa
 * - message: Mensaje descriptivo de la operación
 * - accessToken: Token JWT de acceso (solo en login exitoso)
 * - refreshToken: Token de refresh (solo en login exitoso)
 * - expiresIn: Tiempo de expiración del token en segundos
 * - userInfo: Información básica del usuario autenticado
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthResponse {

    private boolean success;
    private String message;
    private String accessToken;
    private String refreshToken;
    private Long expiresIn;
    private UserInfo userInfo;

    // Constructors
    public AuthResponse() {}

    public AuthResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public AuthResponse(boolean success, String message, String accessToken, Long expiresIn, UserInfo userInfo) {
        this.success = success;
        this.message = message;
        this.accessToken = accessToken;
        this.expiresIn = expiresIn;
        this.userInfo = userInfo;
    }

    // Static factory methods
    public static AuthResponse success(String message) {
        return new AuthResponse(true, message);
    }

    public static AuthResponse success(String message, String accessToken, Long expiresIn, UserInfo userInfo) {
        return new AuthResponse(true, message, accessToken, expiresIn, userInfo);
    }

    public static AuthResponse error(String message) {
        return new AuthResponse(false, message);
    }

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    /**
     * Clase interna para información básica del usuario
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class UserInfo {
        private String curp;
        private String nombreCompleto;
        private String email;
        private String dependencia;
        private String puesto;

        public UserInfo() {}

        public UserInfo(String curp, String nombreCompleto, String email, String dependencia, String puesto) {
            this.curp = curp;
            this.nombreCompleto = nombreCompleto;
            this.email = email;
            this.dependencia = dependencia;
            this.puesto = puesto;
        }

        // Getters and Setters
        public String getCurp() {
            return curp;
        }

        public void setCurp(String curp) {
            this.curp = curp;
        }

        public String getNombreCompleto() {
            return nombreCompleto;
        }

        public void setNombreCompleto(String nombreCompleto) {
            this.nombreCompleto = nombreCompleto;
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
    }
}