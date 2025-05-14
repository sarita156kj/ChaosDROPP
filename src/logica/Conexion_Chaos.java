package logica;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

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
    
   public static List<String> obtenerPermisosPorRol(java.sql.Connection con, int idRol) throws SQLException {
    List<String> permisos = new ArrayList<>();
    String sql = "SELECT p.nombre_permiso FROM roles_permisos rp " +
                 "JOIN permisos p ON rp.id_permiso = p.id_permiso " +
                 "WHERE rp.id_rol = ?";
    try (java.sql.PreparedStatement pstmt = con.prepareStatement(sql)) {
        pstmt.setInt(1, idRol);
        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            permisos.add(rs.getString("nombre_permiso"));
        }
    }
    return permisos;
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