package ventanas;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import java.awt.Component;
import java.awt.Font;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import logica.Usuario;

/**
 *
 * @author marco
 */
public class Ventanamultiple extends javax.swing.JFrame {

    private int rolVendedor = 2; // Asegúrate de que este valor coincida con el ID del rol de vendedor en tu base de datos
    private int rolAdministrador = 1; // Asegúrate de que este valor coincida con el ID del rol de administrador en tu base de datos
    private JMenuItem gestionarUsuariosMenuItem; // Declarar variable para el JMenuItem de "Gestion de Usuarios"
    private Usuario usuarioAutenticado;

    public Ventanamultiple() {
        initComponents(); // Inicialización de componentes de la ventana
        this.setExtendedState(Ventanamultiple.MAXIMIZED_BOTH);
        this.setTitle("Sistema de ventas y pedidos - CHAOSdrop");
        inicializarUsuarioYMenus(); // Llama al método para inicializar usuario y menús
        verificarPermisos();
    }

    private void inicializarUsuarioYMenus() {
        usuarioAutenticado = obtenerUsuarioLogueado(); // Obtener el usuario de la sesión

        if (usuarioAutenticado != null) {
            inhabilitarMenuSegunRol(usuarioAutenticado);
        }

        // Esto debería estar en tu metodo initComponents
        // setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // pack();
        // setLocationRelativeTo(null);
        setVisible(true); // Asegúrate de que esto se llame DESPUÉS de inhabilitar el menú
    }

    private Usuario obtenerUsuarioLogueado() {
        // Aquí va tu lógica para obtener el usuario que inició sesión
        // Esto depende de cómo manejes la autenticación en tu aplicación
        // Por ejemplo:
        // return usuarioService.obtenerUsuarioPorCredenciales(usuario, contraseña);
        if (InicioSesion.getUsuarioAutenticado() != null) {
            return InicioSesion.getUsuarioAutenticado();
        }
        return new Usuario(1, "admin123", rolAdministrador); // Esto es SÓLO para pruebas, DEBES obtenerlo de tu lógica real
    }

    private void inhabilitarMenuSegunRol(Usuario usuario) {
        // Deshabilita el menú GestiondeUsuariomenu y sus submenús
        if (usuario.getIdRol() == rolVendedor && GestiondeUsuariomenu != null) { // Verifica el rol y si el menú existe
            GestiondeUsuariomenu.setEnabled(false); // Deshabilita el menú principal

            // Deshabilita los submenús (JMenuItem) dentro de GestiondeUsuariomenu
            for (Component item : GestiondeUsuariomenu.getMenuComponents()) {
                if (item instanceof JMenuItem) {
                    ((JMenuItem) item).setEnabled(false);
                }
            }
        } else if (usuario.getIdRol() != rolAdministrador && GestiondeUsuariomenu != null) {
            // Si el usuario no es administrador, deshabilita la opción de gestión de usuarios
            if (gestionarUsuariosMenuItem != null) {
                gestionarUsuariosMenuItem.setEnabled(false);
            }
        }
    }

    private void verificarPermisos() {
        usuarioAutenticado = InicioSesion.getUsuarioAutenticado(); // Obtén el usuario autenticado

        if (usuarioAutenticado != null) {
            System.out.println("Permisos del usuario autenticado: " + usuarioAutenticado.getPermisos());
            if (usuarioAutenticado.getIdRol() != rolAdministrador) { // Solo para usuarios que no son administradores
                if (!usuarioAutenticado.tienePermiso("acceder_productos")) {
                    editMenu.setEnabled(false);
                }
                if (!usuarioAutenticado.tienePermiso("acceder_clientes")) {
                    jMenu2.setEnabled(false);
                }
                if (!usuarioAutenticado.tienePermiso("acceder_pedidos") && !usuarioAutenticado.tienePermiso("acceder_envios")) {
                    fileMenu.setEnabled(false);
                }
                if (!usuarioAutenticado.tienePermiso("acceder_ventas")) {
                    helpMenu.setEnabled(false);
                }
                if (!usuarioAutenticado.tienePermiso("crear_usuarios")) {
                    jMenuItem1.setEnabled(false);
                }
                GestiondeUsuariomenu.setEnabled(false);
                for (Component item : GestiondeUsuariomenu.getMenuComponents()) {
                    if (item instanceof JMenuItem) {
                        ((JMenuItem) item).setEnabled(false);
                    }
                }
            }

            // Puedes seguir añadiendo más verificaciones de permisos para otros elementos del menú
        } else {
            // Si no hay usuario autenticado (por alguna razón), podrías deshabilitar todos los menús por seguridad.
            editMenu.setEnabled(false);
            jMenu2.setEnabled(false);
            fileMenu.setEnabled(false);
            helpMenu.setEnabled(false);
            jMenu1.setEnabled(false);
            GestiondeUsuariomenu.setEnabled(false);
        }
    }

