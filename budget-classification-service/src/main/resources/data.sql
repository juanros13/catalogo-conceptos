-- =====================================================
-- Budget Classification Service - Initial Data Population
-- Clasificación Presupuestaria Jerárquica CUBS - Tabasco
-- =====================================================

-- Limpiar datos existentes (solo para desarrollo/testing)
-- TRUNCATE TABLE budget_classifications RESTART IDENTITY CASCADE;

-- =====================================================
-- CAPÍTULOS (Nivel 1) - Terminan en 000
-- =====================================================

INSERT INTO budget_classifications (codigo, nombre, descripcion, nivel, padre_codigo, orden, activo, creado_por) VALUES
('1000', 'Servicios Personales', 'Remuneraciones del personal que presta sus servicios en los entes públicos, en forma regular o eventual, así como las obligaciones del ente público derivadas de las relaciones contractuales y legales con este personal', 'CAPITULO', NULL, 1, true, 'SISTEMA_INICIAL'),
('2000', 'Materiales y Suministros', 'Asignaciones destinadas a la adquisición de toda clase de insumos y suministros requeridos para la prestación de bienes y servicios públicos', 'CAPITULO', NULL, 2, true, 'SISTEMA_INICIAL'),
('3000', 'Servicios Generales', 'Asignaciones destinadas a cubrir el costo de todo tipo de servicios que se contraten con particulares o instituciones del propio sector público', 'CAPITULO', NULL, 3, true, 'SISTEMA_INICIAL'),
('4000', 'Transferencias, Asignaciones, Subsidios y Otras Ayudas', 'Asignaciones que los entes públicos otorgan sin contraprestación y, de manera eventual, a diversos agentes económicos y sociales', 'CAPITULO', NULL, 4, true, 'SISTEMA_INICIAL'),
('5000', 'Bienes Muebles, Inmuebles e Intangibles', 'Asignaciones destinadas a la adquisición de toda clase de bienes muebles, inmuebles e intangibles', 'CAPITULO', NULL, 5, true, 'SISTEMA_INICIAL'),
('6000', 'Inversión Pública', 'Asignaciones destinadas a obras por contrato y proyectos productivos y acciones de fomento', 'CAPITULO', NULL, 6, true, 'SISTEMA_INICIAL'),
('7000', 'Inversiones Financieras y Otras Provisiones', 'Erogaciones que realiza el sector público en la adquisición de acciones, bonos y otros títulos y valores', 'CAPITULO', NULL, 7, true, 'SISTEMA_INICIAL'),
('8000', 'Participaciones y Aportaciones', 'Recursos que por disposición constitucional, legal o del Presupuesto de Egresos corresponden a los gobiernos estatales y municipales', 'CAPITULO', NULL, 8, true, 'SISTEMA_INICIAL'),
('9000', 'Deuda Pública', 'Asignaciones destinadas a cubrir las obligaciones por concepto de deuda pública interna y externa', 'CAPITULO', NULL, 9, true, 'SISTEMA_INICIAL');

-- =====================================================
-- PARTIDAS GENÉRICAS DEL CAPÍTULO 1000 - SERVICIOS PERSONALES
-- Terminan en 00 pero no en 000
-- =====================================================

INSERT INTO budget_classifications (codigo, nombre, descripcion, nivel, padre_codigo, orden, activo, creado_por) VALUES
('1100', 'Remuneraciones al Personal de Carácter Permanente', 'Asignaciones destinadas a remunerar el trabajo del personal de carácter permanente', 'PARTIDA_GENERICA', '1000', 1, true, 'SISTEMA_INICIAL'),
('1200', 'Remuneraciones al Personal de Carácter Transitorio', 'Asignaciones destinadas a remunerar el trabajo del personal eventual', 'PARTIDA_GENERICA', '1000', 2, true, 'SISTEMA_INICIAL'),
('1300', 'Remuneraciones Adicionales y Especiales', 'Asignaciones que se otorgan tanto al personal de carácter permanente como transitorio', 'PARTIDA_GENERICA', '1000', 3, true, 'SISTEMA_INICIAL'),
('1400', 'Seguridad Social', 'Asignaciones destinadas a cubrir las prestaciones establecidas en favor de los trabajadores', 'PARTIDA_GENERICA', '1000', 4, true, 'SISTEMA_INICIAL'),
('1500', 'Otras Prestaciones Sociales y Económicas', 'Asignaciones para el otorgamiento de prestaciones sociales y económicas en favor del personal', 'PARTIDA_GENERICA', '1000', 5, true, 'SISTEMA_INICIAL'),
('1600', 'Previsiones', 'Asignaciones destinadas a cubrir las medidas de incremento en las percepciones de los servidores públicos', 'PARTIDA_GENERICA', '1000', 6, true, 'SISTEMA_INICIAL'),
('1700', 'Pago de Estímulos a Servidores Públicos', 'Asignaciones destinadas a cubrir los estímulos económicos a los servidores públicos', 'PARTIDA_GENERICA', '1000', 7, true, 'SISTEMA_INICIAL');

