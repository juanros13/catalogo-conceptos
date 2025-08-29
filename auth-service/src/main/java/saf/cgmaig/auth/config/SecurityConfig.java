package saf.cgmaig.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuración de seguridad para el Auth Service.
 * 
 * Configura la validación de tokens JWT provenientes del Gateway:
 * - Valida tokens JWT firmados por Keycloak
 * - Extrae roles y authorities del token
 * - Permite endpoints públicos (/auth/login, /actuator/health)
 * - Requiere autenticación para endpoints privados
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authz -> authz
                // Endpoints públicos
                .requestMatchers("/auth/login").permitAll()
                .requestMatchers("/auth/validate-curp").permitAll()
                .requestMatchers("/actuator/health").permitAll()
                .requestMatchers("/actuator/info").permitAll()
                // Endpoints que requieren autenticación
                .requestMatchers("/auth/profile").authenticated()
                .requestMatchers("/auth/logout").authenticated()
                .requestMatchers("/auth/validate-token").authenticated()
                // Todos los demás endpoints requieren autenticación
                .anyRequest().authenticated()
            )
            // Configuración OAuth2 Resource Server para validar JWT
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt
                    .jwtAuthenticationConverter(jwtAuthenticationConverter())
                )
            );

        return http.build();
    }

    /**
     * Convierte el JWT en authorities de Spring Security.
     * Extrae los roles de Keycloak del claim 'realm_access.roles'
     */
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            // Extraer roles del JWT de Keycloak
            var realmAccess = jwt.getClaimAsMap("realm_access");
            if (realmAccess != null && realmAccess.containsKey("roles")) {
                @SuppressWarnings("unchecked")
                var roles = (java.util.List<String>) realmAccess.get("roles");
                return roles.stream()
                    .map(role -> new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                    .collect(java.util.stream.Collectors.toList());
            }
            return java.util.Collections.emptyList();
        });
        return converter;
    }
}