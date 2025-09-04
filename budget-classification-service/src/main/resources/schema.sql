-- =====================================================
-- Budget Classification Service - Database Schema
-- Sistema de Clasificación Presupuestaria Jerárquica
-- Catálogo Único de Bienes y Servicios (CUBS) - Tabasco
-- =====================================================

-- Eliminar tabla si existe (solo para desarrollo)
DROP TABLE IF EXISTS budget_classifications CASCADE;

-- Crear tabla principal de clasificación presupuestaria
CREATE TABLE budget_classifications (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    codigo VARCHAR(4) NOT NULL UNIQUE,
    nombre VARCHAR(200) NOT NULL,
    descripcion TEXT,
    nivel VARCHAR(20) NOT NULL,
    padre_codigo VARCHAR(4),
    orden INTEGER NOT NULL DEFAULT 0,
    activo BOOLEAN NOT NULL DEFAULT true,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    creado_por VARCHAR(100),
    actualizado_por VARCHAR(100),
    
    -- Constraints
    CONSTRAINT ck_codigo_formato CHECK (codigo ~ '^[0-9]{4}$'),
    CONSTRAINT ck_nivel_valido CHECK (nivel IN ('CAPITULO', 'PARTIDA_GENERICA', 'PARTIDA_ESPECIFICA', 'PARTIDA')),
    CONSTRAINT ck_capitulo_sin_padre CHECK (
        (nivel = 'CAPITULO' AND padre_codigo IS NULL) OR
        (nivel != 'CAPITULO' AND padre_codigo IS NOT NULL)
    ),
    CONSTRAINT ck_terminacion_codigo CHECK (
        (nivel = 'CAPITULO' AND codigo LIKE '%000') OR
        (nivel = 'PARTIDA_GENERICA' AND codigo LIKE '%00' AND NOT codigo LIKE '%000') OR
        (nivel = 'PARTIDA_ESPECIFICA' AND codigo LIKE '%0' AND NOT codigo LIKE '%00') OR
        (nivel = 'PARTIDA' AND NOT codigo LIKE '%0')
    ),
    
    -- Foreign Key
    CONSTRAINT fk_padre FOREIGN KEY (padre_codigo) REFERENCES budget_classifications(codigo) ON DELETE RESTRICT
);

-- Crear índices para optimización de consultas
CREATE INDEX idx_codigo ON budget_classifications(codigo);
CREATE INDEX idx_nivel ON budget_classifications(nivel);
CREATE INDEX idx_padre_codigo ON budget_classifications(padre_codigo);
CREATE INDEX idx_activo ON budget_classifications(activo);
CREATE INDEX idx_nivel_activo ON budget_classifications(nivel, activo);
CREATE INDEX idx_padre_activo ON budget_classifications(padre_codigo, activo);
CREATE INDEX idx_nombre_busqueda ON budget_classifications USING gin(to_tsvector('spanish', nombre));
CREATE INDEX idx_descripcion_busqueda ON budget_classifications USING gin(to_tsvector('spanish', descripcion));

-- Función para actualizar timestamp de modificación
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.fecha_actualizacion = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Trigger para actualizar automáticamente fecha de modificación
CREATE TRIGGER update_budget_classifications_updated_at 
    BEFORE UPDATE ON budget_classifications 
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_at_column();

-- Comentarios en tabla y columnas
COMMENT ON TABLE budget_classifications IS 'Clasificación presupuestaria jerárquica de 4 niveles para el sistema CUBS';
COMMENT ON COLUMN budget_classifications.codigo IS 'Código de 4 dígitos que determina el nivel jerárquico';
COMMENT ON COLUMN budget_classifications.nivel IS 'Nivel jerárquico: CAPITULO, PARTIDA_GENERICA, PARTIDA_ESPECIFICA, PARTIDA';
COMMENT ON COLUMN budget_classifications.padre_codigo IS 'Código del elemento padre en la jerarquía';
COMMENT ON COLUMN budget_classifications.orden IS 'Orden para presentación dentro del mismo nivel';