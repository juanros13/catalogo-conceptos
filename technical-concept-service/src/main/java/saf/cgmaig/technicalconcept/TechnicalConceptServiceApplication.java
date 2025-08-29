package saf.cgmaig.technicalconcept;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class TechnicalConceptServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(TechnicalConceptServiceApplication.class, args);
	}
}