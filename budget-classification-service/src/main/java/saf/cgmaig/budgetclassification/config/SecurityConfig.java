package saf.cgmaig.budgetclassification.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

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
                .requestMatchers("/api/budget-classifications/health").permitAll()
                .requestMatchers("/api/budget-classifications/info").permitAll()
                
                // Endpoints de consulta pública (solo lectura)
                .requestMatchers("GET", "/api/budget-classifications/**").permitAll()
                
                // Endpoints de administración - requieren roles específicos
                .requestMatchers("POST", "/api/budget-classifications").hasAnyRole("ADMIN_SISTEMA", "CONFIGURADOR_PRESUPUESTO")
                .requestMatchers("PUT", "/api/budget-classifications/**").hasAnyRole("ADMIN_SISTEMA", "CONFIGURADOR_PRESUPUESTO")
                .requestMatchers("PATCH", "/api/budget-classifications/**").hasAnyRole("ADMIN_SISTEMA", "CONFIGURADOR_PRESUPUESTO")
                .requestMatchers("DELETE", "/api/budget-classifications/**").hasAnyRole("ADMIN_SISTEMA", "CONFIGURADOR_PRESUPUESTO")
                
                // Estadísticas requieren autenticación
                .requestMatchers("/api/budget-classifications/statistics").authenticated()
                
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