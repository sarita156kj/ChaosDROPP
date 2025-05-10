package logica;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class Conexion_DB {

    // Datos de conexión
    private static final String base = "chaos_app";   
    private static final String user = "root";        
    private static final String password = "";      
    private static final String url = "jdbc:mysql://localhost:3306/" + base;

    // Constructor vacío (opcional)
    public Conexion_DB() {}

    // Método estático para obtener la conexión
    public static Connection conectar() {
        Connection link = null;
        try {
            // Cargar el driver de MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establecer la conexión
            link = DriverManager.getConnection(url, user, password);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al conectar con la base de datos: " + e.getMessage());
            e.printStackTrace();
        } catch (ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(null, "Driver de base de datos no encontrado: " + ex.getMessage());
            ex.printStackTrace();
        }
        return link;
    }
}
