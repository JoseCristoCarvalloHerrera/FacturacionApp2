package ni.edu.uam.facturacion.validacion;

import ni.edu.uam.facturacion.exception.Campo;
import ni.edu.uam.facturacion.exception.ValidacionException;
import ni.edu.uam.facturacion.model.Categoria;

public final class ValidadorCategoria {

    public static final int NOMBRE_MAXIMO = 100;

    private ValidadorCategoria() {
    }

    public static void validar(Categoria categoria) {

        if (categoria == null) {
            throw new ValidacionException(Campo.CATEGORIA_NOMBRE,
                    "Debe seleccionar una categoría.");
        }

        validarNombre(categoria.getNombre());
    }

    public static void validarNombre(String nombre) {

        String valor = nombre == null ? "" : nombre.trim();

        if (valor.isEmpty()) {
            throw new ValidacionException(Campo.CATEGORIA_NOMBRE,
                    "El nombre de la categoría es obligatorio.");
        }

        if (valor.length() > NOMBRE_MAXIMO) {
            throw new ValidacionException(Campo.CATEGORIA_NOMBRE,
                    "El nombre de la categoría no puede superar los "
                            + NOMBRE_MAXIMO + " caracteres.");
        }
    }
}
