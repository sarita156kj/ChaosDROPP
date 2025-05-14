package ventanas;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.ListSelectionModel;

public class ControlUsuarios extends javax.swing.JFrame {

    private String dbUrl = "jdbc:mysql://localhost:3306/chaos_app?serverTimezone=UTC";
    private String dbUser = "root";
    private String dbPassword = "";

    private int usuarioSeleccionadoId = -1;

    private String obtenerNombreRol(int idRol) {
        String nombreRol = "";
        String sql = "SELECT nombre_rol FROM roles WHERE id_rol = ?";
        try (Connection conexion = DriverManager.getConnection(dbUrl, dbUser, dbPassword); PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setInt(1, idRol); // **CORRECCIÓN: Mover aquí**
            ResultSet resultado = sentencia.executeQuery();
            if (resultado.next()) {
                nombreRol = resultado.getString("nombre_rol").trim(); // Añadido .trim()
                System.out.println("obtenerNombreRol - ID Recibido: " + idRol + ", Nombre Rol Encontrado: " + nombreRol); // Debug
            } else {
                System.out.println("obtenerNombreRol - No se encontró rol para ID: " + idRol); // Debug
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al obtener el nombre del rol: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        System.out.println("obtenerNombreRol - Retornando: " + nombreRol); // Debug
        return nombreRol;
    }

    private void cargarDatosUsuarios() {
        String[] nombresColumnas = {"ID", "Nombre", "Apellido", "Usuario", "Correo", "Teléfono", "Contraseña", "ID Rol"}; // **DEFINE AQUÍ LOS NOMBRES DESEADOS**
        DefaultTableModel modeloTabla = new DefaultTableModel(null, nombresColumnas); // **CREA EL MODELO CON LOS NOMBRES**
        usuariosTable.setModel(modeloTabla);

        try (Connection conexion = DriverManager.getConnection(dbUrl, dbUser, dbPassword); java.sql.Statement sentencia = conexion.createStatement(); ResultSet resultado = sentencia.executeQuery("SELECT * FROM usuarios")) {

            int numColumnas = resultado.getMetaData().getColumnCount();
            System.out.println("Número de columnas obtenidas de la base de datos: " + numColumnas);

            while (resultado.next()) {
                Object[] fila = new Object[numColumnas];
                for (int i = 1; i <= numColumnas; i++) {
                    fila[i - 1] = resultado.getObject(i);
                }
                modeloTabla.addRow(fila);
            }
            System.out.println("Número de columnas en usuariosTable: " + usuariosTable.getColumnCount());
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar los usuarios: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public ControlUsuarios() {
        initComponents();
        cargarDatosUsuarios();

        usuariosTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && usuariosTable.getSelectedRow() != -1) {
                int filaSeleccionada = usuariosTable.getSelectedRow();

                Object idUsuarioObj = usuariosTable.getValueAt(filaSeleccionada, 0); // ID está en la columna 0
                if (idUsuarioObj != null) {
                    usuarioSeleccionadoId = Integer.parseInt(idUsuarioObj.toString());
                    System.out.println("ID del usuario seleccionado: " + usuarioSeleccionadoId); // Para depuración
                } else {
                    usuarioSeleccionadoId = -1; // Resetear si no hay ID válido
                }
                txtid.setText(usuariosTable.getValueAt(filaSeleccionada, 0).toString());
                txtnombre.setText(usuariosTable.getValueAt(filaSeleccionada, 1).toString());
                txtapellido.setText(usuariosTable.getValueAt(filaSeleccionada, 2).toString());
                txtusuario.setText(usuariosTable.getValueAt(filaSeleccionada, 3).toString());
                txtcorreo.setText(usuariosTable.getValueAt(filaSeleccionada, 4).toString());
                txtContrasena.setText(usuariosTable.getValueAt(filaSeleccionada, 7).toString());

                // **CORRECCIÓN:** Asumiendo que 'id_rol' es la octava columna (índice 7)
                // Si no es así, debes cambiar el índice al correcto después de revisar la consola.
                if (usuariosTable.getColumnCount() > 7) { // Asegurarse de que la columna exista
                    Object idRolObj = usuariosTable.getValueAt(filaSeleccionada, 7);
                    if (idRolObj != null) {
                        int idRol = Integer.parseInt(idRolObj.toString());
                        String nombreRol = obtenerNombreRol(idRol);
                        System.out.println("ListSelectionListener - Nombre Rol a Seleccionar: " + nombreRol); // Debug
                        cbxRol.setSelectedItem(nombreRol);
                        System.out.println("ListSelectionListener - Elemento Seleccionado en cbxRol: " + cbxRol.getSelectedItem()); // Debug
                    } else {
                        cbxRol.setSelectedItem("Seleccionar"); // Establecer un valor por defecto si no hay id_rol
                    }
                } else {
                    System.out.println("Advertencia: La tabla no tiene suficientes columnas para acceder al id_rol (índice 7).");
                    cbxRol.setSelectedItem("Seleccionar"); // Establecer un valor por defecto si la columna no existe
                }
            } else {
                usuarioSeleccionadoId = -1;
            }
        });

        btnEditar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (usuarioSeleccionadoId == -1) {
                    JOptionPane.showMessageDialog(ControlUsuarios.this, "Por favor, seleccione un usuario para editar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                String nombre = txtnombre.getText();
                String apellido = txtapellido.getText();
                String nombreUsuario = txtusuario.getText();
                String correo = txtcorreo.getText();
                String contrasena = new String(txtContrasena.getText());
                String tipoEmpleadoSeleccionado = (String) cbxRol.getSelectedItem();
                int idRol = -1;
                if (tipoEmpleadoSeleccionado.equals("Administrador")) {
                    idRol = 1; // Asume que el ID para Administrador es 1
                } else if (tipoEmpleadoSeleccionado.equals("Vendedor")) {
                    idRol = 2; // Asume que el ID para Vendedor es 2
                }

                if (idRol == -1 && !tipoEmpleadoSeleccionado.equals("Seleccionar")) {
                    JOptionPane.showMessageDialog(ControlUsuarios.this, "Seleccione un tipo de empleado válido.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                    return;
                } else if (tipoEmpleadoSeleccionado.equals("Seleccionar")) {
                    // Decide si permitir o no guardar sin un rol seleccionado
                    // Por ahora, no permitimos guardar sin un rol
                    JOptionPane.showMessageDialog(ControlUsuarios.this, "Por favor, seleccione un tipo de empleado.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                String sql = "UPDATE usuarios SET nombre=?, apellido=?, usuario=?, correo=?, contrasena=?, id_rol=? WHERE id=?";
                try (Connection conexion = DriverManager.getConnection(dbUrl, dbUser, dbPassword); PreparedStatement sentencia = conexion.prepareStatement(sql)) {
                    sentencia.setString(1, nombre);
                    sentencia.setString(2, apellido);
                    sentencia.setString(3, nombreUsuario);
                    sentencia.setString(4, correo);
                    sentencia.setString(5, contrasena);
                    sentencia.setInt(6, idRol);
                    sentencia.setInt(7, usuarioSeleccionadoId);

                    int filasAfectadas = sentencia.executeUpdate();

                    if (filasAfectadas > 0) {
                        JOptionPane.showMessageDialog(ControlUsuarios.this, "Usuario actualizado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                        cargarDatosUsuarios(); // Recargar la tabla
                        // Opcional: Limpiar los campos de texto después de editar
                        txtid.setText("");
                        txtnombre.setText("");
                        txtapellido.setText("");
                        txtusuario.setText("");
                        txtcorreo.setText("");
                        txtContrasena.setText("");
                        cbxRol.setSelectedIndex(0);
                        usuarioSeleccionadoId = -1; // Resetear el ID seleccionado
                    } else {
                        JOptionPane.showMessageDialog(ControlUsuarios.this, "No se pudo actualizar el usuario.", "Error", JOptionPane.ERROR_MESSAGE);
                    }

                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(ControlUsuarios.this, "Error al actualizar el usuario: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnElimianr.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (usuarioSeleccionadoId == -1) {
                    JOptionPane.showMessageDialog(ControlUsuarios.this, "Por favor, seleccione un usuario para eliminar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                int confirmacion = JOptionPane.showConfirmDialog(ControlUsuarios.this, "¿Está seguro de que desea eliminar este usuario?", "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);

                if (confirmacion == JOptionPane.YES_OPTION) {
                    String sql = "DELETE FROM usuarios WHERE id = ?";
                    try (Connection conexion = DriverManager.getConnection(dbUrl, dbUser, dbPassword); PreparedStatement sentencia = conexion.prepareStatement(sql)) {
                        sentencia.setInt(1, usuarioSeleccionadoId);

                        int filasAfectadas = sentencia.executeUpdate();

                        if (filasAfectadas > 0) {
                            JOptionPane.showMessageDialog(ControlUsuarios.this, "Usuario eliminado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                            cargarDatosUsuarios(); // Recargar la tabla
                            // Limpiar los campos de texto después de eliminar
                            txtid.setText("");
                            txtnombre.setText("");
                            txtapellido.setText("");
                            txtusuario.setText("");
                            txtcorreo.setText("");
                            txtContrasena.setText("");
                            cbxRol.setSelectedIndex(0);
                            usuarioSeleccionadoId = -1; // Resetear el ID seleccionado
                        } else {
                            JOptionPane.showMessageDialog(ControlUsuarios.this, "No se pudo eliminar el usuario.", "Error", JOptionPane.ERROR_MESSAGE);
                        }

                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(ControlUsuarios.this, "Error al eliminar el usuario: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        btncrearnuevousuario.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                RegistroUsuarios registroFrame = new RegistroUsuarios();
                registroFrame.setVisible(true);
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        btnElimianr = new javax.swing.JButton();
        btnEditar = new javax.swing.JButton();
        btncrearnuevousuario = new javax.swing.JButton();
        txtnombre = new javax.swing.JTextField();
        txtapellido = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtusuario = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtcorreo = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtContrasena = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        cbxRol = new javax.swing.JComboBox<>();
        txtid = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        usuariosTable = new javax.swing.JTable();
        jLabel9 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        btnLimpiar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(0, 0, 0));

        btnElimianr.setText("Eliminar ");
        btnElimianr.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnElimianrActionPerformed(evt);
            }
        });

        btnEditar.setText("Editar");
        btnEditar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditarActionPerformed(evt);
            }
        });

        btncrearnuevousuario.setText("Crear Nuevo Usuario");
        btncrearnuevousuario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btncrearnuevousuarioActionPerformed(evt);
            }
        });

        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Nombre:");

        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Apellido:");

        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Nombre de Usuario:");

        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Correo:");

        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("Contraseña");

        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Tipo de Empleado:");

        cbxRol.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Seleccionar", "Administrador", "Vendedor" }));

        txtid.setEnabled(false);
        txtid.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtidActionPerformed(evt);
            }
        });

        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("ID:");

        jPanel2.setBackground(new java.awt.Color(204, 204, 204));

        usuariosTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(usuariosTable);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 714, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(21, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 321, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(19, Short.MAX_VALUE))
        );

