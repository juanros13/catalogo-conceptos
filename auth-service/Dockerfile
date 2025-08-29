# Auth Service Dockerfile
FROM openjdk:17-jdk-slim

WORKDIR /app

# Copiar wrapper de Maven
COPY mvnw* ./
COPY .mvn .mvn

# Copiar POM y descargar dependencias
COPY pom.xml ./
RUN ./mvnw dependency:go-offline -B

# Copiar código fuente
COPY src ./src

# Construir la aplicación
RUN ./mvnw clean package -DskipTests

# Exponer puerto
EXPOSE 8081

# Variables de entorno por defecto
ENV SPRING_PROFILES_ACTIVE=dev
ENV EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE=http://discovery-service:8761/eureka
ENV SPRING_CLOUD_CONFIG_URI=http://config-server:8888

# Comando de inicio
ENTRYPOINT ["java", "-jar", "target/auth-service-0.0.1-SNAPSHOT.jar"]