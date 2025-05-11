package logica;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion_Chaos {

    
    private static final String URL = "jdbc:mysql://localhost:3306/chaos_app";
    private static final String USUARIO = "root";
    private static final String CONTRASEÑA = "";

    public static Connection conectar() throws SQLException {
        Connection conexion = null;
        try {
            // 1. Cargar el driver JDBC (específico para tu base de datos)
            Class.forName("com.mysql.jdbc.Driver"); // Para MySQL 8.0+

            // 2. Establecer la conexión
            conexion = DriverManager.getConnection(URL, USUARIO, CONTRASEÑA);

            // Si la conexión se establece sin excepciones, 'conexion' contendrá
            // una instancia válida de Connection.
            System.out.println("Conexión a la base de datos exitosa."); // Opcional: para depuración

        } catch (ClassNotFoundException e) {
            // Error si no se encuentra la clase del driver
            throw new SQLException("Error: No se encontró el driver de la base de datos.", e);
        } catch (SQLException e) {
            // Error al establecer la conexión (URL incorrecta, credenciales inválidas, etc.)
            throw new SQLException("Error al conectar con la base de datos: " + e.getMessage(), e);
        }

        return conexion; 
    }

    public static void main(String[] args) {
        // Método de prueba para verificar si la conexión funciona
        try (Connection con = Conexion_Chaos.conectar()) {
            if (con != null) {
                System.out.println("La conexión de prueba fue exitosa.");
            }
        } catch (SQLException e) {
            System.err.println("Error al probar la conexión: " + e.getMessage());
        }
    }
}