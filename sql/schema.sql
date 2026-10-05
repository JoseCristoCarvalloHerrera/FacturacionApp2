-- Esquema para la base de datos tienda_javafx
-- Proyecto: FacturacionApp (JavaFX + PostgreSQL + JDBC)

CREATE DATABASE tienda_javafx;

\c tienda_javafx;

CREATE TABLE categoria (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    activa BOOLEAN NOT NULL DEFAULT TRUE
);

-- Garantiza unicidad del nombre de categoría, sin distinguir mayúsculas y eliminando espacios
CREATE UNIQUE INDEX ux_categoria_nombre ON categoria (LOWER(TRIM(nombre)));

CREATE TABLE producto (
    id SERIAL PRIMARY KEY,
    codigo VARCHAR(50) NOT NULL UNIQUE,
    nombre VARCHAR(150) NOT NULL,
    categoria_id INTEGER NOT NULL,
    precio_venta NUMERIC(12,2) NOT NULL,
    existencia INTEGER NOT NULL DEFAULT 0,
    ruta_imagen VARCHAR(500),
    activo BOOLEAN NOT NULL DEFAULT TRUE,

    CONSTRAINT fk_producto_categoria
        FOREIGN KEY (categoria_id)
        REFERENCES categoria(id)
            ON UPDATE CASCADE
            ON DELETE RESTRICT
);

-- Datos de ejemplo (opcional)
-- INSERT INTO categoria (nombre, activa) VALUES ('Electrónicos', TRUE);
-- INSERT INTO producto (codigo, nombre, categoria_id, precio_venta, existencia, activo)
-- VALUES ('P001', 'Laptop', 1, 15000.00, 10, TRUE);
