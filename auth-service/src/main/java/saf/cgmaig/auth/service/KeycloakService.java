package saf.cgmaig.auth.service;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import saf.cgmaig.auth.dto.AuthResponse;

import jakarta.annotation.PostConstruct;
import java.util.Map;

/**
 * Servicio para interactuar con Keycloak para autenticación y manejo de tokens.
 * 
 * Funcionalidades:
 * - Autenticar usuarios contra Keycloak
 * - Obtener tokens JWT
 * - Validar tokens
 * - Logout/invalidar tokens
 */
@Service
public class KeycloakService {

    private static final Logger logger = LoggerFactory.getLogger(KeycloakService.class);

    @Value("${keycloak.auth-server-url}")
    private String keycloakUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.resource:gateway-client}")
    private String clientId;

    @Value("${keycloak.credentials.secret}")
    private String clientSecret;

    private RestTemplate restTemplate;
    private String tokenEndpoint;
    private String logoutEndpoint;

    @PostConstruct
    public void init() {
        this.restTemplate = new RestTemplate();
        this.tokenEndpoint = String.format("%s/realms/%s/protocol/openid-connect/token", keycloakUrl, realm);
        this.logoutEndpoint = String.format("%s/realms/%s/protocol/openid-connect/logout", keycloakUrl, realm);
        
        logger.info("KeycloakService initialized with realm: {} and client: {}", realm, clientId);
    }

    /**
     * Autenticar usuario con username (CURP) y password
     */
    public AuthResponse authenticate(String username, String password) {
        logger.info("Autenticando usuario en Keycloak: {}", username);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("grant_type", "password");
            params.add("client_id", clientId);
            params.add("client_secret", clientSecret);
            params.add("username", username);
            params.add("password", password);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(tokenEndpoint, request, Map.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> tokenResponse = response.getBody();
                
                String accessToken = (String) tokenResponse.get("access_token");
                String refreshToken = (String) tokenResponse.get("refresh_token");
                Integer expiresIn = (Integer) tokenResponse.get("expires_in");

                logger.info("Autenticación exitosa en Keycloak para usuario: {}", username);

                AuthResponse authResponse = AuthResponse.success("Autenticación exitosa en Keycloak");
                authResponse.setAccessToken(accessToken);
                authResponse.setRefreshToken(refreshToken);
                authResponse.setExpiresIn(expiresIn != null ? expiresIn.longValue() : null);

                return authResponse;

            } else {
                logger.warn("Autenticación fallida en Keycloak para usuario: {}, status: {}", username, response.getStatusCode());
                return AuthResponse.error("Credenciales inválidas");
            }

        } catch (Exception e) {
            logger.error("Error durante autenticación en Keycloak para usuario: {}", username, e);
            
            // Analizar el tipo de error para dar una respuesta más específica
            if (e.getMessage().contains("401") || e.getMessage().contains("Unauthorized")) {
                return AuthResponse.error("Credenciales inválidas");
            } else if (e.getMessage().contains("400") || e.getMessage().contains("Bad Request")) {
                return AuthResponse.error("Solicitud inválida");
            } else {
                return AuthResponse.error("Error de conexión con el servidor de autenticación");
            }
        }
    }

    /**
     * Logout del usuario invalidando el token
     */
    public boolean logout(String accessToken) {
        logger.info("Procesando logout en Keycloak");

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.setBearerAuth(accessToken);

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("client_id", clientId);
            params.add("client_secret", clientSecret);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(logoutEndpoint, request, String.class);

            boolean success = response.getStatusCode() == HttpStatus.NO_CONTENT || response.getStatusCode() == HttpStatus.OK;
            
            if (success) {
                logger.info("Logout exitoso en Keycloak");
            } else {
                logger.warn("Logout falló en Keycloak, status: {}", response.getStatusCode());
            }

            return success;

        } catch (Exception e) {
            logger.error("Error durante logout en Keycloak", e);
            return false;
        }
    }

    /**
     * Validar token JWT consultando la información del token
     */
    public boolean validateToken(String accessToken) {
        logger.debug("Validando token en Keycloak");

        try {
            String introspectEndpoint = String.format("%s/realms/%s/protocol/openid-connect/token/introspect", keycloakUrl, realm);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("client_id", clientId);
            params.add("client_secret", clientSecret);
            params.add("token", accessToken);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(introspectEndpoint, request, Map.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> tokenInfo = response.getBody();
                Boolean active = (Boolean) tokenInfo.get("active");
                
                if (Boolean.TRUE.equals(active)) {
                    logger.debug("Token válido en Keycloak");
                    return true;
                } else {
                    logger.debug("Token inactivo en Keycloak");
                    return false;
                }
            } else {
                logger.warn("Error validando token en Keycloak, status: {}", response.getStatusCode());
                return false;
            }

        } catch (Exception e) {
            logger.error("Error validando token en Keycloak", e);
            return false;
        }
    }

    /**
     * Refrescar token usando refresh token
     */
    public AuthResponse refreshToken(String refreshToken) {
        logger.info("Refrescando token en Keycloak");

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("grant_type", "refresh_token");
            params.add("client_id", clientId);
            params.add("client_secret", clientSecret);
            params.add("refresh_token", refreshToken);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(tokenEndpoint, request, Map.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> tokenResponse = response.getBody();
                
                String newAccessToken = (String) tokenResponse.get("access_token");
                String newRefreshToken = (String) tokenResponse.get("refresh_token");
                Integer expiresIn = (Integer) tokenResponse.get("expires_in");

                logger.info("Token refrescado exitosamente en Keycloak");

                AuthResponse authResponse = AuthResponse.success("Token refrescado exitosamente");
                authResponse.setAccessToken(newAccessToken);
                authResponse.setRefreshToken(newRefreshToken);
                authResponse.setExpiresIn(expiresIn != null ? expiresIn.longValue() : null);

                return authResponse;

            } else {
                logger.warn("Error refrescando token en Keycloak, status: {}", response.getStatusCode());
                return AuthResponse.error("Error refrescando token");
            }

        } catch (Exception e) {
            logger.error("Error refrescando token en Keycloak", e);
            return AuthResponse.error("Error refrescando token");
        }
    }

    /**
     * Obtener información del usuario desde el token JWT
     */
    public AuthResponse getUserInfoFromToken(String accessToken) {
        logger.info("Obteniendo información del usuario desde token JWT");

        try {
            String userInfoEndpoint = String.format("%s/realms/%s/protocol/openid-connect/userinfo", keycloakUrl, realm);

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);

            HttpEntity<String> request = new HttpEntity<>(headers);

            ResponseEntity<Map> response = restTemplate.exchange(userInfoEndpoint, HttpMethod.GET, request, Map.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> userInfo = response.getBody();
                
                String curp = (String) userInfo.get("preferred_username");
                String nombreCompleto = (String) userInfo.get("name");
                String email = (String) userInfo.get("email");
                String dependencia = (String) userInfo.get("dependencia");
                String puesto = (String) userInfo.get("puesto");

                logger.info("Información de usuario obtenida exitosamente para CURP: {}", curp);

                AuthResponse.UserInfo userInfoDto = new AuthResponse.UserInfo(
                    curp, nombreCompleto, email, dependencia, puesto);

                AuthResponse authResponse = AuthResponse.success("Perfil obtenido exitosamente");
                authResponse.setUserInfo(userInfoDto);

                return authResponse;

            } else {
                logger.warn("Error obteniendo información de usuario desde token, status: {}", response.getStatusCode());
                return AuthResponse.error("Error obteniendo información de usuario");
            }

        } catch (Exception e) {
            logger.error("Error obteniendo información de usuario desde token", e);
            return AuthResponse.error("Error obteniendo información de usuario");
        }
    }
}