# Budget Classification Service

Microservicio para la gestión de la clasificación presupuestaria jerárquica de 4 niveles del sistema CUBS (Catálogo Único de Bienes y Servicios) del Gobierno de Tabasco.

## 📋 Descripción General

El **Budget Classification Service** gestiona la estructura jerárquica de clasificación presupuestaria utilizada para organizar y categorizar todos los conceptos del catálogo CUBS. Implementa una jerarquía estricta de 4 niveles con validación automática y optimización de consultas mediante cache.

## 🏗️ Arquitectura

### Tecnologías
- **Java**: 17
- **Spring Boot**: 3.5.5  
- **Spring Cloud**: 2025.0.0
- **Base de Datos**: PostgreSQL
- **Cache**: Spring Cache
- **Autenticación**: OAuth2 JWT (Keycloak)
- **Testing**: JUnit 5 + Testcontainers

### Puerto de Servicio
- **Desarrollo**: 8084
- **Eureka Registration**: budget-classification-service:8084

## 🎯 Jerarquía Presupuestaria

### Estructura de 4 Niveles

| Nivel | Nombre | Patrón | Ejemplo | Descripción |
|-------|---------|--------|---------|-------------|
| **1** | **Capítulo** | `X000` | `2000` | Materiales y Suministros |
| **2** | **Partida Genérica** | `XX00` | `2100` | Materiales de administración |
| **3** | **Partida Específica** | `XXX0` | `2110` | Materiales y útiles menores |
| **4** | **Partida** | `XXXX` | `2111` | Materiales y útiles de oficina |

### Ejemplo de Jerarquía Completa

```
2000 - Materiales y Suministros (Capítulo)
├── 2100 - Materiales de administración (P. Genérica)
│   ├── 2110 - Materiales y útiles menores (P. Específica)
│   │   ├── 2111 - Materiales y útiles de oficina (Partida)
│   │   ├── 2112 - Materiales de impresión (Partida)
│   │   └── 2113 - Material informático (Partida)
│   └── 2120 - Materiales de limpieza (P. Específica)
│       ├── 2121 - Productos de limpieza (Partida)
│       └── 2122 - Utensilios de limpieza (Partida)
└── 2200 - Alimentos y utensilios (P. Genérica)
    └── 2210 - Productos alimenticios (P. Específica)
        ├── 2211 - Alimentos y bebidas (Partida)
        └── 2212 - Productos lácteos (Partida)
```

### Validación Automática

El sistema valida automáticamente la terminación del código:
- ✅ **Capítulos**: Terminan en `000` (2000, 3000, 5000)
- ✅ **Partidas Genéricas**: Terminan en `00` pero no `000` (2100, 2200)
- ✅ **Partidas Específicas**: Terminan en `0` pero no `00` (2110, 2120)
- ✅ **Partidas**: No terminan en `0` (2111, 2112, 2113)

## 📊 Estructura de Base de Datos

### Tabla Principal: `budget_classifications`

```sql
CREATE TABLE budget_classifications (
    id UUID PRIMARY KEY,
    codigo VARCHAR(4) NOT NULL UNIQUE,
    nombre VARCHAR(200) NOT NULL,
    descripcion TEXT,
    nivel VARCHAR(20) NOT NULL,
    padre_codigo VARCHAR(4),
    orden INTEGER NOT NULL DEFAULT 0,
    activo BOOLEAN NOT NULL DEFAULT true,
    fecha_creacion TIMESTAMP,
    fecha_actualizacion TIMESTAMP,
    creado_por VARCHAR(100),
    actualizado_por VARCHAR(100),
    
    CONSTRAINT fk_padre FOREIGN KEY (padre_codigo) REFERENCES budget_classifications(codigo)
);

-- Índices para optimización
CREATE INDEX idx_codigo ON budget_classifications(codigo);
CREATE INDEX idx_nivel ON budget_classifications(nivel);
CREATE INDEX idx_padre_codigo ON budget_classifications(padre_codigo);
CREATE INDEX idx_activo ON budget_classifications(activo);
```

