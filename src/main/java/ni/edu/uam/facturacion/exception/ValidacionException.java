package ni.edu.uam.facturacion.exception;

public class ValidacionException extends IllegalArgumentException {

    private final Campo campo;

    public ValidacionException(Campo campo, String mensaje) {
        super(mensaje);
        this.campo = campo;
    }

    public Campo getCampo() {
        return campo;
    }
}
