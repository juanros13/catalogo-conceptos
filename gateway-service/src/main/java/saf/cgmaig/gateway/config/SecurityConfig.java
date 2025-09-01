package saf.cgmaig.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoders;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * Configuración de seguridad para Gateway Service.
 * 
 * ARQUITECTURA DE SEGURIDAD EN DOBLE CAPA:
 * - Gateway: Validación JWT inicial + Routing (Primera línea de defensa)
 * - Servicios individuales: Validación JWT secundaria + Autorización específica
 * 
 * BENEFICIOS DE DOBLE VALIDACIÓN:
 * - Máxima seguridad: Dos capas de validación JWT independientes
 * - Performance: Gateway filtra requests inválidos antes de llegar a servicios
 * - Observabilidad: Métricas de seguridad centralizadas en Gateway
 * - Defensa en profundidad: Si una capa falla, la otra protege
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    // URL del issuer de Keycloak (nucleo.rocks)
    private final String issuerUri = "https://auth.nucleo.rocks/realms/nucleo-dash-realm";

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        // Endpoints públicos - Sin autenticación
                        .pathMatchers("/actuator/**").permitAll()
                        .pathMatchers("/public/**").permitAll()
                        .pathMatchers("/auth/login").permitAll()
                        .pathMatchers("/auth/health").permitAll()
                        .pathMatchers("/auth/info").permitAll()
                        
                        // Endpoints protegidos - Requieren autenticación JWT EN EL GATEWAY
                        .pathMatchers("/auth/profile").authenticated()
                        .pathMatchers("/auth/logout").authenticated() 
                        .pathMatchers("/auth/validate-token").authenticated()
                        .pathMatchers("/concepts/**").authenticated()
                        .pathMatchers("/validation/**").authenticated()
                        
                        // Cualquier otro endpoint requiere autenticación
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .jwtDecoder(jwtDecoder())
                        )
                )
                .build();
    }

    @Bean
    public ReactiveJwtDecoder jwtDecoder() {
        return ReactiveJwtDecoders.fromIssuerLocation(issuerUri);
    }
}
