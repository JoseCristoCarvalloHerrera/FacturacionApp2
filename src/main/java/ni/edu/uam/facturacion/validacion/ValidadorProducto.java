package ni.edu.uam.facturacion.validacion;

import ni.edu.uam.facturacion.exception.Campo;
import ni.edu.uam.facturacion.exception.ValidacionException;
import ni.edu.uam.facturacion.model.Categoria;
import ni.edu.uam.facturacion.model.Producto;

import java.math.BigDecimal;

public final class ValidadorProducto {

    public static final int CODIGO_MAXIMO = 50;

    public static final int NOMBRE_MAXIMO = 150;

    public static final int RUTA_IMAGEN_MAXIMO = 500;

    public static final BigDecimal PRECIO_MAXIMO = new BigDecimal("9999999999.99");

    private ValidadorProducto() {
    }

    public static void validar(Producto producto) {

        if (producto == null) {
            throw new ValidacionException(Campo.PRODUCTO_CODIGO,
                    "Debe completar los campos obligatorios.");
        }

        validarCodigo(producto.getCodigo());
        validarNombre(producto.getNombre());
        validarCategoria(producto.getCategoria());
        validarPrecio(producto.getPrecioVenta());
        validarExistencia(producto.getExistencia());
        validarRutaImagen(producto.getRutaImagen());
    }

    public static void validarCodigo(String codigo) {

        String valor = codigo == null ? "" : codigo.trim();

        if (valor.isEmpty()) {
            throw new ValidacionException(Campo.PRODUCTO_CODIGO,
                    "El código del producto es obligatorio.");
        }

        if (valor.length() > CODIGO_MAXIMO) {
            throw new ValidacionException(Campo.PRODUCTO_CODIGO,
                    "El código del producto no puede superar los "
                            + CODIGO_MAXIMO + " caracteres.");
        }
    }

    public static void validarNombre(String nombre) {

        String valor = nombre == null ? "" : nombre.trim();

        if (valor.isEmpty()) {
            throw new ValidacionException(Campo.PRODUCTO_NOMBRE,
                    "El nombre del producto es obligatorio.");
        }

        if (valor.length() > NOMBRE_MAXIMO) {
            throw new ValidacionException(Campo.PRODUCTO_NOMBRE,
                    "El nombre del producto no puede superar los "
                            + NOMBRE_MAXIMO + " caracteres.");
        }
    }

    public static void validarCategoria(Categoria categoria) {

        if (categoria == null) {
            throw new ValidacionException(Campo.PRODUCTO_CATEGORIA,
                    "Debe seleccionar una categoría.");
        }

        if (categoria.getId() == null) {
            throw new ValidacionException(Campo.PRODUCTO_CATEGORIA,
                    "La categoría seleccionada no está registrada.");
        }
    }

    public static void validarPrecio(BigDecimal precio) {

        if (precio == null) {
            throw new ValidacionException(Campo.PRODUCTO_PRECIO,
                    "El precio de venta es obligatorio.");
        }

        if (precio.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidacionException(Campo.PRODUCTO_PRECIO,
                    "El precio de venta debe ser mayor que cero.");
        }

        if (precio.compareTo(PRECIO_MAXIMO) > 0) {
            throw new ValidacionException(Campo.PRODUCTO_PRECIO,
                    "El precio de venta no puede superar los " + PRECIO_MAXIMO + ".");
        }
    }

    public static void validarExistencia(int existencia) {

        if (existencia < 0) {
            throw new ValidacionException(Campo.PRODUCTO_EXISTENCIA,
                    "La existencia no puede ser negativa.");
        }
    }

    public static void validarRutaImagen(String rutaImagen) {

        if (rutaImagen == null) {
            return;
        }

        if (rutaImagen.trim().length() > RUTA_IMAGEN_MAXIMO) {
            throw new ValidacionException(Campo.PRODUCTO_IMAGEN,
                    "La ruta de la imagen no puede superar los "
                            + RUTA_IMAGEN_MAXIMO + " caracteres.");
        }
    }

    public static BigDecimal leerPrecio(String texto) {

        String valor = texto == null ? "" : texto.trim();

        if (valor.isEmpty()) {
            throw new ValidacionException(Campo.PRODUCTO_PRECIO,
                    "El precio de venta es obligatorio.");
        }

        BigDecimal precio;

        try {
            precio = new BigDecimal(valor);
        } catch (NumberFormatException e) {
            throw new ValidacionException(Campo.PRODUCTO_PRECIO,
                    "El precio debe ser un valor numérico.");
        }

        validarPrecio(precio);

        return precio;
    }

    public static int leerExistencia(String texto) {

        String valor = texto == null ? "" : texto.trim();

        if (valor.isEmpty()) {
            throw new ValidacionException(Campo.PRODUCTO_EXISTENCIA,
                    "La existencia es obligatoria.");
        }

        int existencia;

        try {
            existencia = Integer.parseInt(valor);
        } catch (NumberFormatException e) {
            throw new ValidacionException(Campo.PRODUCTO_EXISTENCIA,
                    "La existencia debe ser un número entero.");
        }

        validarExistencia(existencia);

        return existencia;
    }
}
