# Requerimiento funcional y técnico — Sistema CUBS (Versión 2.3)

**Proyecto:** Catálogo Único de Bienes y Servicios (CUBS)

**Alcance:** Backend (microservicios Spring Boot) — API Gateway, Service Discovery y Config Server. Se incorpora módulo obligatorio "Catálogo Técnico de Conceptos Generales" para normalizar la captura de conceptos generales.

**Actualización 2.3:** Incorporación de plantillas estructuradas por capítulo - Cada capítulo tiene campos específicos obligatorios definidos por las áreas facultadas correspondientes (ej: ESPECÍFICA, PRESENTACIÓN, COMPOSICIÓN).

---

## 1. Resumen ejecutivo

Construir un sistema centralizado (CUBS) para gestionar un catálogo único de conceptos de bienes y servicios para el Gobierno del Estado de Tabasco. El sistema permitirá altas, bajas, cambios, validación multietapa por áreas facultadas, autorización, generación de reportes y transferencia de conceptos al ERP Oracle. Implementación basada en microservicios Spring Boot, con Gateway, Service Discovery y Config Server. Versión 2.3 incluye un módulo técnico que obliga a seleccionar conceptos generales desde un catálogo gestionado por áreas facultadas específicas (no texto libre por parte de capturistas). **Los validadores técnicos son las áreas facultadas del gobierno con competencia exclusiva por capítulo y partida. Además, cada capítulo tiene plantillas estructuradas con campos específicos obligatorios que varían según el tipo de bien o servicio.**

---

## 2. Objetivos principales

* Estandarizar y normalizar conceptos generales para evitar variaciones textuales y duplicados.
* Asignar responsabilidades por área facultada: cada área gubernamental gestiona conceptos generales y validación según su competencia (capítulo/partida).
* Estructurar la captura con plantillas específicas por capítulo para garantizar completitud y uniformidad en la descripción de conceptos.
* Permitir flujo controlado de captura → validación → autorización → transferencia a Oracle.
* Proveer APIs seguras y auditables para integración con sistemas internos.
* Facilitar migración y conciliación con catálogo existente.

---

## 3. Modelo de roles por áreas facultadas

### 3.1 Áreas facultadas del Gobierno de Tabasco

**Áreas con competencia exclusiva por capítulo/partida:**

**1. COORDINACIÓN GENERAL DE RECURSOS MATERIALES (CGRM)**
- **Competencia:** Capítulo 2000 - Bienes no inventariables
- **Rol:** ValidadorTécnico_CGRM
- Gestionar conceptos generales de bienes no inventariables
- Validar conceptos específicos del capítulo 2000

**2. COORDINACIÓN GENERAL DE SERVICIOS GENERALES (CGSG)**  
- **Competencia:** Capítulo 3000 - Servicios
- **Rol:** ValidadorTécnico_CGSG
- Gestionar conceptos generales de servicios
- Validar conceptos específicos del capítulo 3000

**3. COORDINACIÓN GENERAL DE MODERNIZACIÓN ADMINISTRATIVA E INNOVACIÓN GUBERNAMENTAL (CGMAIG)**
- **Competencia:** Capítulo 5000 - Bienes informáticos inventariables (partidas tecnológicas)
- **Rol:** ValidadorTécnico_CGMAIG  
- Gestionar conceptos generales de bienes informáticos
- Validar conceptos específicos de partidas tecnológicas del capítulo 5000

**4. SUBDIRECCIÓN DE PATRIMONIO**
- **Competencia:** Capítulo 5000 - Bienes muebles inventariables (partidas no tecnológicas)  
- **Rol:** ValidadorTécnico_Patrimonio
- Gestionar conceptos generales de bienes muebles
- Validar conceptos específicos de partidas no tecnológicas del capítulo 5000

### 3.2 Roles complementarios del sistema

**Capturista:**
- Crear/editar conceptos específicos de cualquier capítulo
- Seleccionar conceptos generales del catálogo (obligatorio)
- Carga masiva de conceptos

**Autorizador:**
- Autorización final de conceptos
- Cambio de estado a AUTORIZADO/TRANSFERIDO
- No gestiona catálogo técnico