-- =====================================================
-- PARTIDAS GENÉRICAS DEL CAPÍTULO 2000 - MATERIALES Y SUMINISTROS
-- =====================================================

INSERT INTO budget_classifications (codigo, nombre, descripcion, nivel, padre_codigo, orden, activo, creado_por) VALUES
('2100', 'Materiales de Administración, Emisión de Documentos y Artículos Oficiales', 'Materiales y útiles de oficina, de impresión, informáticos y de apoyo administrativo', 'PARTIDA_GENERICA', '2000', 1, true, 'SISTEMA_INICIAL'),
('2200', 'Alimentos y Utensilios', 'Productos alimenticios y utensilios para el servicio de alimentación', 'PARTIDA_GENERICA', '2000', 2, true, 'SISTEMA_INICIAL'),
('2300', 'Materias Primas y Materiales de Producción y Comercialización', 'Materiales, productos semielaborados y materias primas para la producción de bienes', 'PARTIDA_GENERICA', '2000', 3, true, 'SISTEMA_INICIAL'),
('2400', 'Materiales y Artículos de Construcción y de Reparación', 'Materiales para construcción, acabados, ferretería y mantenimiento de inmuebles', 'PARTIDA_GENERICA', '2000', 4, true, 'SISTEMA_INICIAL'),
('2500', 'Productos Químicos, Farmacéuticos y de Laboratorio', 'Medicinas, materiales de curación, productos químicos y de laboratorio', 'PARTIDA_GENERICA', '2000', 5, true, 'SISTEMA_INICIAL'),
('2600', 'Combustibles, Lubricantes y Aditivos', 'Gasolina, diésel, lubricantes y otros combustibles para vehículos y maquinaria', 'PARTIDA_GENERICA', '2000', 6, true, 'SISTEMA_INICIAL'),
('2700', 'Vestuario, Blancos, Prendas de Protección y Artículos Deportivos', 'Uniformes, ropa de trabajo, equipo de protección y artículos deportivos', 'PARTIDA_GENERICA', '2000', 7, true, 'SISTEMA_INICIAL'),
('2800', 'Materiales y Suministros para Seguridad', 'Prendas de protección, materiales de seguridad y vigilancia', 'PARTIDA_GENERICA', '2000', 8, true, 'SISTEMA_INICIAL'),
('2900', 'Herramientas, Refacciones y Accesorios Menores', 'Herramientas menores, refacciones y accesorios para equipos y vehículos', 'PARTIDA_GENERICA', '2000', 9, true, 'SISTEMA_INICIAL');

-- =====================================================
-- PARTIDAS GENÉRICAS DEL CAPÍTULO 3000 - SERVICIOS GENERALES
-- =====================================================

