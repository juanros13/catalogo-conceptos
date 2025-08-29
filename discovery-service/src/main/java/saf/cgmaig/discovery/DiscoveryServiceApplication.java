package saf.cgmaig.discovery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * Eureka Server para descubrimiento de servicios del Sistema de Acceso Tabasco.
 * 
 * Este servidor permite que los microservicios se registren automáticamente
 * y se descubran entre sí sin necesidad de configurar IPs fijas.
 * 
 * Servicios que se registrarán:
 * - Gateway Service (puerto 8080)
 * - Auth Service (puerto 8081) 
 * - User Service (puerto 8082)
 * - Request Service (puerto 8083)
 * - Document Service (puerto 8084)
 * - Email Service (puerto 8085)
 * - Repository Service (puerto 8086)
 * - Meeting Service (puerto 8087)
 * 
 * Dashboard disponible en: http://localhost:8761
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableEurekaServer
public class DiscoveryServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DiscoveryServiceApplication.class, args);
    }
}