**AdminSistema:**
- Configuración general del sistema
- Gestión de usuarios y roles
- Acceso a módulos de auditoría

---

## 4. Alcance funcional (módulos)

### 4.1 Módulo Capture (Altas / Bajas / Cambios)

* CRUD de conceptos con **formularios dinámicos por capítulo** (capturista).
* Campo obligatorio `id_concepto_general` (select desde Catálogo Técnico).
* **Campos estructurados según plantilla del capítulo** (ESPECÍFICA, PRESENTACIÓN, COMPOSICIÓN, etc.).
* Validación de completitud según template antes de envío.
* Carga masiva (CSV/Excel) con validaciones estructurales.

### 4.2 Módulo Validation

* Flujo configurable multietapa.
* Acciones por **área facultada correspondiente**: aceptar / rechazar / solicitar aclaración; comentarios y versiones según competencia por capítulo/partida.

### 4.3 Módulo Reporting

* Endpoints para reportes y export (CSV/Excel/PDF).

### 4.4 Módulo Transfer

* Orquestación y logs de transferencia hacia Oracle (batch/API).

### 4.5 Módulo Audit / Logging

* Registro inmutable de acciones y cambios con metadatos.

### 4.6 Módulo Notification

* Publicación de eventos y cola para envío de notificaciones (correo/sistema de mensajería).

### 4.7 Módulo Catálogo Técnico de Conceptos Generales (**ACTUALIZADO**) — *core change*

### 4.8 Módulo Template Manager (**NUEVO**) — *estructura por capítulo*

**Propósito:** Gestionar plantillas de campos específicos por capítulo para estructurar la captura de conceptos según el tipo de bien o servicio.

**Funcionalidades:**

* Definición de plantillas por capítulo (2000, 3000, 5000).
* Gestión de campos obligatorios y opcionales por template.
* CRUD de templates (solo áreas facultadas correspondientes).
* Versionado de plantillas para mantener compatibilidad.
* API pública para obtener plantillas por capítulo.

**Templates predefinidos por capítulo:**

**CAPÍTULO 2000 - MATERIALES EN GENERAL:**
- GENERAL (obligatorio - del catálogo técnico)
- ESPECÍFICA (obligatorio)
- PRESENTACIÓN DEL PRODUCTO (obligatorio)
- COMPOSICIÓN/MATERIAL/MEDIDAS/DIMENSIONES (obligatorio)

**CAPÍTULO 3000 - SERVICIOS:**
- GENERAL (obligatorio - del catálogo técnico)  
- DESCRIPCIÓN TÉCNICA DEL SERVICIO (obligatorio)
- COMPONENTES DEL SERVICIO (obligatorio)
- ACCESORIOS DEL SERVICIO (incluye: elementos humanos, materiales, equipos e insumos, software) (obligatorio)

**CAPÍTULO 5000 - BIENES INVENTARIABLES:**
- GENERAL (obligatorio - del catálogo técnico)
- CARACTERÍSTICAS DE FUNCIONALIDAD (obligatorio)
- PRESENTACIÓN DEL PRODUCTO/COMPONENTES (obligatorio)
- COMPOSICIÓN/CARACTERÍSTICAS FÍSICAS/MATERIALES/MEDIDAS/COLOR (obligatorio)
- MAYORES ESPECIFICACIONES (opcional)

**Reglas de negocio del template:**

* Cada área facultada define y mantiene las plantillas de su capítulo de competencia.
* Campo GENERAL siempre obligatorio y proveniente del catálogo técnico.
* Validación automática de completitud antes de envío a validación.
* Templates versionados para mantener compatibilidad con conceptos existentes.
* UI dinámica que adapta formulario según capítulo seleccionado.

**Propósito:** Garantizar que todos los conceptos generales provienen de una lista predefinida y gestionada por los **ValidadoresTécnicos**, evitando texto libre por parte de capturistas.

**Funcionalidades:**

