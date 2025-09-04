# Technical Concept Service

Microservicio para la gestión del catálogo técnico de conceptos generales CUBS (Catálogo Único de Bienes y Servicios) del Gobierno de Tabasco.

## 📋 Descripción General

El **Technical Concept Service** es responsable de la gestión de conceptos técnicos generales que sirven como base para que las diferentes áreas gubernamentales creen sus conceptos específicos. Implementa un modelo de gobierno donde cada área facultada puede gestionar únicamente los conceptos de su capítulo asignado.

## 🏗️ Arquitectura

### Tecnologías
- **Java**: 17
- **Spring Boot**: 3.5.5  
- **Spring Cloud**: 2025.0.0
- **Base de Datos**: PostgreSQL
- **Autenticación**: OAuth2 JWT (Keycloak)
- **Documentación**: MapStruct 1.5.5
- **Testing**: JUnit 5 + Testcontainers

### Puerto de Servicio
- **Desarrollo**: 8083
- **Eureka Registration**: technical-concept-service:8083

## 🎯 Modelo de Dominio

### Áreas Facultadas y Capítulos

| Área | Descripción | Capítulo | Competencia |
|------|-------------|----------|-------------|
| **CGRM** | Coordinación General de Recursos Materiales | 2000 | Materiales, suministros, equipo |
| **CGSG** | Coordinación General de Servicios Generales | 3000 | Servicios generales |
| **CGMAIG** | Coordinación General de Modernización Administrativa | 5000 | Tecnología, innovación |
| **PATRIMONIO** | Subdirección de Patrimonio | 5000 | Gestión patrimonial |

### Estados de Conceptos

| Estado | Descripción | Disponible para Captura |
|--------|-------------|------------------------|
| **ACTIVO** | Activo - Disponible para captura | ✅ Sí |
| **INACTIVO** | Inactivo - No disponible | ❌ No |
| **PENDIENTE** | Pendiente de aprobación | ❌ No |
| **REVISION** | En revisión | ❌ No |

## 📊 Estructura de Base de Datos

### Tabla Principal: `technical_concepts`

```sql
CREATE TABLE technical_concepts (
    id_concepto_general UUID PRIMARY KEY,
    nombre VARCHAR(200) NOT NULL,
    descripcion_detallada TEXT,
    capitulo INTEGER NOT NULL,
    area_facultada VARCHAR(20) NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
    version INTEGER NOT NULL DEFAULT 1,
    activo BOOLEAN NOT NULL DEFAULT true,
    fecha_creacion TIMESTAMP,
    fecha_actualizacion TIMESTAMP,
    creado_por VARCHAR(100),
    actualizado_por VARCHAR(100),
    motivo_cambio VARCHAR(500),
    UNIQUE(nombre, area_facultada)
);
```

### Tabla de Partidas: `concept_partidas`

```sql
CREATE TABLE concept_partidas (
    concept_id UUID REFERENCES technical_concepts(id_concepto_general),
    partida VARCHAR(255)
);
```

## 🔐 Seguridad y Roles

### Roles del Sistema

| Rol | Permisos | Descripción |
|-----|----------|-------------|
| **CAPTURISTA** | Lectura conceptos activos | Solo consulta conceptos disponibles |
| **VALIDADOR_TECNICO_CGRM** | CRUD capítulo 2000 | Gestión completa materiales |
| **VALIDADOR_TECNICO_CGSG** | CRUD capítulo 3000 | Gestión completa servicios |
| **VALIDADOR_TECNICO_CGMAIG** | CRUD capítulo 5000 | Gestión completa tecnología |
| **VALIDADOR_TECNICO_PATRIMONIO** | CRUD capítulo 5000 | Gestión completa patrimonio |
| **ADMIN_SISTEMA** | Administración completa | Acceso total al sistema |

### Autenticación JWT

El servicio valida tokens JWT de Keycloak extrayendo:
- **Subject**: ID del usuario
- **preferred_username**: CURP del usuario  
- **realm_access.roles**: Roles asignados
- **email**: Email del usuario

