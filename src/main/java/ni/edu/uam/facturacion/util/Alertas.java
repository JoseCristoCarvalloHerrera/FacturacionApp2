package ni.edu.uam.facturacion.util;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public final class Alertas {

    private static final String TITULO_VALIDACION = "Validación";

    private static final String TITULO_ERROR = "Error";

    private Alertas() {
    }

    public static void exito(String titulo, String mensaje) {
        mostrar(Alert.AlertType.INFORMATION, titulo, mensaje);
    }

    public static void advertencia(String titulo, String mensaje) {
        mostrar(Alert.AlertType.WARNING, titulo, mensaje);
    }

    public static void advertencia(String mensaje) {
        mostrar(Alert.AlertType.WARNING, TITULO_VALIDACION, mensaje);
    }

    public static void error(String titulo, String mensaje) {
        mostrar(Alert.AlertType.ERROR, titulo, mensaje);
    }

    public static void error(String mensaje) {
        mostrar(Alert.AlertType.ERROR, TITULO_ERROR, mensaje);
    }

    public static boolean confirmar(String titulo, String mensaje) {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, mensaje);

        alert.setTitle(titulo);
        alert.setHeaderText(null);

        return alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }

    private static void mostrar(Alert.AlertType tipo, String titulo, String mensaje) {

        Alert alert = new Alert(tipo, mensaje);

        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}
