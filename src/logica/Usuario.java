package logica;

import java.util.List;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane; // Importa JOptionPane

public class Usuario {
    private int idUsuario;
    private String nombreUsuario;
    private int idRol;
    private List<String> permisos;

    public Usuario(int idUsuario, String nombreUsuario, int idRol, List<String> permisos) {
        this.idUsuario = idUsuario;
        this.nombreUsuario = nombreUsuario;
        this.idRol = idRol;
        this.permisos = permisos;
    }

    public Usuario(int idUsuario, String nombreUsuario, int idRol) {
        this.idUsuario = idUsuario;
        this.nombreUsuario = nombreUsuario;
        this.idRol = idRol;
        this.permisos = new ArrayList<>();
    }

    public boolean tienePermiso(String permisoNecesario) {
        return permisos != null && permisos.contains(permisoNecesario);
    }

    public List<String> getPermisos() {
        return permisos;
    }

    public void setPermisos(List<String> permisos) {
        this.permisos = permisos;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public int getIdRol() {
        return idRol;
    }

    public void setIdRol(int idRol) {
        this.idRol = idRol;
    }

    /**
     * Este método deshabilita el menú "Gestión de Usuarios"
     * si el usuario tiene el idRol 2 (Vendedor).
     * @param frame El JFrame que contiene el menú.
     */
    public void inhabilitarMenuSegunRol(JFrame frame) {
        if (this.idRol == 2) { // 2 = Vendedor
            for (java.awt.Component comp : frame.getJMenuBar().getComponents()) { // Usa getJMenuBar()
                if (comp instanceof JMenu) {
                    JMenu menu = (JMenu) comp;
                    if ("Gestión de Usuarios".equals(menu.getText())) { // Por ejemplo, si "Gestión de Usuarios" es el texto del menú
                        menu.setEnabled(false); // Deshabilita el menú
                        JOptionPane.showMessageDialog(frame, "El menú 'Gestión de Usuarios' ha sido deshabilitado para Vendedor.", "Menú Deshabilitado", JOptionPane.INFORMATION_MESSAGE);
                        break; // Sale del bucle después de deshabilitar
                    }
                }
            }
        }
    }
    
     public void inhabilitarMenuSegunRol(JFrame frame, String nombreMenu) {
        if (this.idRol == 2) { // 2 = Vendedor
            for (java.awt.Component comp : frame.getJMenuBar().getComponents()) { // Usa getJMenuBar()
                if (comp instanceof JMenu) {
                    JMenu menu = (JMenu) comp;
                    if (nombreMenu.equals(menu.getText())) { // Compara el nombre del menú
                        menu.setEnabled(false); // Deshabilita el menú
                        JOptionPane.showMessageDialog(frame, "El menú '" + nombreMenu + "' ha sido deshabilitado para Vendedor.", "Menú Deshabilitado", JOptionPane.INFORMATION_MESSAGE);
                        break; // Sale del bucle después de deshabilitar
                    }
                }
            }
        }
    }
}