## 🛠️ API Endpoints

### Base URL: `http://localhost:8083`

#### 📖 Consulta (Capturistas y Validadores)

```http
# Obtener conceptos activos para captura
GET /api/general-concepts

# Conceptos por capítulo específico
GET /api/general-concepts?capitulo=2000

# Obtener concepto por ID
GET /api/general-concepts/{id}

# Conceptos de mi área (validadores)
GET /api/general-concepts/my-area?page=0&size=20
```

#### ✏️ Gestión (Solo Validadores Técnicos)

```http
# Crear concepto técnico
POST /api/general-concepts
Content-Type: application/json
{
  "nombre": "Equipo de Cómputo Estándar",
  "descripcionDetallada": "Equipo base para oficinas",
  "capitulo": 2000,
  "partidasPermitidas": ["5151", "5152"],
  "areaFacultada": "CGMAIG",
  "motivoCreacion": "Estandarización equipos"
}

# Actualizar concepto
PUT /api/general-concepts/{id}
Content-Type: application/json
{
  "nombre": "Equipo de Cómputo Estándar v2",
  "descripcionDetallada": "Versión actualizada",
  "partidasPermitidas": ["5151"],
  "motivoCambio": "Actualización especificaciones"
}

# Inactivar concepto
DELETE /api/general-concepts/{id}?motivo=Obsoleto

# Reactivar concepto  
PATCH /api/general-concepts/{id}/reactivate?motivo=Requerido nuevamente
```

#### 🔍 Búsqueda Avanzada

```http
GET /api/general-concepts/search?capitulo=2000&areaFacultada=CGMAIG&estado=ACTIVO&searchTerm=equipo&page=0&size=10
```

### 📊 Health Check

```http
GET /actuator/health
GET /actuator/info
```

## 🎛️ Configuración

### Variables de Entorno

| Variable | Valor Por Defecto | Descripción |
|----------|-------------------|-------------|
| `CONFIG_SERVER_URL` | http://localhost:8888 | URL del Config Server |
| `SPRING_PROFILES_ACTIVE` | dev | Profile activo |
| `DB_URL` | jdbc:postgresql://localhost:5432/acceso_tabasco_dev | URL PostgreSQL |
| `DB_USER` | postgres | Usuario BD |
| `DB_PASSWORD` | activo | Password BD |
| `KEYCLOAK_ISSUER_URI` | https://auth.nucleo.rocks/realms/nucleo-dash-realm | Keycloak Realm |

### Configuración de Desarrollo (config-server)

```yaml
server:
  port: 8083

spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/acceso_tabasco_dev
    username: postgres
    password: activo
  
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
  
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: https://auth.nucleo.rocks/realms/nucleo-dash-realm

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka
```

## 🧩 Componentes Internos

### 📦 Entidades

#### `TechnicalConcept.java`
```java
@Entity
@Table(name = "technical_concepts")
public class TechnicalConcept {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @NotBlank
    @Size(max = 200)
    private String nombre;
    
    @Enumerated(EnumType.STRING)
    private AreaFacultada areaFacultada;
    
    @Enumerated(EnumType.STRING)  
    private ConceptStatus estado;
    
    // Métodos de negocio
    public void inactivar(String motivo, String usuario) { ... }
    public void reactivar(String motivo, String usuario) { ... }
    public boolean isActivo() { ... }
    public boolean canBeEditedBy(AreaFacultada area) { ... }
}
```

### 🔄 DTOs

#### Request/Response Objects
- **`TechnicalConceptCreateRequest`**: Creación de conceptos
- **`TechnicalConceptUpdateRequest`**: Actualización de conceptos  
- **`TechnicalConceptResponse`**: Respuesta con datos completos

### 🏪 Repository