## 🔐 Seguridad y Roles

### Modelo de Seguridad

| Operación | Acceso | Descripción |
|-----------|--------|-------------|
| **Consultas GET** | 🌐 **Público** | Todas las consultas son públicas |
| **Creación POST** | 🔒 **Administradores** | Solo `ADMIN_SISTEMA`, `CONFIGURADOR_PRESUPUESTO` |
| **Actualización PUT/PATCH** | 🔒 **Administradores** | Solo `ADMIN_SISTEMA`, `CONFIGURADOR_PRESUPUESTO` |
| **Estadísticas** | 👤 **Autenticados** | Usuarios con JWT válido |

### Roles Específicos

| Rol | Permisos | Descripción |
|-----|----------|-------------|
| **ADMIN_SISTEMA** | CRUD completo | Administrador del sistema |
| **CONFIGURADOR_PRESUPUESTO** | CRUD completo | Configurador de clasificaciones |
| **Usuario Autenticado** | Consultas + Estadísticas | Acceso a consultas y métricas |
| **Público** | Solo consultas | Acceso de lectura únicamente |

## 🛠️ API Endpoints

### Base URL: `http://localhost:8084`

#### 🌐 Consultas Públicas (Sin Autenticación)

```http
# Obtener todos los capítulos (nivel raíz)
GET /api/budget-classifications/capitulos

# Obtener clasificación por código específico
GET /api/budget-classifications/2111

# Obtener hijos directos de un código
GET /api/budget-classifications/2100/hijos

# Obtener jerarquía completa desde un código
GET /api/budget-classifications/2000/jerarquia

# Obtener por nivel específico (paginado)
GET /api/budget-classifications/nivel/CAPITULO?page=0&size=20

# Búsqueda avanzada con filtros
GET /api/budget-classifications/search?nivel=PARTIDA&padreCodigo=2110&searchTerm=oficina

# Búsqueda por texto libre
GET /api/budget-classifications/search/text?searchTerm=materiales

# Obtener breadcrumb (ruta jerárquica hacia arriba)
GET /api/budget-classifications/2111/breadcrumb
```

#### 🔒 Gestión Administrativa (Requiere Autenticación)

```http
# Crear nueva clasificación
POST /api/budget-classifications
Authorization: Bearer {jwt_token}
Content-Type: application/json
{
  "codigo": "2114",
  "nombre": "Material de archivo",
  "descripcion": "Carpetas, folders y material de archivo",
  "orden": 10
}

# Actualizar clasificación existente
PUT /api/budget-classifications/2114
Authorization: Bearer {jwt_token}
Content-Type: application/json
{
  "nombre": "Material de archivo y organización",
  "descripcion": "Carpetas, folders y material para organización documental",
  "orden": 15
}

# Activar/Inactivar clasificación
PATCH /api/budget-classifications/2114/toggle-active?activo=false
Authorization: Bearer {jwt_token}

# Verificar si se puede eliminar
GET /api/budget-classifications/2114/can-delete
Authorization: Bearer {jwt_token}

# Obtener estadísticas del sistema
GET /api/budget-classifications/statistics
Authorization: Bearer {jwt_token}
```

#### 📊 Información del Sistema

```http
# Health check
GET /api/budget-classifications/health

# Información del servicio
GET /api/budget-classifications/info
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
| `EUREKA_SERVER_URL` | http://localhost:8761/eureka | URL Eureka |

### Configuración de Desarrollo

```yaml
server:
  port: 8084

spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/acceso_tabasco_dev
    username: postgres
    password: activo
  
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: false
  
  cache:
    type: simple
    cache-names: budgetClassifications,hierarchies
  
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