INSERT INTO budget_classifications (codigo, nombre, descripcion, nivel, padre_codigo, orden, activo, creado_por) VALUES
('3100', 'Servicios Básicos', 'Energía eléctrica, gas, agua, telefonía, internet y servicios básicos', 'PARTIDA_GENERICA', '3000', 1, true, 'SISTEMA_INICIAL'),
('3200', 'Servicios de Arrendamiento', 'Arrendamiento de edificios, vehículos, mobiliario, equipo y otros bienes muebles e inmuebles', 'PARTIDA_GENERICA', '3000', 2, true, 'SISTEMA_INICIAL'),
('3300', 'Servicios Profesionales, Científicos, Técnicos y Otros Servicios', 'Servicios profesionales especializados, consultoría, estudios e investigaciones', 'PARTIDA_GENERICA', '3000', 3, true, 'SISTEMA_INICIAL'),
('3400', 'Servicios Financieros, Bancarios y Comerciales', 'Comisiones bancarias, seguros, fletes y servicios comerciales', 'PARTIDA_GENERICA', '3000', 4, true, 'SISTEMA_INICIAL'),
('3500', 'Servicios de Instalación, Reparación, Mantenimiento y Conservación', 'Mantenimiento y reparación de bienes muebles e inmuebles', 'PARTIDA_GENERICA', '3000', 5, true, 'SISTEMA_INICIAL'),
('3600', 'Servicios de Comunicación Social y Publicidad', 'Difusión por radio, televisión, medios impresos y digitales', 'PARTIDA_GENERICA', '3000', 6, true, 'SISTEMA_INICIAL'),
('3700', 'Servicios de Traslado y Viáticos', 'Pasajes, hospedaje, alimentación y otros gastos de comisión y representación', 'PARTIDA_GENERICA', '3000', 7, true, 'SISTEMA_INICIAL'),
('3800', 'Servicios Oficiales', 'Gastos de ceremonial, gastos de orden social y cultural', 'PARTIDA_GENERICA', '3000', 8, true, 'SISTEMA_INICIAL'),
('3900', 'Otros Servicios Generales', 'Servicios funerarios, de jardinería, limpieza y otros no clasificados', 'PARTIDA_GENERICA', '3000', 9, true, 'SISTEMA_INICIAL');

-- =====================================================
-- PARTIDAS ESPECÍFICAS DEL CAPÍTULO 2000 - MATERIALES Y SUMINISTROS
-- Ejemplo detallado para 2100 - Materiales de Administración
-- Terminan en 0 pero no en 00
-- =====================================================

INSERT INTO budget_classifications (codigo, nombre, descripcion, nivel, padre_codigo, orden, activo, creado_por) VALUES
('2110', 'Materiales, Útiles y Equipos Menores de Oficina', 'Papelería, útiles de escritorio, artículos de oficina y equipos menores', 'PARTIDA_ESPECIFICA', '2100', 1, true, 'SISTEMA_INICIAL'),
('2120', 'Materiales y Útiles de Impresión y Reproducción', 'Materiales para impresión, fotocopiado, encuadernación y reproducción', 'PARTIDA_ESPECIFICA', '2100', 2, true, 'SISTEMA_INICIAL'),
('2130', 'Material Estadístico y Geográfico', 'Materiales para levantamientos estadísticos, cartográficos y geográficos', 'PARTIDA_ESPECIFICA', '2100', 3, true, 'SISTEMA_INICIAL'),
('2140', 'Materiales, Útiles y Equipos Menores de Tecnologías de la Información y Comunicaciones', 'Insumos informáticos, medios magnéticos, equipos menores de cómputo', 'PARTIDA_ESPECIFICA', '2100', 4, true, 'SISTEMA_INICIAL'),
('2150', 'Material Impreso e Información Digital', 'Libros, revistas, periódicos, material bibliográfico e información digital', 'PARTIDA_ESPECIFICA', '2100', 5, true, 'SISTEMA_INICIAL'),
('2160', 'Material de Limpieza', 'Productos y utensilios de limpieza e higiene', 'PARTIDA_ESPECIFICA', '2100', 6, true, 'SISTEMA_INICIAL'),
('2170', 'Materiales y Útiles de Enseñanza', 'Material didáctico, educativo y de capacitación', 'PARTIDA_ESPECIFICA', '2100', 7, true, 'SISTEMA_INICIAL'),
('2180', 'Materiales para el Registro e Identificación de Bienes y Personas', 'Materiales para credencialización, identificación y registro', 'PARTIDA_ESPECIFICA', '2100', 8, true, 'SISTEMA_INICIAL');

-- =====================================================
-- PARTIDAS ESPECÍFICAS ADICIONALES
-- =====================================================

