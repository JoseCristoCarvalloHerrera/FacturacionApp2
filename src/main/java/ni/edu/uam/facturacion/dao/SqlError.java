package ni.edu.uam.facturacion.dao;

import java.sql.SQLException;

public final class SqlError {

    private static final String SQLSTATE_UNIQUE = "23505";

    private static final String SQLSTATE_FOREIGN_KEY = "23503";

    private SqlError() {
    }

    public static boolean esCodigoDuplicado(SQLException e) {
        return esSqlState(e, SQLSTATE_UNIQUE);
    }

    public static boolean esLlaveForanea(SQLException e) {
        return esSqlState(e, SQLSTATE_FOREIGN_KEY);
    }

    private static boolean esSqlState(SQLException e, String sqlState) {

        if (e == null || sqlState == null) {
            return false;
        }

        String estado = e.getSQLState();

        if (sqlState.equals(estado)) {
            return true;
        }

        Throwable cause = e.getCause();

        if (cause instanceof SQLException sqlEx) {
            return sqlState.equals(sqlEx.getSQLState());
        }

        return false;
    }
}
