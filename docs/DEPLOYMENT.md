# Deployment Guide - Manejo Seguro de Secrets

## Variables de Entorno Requeridas

### Para Desarrollo Local
1. Copia `.env.example` como `.env`:
```bash
cp .env.example .env
```

2. Configura tus valores reales en `.env` (NO commitear)

### Para Producción

#### Opción 1: Docker Secrets (Recomendado)
```bash
# Crear secrets en Docker Swarm
echo "od5xYtQfHjRM5VUvSvBZkmiHZfhKCRQW" | docker secret create keycloak_client_secret -
echo "nucleo-dash-back-client" | docker secret create keycloak_client_id -
echo "jdbc:postgresql://prod-db:5432/acceso_tabasco" | docker secret create db_url -

# Usar en docker-compose-prod.yml:
secrets:
  - keycloak_client_secret
  - keycloak_client_id
  - db_url
```

#### Opción 2: Variables de Entorno del Sistema
```bash
export KEYCLOAK_CLIENT_SECRET="od5xYtQfHjRM5VUvSvBZkmiHZfhKCRQW"
export KEYCLOAK_CLIENT_ID="nucleo-dash-back-client"
export KEYCLOAK_URL="https://auth.nucleo.rocks/"
export KEYCLOAK_REALM="nucleo-dash-realm"
```

#### Opción 3: Kubernetes Secrets
```yaml
apiVersion: v1
kind: Secret
metadata:
  name: keycloak-secrets
type: Opaque
stringData:
  client-secret: od5xYtQfHjRM5VUvSvBZkmiHZfhKCRQW
  client-id: nucleo-dash-back-client
```

#### Opción 4: Azure Key Vault / AWS Secrets Manager
```yaml
# application-prod.yml
spring:
  cloud:
    azure:
      keyvault:
        secret:
          enabled: true
          endpoint: https://your-keyvault.vault.azure.net/
```

## Variables de Entorno por Servicio

### Auth Service
```bash
KEYCLOAK_URL=https://auth.nucleo.rocks/
KEYCLOAK_REALM=nucleo-dash-realm
KEYCLOAK_CLIENT_ID=nucleo-dash-back-client
KEYCLOAK_CLIENT_SECRET=od5xYtQfHjRM5VUvSvBZkmiHZfhKCRQW
KEYCLOAK_ISSUER_URI=https://auth.nucleo.rocks/realms/nucleo-dash-realm
KEYCLOAK_JWK_SET_URI=https://auth.nucleo.rocks/realms/nucleo-dash-realm/protocol/openid-connect/certs
DB_URL=jdbc:postgresql://localhost:5432/acceso_tabasco
DB_USER=auth_user
DB_PASSWORD=secure_password
EUREKA_SERVER_URL=http://localhost:8761/eureka
```

### User Management Service
```bash
KEYCLOAK_URL=https://auth.nucleo.rocks/
KEYCLOAK_REALM=nucleo-dash-realm
KEYCLOAK_USER_MGMT_CLIENT_SECRET=od5xYtQfHjRM5VUvSvBZkmiHZfhKCRQW
KEYCLOAK_ISSUER_URI=https://auth.nucleo.rocks/realms/nucleo-dash-realm
KEYCLOAK_JWK_SET_URI=https://auth.nucleo.rocks/realms/nucleo-dash-realm/protocol/openid-connect/certs
DB_URL=jdbc:postgresql://localhost:5432/acceso_tabasco
DB_USER=user_mgmt_user
DB_PASSWORD=secure_password
EUREKA_SERVER_URL=http://localhost:8761/eureka
```

### Gateway Service
```bash
KEYCLOAK_URL=https://auth.nucleo.rocks/
KEYCLOAK_CLIENT_SECRET=od5xYtQfHjRM5VUvSvBZkmiHZfhKCRQW
KEYCLOAK_ISSUER_URI=https://auth.nucleo.rocks/realms/nucleo-dash-realm
KEYCLOAK_JWK_SET_URI=https://auth.nucleo.rocks/realms/nucleo-dash-realm/protocol/openid-connect/certs
EUREKA_SERVER_URL=http://localhost:8761/eureka
```

## Seguridad

### ⚠️ IMPORTANTE - NO COMMITEAR:
- `.env`
- `docker-compose.secrets.yml`
- `application-local.yml`
- Cualquier archivo con secrets reales

### ✅ SÍ COMMITEAR:
- `.env.example` (con valores de ejemplo)
- Configuraciones con `${VARIABLE}` placeholders
- Esta documentación

## Inicio de Servicios con Docker

### Windows (PowerShell)
```powershell
# Servicios básicos (Discovery + Config + Gateway)
powershell -ExecutionPolicy Bypass -File scripts/docker/start-services.ps1 basic

# Todos los servicios (incluye Auth y User Management)
powershell -ExecutionPolicy Bypass -File scripts/docker/start-services.ps1 full
```

### Linux/Mac/Git Bash
```bash
# Hacer ejecutable (solo la primera vez)
chmod +x scripts/docker/start-services.sh

# Servicios básicos
scripts/docker/start-services.sh basic

# Todos los servicios
scripts/docker/start-services.sh full
```

### Manual con Docker Compose
```bash
# Servicios básicos
docker-compose up --build -d discovery-service config-server gateway-service

# Todos los servicios
docker-compose --profile full up --build -d

# Ver logs
docker-compose logs -f

# Detener servicios
docker-compose down
```

## Comandos de Deployment

### Desarrollo
```bash
# Con archivo .env
docker-compose up -d

# Con variables inline
KEYCLOAK_CLIENT_SECRET=xxx docker-compose up -d
```

### Producción
```bash
# Con Docker secrets
docker stack deploy -c docker-compose-prod.yml acceso-tabasco

# Con variables de entorno
docker-compose -f docker-compose-prod.yml up -d
```