-- Partidas Específicas de 2200 - Alimentos y Utensilios
INSERT INTO budget_classifications (codigo, nombre, descripcion, nivel, padre_codigo, orden, activo, creado_por) VALUES
('2210', 'Productos Alimenticios para Personas', 'Alimentos y bebidas para el personal y beneficiarios', 'PARTIDA_ESPECIFICA', '2200', 1, true, 'SISTEMA_INICIAL'),
('2220', 'Productos Alimenticios para Animales', 'Alimentos balanceados y forrajes para animales', 'PARTIDA_ESPECIFICA', '2200', 2, true, 'SISTEMA_INICIAL'),
('2230', 'Utensilios para el Servicio de Alimentación', 'Vajillas, cubiertos, utensilios de cocina y comedor', 'PARTIDA_ESPECIFICA', '2200', 3, true, 'SISTEMA_INICIAL');

-- Partidas Específicas de 2600 - Combustibles
INSERT INTO budget_classifications (codigo, nombre, descripcion, nivel, padre_codigo, orden, activo, creado_por) VALUES
('2610', 'Combustibles, Lubricantes y Aditivos', 'Gasolina, diésel, gas, lubricantes y aditivos para vehículos', 'PARTIDA_ESPECIFICA', '2600', 1, true, 'SISTEMA_INICIAL'),
('2620', 'Carbón y Sus Derivados', 'Carbón mineral, vegetal y sus derivados', 'PARTIDA_ESPECIFICA', '2600', 2, true, 'SISTEMA_INICIAL');

-- Partidas Específicas de 3100 - Servicios Básicos
INSERT INTO budget_classifications (codigo, nombre, descripcion, nivel, padre_codigo, orden, activo, creado_por) VALUES
('3110', 'Energía Eléctrica', 'Servicio de energía eléctrica para inmuebles e instalaciones', 'PARTIDA_ESPECIFICA', '3100', 1, true, 'SISTEMA_INICIAL'),
('3120', 'Gas', 'Servicio de gas natural, LP y otros gases combustibles', 'PARTIDA_ESPECIFICA', '3100', 2, true, 'SISTEMA_INICIAL'),
('3130', 'Agua', 'Servicio de agua potable, alcantarillado y saneamiento', 'PARTIDA_ESPECIFICA', '3100', 3, true, 'SISTEMA_INICIAL'),
('3140', 'Telefonía Tradicional', 'Servicio telefónico fijo, larga distancia y enlace de datos', 'PARTIDA_ESPECIFICA', '3100', 4, true, 'SISTEMA_INICIAL'),
('3150', 'Telefonía Celular', 'Servicio de telefonía móvil, planes corporativos', 'PARTIDA_ESPECIFICA', '3100', 5, true, 'SISTEMA_INICIAL'),
('3160', 'Servicios de Telecomunicaciones y Satélites', 'Internet, satelital, fibra óptica y telecomunicaciones', 'PARTIDA_ESPECIFICA', '3100', 6, true, 'SISTEMA_INICIAL'),
('3170', 'Servicios de Acceso de Internet, Redes y Procesamiento de Información', 'Servicios de internet, hosting, cloud computing y procesamiento de datos', 'PARTIDA_ESPECIFICA', '3100', 7, true, 'SISTEMA_INICIAL'),
('3180', 'Servicios Postales y Telegráficos', 'Correo, paquetería, mensajería y servicios postales', 'PARTIDA_ESPECIFICA', '3100', 8, true, 'SISTEMA_INICIAL'),
('3190', 'Integración de Expedientes y Servicios Archivísticos', 'Digitalización, microfilmación y servicios de archivo', 'PARTIDA_ESPECIFICA', '3100', 9, true, 'SISTEMA_INICIAL');

-- =====================================================
-- PARTIDAS (Nivel 4) - Ejemplos detallados
-- No terminan en 0
-- =====================================================

