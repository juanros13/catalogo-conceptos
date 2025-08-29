# Config Server - Spring Cloud 2025

## Configuración actualizada para Spring Cloud 2025.0.0

### ⚠️ Cambios importantes

#### 1. Autenticación Git
**ANTES (deprecated):**
```yaml
git:
  username: user
  password: pass
```

**AHORA (Spring Cloud 2025):**
```yaml
git:
  access-token: ${GIT_ACCESS_TOKEN:ghp_xxxxx}
```

#### 2. Variables de entorno requeridas

```bash
# Token de acceso de GitHub (reemplaza username/password)
export GIT_ACCESS_TOKEN=ghp_your_github_personal_access_token

# Alternativa con SSH
export GIT_SSH_KEY=/path/to/ssh/key
export GIT_SSH_PASSPHRASE=your_ssh_passphrase
```

### 🔑 Crear Personal Access Token en GitHub

1. Ve a GitHub Settings → Developer settings → Personal access tokens
2. Generate new token (classic)
3. Selecciona scopes:
   - `repo` (para repositorios privados)
   - `public_repo` (para repositorios públicos)
4. Copia el token `ghp_xxxxxxxxxxxxx`

### 🚀 Ejecutar Config Server

```bash
# Con archivos locales (desarrollo)
./mvnw spring-boot:run -pl config-server

# Con Git + token
export GIT_ACCESS_TOKEN=ghp_your_token
./mvnw spring-boot:run -pl config-server -Dspring.profiles.active=git

# Con Docker
docker run -e GIT_ACCESS_TOKEN=ghp_your_token config-server
```

### 📋 Endpoints disponibles

```bash
# Health check
curl http://localhost:8888/actuator/health

# Configuración de servicio
curl http://config-client:config-secret@localhost:8888/gateway-service/dev

# Refresh configuración
curl -X POST http://config-admin:admin-secret@localhost:8888/actuator/refresh
```

### 🏗️ Estructura de repositorio Git esperada

```
acceso-tabasco-config/
├── gateway-service.yml
├── auth-service.yml
├── user-service.yml
├── request-service.yml
├── application.yml          # Configuración común
└── application-prod.yml     # Configuración de producción
```

### 🔒 Seguridad

- HTTP Basic Auth con usuarios in-memory
- `config-client` / `config-secret` → Para microservicios
- `config-admin` / `admin-secret` → Para administradores
- Endpoints de actuator públicos: `/actuator/health`, `/actuator/info`

### 🐛 Troubleshooting

**Error: "Authentication failed"**
```bash
# Verificar token
curl -H "Authorization: token $GIT_ACCESS_TOKEN" https://api.github.com/user
```

**Error: "Repository not found"**
- Verificar que el repositorio existe
- Verificar permisos del token
- Para repos privados, usar scope `repo`