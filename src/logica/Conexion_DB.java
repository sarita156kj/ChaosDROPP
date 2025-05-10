package logica;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class Conexion_DB {

    // Datos de conexión
    private static final String BASE = "chaos_app";   
    private static final String USER = "root";        
    private static final String PASSWORD = "";      
    private static final String URL = "jdbc:mysql://localhost:3306/"+ BASE;

    // Constructor vacío (opcional)
    public Conexion_DB() {}

    // Método estático para obtener la conexión
    public static Connection conectar() {
        Connection link = null;
        try {
            // Cargar el driver de MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establecer la conexión
            link = DriverManager.getConnection(URL, USER, PASSWORD);

        } catch (SQLException e) {
            // Si ocurre un error de conexión
            JOptionPane.showMessageDialog(null, "Error al conectar con la base de datos: " + e.getMessage());
            e.printStackTrace(); // Imprime el error completo en la consola
        } catch (ClassNotFoundException ex) {
            // Si no se encuentra el driver
            JOptionPane.showMessageDialog(null, "Driver de base de datos no encontrado: " + ex.getMessage());
            ex.printStackTrace(); // Imprime el error completo en la consola
        }
        return link; // Devuelve el objeto Connection, o null si hubo un error
    }
}