-- Partidas de 2110 - Materiales y Útiles de Oficina
INSERT INTO budget_classifications (codigo, nombre, descripcion, nivel, padre_codigo, orden, activo, creado_por) VALUES
('2111', 'Materiales y Útiles de Oficina', 'Papel, bolígrafos, lápices, grapas, clips, folders, archiveros y artículos de papelería', 'PARTIDA', '2110', 1, true, 'SISTEMA_INICIAL'),
('2112', 'Materiales y Útiles de Impresión y Reproducción', 'Tintas, tóneres, cartuchos, papel especial para impresión', 'PARTIDA', '2110', 2, true, 'SISTEMA_INICIAL'),
('2113', 'Material Estadístico y Geográfico', 'Formularios, encuestas, mapas, material cartográfico', 'PARTIDA', '2110', 3, true, 'SISTEMA_INICIAL'),
('2114', 'Materiales, Útiles y Equipos Menores de Tecnologías de la Información', 'CDs, DVDs, USBs, cables, mouse, teclados, memorias', 'PARTIDA', '2110', 4, true, 'SISTEMA_INICIAL'),
('2115', 'Material Impreso e Información Digital', 'Libros técnicos, manuales, suscripciones digitales', 'PARTIDA', '2110', 5, true, 'SISTEMA_INICIAL'),
('2116', 'Materiales de Limpieza', 'Detergentes, desinfectantes, escobas, trapeadores', 'PARTIDA', '2110', 6, true, 'SISTEMA_INICIAL'),
('2117', 'Materiales y Útiles de Enseñanza', 'Pizarrones, marcadores, material didáctico', 'PARTIDA', '2110', 7, true, 'SISTEMA_INICIAL'),
('2118', 'Materiales para el Registro e Identificación', 'Credenciales, gafetes, cintas, materiales de identificación', 'PARTIDA', '2110', 8, true, 'SISTEMA_INICIAL');

-- Partidas de 2120 - Materiales de Impresión
INSERT INTO budget_classifications (codigo, nombre, descripcion, nivel, padre_codigo, orden, activo, creado_por) VALUES
('2121', 'Materiales de Impresión', 'Papel bond, papel especial, sobres, formas continuas', 'PARTIDA', '2120', 1, true, 'SISTEMA_INICIAL'),
('2122', 'Materiales de Encuadernación', 'Pastas, espirales, anillados, materiales de encuadernación', 'PARTIDA', '2120', 2, true, 'SISTEMA_INICIAL'),
('2123', 'Materiales de Serigrafía e Impresión Especializada', 'Tintas especiales, materiales para serigrafía', 'PARTIDA', '2120', 3, true, 'SISTEMA_INICIAL');

-- Partidas de 2210 - Productos Alimenticios
INSERT INTO budget_classifications (codigo, nombre, descripcion, nivel, padre_codigo, orden, activo, creado_por) VALUES
('2211', 'Productos Alimenticios para Personas', 'Alimentos procesados, bebidas, productos perecederos', 'PARTIDA', '2210', 1, true, 'SISTEMA_INICIAL'),
('2212', 'Productos de Panadería y Tortillería', 'Pan, tortillas, productos de panadería', 'PARTIDA', '2210', 2, true, 'SISTEMA_INICIAL'),
('2213', 'Carnes y Embutidos', 'Carnes frescas, embutidos, productos cárnicos', 'PARTIDA', '2210', 3, true, 'SISTEMA_INICIAL'),
('2214', 'Pescados y Mariscos', 'Pescados, mariscos frescos y procesados', 'PARTIDA', '2210', 4, true, 'SISTEMA_INICIAL'),
('2215', 'Productos Lácteos', 'Leche, quesos, yogur, productos lácteos', 'PARTIDA', '2210', 5, true, 'SISTEMA_INICIAL'),
('2216', 'Frutas y Verduras', 'Frutas frescas, verduras, productos agrícolas', 'PARTIDA', '2210', 6, true, 'SISTEMA_INICIAL'),
('2217', 'Especias, Condimentos y Aderezos', 'Sal, azúcar, especias, condimentos diversos', 'PARTIDA', '2210', 7, true, 'SISTEMA_INICIAL'),
('2218', 'Productos Alimenticios Diversos', 'Conservas, productos enlatados, alimentos diversos', 'PARTIDA', '2210', 8, true, 'SISTEMA_INICIAL');

