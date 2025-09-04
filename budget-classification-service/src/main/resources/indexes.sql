-- =====================================================
-- Budget Classification Service - Performance Indexes
-- Índices adicionales para optimización de consultas
-- =====================================================

-- =====================================================
-- ÍNDICES COMPUESTOS PARA CONSULTAS FRECUENTES
-- =====================================================

-- Índice para consultas jerárquicas (padre -> hijos activos)
CREATE INDEX IF NOT EXISTS idx_padre_activo_orden ON budget_classifications(padre_codigo, activo, orden, codigo) 
WHERE activo = true;

-- Índice para búsquedas por nivel y estado
CREATE INDEX IF NOT EXISTS idx_nivel_activo_codigo ON budget_classifications(nivel, activo, codigo) 
WHERE activo = true;

-- Índice para consultas de breadcrumb (navegación hacia arriba)
CREATE INDEX IF NOT EXISTS idx_codigo_padre_nivel ON budget_classifications(codigo, padre_codigo, nivel);

-- Índice para búsquedas de texto en nombres (trigram para búsquedas parciales)
CREATE EXTENSION IF NOT EXISTS pg_trgm;
CREATE INDEX IF NOT EXISTS idx_nombre_trigram ON budget_classifications USING gin(nombre gin_trgm_ops);

-- Índice para búsquedas de texto en descripciones
CREATE INDEX IF NOT EXISTS idx_descripcion_trigram ON budget_classifications USING gin(descripcion gin_trgm_ops);

-- =====================================================
-- ÍNDICES PARA ESTADÍSTICAS Y REPORTING
-- =====================================================

-- Índice para conteos por nivel
CREATE INDEX IF NOT EXISTS idx_nivel_activo_count ON budget_classifications(nivel) WHERE activo = true;

-- Índice para auditoría temporal
CREATE INDEX IF NOT EXISTS idx_fecha_creacion ON budget_classifications(fecha_creacion DESC);
CREATE INDEX IF NOT EXISTS idx_fecha_actualizacion ON budget_classifications(fecha_actualizacion DESC);

-- Índice para consultas por usuario creador
CREATE INDEX IF NOT EXISTS idx_creado_por ON budget_classifications(creado_por) WHERE creado_por IS NOT NULL;

-- =====================================================
-- ÍNDICES PARCIALES PARA DIFERENTES NIVELES
-- =====================================================

-- Índice específico para capítulos (raíces del árbol)
CREATE INDEX IF NOT EXISTS idx_capitulos ON budget_classifications(codigo, nombre, orden) 
WHERE nivel = 'CAPITULO' AND activo = true;

-- Índice para partidas genéricas con padre
CREATE INDEX IF NOT EXISTS idx_partidas_genericas ON budget_classifications(padre_codigo, codigo, nombre) 
WHERE nivel = 'PARTIDA_GENERICA' AND activo = true;

-- Índice para partidas específicas
CREATE INDEX IF NOT EXISTS idx_partidas_especificas ON budget_classifications(padre_codigo, codigo, nombre) 
WHERE nivel = 'PARTIDA_ESPECIFICA' AND activo = true;

-- Índice para partidas finales (hojas del árbol)
CREATE INDEX IF NOT EXISTS idx_partidas ON budget_classifications(padre_codigo, codigo, nombre) 
WHERE nivel = 'PARTIDA' AND activo = true;

-- =====================================================
-- ÍNDICES PARA VALIDACIONES DE INTEGRIDAD
-- =====================================================

-- Índice único compuesto para evitar duplicados por padre-orden
CREATE UNIQUE INDEX IF NOT EXISTS idx_unique_padre_orden ON budget_classifications(padre_codigo, orden) 
WHERE activo = true AND padre_codigo IS NOT NULL;

-- Índice para validar códigos por patrón de terminación
CREATE INDEX IF NOT EXISTS idx_codigo_pattern_validation ON budget_classifications(codigo, nivel) 
WHERE activo = true;

-- =====================================================
-- ÍNDICES PARA CACHE Y FRECUENCIA DE CONSULTA
-- =====================================================

-- Índice para consultas de jerarquía completa (LIKE queries)
CREATE INDEX IF NOT EXISTS idx_codigo_like ON budget_classifications(codigo text_pattern_ops, nivel, activo);

-- Índice para consultas frecuentes de hijos directos ordenados
CREATE INDEX IF NOT EXISTS idx_hijos_ordenados ON budget_classifications(padre_codigo, orden ASC, codigo ASC) 
WHERE activo = true;

-- =====================================================
-- ÍNDICES DE TEXTO COMPLETO PARA BÚSQUEDAS AVANZADAS
-- =====================================================

-- Crear configuración de búsqueda en español
CREATE TEXT SEARCH CONFIGURATION IF NOT EXISTS spanish_cubs (COPY = spanish);

-- Índice de texto completo combinando nombre y descripción
CREATE INDEX IF NOT EXISTS idx_fulltext_search ON budget_classifications 
USING gin((to_tsvector('spanish_cubs', COALESCE(nombre, '') || ' ' || COALESCE(descripcion, ''))));

