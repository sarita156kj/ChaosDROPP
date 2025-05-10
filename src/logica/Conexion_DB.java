package logica;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion_DB {

    // Nombre más descriptivo para el método
    public static Connection getConexion() {
        try {
            String url = "jdbc:mysql://localhost:3306/chaos_app";  // Asegúrate que el nombre de la base de datos es correcto
            String user = "root";  // Usualmente 'root', pero cambia según tu configuración
            String pass = "";  // Asegúrate de que este es el password correcto
            return DriverManager.getConnection(url, user, pass);
        } catch (SQLException e) {
            // Mejor manejo de errores
            System.err.println("Error de conexión: " + e.getMessage());
            e.printStackTrace();  // Esto te dará más detalles sobre el error
            return null;
        }
    }
}