* CRUD (solo para **áreas facultadas** según competencia: CGRM, CGSG, CGMAIG, Patrimonio).
* Campos: `id_concepto_general`, `nombre`, `descripcion_detallada`, `estado` (ACTIVO/INACTIVO), `fecha_creacion`, `creado_por`.
* Control de versiones / historial de cambios.
* Baja lógica (INACTIVO) — conservar histórico.
* API pública para consulta de conceptos activos (p. ej. `GET /general-concepts?status=ACTIVO`).

**Reglas de negocio actualizadas:**

* Los capturistas **no pueden** escribir texto libre en el campo "concepto general". Deben seleccionar **obligatoriamente** un `id_concepto_general` activo.
* Sólo **áreas facultadas** pueden crear/editar/inactivar conceptos generales según su competencia (capítulo/partida).
* **Cada área facultada gestiona conceptos generales Y valida conceptos específicos** de su competencia (capítulo/partida).
* Un `nombre` de concepto general debe ser único.
* Cambios mantienen versiones y registros para trazabilidad.
* Conceptos inactivos permanecen en histórico y no aparecen en nuevos selectores.

---

## 5. Requerimientos no funcionales

* Disponibilidad objetivo: 99.5% horario laboral (99% 24/7).
* p95 respuesta APIs críticas < 500 ms.
* Escalabilidad horizontal; contenerización (Docker) y orquestación (Kubernetes recomendado).
* Seguridad: OAuth2/JWT, RBAC, TLS1.2+, logging estructurado.
* Observabilidad: Prometheus, Grafana, OpenTelemetry, ELK/EFK.
* Backups: incrementales diarios, full semanal. RPO/RTO objetivo 4 horas.

---

## 6. Arquitectura técnica propuesta

* API Gateway (Spring Cloud Gateway) — auth, rate-limit, routing.
* Service Discovery (Eureka/Consul).
* Config Server (Spring Cloud Config, Git-backed) con perfiles.
* Microservicios:

  * `catalog-service` (conceptos, búsquedas)
  * `capture-service` (solicitudes, folios)
  * `validation-service` (workflow - **ValidadorTécnico**)
  * `transfer-service` (integración Oracle)
  * `user-authz-service` (usuarios/roles / o integración SSO)
  * `reporting-service`
  * `audit-service`
  * `notification-service`
  * `technical-concept-service` (gestión catálogo - **áreas facultadas**)
  * `template-service` (plantillas por capítulo - **áreas facultadas**)
* Mensajería: RabbitMQ o Kafka (events, transfer queue).
* DB: PostgreSQL (schema por servicio o DB por servicio según estrategia).
* CI/CD: GitLab CI / GitHub Actions / Jenkins.

---

## 7. Modelo de datos (alto nivel)

### Entidad Concepto

* `concept_id` (UUID)
* `folio_solicitud` (string)
* `id_concepto_general` (UUID)  // obligatorio — FK a technical\_concepts
* `codigo_interno` (string)
* `partida` (string)
* `capitulo` (enum)
* `template_id` (UUID) // FK a concept_field_templates
* `structured_fields` (jsonb) // Campos dinámicos según template
* `descripcion_especifica` (text) // DEPRECADO - usar structured_fields
* `presentacion` (string) // DEPRECADO - usar structured_fields  
* `composicion` (text) // DEPRECADO - usar structured_fields
* `unidad_medida` (string)
* `atributos` (jsonb) // Metadatos adicionales
* `estado` (enum)
* `version` (int)
* `fecha_creacion`, `creado_por`, `fecha_modificacion`, `modificado_por`

### Tabla concept_field_templates (**NUEVA**)

* `template_id` (UUID)
* `capitulo` (INTEGER) - 2000, 3000, 5000
* `field_name` (VARCHAR) - "ESPECÍFICA", "PRESENTACIÓN", etc.
* `field_label` (VARCHAR) - Label para UI
* `field_type` (VARCHAR) - TEXT, TEXTAREA, SELECT, etc.
* `is_required` (BOOLEAN)
* `field_order` (INTEGER) - Orden de presentación
* `validation_rules` (JSONB) - Reglas específicas
* `help_text` (TEXT) - Ayuda contextual
* `area_facultada` (ENUM: CGRM, CGSG, CGMAIG, PATRIMONIO)
* `active` (BOOLEAN)
* `version` (INTEGER)
* `fecha_creacion`, `creado_por`

