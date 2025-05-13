package logica; 


import java.util.List;
import java.util.ArrayList;

public class Usuario {
    private int idUsuario;
    private String nombreUsuario; // O correo, según cómo lo manejes
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
}