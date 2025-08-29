package saf.cgmaig.auth;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * Configuración de Testcontainers para tests de integración con PostgreSQL.
 * 
 * Esta configuración levanta automáticamente un contenedor de PostgreSQL
 * para ejecutar los tests de integración con una base de datos real.
 */
@TestConfiguration(proxyBeanMethods = false)
public class TestConfig {

    @Bean
    @ServiceConnection
    public PostgreSQLContainer<?> postgreSQLContainer() {
        return new PostgreSQLContainer<>(DockerImageName.parse("postgres:15-alpine"))
                .withDatabaseName("auth_service_test")
                .withUsername("test_user")
                .withPassword("test_password")
                .withReuse(true); // Reutilizar contenedor entre tests para mayor velocidad
    }
}