#### `BudgetLevel.java` - Enum de Niveles
```java
public enum BudgetLevel {
    CAPITULO(1, "Capítulo", "X000"),
    PARTIDA_GENERICA(2, "Partida Genérica", "XX00"),
    PARTIDA_ESPECIFICA(3, "Partida Específica", "XXX0"),
    PARTIDA(4, "Partida", "XXXX");
    
    // Métodos de validación automática
    public static BudgetLevel fromCode(String code);
    public boolean isValidCode(String code);
    public String getParentCode(String code);
}
```

#### `BudgetClassification.java` - Entidad Principal
```java
@Entity
@Table(name = "budget_classifications")
public class BudgetClassification {
    @Id
    private UUID id;
    
    @Column(unique = true)
    private String codigo;
    
    private String nombre;
    private String descripcion;
    
    @Enumerated(EnumType.STRING)
    private BudgetLevel nivel;
    
    private String padreCodigo;
    private Integer orden;
    private Boolean activo;
    
    // Relación jerárquica
    @OneToMany(mappedBy = "padreCodigo")
    private List<BudgetClassification> hijos;
    
    // Métodos de negocio
    public void validarJerarquia();
    public boolean isCapitulo();
    public boolean tieneHijos();
}
```

### 🔄 DTOs

#### Request/Response Objects
- **`BudgetClassificationCreateRequest`**: Creación con validaciones
- **`BudgetClassificationUpdateRequest`**: Actualización
- **`BudgetClassificationResponse`**: Respuesta completa con metadatos
- **`HierarchyResponse`**: Jerarquías completas con estadísticas

### 🏪 Repository

#### `BudgetClassificationRepository.java`
```java
@Repository
public interface BudgetClassificationRepository extends JpaRepository<BudgetClassification, UUID> {
    
    // Búsquedas básicas
    Optional<BudgetClassification> findByCodigo(String codigo);
    List<BudgetClassification> findByNivelAndActivoTrueOrderByCodigoAsc(BudgetLevel nivel);
    
    // Consultas jerárquicas
    List<BudgetClassification> findByPadreCodigoAndActivoTrueOrderByOrdenAscCodigoAsc(String padreCodigo);
    
    // Búsqueda avanzada
    @Query("SELECT bc FROM BudgetClassification bc WHERE ...")
    Page<BudgetClassification> findWithFilters(...);
    
    // Validaciones de integridad
    @Query("SELECT COUNT(bc) > 0 FROM BudgetClassification bc WHERE bc.codigo = :padreCodigo")
    boolean existsActivePadre(String padreCodigo);
    
    // Jerarquías completas
    @Query("SELECT bc FROM BudgetClassification bc WHERE bc.codigo LIKE CONCAT(:codigoRaiz, '%')")
    List<BudgetClassification> findJerarquiaCompleta(String codigoRaiz);
}
```

### 🎯 Service Layer

#### `BudgetClassificationService.java`
```java
@Service
@Transactional("transactionManager")
public class BudgetClassificationService {
    
    // CRUD con validaciones
    @CacheEvict(value = {"budgetClassifications", "hierarchies"}, allEntries = true)
    public BudgetClassificationResponse create(BudgetClassificationCreateRequest request, String userCurp);
    
    @CacheEvict(value = {"budgetClassifications", "hierarchies"}, allEntries = true)
    public BudgetClassificationResponse update(String codigo, BudgetClassificationUpdateRequest request, String userCurp);
    
    // Consultas optimizadas con cache
    @Cacheable(value = "budgetClassifications", key = "#codigo")
    public BudgetClassificationResponse getByCodigo(String codigo);
    
    @Cacheable(value = "hierarchies", key = "#codigoRaiz")
    public HierarchyResponse getJerarquiaCompleta(String codigoRaiz);
    
    // Búsquedas y estadísticas
    public Page<BudgetClassificationResponse> search(...);
    public Map<BudgetLevel, Long> getStatistics();
}
```

### 🎮 Controller Layer

