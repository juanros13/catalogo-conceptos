package saf.cgmaig.auth;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestConfig.class)
class AuthServiceApplicationTests {

    @Test
    void contextLoads() {
        // Verifica que el contexto de Spring Boot carga correctamente
        // incluyendo la configuración de JPA con PostgreSQL Testcontainers,
        // Security y configuración de testing
    }
}