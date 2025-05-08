package conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion_DB {

    public static Connection conectar() {
        try {
            String url = "jdbc:mysql://localhost:3306/chaos_app";
            String user = "root";
            String pass = "";
            return DriverManager.getConnection(url, user, pass);
        } catch (SQLException e) {
            System.out.println("Error de conexión: " + e.getMessage());
            return null;
        }
    }

}