#### `TechnicalConceptRepository.java`
```java
@Repository
public interface TechnicalConceptRepository extends JpaRepository<TechnicalConcept, UUID> {
    
    // Para capturistas - conceptos activos
    List<TechnicalConcept> findByEstadoAndActivoTrueOrderByNombreAsc(ConceptStatus estado);
    
    // Por capítulo específico
    List<TechnicalConcept> findByCapituloAndEstadoAndActivoTrueOrderByNombreAsc(Integer capitulo, ConceptStatus estado);
    
    // Gestión por área
    Page<TechnicalConcept> findByAreaFacultadaOrderByFechaCreacionDesc(AreaFacultada area, Pageable pageable);
    
    // Búsqueda avanzada con filtros
    @Query("SELECT tc FROM TechnicalConcept tc WHERE ...")
    Page<TechnicalConcept> findConceptsWithFilters(...);
    
    // Validaciones de negocio
    Optional<TechnicalConcept> findByNombreAndAreaFacultada(String nombre, AreaFacultada area);
    boolean existsByNombreAndAreaFacultadaAndIdNot(String nombre, AreaFacultada area, UUID id);
}
```

### 🎯 Service Layer

#### `TechnicalConceptService.java`
```java
@Service
@Transactional("transactionManager")
public class TechnicalConceptService {
    
    // Operaciones CRUD con validaciones de negocio
    public TechnicalConceptResponse create(TechnicalConceptCreateRequest request, String userCurp);
    public TechnicalConceptResponse update(UUID id, TechnicalConceptUpdateRequest request, AreaFacultada userArea, String userCurp);
    public TechnicalConceptResponse inactivate(UUID id, String motivo, AreaFacultada userArea, String userCurp);
    public TechnicalConceptResponse reactivate(UUID id, String motivo, AreaFacultada userArea, String userCurp);
    
    // Consultas optimizadas por rol
    public List<TechnicalConceptResponse> getActiveConceptsForCapture();
    public List<TechnicalConceptResponse> getActiveConceptsByCapitulo(Integer capitulo);
    public Page<TechnicalConceptResponse> getConceptsByArea(AreaFacultada area, Pageable pageable);
    public Page<TechnicalConceptResponse> searchConcepts(...);
    
    // Validaciones de negocio privadas
    private void validateAreaCapitulo(AreaFacultada area, Integer capitulo);
    private TechnicalConcept findConceptById(UUID id);
}
```

### 🎮 Controller Layer

#### `TechnicalConceptController.java`
```java
@RestController
@RequestMapping("/api/general-concepts")
@CrossOrigin(origins = "*", maxAge = 3600)
public class TechnicalConceptController {
    
    @GetMapping
    @PreAuthorize("hasAnyRole('CAPTURISTA', 'VALIDADOR_TECNICO_...')")
    public ResponseEntity<List<TechnicalConceptResponse>> getActiveConcepts(...);
    
    @PostMapping  
    @PreAuthorize("hasAnyRole('VALIDADOR_TECNICO_...')")
    public ResponseEntity<TechnicalConceptResponse> createConcept(...);
    
    // Extracción de área del JWT
    private AreaFacultada extractAreaFromJwt(Jwt jwt);
    private boolean hasAdminRole(Jwt jwt);
}
```

## 🔒 Validaciones de Negocio

### 1. **Coherencia Área-Capítulo**
- CGRM solo puede gestionar capítulo 2000
- CGSG solo puede gestionar capítulo 3000  
- CGMAIG/PATRIMONIO solo capítulo 5000

### 2. **Unicidad de Nombres**
- Un nombre de concepto es único por área facultada
- Permite mismo nombre en áreas diferentes

### 3. **Control de Permisos**
- Solo validadores pueden crear/editar conceptos
- Cada área solo puede gestionar sus propios conceptos
- Capturistas solo pueden consultar conceptos activos

### 4. **Auditoría Completa**
- Registro de quién creó/modificó cada concepto
- Versionado automático en cada cambio
- Motivo obligatorio para cambios de estado

## 🚀 Comandos de Desarrollo

