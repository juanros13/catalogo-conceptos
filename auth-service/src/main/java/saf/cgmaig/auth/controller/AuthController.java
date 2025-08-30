package saf.cgmaig.auth.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import saf.cgmaig.auth.dto.AuthResponse;
import saf.cgmaig.auth.dto.LoginRequest;
import saf.cgmaig.auth.service.AuthService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

/**
 * Controlador REST para endpoints de autenticación del Sistema de Acceso del Gobierno de Tabasco.
 * 
 * Endpoints disponibles:
 * - POST /auth/login - Login con CURP y password
 * - GET /auth/validate-curp/{curp} - Validar CURP en nómina (público)
 * - GET /auth/profile - Obtener perfil del usuario autenticado
 * - POST /auth/logout - Logout del usuario
 * - GET /auth/validate-token - Validar token JWT actual
 */
@RestController
@RequestMapping("/auth")
@Validated
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;

    /**
     * Login del usuario con CURP y password
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        logger.info("Solicitud de login recibida para CURP: {}", loginRequest.getCurp());
        
        AuthResponse response = authService.login(loginRequest);
        
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.UNAUTHORIZED;
        return ResponseEntity.status(status).body(response);
    }

    /**
     * Validar CURP en nómina (endpoint público)
     */
//   z

    /**
     * Obtener perfil del usuario autenticado
     */
    @GetMapping("/profile")
    public ResponseEntity<AuthResponse> getUserProfile(Authentication authentication) {
        logger.info("Solicitud de perfil de usuario autenticado");
        
        try {
            // Extraer información directamente del JWT sin llamar a Keycloak
            Jwt jwt = (Jwt) authentication.getPrincipal();
            
            String email = jwt.getClaimAsString("email");
            String username = jwt.getClaimAsString("preferred_username");
            String name = jwt.getClaimAsString("name");
            String givenName = jwt.getClaimAsString("given_name");
            String familyName = jwt.getClaimAsString("family_name");
            
            // Construir información del usuario desde el token
            AuthResponse.UserInfo userInfo = new AuthResponse.UserInfo();
            userInfo.setEmail(email);
            userInfo.setCurp(username); // preferred_username es el email en este caso
            userInfo.setNombreCompleto(name != null ? name : (givenName + " " + familyName));
            
            // Extraer dependencia y puesto si están en custom claims
            String dependencia = jwt.getClaimAsString("dependencia");
            String puesto = jwt.getClaimAsString("puesto");
            userInfo.setDependencia(dependencia);
            userInfo.setPuesto(puesto);

            AuthResponse response = AuthResponse.success("Perfil obtenido exitosamente");
            response.setUserInfo(userInfo);
            
            logger.info("Perfil obtenido exitosamente para usuario: {}", email);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error obteniendo perfil de usuario", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(AuthResponse.error("Error obteniendo perfil de usuario"));
        }
    }

    /**
     * Logout del usuario
     */
    @PostMapping("/logout")
    public ResponseEntity<AuthResponse> logout(Authentication authentication) {
        logger.info("Solicitud de logout recibida");
        
        try {
            // Extraer token del JWT
            Jwt jwt = (Jwt) authentication.getPrincipal();
            String tokenValue = jwt.getTokenValue();
            
            AuthResponse response = authService.logout(tokenValue);
            
            HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR;
            return ResponseEntity.status(status).body(response);

        } catch (Exception e) {
            logger.error("Error durante logout", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(AuthResponse.error("Error durante logout"));
        }
    }

    /**
     * Validar token JWT actual
     */
    @GetMapping("/validate-token")
    public ResponseEntity<AuthResponse> validateToken(Authentication authentication) {
        logger.info("Validando token JWT actual");
        
        try {
            // Si llegamos aquí, el token ya fue validado por Spring Security
            Jwt jwt = (Jwt) authentication.getPrincipal();
            
            // Extraer información básica del token
            String email = jwt.getClaimAsString("email");
            String username = jwt.getClaimAsString("preferred_username");
            String name = jwt.getClaimAsString("name");
            
            // Verificar expiración
            java.time.Instant expiresAt = jwt.getExpiresAt();
            boolean isExpired = expiresAt != null && expiresAt.isBefore(java.time.Instant.now());
            
            if (isExpired) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(AuthResponse.error("Token expirado"));
            }
            
            AuthResponse response = AuthResponse.success("Token válido");
            
            // Agregar información del usuario desde el token
            if (email != null || name != null) {
                AuthResponse.UserInfo userInfo = new AuthResponse.UserInfo();
                userInfo.setEmail(email);
                userInfo.setCurp(username);
                userInfo.setNombreCompleto(name);
                response.setUserInfo(userInfo);
            }
            
            logger.info("Token validado exitosamente para usuario: {}", email);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error validando token", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(AuthResponse.error("Error validando token"));
        }
    }

    /**
     * Health check específico del auth service
     */
    @GetMapping("/health")
    public ResponseEntity<AuthResponse> healthCheck() {
        return ResponseEntity.ok(AuthResponse.success("Auth Service is running"));
    }

    /**
     * Información del servicio
     */
    @GetMapping("/info")
    public ResponseEntity<Object> info() {
        return ResponseEntity.ok(java.util.Map.of(
            "service", "auth-service",
            "version", "1.0.0",
            "description", "Servicio de Autenticación - Sistema de Acceso Gobierno de Tabasco",
            "features", java.util.List.of(
                "Validación de nómina gubernamental",
                "Integración con Keycloak",
                "Validación de CURP",
                "Manejo de tokens JWT"
            )
        ));
    }
}