#### `BudgetClassificationController.java`
```java
@RestController
@RequestMapping("/api/budget-classifications")
@CrossOrigin(origins = "*", maxAge = 3600)
public class BudgetClassificationController {
    
    // APIs públicas de consulta
    @GetMapping("/capitulos")
    public ResponseEntity<List<BudgetClassificationResponse>> getCapitulos();
    
    @GetMapping("/{codigo}")
    public ResponseEntity<BudgetClassificationResponse> getByCodigo(@PathVariable String codigo);
    
    // APIs administrativas con seguridad
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN_SISTEMA', 'CONFIGURADOR_PRESUPUESTO')")
    public ResponseEntity<BudgetClassificationResponse> create(...);
    
    // Manejo de excepciones
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(...);
}
```

## 🔒 Validaciones de Negocio

### 1. **Validación de Códigos**
- Códigos deben tener exactamente 4 dígitos
- Terminación debe corresponder al nivel jerárquico
- Códigos deben ser únicos en el sistema

### 2. **Integridad Referencial**
- Nodos padre deben existir (excepto capítulos)
- No se pueden inactivar nodos con hijos activos
- Jerarquía debe ser consistente

### 3. **Reglas de Negocio**
- Capítulos son el nivel raíz (sin padre)
- Cada nivel solo puede tener hijos del siguiente nivel
- Orden de elementos respeta código y campo `orden`

### 4. **Validaciones Automáticas**
- Cálculo automático de nivel por código
- Asignación automática de código padre
- Validación en `@PrePersist` y `@PreUpdate`

## ⚡ Cache y Optimización

### Estrategia de Cache

```java
// Cache por código individual
@Cacheable(value = "budgetClassifications", key = "#codigo")

// Cache de jerarquías completas  
@Cacheable(value = "hierarchies", key = "#codigoRaiz")

// Invalidación en operaciones de escritura
@CacheEvict(value = {"budgetClassifications", "hierarchies"}, allEntries = true)
```

### Configuración de Cache

```yaml
spring:
  cache:
    type: simple
    cache-names: budgetClassifications,hierarchies

cubs:
  budget-classification:
    cache:
      ttl-minutes: 30
      max-entries: 1000
      enable-statistics: true
```

## 🚀 Comandos de Desarrollo

### Iniciar Servicio
```bash
# Desde directorio raíz del proyecto
cd budget-classification-service
../mvnw spring-boot:run

# Con profile específico
../mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

### Compilar y Testing
```bash
# Compilar
../mvnw clean compile

# Empaquetar
../mvnw clean package

# Tests
../mvnw test

