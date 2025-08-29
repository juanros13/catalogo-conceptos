# Config Server Dockerfile
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

# Copiar código fuente y configuraciones
COPY config-server/src ./config-server/src

# Construir la aplicación
RUN mvn clean package -DskipTests -pl config-server

# Imagen final
FROM openjdk:17-jdk-slim

WORKDIR /app

# Copiar JAR construido
COPY --from=builder /build/config-server/target/config-server-0.0.1-SNAPSHOT.jar app.jar

# Exponer puerto
EXPOSE 8888

# Variables de entorno por defecto
ENV SPRING_PROFILES_ACTIVE=dev,native
ENV SPRING_CLOUD_CONFIG_SERVER_NATIVE_SEARCHLOCATIONS=classpath:/config-repo

# Comando de inicio
ENTRYPOINT ["java", "-jar", "app.jar"]