### Iniciar Servicio
```bash
# Desde directorio raíz del proyecto
cd technical-concept-service
../mvnw spring-boot:run

# Con profile específico
../mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

### Compilar
```bash
# Compilar sin tests
../mvnw clean compile

# Empaquetar
../mvnw clean package

# Con tests
../mvnw clean package -DskipTests=false
```

### Testing
```bash
# Tests unitarios
../mvnw test

# Tests de integración con Testcontainers  
../mvnw verify
```

## 📊 Casos de Uso Principales

### 1. **Capturista Consulta Conceptos**
```
GET /api/general-concepts?capitulo=2000
→ Lista conceptos activos de materiales
→ Solo conceptos con estado=ACTIVO y activo=true
```

### 2. **Validador CGMAIG Crea Concepto Tecnológico**
```
POST /api/general-concepts
{
  "nombre": "Laptop Administrativa",
  "capitulo": 5000,
  "areaFacultada": "CGMAIG"
}
→ Valida que CGMAIG puede gestionar capítulo 5000
→ Valida unicidad del nombre en área CGMAIG
→ Crea concepto con auditoría completa
```

### 3. **Búsqueda Avanzada por Administrador**
```
GET /api/general-concepts/search?areaFacultada=CGRM&estado=ACTIVO&searchTerm=escritorio
→ Busca conceptos de CGRM que contengan "escritorio"
→ Resultados paginados con metadatos
```

## 🔄 Integración con Otros Servicios

### Con **Concept Management Service**
- Los conceptos técnicos sirven como base para conceptos específicos de área
- API de consulta para obtener conceptos disponibles por capítulo

### Con **Validation Service**  
- Conceptos técnicos definen templates de validación
- Estructura de especificaciones técnicas requeridas

### Con **Gateway Service**
- Rutas: `/api/general-concepts/**` → `technical-concept-service:8083`
- Balanceo de carga automático vía Eureka

### Con **Auth Service**
- Validación JWT para todos los endpoints protegidos
- Extracción de roles para control granular de permisos

## 📋 Estado Actual

### ✅ **Implementado y Funcionando**
- ✅ Modelo de dominio completo
- ✅ API REST con seguridad granular
- ✅ Validaciones de negocio robustas  
- ✅ Integración con config-server
- ✅ Registration con Eureka
- ✅ Configuración de base de datos
- ✅ Manejo de excepciones
- ✅ Auditoría completa
- ✅ Testing preparado (Testcontainers)

### 📝 **Próximas Mejoras**
- [ ] Tests unitarios e integración
- [ ] Documentación OpenAPI/Swagger
- [ ] Métricas customizadas con Micrometer
- [ ] Cache con Redis para consultas frecuentes
- [ ] Eventos de dominio con Spring Events

## 🚨 Troubleshooting

### Problemas Comunes

#### Error: "Usuario no tiene rol de área facultada válido"
```
Causa: JWT no contiene roles válidos en realm_access
Solución: Verificar configuración de roles en Keycloak
```

#### Error: "Área CGMAIG no puede gestionar capítulo 2000"
```
Causa: Intento de crear concepto con capítulo incorrecto
Solución: Verificar mapeo área-capítulo en AreaFacultada enum
```

#### Error: "Ya existe un concepto general con el nombre"
```
Causa: Violación de constraint UNIQUE(nombre, area_facultada)
Solución: Cambiar nombre o verificar área correcta
```

### Logs Importantes

```bash
# Ver logs del servicio
docker logs technical-concept-service

# Filtrar por nivel
docker logs technical-concept-service | grep ERROR
docker logs technical-concept-service | grep "saf.cgmaig.technicalconcept"
```

---

## 📖 Versionado

- **Versión Actual**: 1.0.0
- **Última Actualización**: 2025-01-04
- **Compatibilidad**: Spring Boot 3.5.5, Java 17+

---

*Este README se actualiza automáticamente conforme evoluciona el servicio.*