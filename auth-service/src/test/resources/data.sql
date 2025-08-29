-- Datos de prueba para la vista de empleados de nómina
-- Estos datos se cargarán en PostgreSQL Testcontainers durante los tests

-- Crear tabla para tests (simula la vista de nómina)
CREATE TABLE IF NOT EXISTS vw_empleados_nomina (
    curp VARCHAR(18) PRIMARY KEY,
    nombres VARCHAR(100) NOT NULL,
    apellido_paterno VARCHAR(50),
    apellido_materno VARCHAR(50),
    email VARCHAR(100),
    dependencia VARCHAR(100),
    puesto VARCHAR(100),
    status_nomina VARCHAR(20) DEFAULT 'ACTIVO',
    fecha_ingreso DATE,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Empleados de prueba del Gobierno de Tabasco
INSERT INTO vw_empleados_nomina (curp, nombres, apellido_paterno, apellido_materno, email, dependencia, puesto, status_nomina, fecha_ingreso) VALUES
('AAAA800101HTABCD01', 'Juan Carlos', 'García', 'López', 'juan.garcia@tabasco.gob.mx', 'CGMAIG', 'Coordinador de TI', 'ACTIVO', '2020-01-15'),
('BBBB850215MTABCD02', 'María Elena', 'Martínez', 'Hernández', 'maria.martinez@tabasco.gob.mx', 'Secretaría de Finanzas', 'Analista de Sistemas', 'ACTIVO', '2019-03-10'),
('CCCC900301HTABCD03', 'Roberto', 'Pérez', 'Sánchez', 'roberto.perez@tabasco.gob.mx', 'CGMAIG', 'Desarrollador', 'INACTIVO', '2021-06-01'),
('DDDD750720MTABCD04', 'Ana Patricia', 'González', 'Rodríguez', 'ana.gonzalez@tabasco.gob.mx', 'Secretaría de Salud', 'Administradora de BD', 'ACTIVO', '2018-09-20'),
('EEEE880412HTABCD05', 'Carlos Alberto', 'Jiménez', 'Torres', 'carlos.jimenez@tabasco.gob.mx', 'CGMAIG', 'Analista de Seguridad', 'SUSPENDIDO', '2020-11-05');