### Tabla technical\_concepts

* `id_concepto_general` (UUID)
* `nombre` (string)
* `descripcion_detallada` (text)
* `estado` (ACTIVO/INACTIVO)
* `version` (int)
* `capitulo` (enum: 2000, 3000, 5000)
* `partidas_permitidas` (jsonb) - partidas específicas por área
* `area_facultada` (enum: CGRM, CGSG, CGMAIG, PATRIMONIO)
* `fecha_creacion`, `creado_por` (área facultada correspondiente)

### Tabla usuarios (roles actualizados)

* `user_id` (UUID)
* `curp` (string)
* `roles` (enum: Capturista, ValidadorTécnico_CGRM, ValidadorTécnico_CGSG, ValidadorTécnico_CGMAIG, ValidadorTécnico_Patrimonio, Autorizador, AdminSistema)
* `area_facultada` (enum: CGRM, CGSG, CGMAIG, PATRIMONIO) - solo para ValidadoresTécnicos

---

## 8. Contratos API (ejemplos relevantes al cambio)

* `GET /templates/{capitulo}` — obtiene plantilla de campos para capítulo.
* `POST /templates` — crea template (área facultada correspondiente).
* `GET /general-concepts?status=ACTIVO` — lista para selector de capturistas.
* `POST /general-concepts` — crea (rol área facultada según competencia).
* `PUT /general-concepts/{id}` — edita (rol área facultada según competencia).
* `DELETE /general-concepts/{id}` — inactiva (rol área facultada según competencia).
* `POST /concepts` — requiere `id_concepto_general` válido y ACTIVO.
* `POST /validation/{concept_id}/approve` — aprueba (rol área facultada correspondiente al capítulo/partida).

**Ejecución de validación en capture-service:** antes de persistir, validar existencia y estado ACTIVO del `id_concepto_general`.

---

## 9. Flujos de negocio actualizados

### Alta de concepto (capturista)

1. Capturista selecciona **capítulo** (2000, 3000, 5000) → sistema carga plantilla correspondiente.
2. Formulario se adapta dinámicamente según template del capítulo.
3. Selecciona `Concepto General` desde dropdown poblado por `technical-concept-service` (solo valores ACTIVO del capítulo).
4. Llena **campos estructurados** según plantilla (ESPECÍFICA, PRESENTACIÓN, COMPOSICIÓN, etc.).
5. Sistema **valida completitud** según template antes de permitir envío.
6. Envía solicitud → pasa al **área facultada correspondiente** según capítulo/partida para validación.

### Gestión de conceptos generales (por área facultada)

1. **Área facultada** (CGRM/CGSG/CGMAIG/Patrimonio) crea/edita/inactiva conceptos generales según su competencia.
2. Cambios versionados y auditados.
3. Nuevos conceptos activos aparecen inmediatamente para captura.
4. **La misma área facultada** también valida conceptos específicos de su capítulo/partida en el flujo multietapa.

### Validación técnica (por área facultada)

1. **Área facultada correspondiente** revisa conceptos específicos enviados por capturistas según competencia (capítulo/partida).
2. Puede aprobar, rechazar o solicitar aclaraciones.
3. Tiene conocimiento completo del catálogo técnico Y de las plantillas de campos de su área que ella misma gestiona.
4. **Valida completitud y coherencia** de campos estructurados según template.
5. Decisión informada basada en coherencia con conceptos generales y estructura esperada de su competencia.

---

## 10. Reglas de negocio (resumen actualizado)

* Conceptos generales sólo desde catálogo técnico (no texto libre).
* **Áreas facultadas** unifican gestión de catálogo y validación de conceptos según competencia (capítulo/partida).
* Unicidad de `nombre` en technical\_concepts.
* Sólo `Autorizador` puede mover estado a `AUTORIZADO` y `TRANSFERIDO`.
* Detección de duplicados por fuzzy-match en descripción específica.
* Transferencia idempotente y con reintentos controlados.