# Tests de integración
../mvnw verify
```

## 📊 Casos de Uso Principales

### 1. **Frontend Consulta Estructura**
```http
GET /api/budget-classifications/capitulos
→ Obtiene todos los capítulos para mostrar en UI
→ [{"codigo": "2000", "nombre": "Materiales y Suministros"}, ...]
```

### 2. **Expansión Jerárquica Dinámica**
```http
GET /api/budget-classifications/2000/hijos  
→ Al expandir capítulo 2000, obtiene partidas genéricas
→ [{"codigo": "2100", "nombre": "Materiales de administración"}, ...]
```

### 3. **Administrador Crea Nueva Partida**
```http
POST /api/budget-classifications
{
  "codigo": "2115",
  "nombre": "Material de seguridad",
  "descripcion": "Equipos y suministros de seguridad industrial"
}
→ Valida que 2115 es válido (partida específica)
→ Asigna padre automáticamente (2110)
→ Crea con auditoría completa
```

### 4. **Búsqueda Inteligente**
```http
GET /api/budget-classifications/search/text?searchTerm=oficina
→ Busca en nombres y descripciones
→ Incluye códigos que empiecen con el término
→ Resultados ordenados jerárquicamente
```

### 5. **Navegación Breadcrumb**
```http
GET /api/budget-classifications/2111/breadcrumb
→ Obtiene ruta completa: 2000 → 2100 → 2110 → 2111
→ Útil para navegación y contexto
```

## 🔄 Integración con Otros Servicios

### Con **Technical Concept Service**
- Los conceptos técnicos se asocian a partidas específicas (nivel 4)
- Validación de que la partida existe antes de crear conceptos

### Con **Concept Management Service**
- Los conceptos específicos de área se categorizan por partida
- Filtros por clasificación presupuestaria en listados

### Con **Validation Service**
- Validación de que las partidas asignadas son válidas
- Verificación de coherencia presupuestaria

### Con **Gateway Service**
- Rutas: `/api/budget-classifications/**` → `budget-classification-service:8084`
- Cache a nivel de gateway para consultas frecuentes

## 📋 Estado Actual

### ✅ **Implementado y Funcionando**
- ✅ Modelo de dominio con 4 niveles jerárquicos
- ✅ Validación automática por terminación de código
- ✅ API REST completa con seguridad granular
- ✅ Repository con consultas optimizadas
- ✅ Cache para mejor performance
- ✅ Integración con config-server y Eureka
- ✅ Manejo de excepciones robusto
- ✅ Auditoría completa de cambios
- ✅ Consultas públicas para frontend
- ✅ APIs administrativas seguras

### 📝 **Próximas Mejoras**
- [ ] Tests unitarios e integración
- [ ] Documentación OpenAPI/Swagger
- [ ] Import/Export masivo de clasificaciones
- [ ] Versionado de estructura jerárquica
- [ ] Dashboard administrativo
- [ ] Métricas customizadas con Micrometer

## 🚨 Troubleshooting

### Problemas Comunes

#### Error: "Código debe tener exactamente 4 dígitos"
```
Causa: Código proporcionado no tiene formato correcto
Solución: Usar códigos de 4 dígitos numéricos (ej: 2111, no 211 o 21110)
```

#### Error: "No existe el código padre X para el código Y"
```
Causa: Intento de crear hijo sin que exista el padre
Solución: Crear primero el código padre o verificar jerarquía
```

#### Error: "No se puede inactivar el código porque tiene elementos hijos activos"
```
Causa: Intento de inactivar nodo con dependencias
Solución: Inactivar primero todos los hijos activos
```

#### Performance Lenta en Consultas Jerárquicas
```
Causa: Cache deshabilitado o invalidado frecuentemente
Solución: Verificar configuración de cache y uso de @Cacheable
```

### Logs Importantes

```bash
# Ver logs del servicio
docker logs budget-classification-service

# Filtrar por errores
docker logs budget-classification-service | grep ERROR

# Ver logs de cache
docker logs budget-classification-service | grep "Cache"
```

## 📖 Ejemplos de Respuesta

### Jerarquía Completa
```json
{
  "codigoRaiz": "2000",
  "nombreRaiz": "Materiales y Suministros",
  "totalNodos": 15,
  "profundidadMaxima": 4,
  "jerarquia": [
    {
      "codigo": "2000",
      "nombre": "Materiales y Suministros",
      "nivel": "CAPITULO",
      "hijos": [
        {
          "codigo": "2100",
          "nombre": "Materiales de administración",
          "nivel": "PARTIDA_GENERICA",
          "padreCodigo": "2000",
          "hijos": [
            {
              "codigo": "2110", 
              "nombre": "Materiales y útiles menores",
              "nivel": "PARTIDA_ESPECIFICA",
              "padreCodigo": "2100",
              "hijos": [
                {
                  "codigo": "2111",
                  "nombre": "Materiales y útiles de oficina",
                  "nivel": "PARTIDA",
                  "padreCodigo": "2110",
                  "hijos": []
                }
              ]
            }
          ]
        }
      ]
    }
  ]
}
```

---

## 📖 Versionado

- **Versión Actual**: 1.0.0
- **Última Actualización**: 2025-01-04
- **Compatibilidad**: Spring Boot 3.5.5, Java 17+

---

*Este README se actualiza automáticamente conforme evoluciona el servicio.*