    // Método para crear un nuevo usuario con el rol de vendedor por defecto
    public Usuario crearNuevoUsuario(String nombreUsuario, String contraseña) {
        // Aquí iría la lógica para crear un nuevo usuario en la base de datos
        // y obtener el ID del nuevo usuario.  Esto es un ejemplo.
        int nuevoIdUsuario
                = // Obtener el ID del nuevo usuario de la base de datos
                -1; // Inicializar en -1 para indicar un error potencial
        // Ejemplo de cómo podrías obtener el ID después de insertar en la base de datos:
        // nuevoIdUsuario = databaseService.insertarUsuario(nombreUsuario, contraseña, rolVendedor);

        if (nuevoIdUsuario != -1) {
            Usuario nuevoUsuario = new Usuario(nuevoIdUsuario, nombreUsuario, rolVendedor);
            // También podrías necesitar guardar el nuevoUsuario en la base de datos
            // o en una sesión, dependiendo de tu arquitectura.
            return nuevoUsuario;
        } else {
            return null; // Indica que hubo un error al crear el usuario
        }
    }

    // Método para asignar rol de administrador a un usuario
    public void asignarRolAdministrador(Usuario usuario) {
        // Verificar si el usuario que realiza la asignación es un administrador
        if (usuarioAutenticado != null && usuarioAutenticado.getIdRol() == rolAdministrador) {
            // Lógica para actualizar el rol del usuario en la base de datos
            // Por ejemplo:
            // databaseService.actualizarRolUsuario(usuario.getIdUsuario(), rolAdministrador);

            usuario.setIdRol(rolAdministrador); // Actualiza el rol del usuario en el objeto Usuario
            // También podrías necesitar actualizar el usuario en la sesión o en cualquier
            // otro lugar donde se almacene la información del usuario.
        } else {
            // Lanzar una excepción o mostrar un mensaje de error indicando que
            // el usuario no tiene permisos para realizar esta acción.
            JOptionPane.showMessageDialog(this, "No tiene permisos para asignar el rol de administrador.", "Error de Permisos", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        escritorio = new javax.swing.JDesktopPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        menuBar = new javax.swing.JMenuBar();
        editMenu = new javax.swing.JMenu();
        cutMenuItem = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem3 = new javax.swing.JMenuItem();
        fileMenu = new javax.swing.JMenu();
        saveMenuItem = new javax.swing.JMenuItem();
        openMenuItem = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        GestiondeUsuariomenu = new javax.swing.JMenu();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();
        helpMenu = new javax.swing.JMenu();
        contentMenuItem = new javax.swing.JMenuItem();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenuItem8 = new javax.swing.JMenuItem();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem6 = new javax.swing.JMenuItem();
        jMenuItem7 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        escritorio.setBackground(new java.awt.Color(8, 9, 39));

        jPanel1.setBackground(new java.awt.Color(0, 0, 0));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/opcion 9 (5).png"))); // NOI18N
        jLabel2.setText("jLabel2");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, -40, 1530, -1));

        escritorio.add(jPanel1);
        jPanel1.setBounds(0, 0, 1540, 870);

        menuBar.setBackground(new java.awt.Color(0, 0, 0));

        editMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/íconos/lista-de-verificacion (1).png"))); // NOI18N
        editMenu.setMnemonic('e');
        editMenu.setText("Gestión Producto");
        editMenu.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N

        cutMenuItem.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        cutMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/íconos/inventario-disponible (1).png"))); // NOI18N
        cutMenuItem.setMnemonic('t');
        cutMenuItem.setText("Inventario");
        cutMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cutMenuItemActionPerformed(evt);
            }
        });
        editMenu.add(cutMenuItem);

        menuBar.add(editMenu);

        jMenu2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/íconos/control-parental.png"))); // NOI18N
        jMenu2.setText("Registro de Cliente");
        jMenu2.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jMenu2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jMenu2MouseReleased(evt);
            }
        });

        jMenuItem3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jMenuItem3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/íconos/contacto.png"))); // NOI18N
        jMenuItem3.setText("Registro de Cliente");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem3);

        menuBar.add(jMenu2);

        fileMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/íconos/rastreo (2).png"))); // NOI18N
        fileMenu.setMnemonic('f');
        fileMenu.setText("Gestión Pedido");
        fileMenu.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        fileMenu.addMenuListener(new javax.swing.event.MenuListener() {
            public void menuCanceled(javax.swing.event.MenuEvent evt) {
            }
            public void menuDeselected(javax.swing.event.MenuEvent evt) {
            }
            public void menuSelected(javax.swing.event.MenuEvent evt) {
                fileMenuMenuSelected(evt);
            }
        });
        fileMenu.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fileMenuMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                fileMenuMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                fileMenuMouseReleased(evt);
            }
        });
        fileMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fileMenuActionPerformed(evt);
            }
        });

        saveMenuItem.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        saveMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/íconos/historial-de-pedidos.png"))); // NOI18N
        saveMenuItem.setMnemonic('s');
        saveMenuItem.setText("Historial de Pedidos");
        saveMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(saveMenuItem);

        openMenuItem.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        openMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/íconos/icons8-seguimiento-de-entrega-24.png"))); // NOI18N
        openMenuItem.setMnemonic('o');
        openMenuItem.setText("Seguimiento de Envíos");
        openMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(openMenuItem);

        jMenuItem2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jMenuItem2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/íconos/contador-de-salidas 2.png"))); // NOI18N
        jMenuItem2.setText("Registro de Pedidos");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        fileMenu.add(jMenuItem2);

        menuBar.add(fileMenu);

        GestiondeUsuariomenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/íconos/gestion-de-usuarios.png"))); // NOI18N
        GestiondeUsuariomenu.setText("Gestión de Usuarios");
        GestiondeUsuariomenu.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        GestiondeUsuariomenu.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                GestiondeUsuariomenuMouseClicked(evt);
            }
        });
        GestiondeUsuariomenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                GestiondeUsuariomenuActionPerformed(evt);
            }
        });

        jMenuItem4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jMenuItem4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/íconos/contador-de-salidas.png"))); // NOI18N
        jMenuItem4.setText("Control de Usuario");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        GestiondeUsuariomenu.add(jMenuItem4);

        jMenuItem1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jMenuItem1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/íconos/nuevo-usuario.png"))); // NOI18N
        jMenuItem1.setText("Crear Nuevo Usuario");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        GestiondeUsuariomenu.add(jMenuItem1);

        menuBar.add(GestiondeUsuariomenu);

        helpMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/íconos/libro.png"))); // NOI18N
        helpMenu.setMnemonic('h');
        helpMenu.setText("Reportes");
        helpMenu.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N

        contentMenuItem.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        contentMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/íconos/estadistica.png"))); // NOI18N
        contentMenuItem.setMnemonic('c');
        contentMenuItem.setText("Generar reporte");
        contentMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                contentMenuItemActionPerformed(evt);
            }
        });
        helpMenu.add(contentMenuItem);

        jMenuItem5.setText("jMenuItem5");
        helpMenu.add(jMenuItem5);

        jMenuItem8.setText("jMenuItem8");
        helpMenu.add(jMenuItem8);

        menuBar.add(helpMenu);

        jMenu1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/íconos/engranaje.png"))); // NOI18N
        jMenu1.setText("Configuración");
        jMenu1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N

        jMenuItem6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jMenuItem6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/íconos/boton-de-informacion.png"))); // NOI18N
        jMenuItem6.setText("Ayuda / Instrucciones de uso");
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem6ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem6);

        jMenuItem7.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jMenuItem7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/íconos/cerrar-sesion.png"))); // NOI18N
        jMenuItem7.setText("Cerrar Sesión");
        jMenuItem7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem7ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem7);

        menuBar.add(jMenu1);

        setJMenuBar(menuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(escritorio, javax.swing.GroupLayout.DEFAULT_SIZE, 1519, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(escritorio, javax.swing.GroupLayout.DEFAULT_SIZE, 870, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void openMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openMenuItemActionPerformed
        Seguimientodeenvios form = new Seguimientodeenvios();
        escritorio.add(form);
        form.setVisible(true);

    }//GEN-LAST:event_openMenuItemActionPerformed

    private void GestiondeUsuariomenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_GestiondeUsuariomenuActionPerformed

    }//GEN-LAST:event_GestiondeUsuariomenuActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        RegistroUsuarios form = new RegistroUsuarios();
        form.setVisible(true);
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void cutMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cutMenuItemActionPerformed
this.dispose();
        Controlinventario1 form = new Controlinventario1();
        escritorio.add(form);
        form.setVisible(true);
    }//GEN-LAST:event_cutMenuItemActionPerformed

    private void saveMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveMenuItemActionPerformed
        Historialpedidos_1 form = new Historialpedidos_1();
        escritorio.add(form);
        form.setVisible(true);
    }//GEN-LAST:event_saveMenuItemActionPerformed

    private void jMenuItem7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem7ActionPerformed
        this.dispose();

        InicioSesion form = new InicioSesion();
        form.setVisible(true);
    }//GEN-LAST:event_jMenuItem7ActionPerformed

    private void jMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem6ActionPerformed
        Ayudaeistrucciones form = new Ayudaeistrucciones();
        escritorio.add(form);
        form.setVisible(true);
    }//GEN-LAST:event_jMenuItem6ActionPerformed

    private void GestiondeUsuariomenuMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_GestiondeUsuariomenuMouseClicked

    }//GEN-LAST:event_GestiondeUsuariomenuMouseClicked

    private void fileMenuMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fileMenuMouseClicked

    }//GEN-LAST:event_fileMenuMouseClicked

    private void fileMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fileMenuActionPerformed

    }//GEN-LAST:event_fileMenuActionPerformed

    private void fileMenuMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fileMenuMousePressed

    }//GEN-LAST:event_fileMenuMousePressed

    private void fileMenuMenuSelected(javax.swing.event.MenuEvent evt) {//GEN-FIRST:event_fileMenuMenuSelected

    }//GEN-LAST:event_fileMenuMenuSelected

    private void fileMenuMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fileMenuMouseReleased

    }//GEN-LAST:event_fileMenuMouseReleased

    private void jMenu2MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenu2MouseReleased
        RegistroCliente form = new RegistroCliente();
        escritorio.add(form);
        form.setVisible(true);
    }//GEN-LAST:event_jMenu2MouseReleased

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        RegistroCliente form = new RegistroCliente();
        escritorio.add(form);
        form.setVisible(true);
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void contentMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_contentMenuItemActionPerformed
        Document documento = new Document();

        try {

            String ruta = System.getProperty("user.home");

            // System.out.println(ruta); // Puedes descomentar para verificar la ruta
            // Asegúrate de que la ruta de la imagen sea accesible.
            // Considera cargarla como recurso del classpath si está dentro de tu JAR.
            // Image header = Image.getInstance(ReporteProductos.class.getResource("/imagenes/Opcion 3 (1).png")); // Ejemplo cargando desde classpath (ajusta la extensión si no es png)
            Image header = Image.getInstance("src/imagenes/Opcion 3 (1).png"); // Mantengo tu ruta original pero ten en cuenta el problema de ruta relativa

            header.scaleToFit(650, 1000);

            header.setAlignment(Chunk.ALIGN_CENTER);

            Paragraph parrafo = new Paragraph();

            parrafo.setAlignment(Paragraph.ALIGN_CENTER);

            parrafo.add("Formato creado por Chaos © \n\n");

            // Asegúrate de que la fuente "Tahoma" esté disponible o usa una fuente estándar
            parrafo.setFont(FontFactory.getFont("Segoe UI", 18, Font.BOLD, BaseColor.DARK_GRAY));

            parrafo.add("Reporte de Productos Registrados \n\n"); // Título más descriptivo

            PdfPTable tabla = new PdfPTable(5); // 5 columnas

            // Añadir encabezados de tabla
            tabla.addCell("ID");

            tabla.addCell("Nombre del producto");

            tabla.addCell("Precio");

            tabla.addCell("Stock");

            tabla.addCell("Codigo Articulo");

            documento.open();

            documento.add(header);

            documento.add(parrafo);

            // Usando try-with-resources para asegurar el cierre de los recursos de la base de datos
            try (Connection cn = DriverManager.getConnection("jdbc:mysql://localhost/chaos_app", "root", ""); PreparedStatement pst = cn.prepareStatement("SELECT id, nombre, precio, stock, codigo_articulo FROM catalogo"); // Selecciona columnas específicas
                     ResultSet rs = pst.executeQuery()) {

                // Iterar sobre el ResultSet usando un simple while
                while (rs.next()) {

                    // Usa nombres de columna en lugar de índices numéricos (verifica los nombres exactos en tu BD)
                    tabla.addCell(rs.getString("id"));

                    tabla.addCell(rs.getString("nombre"));

                    tabla.addCell(rs.getString("precio"));

                    tabla.addCell(rs.getString("stock"));

                    tabla.addCell(rs.getString("codigo_articulo")); // Asumo este nombre de columna

                }

                // Solo añadir la tabla si hay datos (el while no se ejecuta si no hay)
                // o si quieres añadirla siempre, puedes mover esto fuera del try-with-resources
                documento.add(tabla);

            } catch (SQLException e) {

                System.out.println("Error en conexión a la base de datos: " + e.getMessage());

                JOptionPane.showMessageDialog(null, "Error al obtener datos de la base de datos.", "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);

            }

            documento.close();

            JOptionPane.showMessageDialog(null, "Reporte PDF creado exitosamente en: " + ruta + "\\Desktop\\Reporte_Productos.pdf");

        } catch (FileNotFoundException e) {

            System.out.println("Error al crear el archivo PDF: " + e.getMessage());

            JOptionPane.showMessageDialog(null, "Error al crear el archivo PDF. Verifica si la ruta es válida o si el archivo ya está abierto.", "Error de Archivo", JOptionPane.ERROR_MESSAGE);

        } catch (DocumentException e) {

            System.out.println("Error al generar el contenido del PDF: " + e.getMessage());

            JOptionPane.showMessageDialog(null, "Error al generar el contenido del reporte PDF.", "Error de PDF", JOptionPane.ERROR_MESSAGE);

        } catch (IOException e) {

            System.out.println("Error al cargar la imagen: " + e.getMessage());

            JOptionPane.showMessageDialog(null, "Error al cargar la imagen del encabezado. Verifica la ruta.", "Error de Imagen", JOptionPane.ERROR_MESSAGE);

        } catch (Exception e) { // Captura cualquier otra excepción inesperada

            System.out.println("Ocurrió un error inesperado: " + e.getMessage());

            JOptionPane.showMessageDialog(null, "Ocurrió un error inesperado al crear el reporte.", "Error Desconocido", JOptionPane.ERROR_MESSAGE);

        }
    }//GEN-LAST:event_contentMenuItemActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        ControlUsuarios form = new ControlUsuarios();
        escritorio.add(form);
        form.setVisible(true);
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed

    }//GEN-LAST:event_jMenuItem2ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Ventanamultiple.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Ventanamultiple.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Ventanamultiple.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Ventanamultiple.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Ventanamultiple().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu GestiondeUsuariomenu;
    private javax.swing.JMenuItem contentMenuItem;
    private javax.swing.JMenuItem cutMenuItem;
    private javax.swing.JMenu editMenu;
    private javax.swing.JDesktopPane escritorio;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItem7;
    private javax.swing.JMenuItem jMenuItem8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenuItem openMenuItem;
    private javax.swing.JMenuItem saveMenuItem;
    // End of variables declaration//GEN-END:variables

}