---

## 11. Migración y datos iniciales

* Extraer catálogo actual → normalizar nombres para `technical_concepts` → curación por **áreas facultadas correspondientes** → importar.
* Mantener log de mapeo (old\_value → id\_concepto\_general).

---

## 12. Pruebas y calidad

* Unit + Integration tests (cobertura ≥ 70%).
* Contract tests (Pact) entre `technical-concept-service`, `template-service` y `capture-service`.
* E2E en staging simulando flujo captura/validación/transferencia.
* Tests de roles: verificar que solo **áreas facultadas** pueden gestionar catálogo y validar según su competencia.
* SAST / DAST / SCA.

---

## 13. Entregables (versión 2.1)

1. Documento de requerimientos completo (esta versión).
2. OpenAPI skeleton para endpoints críticos (incluyendo `technical-concept-service` y `template-service`).
3. DDL inicial para tablas `concepts`, `technical_concepts` y `concept_field_templates`.
4. Matriz de roles y permisos actualizada.
5. Plan de migración y script de transformación.
6. Repositorio con microservicios (esqueleto).

---

## 14. Roadmap sugerido (resumido)

* Fase 0: Validación del modelo de roles unificado con stakeholders (1-2 semanas).
* Fase 1: Infra y plataforma (Gateway, Discovery, Config)(2-3 semanas).
* Fase 2: MVP con `technical-concept-service`, `template-service`, `capture-service`, `user-authz-service` con roles por **áreas facultadas** (4-6 semanas).
* Fase 3: Validation, Transfer y Reporting (4-6 semanas).
* Fase 4: Migración datos y E2E (3-4 semanas).

---

## 15. Riesgos y mitigaciones actualizados

* Riesgo: sobrecarga de **áreas facultadas** al gestionar catálogo Y validar conceptos → Mitigación: definir múltiples usuarios por área facultada y distribuir carga por partidas.
* Riesgo: mala curación en catálogo técnico → Mitigación: proceso de revisión inter-áreas y coordinación entre CGRM, CGSG, CGMAIG y Patrimonio.
* Riesgo: conflictos de competencia en capítulo 5000 entre CGMAIG y Patrimonio → Mitigación: definición clara de partidas tecnológicas vs no tecnológicas.
* Riesgo: resistencia de usuarios a selector estandarizado → Mitigación: capacitaciones y UI/UX de ayuda (glosario).

---

## 16. Próximos pasos inmediatos

1. Validar y firmar esta Versión 2.3 con las áreas facultadas: CGRM, CGSG, CGMAIG, Subdirección de Patrimonio y área jurídica.
2. Definir usuarios de **áreas facultadas** iniciales (CGRM, CGSG, CGMAIG, Patrimonio) que gestionarán el catálogo por competencia.
3. Crear lista inicial de conceptos técnicos para importación.
4. **Definir estructura específica de partidas tecnológicas vs no tecnológicas** para capítulo 5000.
5. Decidir estrategia para publicación (inmediata vs programada) de nuevos conceptos técnicos.
6. **Validar plantillas de campos con áreas facultadas** antes de implementación.

---

## 17. Estado actual del proyecto

**Base técnica disponible (Fase 1 completada):**
- ✅ `discovery-service` (Puerto 8761) - Eureka Server
- ✅ `config-server` (Puerto 8888) - Spring Cloud Config  
- ✅ `gateway-service` (Puerto 8080) - API Gateway
- ✅ `auth-service` (Puerto 8081) - Autenticación CURP/Keycloak

**Próximos servicios a implementar (Fase 2):**
- 🔄 `technical-concept-service` - **PRIORITARIO**
- 🔄 `template-service` - **NUEVO - PRIORITARIO**
- 🔄 `capture-service` con formularios dinámicos
- 🔄 Expansión de `auth-service` a `user-authz-service`

**Tecnologías:**
- Spring Boot 3.5.5
- Spring Cloud 2025.0.0
- PostgreSQL
- Docker/Docker Compose
- Keycloak OAuth2/JWT