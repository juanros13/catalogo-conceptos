package saf.cgmaig.validation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Validation Service Application
 * 
 * Microservicio especializado en la validación de reglas de negocio 
 * y verificación de integridad para conceptos técnicos del CUBS.
 * 
 * RESPONSABILIDADES:
 * - Validación de unicidad por área
 * - Verificación de relaciones área-capítulo 
 * - Validación de formatos y especificaciones
 * - Aplicación de reglas de negocio específicas del CUBS
 * - Registro de auditoría de validaciones
 * 
 * INTEGRACIÓN:
 * - Eureka Client para service discovery
 * - Feign Client para comunicación con otros microservicios
 * - JWT authentication para seguridad
 * - Spring Cloud Config para configuración centralizada
 */
@SpringBootApplication
@EnableFeignClients
public class ValidationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ValidationServiceApplication.class, args);
    }
}