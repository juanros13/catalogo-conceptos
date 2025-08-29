# Gateway Service Dockerfile
FROM maven:latest AS builder

WORKDIR /build

# Copiar todos los POMs para el proyecto multi-módulo
COPY pom.xml ./
COPY discovery-service/pom.xml ./discovery-service/
COPY config-server/pom.xml ./config-server/
COPY gateway-service/pom.xml ./gateway-service/
COPY auth-service/pom.xml ./auth-service/
COPY user-management-service/pom.xml ./user-management-service/

# Descargar dependencias
RUN mvn dependency:go-offline -B

# Copiar código fuente
COPY gateway-service/src ./gateway-service/src

# Construir la aplicación
RUN mvn clean package -DskipTests -pl gateway-service

# Imagen final
FROM openjdk:17-jdk-slim

WORKDIR /app

# Copiar JAR construido
COPY --from=builder /build/gateway-service/target/gateway-service-0.0.1-SNAPSHOT.jar app.jar

# Exponer puerto
EXPOSE 8080

# Variables de entorno por defecto
ENV SPRING_PROFILES_ACTIVE=dev
ENV EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE=http://discovery-service:8761/eureka
ENV SPRING_CLOUD_CONFIG_URI=http://config-server:8888

# Comando de inicio
ENTRYPOINT ["java", "-jar", "app.jar"]