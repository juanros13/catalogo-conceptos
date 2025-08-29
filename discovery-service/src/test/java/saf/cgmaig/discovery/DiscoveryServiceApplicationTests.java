package saf.cgmaig.discovery;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class DiscoveryServiceApplicationTests {

    @Test
    void contextLoads() {
        // Verifica que el contexto de Spring Boot carga correctamente
        // y que Eureka Server se inicia sin errores
    }

    @Test
    void eurekaServerIsRunning() {
        // Este test verifica implícitamente que @EnableEurekaServer
        // está configurado correctamente al cargar el contexto
    }
}