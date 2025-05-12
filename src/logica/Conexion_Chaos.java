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
            Class.forName("com.mysql.jdbc.Driver"); 
            
            conexion = DriverManager.getConnection(URL, USUARIO, CONTRASEÑA);

            System.out.println("Conexión a la base de datos exitosa."); // Opcional: para depuración

        } catch (ClassNotFoundException e) {
     
            throw new SQLException("Error: No se encontró el driver de la base de datos.", e);
        } catch (SQLException e) {
            
            throw new SQLException("Error al conectar con la base de datos: " + e.getMessage(), e);
        }

        return conexion; 
    }

    public static void main(String[] args) {
        
        try (Connection con = Conexion_Chaos.conectar()) {
            if (con != null) {
                System.out.println("La conexión de prueba fue exitosa.");
            }
        } catch (SQLException e) {
            System.err.println("Error al probar la conexión: " + e.getMessage());
        }
    }
}