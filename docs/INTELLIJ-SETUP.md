# IntelliJ IDEA - Configuración para Desarrollo Híbrido

## Configuración de Variables de Entorno

### 1. Cargar archivo .env en IntelliJ

Crea el archivo `.env` en la raíz del proyecto con:

```bash
KEYCLOAK_URL=https://auth.nucleo.rocks/
KEYCLOAK_REALM=nucleo-dash-realm  
KEYCLOAK_CLIENT_ID=nucleo-dash-back-client
KEYCLOAK_CLIENT_SECRET=od5xYtQfHjRM5VUvSvBZkmiHZfhKCRQW

DB_URL=jdbc:postgresql://localhost:5432/acceso_tabasco_dev
DB_USERNAME=auth_dev
DB_PASSWORD=dev_password

EUREKA_SERVER_URL=http://localhost:8761/eureka
CONFIG_SERVER_URL=http://localhost:8888
```

### 2. Plugin EnvFile (Recomendado)

1. Ve a **File → Settings → Plugins**
2. Buscar **EnvFile** e instalar
3. Reiniciar IntelliJ

## Run Configurations

### ConfigServer
```
Main Class: saf.cgmaig.config.ConfigServerApplication
VM Options: -Dspring.profiles.active=native
Environment Variables:
  SPRING_PROFILES_ACTIVE=native
Working Directory: $MODULE_WORKING_DIR$
Use EnvFile: ✓ (seleccionar .env)
```

### DiscoveryService
```
Main Class: saf.cgmaig.discovery.DiscoveryServiceApplication
VM Options: -Dspring.profiles.active=dev
Environment Variables:
  SPRING_CLOUD_CONFIG_URI=http://localhost:8888
Working Directory: $MODULE_WORKING_DIR$
Use EnvFile: ✓ (seleccionar .env)
```

### GatewayService
```
Main Class: saf.cgmaig.gateway.GatewayServiceApplication
VM Options: -Dspring.profiles.active=dev
Environment Variables:
  EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE=http://localhost:8761/eureka
  SPRING_CLOUD_CONFIG_URI=http://localhost:8888
Working Directory: $MODULE_WORKING_DIR$
Use EnvFile: ✓ (seleccionar .env)
```

### AuthService  
```
Main Class: saf.cgmaig.auth.AuthServiceApplication
VM Options: -Dspring.profiles.active=dev
Environment Variables:
  EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE=http://localhost:8761/eureka
  SPRING_CLOUD_CONFIG_URI=http://localhost:8888
Working Directory: $MODULE_WORKING_DIR$
Use EnvFile: ✓ (seleccionar .env)
```

### UserManagementService
```
Main Class: saf.cgmaig.usermanagement.UserManagementServiceApplication
VM Options: -Dspring.profiles.active=dev
Environment Variables:
  EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE=http://localhost:8761/eureka
  SPRING_CLOUD_CONFIG_URI=http://localhost:8888
Working Directory: $MODULE_WORKING_DIR$
Use EnvFile: ✓ (seleccionar .env)
```

## Workflow de Desarrollo

### 1. Iniciar Infraestructura
```powershell
# Desde PowerShell en la raíz del proyecto
.\scripts\docker\start-infrastructure.ps1
```

### 2. Verificar Servicios Base
- **Eureka Dashboard**: http://localhost:8761  
- **Config Server**: http://localhost:8888/actuator/health

### 3. Ejecutar Servicios en IntelliJ

**🚨 PASO A PASO OBLIGATORIO:**

**Paso 1: Iniciar ConfigServer**
1. Seleccionar **ConfigServer** en el dropdown de Run Configurations
2. Hacer clic en **Run** (Shift+F10)
3. **ESPERAR** hasta ver: `Started ConfigServerApplication in X.XXX seconds`
4. **VERIFICAR**: Abrir http://localhost:8888/actuator/health - debe devolver `{"status":"UP"}`

**Paso 2: Iniciar DiscoveryService** 
1. Seleccionar **DiscoveryService** en el dropdown
2. Hacer clic en **Run** (Shift+F10)
3. **ESPERAR** hasta ver: `Started DiscoveryServiceApplication in X.XXX seconds`
4. **VERIFICAR**: Abrir http://localhost:8761 - debe mostrar Eureka Dashboard

**Paso 3: Iniciar otros servicios**
1. Iniciar **GatewayService, AuthService, UserManagementService** en cualquier orden
2. Los servicios se registrarán automáticamente en Eureka
3. **VERIFICAR**: En http://localhost:8761 deben aparecer registrados

### 4. Debugging
- Usar **Debug** en lugar de **Run**
- Colocar breakpoints normalmente
- Hot reload con **Build → Build Project** (Ctrl+F9)

## Puertos de Servicios

| Servicio | Puerto | URL | Configuración IntelliJ |
|----------|--------|-----|----------------------|
| Config Server | 8888 | http://localhost:8888 | ConfigServer |
| Discovery Service | 8761 | http://localhost:8761 | DiscoveryService |
| Gateway Service | 8080 | http://localhost:8080 | GatewayService |
| Auth Service | 8081 | http://localhost:8081 | AuthService |
| User Management Service | 8082 | http://localhost:8082 | UserManagementService |

### Configuraciones IntelliJ Disponibles:
- **ConfigServer** - Servidor de configuración centralizada
- **DiscoveryService** - Registro y descubrimiento de servicios (Eureka)
- **GatewayService** - API Gateway principal
- **AuthService** - Servicio de autenticación con Keycloak
- **UserManagementService** - Gestión de usuarios y roles

## Troubleshooting

### Problema: Servicio no se registra en Eureka
**Solución**: Verificar que `EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE` esté configurado

### Problema: No carga configuración del config-server  
**Solución**: Verificar que `SPRING_CLOUD_CONFIG_URI` apunte a http://localhost:8888

### Problema: Error de base de datos
**Solución**: Verificar variables `DB_*` en .env y que PostgreSQL esté ejecutándose

### Problema: Error de autenticación Keycloak
**Solución**: Verificar variables `KEYCLOAK_*` en .env

### Problema: ConfigServer falla con "Invalid config server configuration"
**Solución**: Verificar que `SPRING_PROFILES_ACTIVE=native` esté configurado. El perfil `native` usa archivos locales en lugar de Git.

### Problema: DiscoveryService falla con "Connection refused" o "RetryableEurekaHttpClient"
**Causas posibles:**
- ConfigServer no está ejecutándose
- DiscoveryService se inició antes que ConfigServer
- Puerto 8888 no está disponible

**Solución:**
1. Detener DiscoveryService si está ejecutándose
2. Verificar que ConfigServer esté ejecutándose: http://localhost:8888/actuator/health
3. Esperar 10-15 segundos después de iniciar ConfigServer
4. Luego iniciar DiscoveryService

### Problema: DiscoveryService muestra "was unable to send heartbeat"
**Causa**: DiscoveryService está actuando como cliente y servidor al mismo tiempo.
**Solución**: Esto es normal y se puede ignorar, o reiniciar DiscoveryService para aplicar configuración actualizada.

### Problema: Servicios no se registran en Eureka
**Solución**: Verificar orden de inicio: ConfigServer → DiscoveryService → otros servicios