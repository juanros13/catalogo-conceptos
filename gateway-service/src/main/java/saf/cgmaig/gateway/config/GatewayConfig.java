package saf.cgmaig.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // Auth Service Route
                .route("auth-service-route", r -> r
                        .path("/auth/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("http://localhost:8081"))
                
                // Generic API Route (fallback to auth service)
                .route("api-service-route", r -> r
                        .path("/api/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("http://localhost:8081"))
                
                .build();
    }
}