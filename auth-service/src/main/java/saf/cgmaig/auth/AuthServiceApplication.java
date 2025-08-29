package saf.cgmaig.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Servicio de Autenticación para el Sistema de Acceso Unificado del Gobierno de Tabasco.
 * 
 * Funcionalidades principales:
 * - Integración con Keycloak para manejo de tokens JWT
 * - Autenticación basada en CURP
 * - Endpoints para login, logout y validación de tokens
 * - Registro automático en Eureka Discovery Service
 * 
 * Puerto: 8081
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