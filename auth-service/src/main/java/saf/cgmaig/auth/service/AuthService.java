package saf.cgmaig.auth.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import saf.cgmaig.auth.dto.AuthResponse;
import saf.cgmaig.auth.dto.LoginRequest;

/**
 * Servicio principal de autenticación para el Sistema de Acceso del Gobierno de Tabasco.
 * 
 * Funcionalidades:
 * 1. Validar credenciales contra Keycloak
 * 2. Manejar tokens JWT y renovación
 * 3. Proveer información de usuario desde JWT
 */
@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private KeycloakService keycloakService;

    /**
     * Autenticar usuario con CURP y password
     */
    public AuthResponse login(LoginRequest loginRequest) {
        logger.info("Iniciando proceso de login para CURP: {}", loginRequest.getCurp());

        try {
            // Autenticar contra Keycloak
            AuthResponse keycloakResponse = keycloakService.authenticate(loginRequest.getCurp(), loginRequest.getPassword());
            
            if (!keycloakResponse.isSuccess()) {
                logger.warn("Autenticación fallida en Keycloak para CURP: {}", loginRequest.getCurp());
                return keycloakResponse;
            }

            logger.info("Login exitoso para CURP: {}", loginRequest.getCurp());
            return keycloakResponse;

        } catch (Exception e) {
            logger.error("Error durante el proceso de login para CURP: {}", loginRequest.getCurp(), e);
            return AuthResponse.error("Error interno durante la autenticación");
        }
    }

    /**
     * Obtener información del usuario desde JWT token
     */
    public AuthResponse getUserProfile(String accessToken) {
        logger.info("Obteniendo perfil de usuario desde token JWT");

        try {
            // Keycloak service should extract user info from JWT
            AuthResponse response = keycloakService.getUserInfoFromToken(accessToken);
            
            if (response.isSuccess()) {
                logger.info("Perfil obtenido exitosamente");
            }

            return response;

        } catch (Exception e) {
            logger.error("Error obteniendo perfil de usuario desde token", e);
            return AuthResponse.error("Error obteniendo perfil de usuario");
        }
    }

    /**
     * Logout del usuario (invalidar token en Keycloak)
     */
    public AuthResponse logout(String accessToken) {
        logger.info("Procesando logout de usuario");

        try {
            boolean success = keycloakService.logout(accessToken);
            
            if (success) {
                return AuthResponse.success("Logout exitoso");
            } else {
                return AuthResponse.error("Error durante logout");
            }

        } catch (Exception e) {
            logger.error("Error durante logout", e);
            return AuthResponse.error("Error interno durante logout");
        }
    }

    /**
     * Validar token JWT
     */
    public AuthResponse validateToken(String accessToken) {
        logger.info("Validando token JWT");

        try {
            boolean isValid = keycloakService.validateToken(accessToken);
            
            if (isValid) {
                return AuthResponse.success("Token válido");
            } else {
                return AuthResponse.error("Token inválido o expirado");
            }

        } catch (Exception e) {
            logger.error("Error validando token", e);
            return AuthResponse.error("Error validando token");
        }
    }
}