package saf.cgmaig.conceptmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Concept Management Service - Aplicación principal
 * 
 * Servicio para gestión de conceptos específicos por áreas del CUBS.
 * Los usuarios de cada área crean conceptos basados en templates de capítulo
 * seleccionando conceptos base del technical-concept-service.
 * 
 * Flujo:
 * 1. Usuarios consultan conceptos base por área
 * 2. Seleccionan template por capítulo (2000_MATERIALES, 2000_SERVICIOS, 5000)
 * 3. Completan campos específicos del template
 * 4. Envían a validación (validation-service)
 * 5. Validadores técnicos aprueban/rechazan
 */
@SpringBootApplication
@EnableFeignClients
public class ConceptManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConceptManagementApplication.class, args);
    }
}