        jLabel9.setFont(new java.awt.Font("Segoe UI Black", 1, 45)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setText("GESTIÓN USUARIOS");

        jLabel8.setFont(new java.awt.Font("Segoe UI", 2, 25)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("Cree, modifique y elimine usuarios desde aquí.");

        btnLimpiar.setText("Limpiar");
        btnLimpiar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLimpiarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(87, 87, 87)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 650, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jLabel8))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(50, 50, 50)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btncrearnuevousuario, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnEditar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnElimianr, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnLimpiar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtid, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(45, 45, 45)
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel3)
                            .addComponent(txtusuario, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtcorreo, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtnombre, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(31, 31, 31)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtapellido, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtContrasena, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(cbxRol, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel6))))))
                .addContainerGap(263, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(44, 44, 44)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(60, 60, 60)
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(35, 35, 35)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addGap(41, 41, 41)
                        .addComponent(btnEditar, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnLimpiar, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(14, 14, 14)
                        .addComponent(btnElimianr, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(54, 54, 54)
                        .addComponent(btncrearnuevousuario, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(50, 50, 50)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7)
                            .addComponent(jLabel1)
                            .addComponent(txtnombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtapellido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtusuario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtcorreo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbxRol, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtContrasena, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(259, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btncrearnuevousuarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btncrearnuevousuarioActionPerformed
        btncrearnuevousuarioActionPerformed(evt);
    }//GEN-LAST:event_btncrearnuevousuarioActionPerformed

    private void txtidActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtidActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtidActionPerformed

    private void btnElimianrActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnElimianrActionPerformed
        btnElimianrActionPerformed(evt);
    }//GEN-LAST:event_btnElimianrActionPerformed

    private void btnEditarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditarActionPerformed
        btnEditarActionPerformed(evt);
    }//GEN-LAST:event_btnEditarActionPerformed

    private void btnLimpiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimpiarActionPerformed
        txtnombre.setText("");
        txtapellido.setText("");
        txtusuario.setText("");
        txtContrasena.setText("");
        txtcorreo.setText("");
        txtid.setText("");
        cbxRol.setSelectedIndex(0);
    }//GEN-LAST:event_btnLimpiarActionPerformed

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
            java.util.logging.Logger.getLogger(ControlUsuarios.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ControlUsuarios.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ControlUsuarios.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ControlUsuarios.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ControlUsuarios().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnEditar;
    private javax.swing.JButton btnElimianr;
    private javax.swing.JButton btnLimpiar;
    private javax.swing.JButton btncrearnuevousuario;
    private javax.swing.JComboBox<String> cbxRol;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField txtContrasena;
    private javax.swing.JTextField txtapellido;
    private javax.swing.JTextField txtcorreo;
    private javax.swing.JTextField txtid;
    private javax.swing.JTextField txtnombre;
    private javax.swing.JTextField txtusuario;
    private javax.swing.JTable usuariosTable;
    // End of variables declaration//GEN-END:variables
}
