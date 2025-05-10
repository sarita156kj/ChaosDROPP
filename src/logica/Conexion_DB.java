package logica;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

public class Conexion_DB {
    
    private final String base = "chaos_app";
    private final String user = "root";
    private final String password = "";
    private final String url = "jdbc:mysql://localhost:3306/" + base;
 
    public Conexion_DB(){
        
    }
        public Connection conectar(){
        Connection link=null;
        try
       {
            Class.forName("com.mysql.cj.jdbc.Driver");
 
           link=DriverManager.getConnection(this.url, this.user, this.password);
            
        }catch(SQLException e){
            
        JOptionPane.showConfirmDialog(null, e);
        
    }   catch (ClassNotFoundException ex) {   
            Logger.getLogger(Conexion_DB.class.getName()).log(Level.SEVERE, null, ex);
        }
return link;
} 
} 
