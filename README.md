# Sistema de Facturación — JavaFX + PostgreSQL + JDBC

Proyecto de la clase **Programación de Aplicaciones de Escritorio** — Universidad Americana (UAM).

## Integrantes

- Jose Cristo Carvallo Herrer
- Jesy Nicole González Jarquín

## La asignación

La práctica consiste en desarrollar una aplicación de escritorio con **JavaFX** conectada a una base de datos **PostgreSQL** mediante **JDBC**. Los objetivos son:

- Crear un proyecto JavaFX utilizando Maven.
- Conectar Java con PostgreSQL mediante JDBC.
- Utilizar `Connection` y `PreparedStatement`.
- Crear clases modelo e implementar el patrón DAO.
- Relacionar las clases `Categoria` y `Producto`.
- Mostrar la información utilizando JavaFX.

## ¿Qué hace la aplicación?

Es un sistema sencillo para administrar el catálogo de una tienda:

- **Menú principal** con acceso a Categorías y Productos.
- **Categorías:** crear, listar, editar y eliminar categorías. Una categoría que ya tiene productos no se puede eliminar (lo impide la llave foránea); en su lugar se puede desactivar.
- **Productos:** crear, listar, editar y eliminar productos con código, nombre, categoría, precio de venta, existencia, ruta de imagen y estado activo. La categoría se elige en un `ComboBox` que muestra solo las categorías activas.
- Los datos se muestran en un `TableView`; al seleccionar una fila, el formulario se llena para editarla.

### Arquitectura

```
JavaFX (FXML)  →  Controller  →  DAO  →  PostgreSQL
```

- **Vista (FXML):** define la interfaz.
- **Controller:** maneja los eventos de la interfaz y valida los datos.
- **DAO:** ejecuta las consultas SQL con `PreparedStatement`.
- **PostgreSQL:** guarda la información.

## Requisitos

- JDK 21
- PostgreSQL
- Maven (se incluye el wrapper `mvnw` / `mvnw.cmd`)

## Configuración de la base de datos

1. Crear la base de datos:

```sql
CREATE DATABASE tienda_javafx;
```

2. Conectado a `tienda_javafx`, crear las tablas (también disponible en `sql/schema.sql`):

```sql
CREATE TABLE categoria (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    activa BOOLEAN NOT NULL DEFAULT TRUE
);

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
```

3. Ajustar el usuario y la contraseña de PostgreSQL en
   `src/main/java/ni/edu/uam/facturacion/util/DatabaseConnection.java`.

Para comprobar la conexión se puede ejecutar la clase `TestConexion`, que está en el mismo paquete `util`.

## Validaciones y control de errores

Antes de cualquier INSERT, UPDATE o DELETE se valida la información según las reglas de la práctica:

| Situación | Mensaje esperado |
|---|---|
| Código vacío | El código es obligatorio. |
| Código duplicado | Ya existe un producto con ese código. |
| Precio incorrecto | El precio debe ser un valor numérico. |
| Existencia negativa | La existencia no puede ser negativa. |
| Categoría no seleccionada | Debe seleccionar una categoría. |
| Categoría con productos | No puede eliminar la categoría porque tiene productos asociados. |
| Error SQL | No fue posible completar la operación. |

## Pruebas automatizadas

Las reglas de validación se prueban con JUnit 5:

```bash
./mvnw test
```

Los tests cubren las matrices de las secciones 22 y 23 del enunciado: campos obligatorios, fórmulas numéricas inválidas (`NumberFormatException`), precio menor o igual a cero, existencia negativa, duplicados y categorías inválidas.

Para el reto práctico (sección 24) se recomienda correr la aplicación y recorrer estas 10 situaciones, que están controladas por validaciones o excepciones con mensajes al usuario:

1. Registrar una categoría sin nombre → advertir y no guardar.
2. Registrar una categoría correctamente.
3. Intentar registrar la misma categoría otra vez → advertencia de duplicado.
4. Registrar un producto sin categoría → advertencia y no guardar.
5. Ingresar letras en el precio → error de formato.
6. Ingresar una existencia negativa → error de validación.
7. Registrar un producto correctamente.
8. Intentar registrar otro producto con el mismo código → advertencia.
9. Intentar eliminar la categoría usada por el producto → impedir y explicar.
10. Detener el servicio de PostgreSQL y recargar → la aplicación muestra el error sin cerrarse.

## Cómo ejecutar

```bash
./mvnw clean javafx:run
```

También se puede ejecutar la clase `FacturacionApplication` desde IntelliJ IDEA.

## Estructura del proyecto

```
FacturacionApp/
├── pom.xml
├── README.md
├── sql/
│   └── schema.sql        → esquema completo de la base
└── src/main/
    ├── java/
    │   ├── module-info.java
    │   └── ni/edu/uam/facturacion/
    │       ├── application/    → clase Application (punto de entrada)
    │       ├── controller/     → controladores de las vistas (Menú, Categoría, Producto)
    │       ├── dao/            → acceso a datos (CategoriaDAO, ProductoDAO, SqlError)
    │       ├── exception/      → ValidacionException, enum Campo
    │       ├── model/          → entidades (Categoria, Producto, ...)
    │       ├── util/           → conexión JDBC, SceneManager, Alertas, prueba de conexión
    │       └── validacion/     → ValidadorCategoria, ValidadorProducto
    ├── resources/ni/edu/uam/facturacion/
    │   ├── fxml/               → vistas (menú, categorías, productos)
    │   ├── images/             → logo e imágenes de productos
    │   └── icons/              → íconos de los botones
    └── test/java/ni/edu/uam/facturacion/validacion/
        ├── ValidadorCategoriaTest.java
        └── ValidadorProductoTest.java
```

## Tecnologías

- Java 21
- JavaFX 21 (controls, fxml)
- PostgreSQL + driver JDBC 42.7.8
- Lombok
- Maven