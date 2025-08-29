package saf.cgmaig.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Servicio de Autenticación para el Sistema de Acceso Unificado del Gobierno de Tabasco.
 * 
 * Funcionalidades principales:
 * - Validación de usuarios en nómina gubernamental (vista vw_empleados_nomina)
 * - Integración con Keycloak para manejo de tokens JWT
 * - Validación de CURP y status activo en nómina
 * - Endpoints para login, logout y validación de tokens
 * - Registro automático en Eureka Discovery Service
 * 
 * Puerto: 8081
 * Base de datos: PostgreSQL
 * 
 * @author CGMAIG - Gobierno de Tabasco
 */
@SpringBootApplication
@EnableDiscoveryClient
public class AuthServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }
}