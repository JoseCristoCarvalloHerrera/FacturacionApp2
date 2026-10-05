package ni.edu.uam.facturacion.validacion;

import ni.edu.uam.facturacion.exception.ValidacionException;
import ni.edu.uam.facturacion.model.Categoria;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ValidadorProductoTest {

    @Test
    void codigoVacioDebeLanzarExcepcion() {
        ValidacionException ex = assertThrows(ValidacionException.class,
                () -> ValidadorProducto.validarCodigo(""));
        assertTrue(ex.getMessage().toLowerCase().contains("obligatorio"));
    }

    @Test
    void nombreVacioDebeLanzarExcepcion() {
        ValidacionException ex = assertThrows(ValidacionException.class,
                () -> ValidadorProducto.validarNombre(""));
        assertTrue(ex.getMessage().toLowerCase().contains("obligatorio"));
    }

    @Test
    void categoriaNullDebeLanzarExcepcion() {
        assertThrows(ValidacionException.class, () -> ValidadorProducto.validarCategoria(null));
    }

    @Test
    void precioTextoDebeLanzarExcepcion() {
        assertThrows(ValidacionException.class,
                () -> ValidadorProducto.leerPrecio("abc"));
    }

    @Test
    void precioCeroDebeLanzarExcepcion() {
        ValidacionException ex = assertThrows(ValidacionException.class,
                () -> ValidadorProducto.leerPrecio("0"));
        assertTrue(ex.getMessage().toLowerCase().contains("mayor que cero"));
    }

    @Test
    void precioNegativoDebeLanzarExcepcion() {
        ValidacionException ex = assertThrows(ValidacionException.class,
                () -> ValidadorProducto.leerPrecio("-15.50"));
        assertTrue(ex.getMessage().toLowerCase().contains("mayor que cero"));
    }

    @Test
    void existenciaTextoDebeLanzarExcepcion() {
        assertThrows(ValidacionException.class,
                () -> ValidadorProducto.leerExistencia("diez"));
    }

    @Test
    void existenciaNegativaDebeLanzarExcepcion() {
        ValidacionException ex = assertThrows(ValidacionException.class,
                () -> ValidadorProducto.leerExistencia("-3"));
        assertTrue(ex.getMessage().toLowerCase().contains("negativa"));
    }

    @Test
    void precioDecimalDebeLeerCorrectamente() {
        BigDecimal p = assertDoesNotThrow(() -> ValidadorProducto.leerPrecio("10.5"));
        assertEquals(new BigDecimal("10.5"), p);
    }

    @Test
    void existenciaDecimalDebeLanzarExcepcion() {
        assertThrows(ValidacionException.class,
                () -> ValidadorProducto.leerExistencia("10.5"));
    }

    @Test
    void valoresValidosNoLanzanExcepcion() {
        Categoria c = new Categoria(1, "Hogar", true);
        assertDoesNotThrow(() -> ValidadorProducto.validarCodigo("PRD001"));
        assertDoesNotThrow(() -> ValidadorProducto.validarNombre("Lámpara"));
        assertDoesNotThrow(() -> ValidadorProducto.validarCategoria(c));
        assertDoesNotThrow(() -> ValidadorProducto.leerPrecio("25.00"));
        assertDoesNotThrow(() -> ValidadorProducto.leerExistencia("5"));
    }
}