-- Partidas de 3110 - Energía Eléctrica
INSERT INTO budget_classifications (codigo, nombre, descripcion, nivel, padre_codigo, orden, activo, creado_por) VALUES
('3111', 'Energía Eléctrica', 'Consumo de energía eléctrica en oficinas y instalaciones gubernamentales', 'PARTIDA', '3110', 1, true, 'SISTEMA_INICIAL');

-- Partidas de 3120 - Gas
INSERT INTO budget_classifications (codigo, nombre, descripcion, nivel, padre_codigo, orden, activo, creado_por) VALUES
('3121', 'Gas', 'Servicio de gas LP, natural para calentadores, cocinas y calefacción', 'PARTIDA', '3120', 1, true, 'SISTEMA_INICIAL');

-- Partidas de 3130 - Agua
INSERT INTO budget_classifications (codigo, nombre, descripcion, nivel, padre_codigo, orden, activo, creado_por) VALUES
('3131', 'Agua', 'Servicio de agua potable, drenaje y alcantarillado', 'PARTIDA', '3130', 1, true, 'SISTEMA_INICIAL');

-- Partidas de 3140 - Telefonía Tradicional
INSERT INTO budget_classifications (codigo, nombre, descripcion, nivel, padre_codigo, orden, activo, creado_por) VALUES
('3141', 'Telefonía Tradicional', 'Servicios de telefonía fija local y larga distancia', 'PARTIDA', '3140', 1, true, 'SISTEMA_INICIAL');

-- Partidas de 3150 - Telefonía Celular
INSERT INTO budget_classifications (codigo, nombre, descripcion, nivel, padre_codigo, orden, activo, creado_por) VALUES
('3151', 'Telefonía Celular', 'Planes corporativos de telefonía móvil y servicios de datos', 'PARTIDA', '3150', 1, true, 'SISTEMA_INICIAL');

-- Partidas de 3160 - Servicios de Telecomunicaciones
INSERT INTO budget_classifications (codigo, nombre, descripcion, nivel, padre_codigo, orden, activo, creado_por) VALUES
('3161', 'Servicios de Telecomunicaciones y Satélites', 'Enlaces satelitales, microondas, fibra óptica', 'PARTIDA', '3160', 1, true, 'SISTEMA_INICIAL');

-- Partidas de 3170 - Internet y Procesamiento
INSERT INTO budget_classifications (codigo, nombre, descripcion, nivel, padre_codigo, orden, activo, creado_por) VALUES
('3171', 'Servicios de Acceso a Internet', 'Servicios de internet dedicado, banda ancha', 'PARTIDA', '3170', 1, true, 'SISTEMA_INICIAL'),
('3172', 'Servicios de Hosting y Cloud Computing', 'Alojamiento web, servicios en la nube, servidores virtuales', 'PARTIDA', '3170', 2, true, 'SISTEMA_INICIAL'),
('3173', 'Procesamiento de Información', 'Servicios de procesamiento de datos, backup, recuperación', 'PARTIDA', '3170', 3, true, 'SISTEMA_INICIAL');

-- =====================================================
-- VERIFICACIÓN DE INTEGRIDAD DE DATOS
-- =====================================================

-- Verificar que todos los códigos tienen el formato correcto
-- SELECT codigo, nivel FROM budget_classifications
-- WHERE NOT (codigo ~ '^[0-9]{4}$');

-- Verificar que las terminaciones corresponden al nivel
-- SELECT codigo, nivel FROM budget_classifications
-- WHERE (nivel = 'CAPITULO' AND NOT codigo LIKE '%000') OR
--       (nivel = 'PARTIDA_GENERICA' AND (codigo LIKE '%000' OR NOT codigo LIKE '%00')) OR
--       (nivel = 'PARTIDA_ESPECIFICA' AND (codigo LIKE '%00' OR NOT codigo LIKE '%0')) OR
--       (nivel = 'PARTIDA' AND codigo LIKE '%0');

-- Verificar referencias de padre
-- SELECT bc.codigo, bc.padre_codigo FROM budget_classifications bc
-- LEFT JOIN budget_classifications padre ON bc.padre_codigo = padre.codigo
-- WHERE bc.padre_codigo IS NOT NULL AND padre.codigo IS NULL;

-- Estadísticas finales
-- SELECT nivel, COUNT(*) as total FROM budget_classifications GROUP BY nivel;