-- Índice solo para nombres (búsquedas más rápidas)
CREATE INDEX IF NOT EXISTS idx_nombre_fulltext ON budget_classifications 
USING gin(to_tsvector('spanish_cubs', nombre));

-- =====================================================
-- ESTADÍSTICAS PARA EL QUERY PLANNER
-- =====================================================

-- Actualizar estadísticas de la tabla
ANALYZE budget_classifications;

-- =====================================================
-- VISTAS MATERIALIZADAS PARA CONSULTAS COMPLEJAS
-- =====================================================

-- Vista materializada para jerarquía completa (mejora performance de consultas recursivas)
CREATE MATERIALIZED VIEW IF NOT EXISTS mv_budget_hierarchy AS
WITH RECURSIVE jerarquia AS (
    -- Casos base: todos los capítulos
    SELECT 
        codigo,
        nombre,
        descripcion,
        nivel,
        padre_codigo,
        orden,
        activo,
        ARRAY[codigo] AS path,
        1 as depth,
        codigo as root_codigo
    FROM budget_classifications 
    WHERE nivel = 'CAPITULO' AND activo = true
    
    UNION ALL
    
    -- Recursión: todos los hijos
    SELECT 
        bc.codigo,
        bc.nombre,
        bc.descripcion,
        bc.nivel,
        bc.padre_codigo,
        bc.orden,
        bc.activo,
        j.path || bc.codigo,
        j.depth + 1,
        j.root_codigo
    FROM budget_classifications bc
    INNER JOIN jerarquia j ON bc.padre_codigo = j.codigo
    WHERE bc.activo = true
)
SELECT * FROM jerarquia;

-- Índices en la vista materializada
CREATE INDEX IF NOT EXISTS idx_mv_budget_root ON mv_budget_hierarchy(root_codigo, depth);
CREATE INDEX IF NOT EXISTS idx_mv_budget_path ON mv_budget_hierarchy USING gin(path);
CREATE INDEX IF NOT EXISTS idx_mv_budget_level ON mv_budget_hierarchy(nivel, activo);

-- =====================================================
-- FUNCIONES AUXILIARES PARA MANTENIMIENTO DE ÍNDICES
-- =====================================================

-- Función para refrescar vista materializada
CREATE OR REPLACE FUNCTION refresh_budget_hierarchy()
RETURNS void AS $$
BEGIN
    REFRESH MATERIALIZED VIEW CONCURRENTLY mv_budget_hierarchy;
END;
$$ LANGUAGE plpgsql;

-- Función para reindexar tabla completa
CREATE OR REPLACE FUNCTION reindex_budget_classifications()
RETURNS void AS $$
BEGIN
    REINDEX TABLE budget_classifications;
    ANALYZE budget_classifications;
    PERFORM refresh_budget_hierarchy();
END;
$$ LANGUAGE plpgsql;

-- =====================================================
-- JOBS PARA MANTENIMIENTO AUTOMÁTICO
-- =====================================================

-- Trigger para refrescar vista materializada después de cambios
CREATE OR REPLACE FUNCTION trigger_refresh_hierarchy()
RETURNS TRIGGER AS $$
BEGIN
    -- Refrescar la vista materializada después de cambios
    PERFORM refresh_budget_hierarchy();
    RETURN COALESCE(NEW, OLD);
END;
$$ LANGUAGE plpgsql;

-- Crear trigger para refrescar automáticamente
DROP TRIGGER IF EXISTS budget_classifications_change_trigger ON budget_classifications;
CREATE TRIGGER budget_classifications_change_trigger
    AFTER INSERT OR UPDATE OR DELETE ON budget_classifications
    FOR EACH STATEMENT
    EXECUTE FUNCTION trigger_refresh_hierarchy();

-- =====================================================
-- COMENTARIOS Y DOCUMENTACIÓN
-- =====================================================

COMMENT ON INDEX idx_padre_activo_orden IS 'Optimiza consultas jerárquicas padre->hijos activos ordenados';
COMMENT ON INDEX idx_nivel_activo_codigo IS 'Optimiza filtros por nivel y estado con ordenamiento por código';
COMMENT ON INDEX idx_nombre_trigram IS 'Permite búsquedas parciales eficientes en nombres usando trigramas';
COMMENT ON INDEX idx_fulltext_search IS 'Búsqueda de texto completo en español para nombres y descripciones';
COMMENT ON MATERIALIZED VIEW mv_budget_hierarchy IS 'Vista materializada con jerarquía completa para consultas recursivas optimizadas';

-- =====================================================
-- VERIFICACIÓN DE PERFORMANCE
-- =====================================================

-- Query para verificar uso de índices
-- SELECT schemaname, tablename, indexname, idx_tup_read, idx_tup_fetch 
-- FROM pg_stat_user_indexes 
-- WHERE tablename = 'budget_classifications'
-- ORDER BY idx_tup_read DESC;

-- Query para verificar tamaño de índices
-- SELECT indexname, pg_size_pretty(pg_relation_size(indexname::regclass)) as size
-- FROM pg_indexes 
-- WHERE tablename = 'budget_classifications'
-- ORDER BY pg_relation_size(indexname::regclass) DESC;