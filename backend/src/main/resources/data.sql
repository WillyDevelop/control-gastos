INSERT INTO usuarios (id, email, password, nombre, activo, rol) VALUES (1, 'admin@controlgastos.com', '$2b$10$BTTBaSG2tTBJg92b/zsrIeXId9guFU9J.IGlRqisUnOVl2GFEdZTe', 'Administrador', true, 'ROLE_USER');
INSERT INTO categorias (id, nombre, icono, tipo, limite_mensual, usuario_id) VALUES (1, 'Supermercado', '', 'GASTO', 500.00, 1);
INSERT INTO categorias (id, nombre, icono, tipo, limite_mensual, usuario_id) VALUES (2, 'Transporte', '', 'GASTO', 100.00, 1);
INSERT INTO categorias (id, nombre, icono, tipo, limite_mensual, usuario_id) VALUES (3, 'Sueldo', '', 'INGRESO', NULL, 1);
INSERT INTO categorias (id, nombre, icono, tipo, limite_mensual, usuario_id) VALUES (4, 'Ocio', '', 'GASTO', 200.00, 1);
INSERT INTO categorias (id, nombre, icono, tipo, limite_mensual, usuario_id) VALUES (5, 'Servicios', '', 'GASTO', 150.00, 1);
