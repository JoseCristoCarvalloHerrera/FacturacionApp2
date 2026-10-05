package ni.edu.uam.facturacion.validacion;

import ni.edu.uam.facturacion.exception.ValidacionException;
import ni.edu.uam.facturacion.model.Categoria;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ValidadorCategoriaTest {

    @Test
    void nombreVacioDebeLanzarExcepcion() {
        ValidacionException ex = assertThrows(ValidacionException.class,
                () -> ValidadorCategoria.validarNombre(""));
        assertTrue(ex.getMessage().toLowerCase().contains("obligatorio"));
    }

    @Test
    void nombreConSoloEspaciosDebeLanzarExcepcion() {
        ValidacionException ex = assertThrows(ValidacionException.class,
                () -> ValidadorCategoria.validarNombre("   "));
        assertTrue(ex.getMessage().toLowerCase().contains("obligatorio"));
    }

    @Test
    void nombreValidoNoDebeLanzarExcepcion() {
        assertDoesNotThrow(() -> ValidadorCategoria.validarNombre("Electrodomésticos"));
    }

    @Test
    void categoriaNullDebeLanzarExcepcion() {
        assertThrows(ValidacionException.class, () -> ValidadorCategoria.validar(null));
    }
}
