package saf.cgmaig.validation.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuración de seguridad para Validation Service
 * 
 * ARQUITECTURA DE SEGURIDAD:
 * - JWT Authentication con Keycloak
 * - Control de acceso basado en roles por área
 * - Validaciones específicas según permisos del usuario
 * - Auditoría de todas las validaciones realizadas
 * 
 * ROLES SOPORTADOS:
 * - VALIDADOR_TECNICO_CGMAIG: Validador técnico de CGMAIG
 * - VALIDADOR_TECNICO_CGRM: Validador técnico de CGRM
 * - VALIDADOR_TECNICO_CGSG: Validador técnico de CGSG
 * - VALIDADOR_TECNICO_PATRIMONIO: Validador técnico de Patrimonio
 * - ADMIN_SISTEMA: Administrador del sistema
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authz -> authz
                // Endpoints públicos
                .requestMatchers("/actuator/health").permitAll()
                .requestMatchers("/actuator/info").permitAll()
                
                // Endpoints de validación - Acceso para validadores técnicos
                .requestMatchers("POST", "/api/validation/concept").hasAnyRole("VALIDADOR_TECNICO_CGRM", "VALIDADOR_TECNICO_CGSG", "VALIDADOR_TECNICO_CGMAIG", "VALIDADOR_TECNICO_PATRIMONIO")
                .requestMatchers("POST", "/api/validation/uniqueness").hasAnyRole("VALIDADOR_TECNICO_CGRM", "VALIDADOR_TECNICO_CGSG", "VALIDADOR_TECNICO_CGMAIG", "VALIDADOR_TECNICO_PATRIMONIO")
                .requestMatchers("POST", "/api/validation/area-chapter").hasAnyRole("VALIDADOR_TECNICO_CGRM", "VALIDADOR_TECNICO_CGSG", "VALIDADOR_TECNICO_CGMAIG", "VALIDADOR_TECNICO_PATRIMONIO")
                .requestMatchers("POST", "/api/validation/format").hasAnyRole("VALIDADOR_TECNICO_CGRM", "VALIDADOR_TECNICO_CGSG", "VALIDADOR_TECNICO_CGMAIG", "VALIDADOR_TECNICO_PATRIMONIO")
                
                // Endpoints de consulta de reglas - Lectura para validadores
                .requestMatchers("GET", "/api/validation/rules").hasAnyRole("VALIDADOR_TECNICO_CGRM", "VALIDADOR_TECNICO_CGSG", "VALIDADOR_TECNICO_CGMAIG", "VALIDADOR_TECNICO_PATRIMONIO")
                .requestMatchers("GET", "/api/validation/rules/**").hasAnyRole("VALIDADOR_TECNICO_CGRM", "VALIDADOR_TECNICO_CGSG", "VALIDADOR_TECNICO_CGMAIG", "VALIDADOR_TECNICO_PATRIMONIO")
                
                // Endpoints de administración - Solo admin
                .requestMatchers("/api/validation/admin/**").hasRole("ADMIN_SISTEMA")
                
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
        
        // Extraer el CURP como nombre de usuario
        converter.setPrincipalClaimName("preferred_username");
        return converter;
    }
}