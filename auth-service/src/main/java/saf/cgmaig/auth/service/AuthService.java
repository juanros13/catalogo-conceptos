package saf.cgmaig.auth.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import saf.cgmaig.auth.dto.AuthResponse;
import saf.cgmaig.auth.dto.LoginRequest;
import saf.cgmaig.auth.entity.EmpleadoNomina;
import saf.cgmaig.auth.repository.EmpleadoNominaRepository;

import java.util.Optional;

/**
 * Servicio principal de autenticación para el Sistema de Acceso del Gobierno de Tabasco.
 * 
 * Funcionalidades:
 * 1. Validar credenciales contra Keycloak
 * 2. Verificar que el usuario esté activo en nómina
 * 3. Obtener información del empleado para la sesión
 * 4. Manejar tokens JWT y renovación
 */
@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private EmpleadoNominaRepository empleadoNominaRepository;

    @Autowired
    private KeycloakService keycloakService;

    @Value("${tabasco.gov.nomina.validation.required-status:ACTIVO}")
    private String requiredStatus;

    /**
     * Autenticar usuario con CURP y password
     */
    public AuthResponse login(LoginRequest loginRequest) {
        logger.info("Iniciando proceso de login para CURP: {}", loginRequest.getCurp());

        try {
            // 1. Validar que el CURP existe y está activo en nómina
            Optional<EmpleadoNomina> empleadoOpt = empleadoNominaRepository.findActiveByCurp(loginRequest.getCurp());
            
            if (empleadoOpt.isEmpty()) {
                logger.warn("CURP {} no encontrado en nómina activa", loginRequest.getCurp());
                return AuthResponse.error("Usuario no encontrado o no activo en nómina");
            }

            EmpleadoNomina empleado = empleadoOpt.get();
            logger.info("Empleado encontrado en nómina: {} - {}", empleado.getCurp(), empleado.getNombreCompleto());

            // 2. Autenticar contra Keycloak
            AuthResponse keycloakResponse = keycloakService.authenticate(loginRequest.getCurp(), loginRequest.getPassword());
            
            if (!keycloakResponse.isSuccess()) {
                logger.warn("Autenticación fallida en Keycloak para CURP: {}", loginRequest.getCurp());
                return keycloakResponse;
            }

            // 3. Crear información de usuario para la respuesta
            AuthResponse.UserInfo userInfo = new AuthResponse.UserInfo(
                empleado.getCurp(),
                empleado.getNombreCompleto(),
                empleado.getEmail(),
                empleado.getDependencia(),
                empleado.getPuesto()
            );

            // 4. Crear respuesta exitosa con tokens y información del usuario
            AuthResponse response = AuthResponse.success(
                "Login exitoso",
                keycloakResponse.getAccessToken(),
                keycloakResponse.getExpiresIn(),
                userInfo
            );
            response.setRefreshToken(keycloakResponse.getRefreshToken());

            logger.info("Login exitoso para empleado: {} - {}", empleado.getCurp(), empleado.getNombreCompleto());
            return response;

        } catch (Exception e) {
            logger.error("Error durante el proceso de login para CURP: {}", loginRequest.getCurp(), e);
            return AuthResponse.error("Error interno durante la autenticación");
        }
    }

    /**
     * Validar solo si un CURP existe y está activo en nómina (sin autenticación)
     */
    public AuthResponse validateCurp(String curp) {
        logger.info("Validando CURP en nómina: {}", curp);

        try {
            Optional<EmpleadoNomina> empleadoOpt = empleadoNominaRepository.findActiveByCurp(curp);
            
            if (empleadoOpt.isEmpty()) {
                return AuthResponse.error("CURP no encontrado o no activo en nómina");
            }

            EmpleadoNomina empleado = empleadoOpt.get();
            AuthResponse.UserInfo userInfo = new AuthResponse.UserInfo(
                empleado.getCurp(),
                empleado.getNombreCompleto(),
                empleado.getEmail(),
                empleado.getDependencia(),
                empleado.getPuesto()
            );

            AuthResponse response = AuthResponse.success("CURP válido y activo en nómina");
            response.setUserInfo(userInfo);

            return response;

        } catch (Exception e) {
            logger.error("Error validando CURP: {}", curp, e);
            return AuthResponse.error("Error validando CURP en nómina");
        }
    }

    /**
     * Obtener información del empleado por CURP (requiere token válido)
     */
    public AuthResponse getUserProfile(String curp) {
        logger.info("Obteniendo perfil de usuario para CURP: {}", curp);

        try {
            Optional<EmpleadoNomina> empleadoOpt = empleadoNominaRepository.findActiveByCurp(curp);
            
            if (empleadoOpt.isEmpty()) {
                return AuthResponse.error("Usuario no encontrado");
            }

            EmpleadoNomina empleado = empleadoOpt.get();
            AuthResponse.UserInfo userInfo = new AuthResponse.UserInfo(
                empleado.getCurp(),
                empleado.getNombreCompleto(),
                empleado.getEmail(),
                empleado.getDependencia(),
                empleado.getPuesto()
            );

            AuthResponse response = AuthResponse.success("Perfil obtenido exitosamente");
            response.setUserInfo(userInfo);

            return response;

        } catch (Exception e) {
            logger.error("Error obteniendo perfil para CURP: {}", curp, e);
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