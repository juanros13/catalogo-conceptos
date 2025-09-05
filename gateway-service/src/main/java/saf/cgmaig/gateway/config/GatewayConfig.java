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
                        .uri("lb://auth-service"))
                
                // Technical Concept Service Route
                .route("technical-concept-service-route", r -> r
                        .path("/concepts/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://technical-concept-service"))
                
                // Validation Service Route
                .route("validation-service-route", r -> r
                        .path("/validation/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://validation-service"))
                
                // Budget Classification Service Route
                .route("budget-classification-service-route", r -> r
                        .path("/budget-classifications/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://budget-classification-service"))
                
                // Generic API Route (fallback to auth service)
                .route("api-service-route", r -> r
                        .path("/api/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://auth-service"))
                
                .build();
    }
}