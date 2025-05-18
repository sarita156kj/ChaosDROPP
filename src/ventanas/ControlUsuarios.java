package ventanas;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class ControlUsuarios extends javax.swing.JInternalFrame {

    private String dbUrl = "jdbc:mysql://localhost:3306/chaos_app?serverTimezone=UTC";
    private String dbUser = "root";
    private String dbPassword = "";

    private int usuarioSeleccionadoId = -1;

    private String obtenerNombreRol(int idRol) {
        String nombreRol = "";
        String sql = "SELECT nombre_rol FROM roles WHERE id_rol = ?";
        try (Connection conexion = DriverManager.getConnection(dbUrl, dbUser, dbPassword); PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setInt(1, idRol);
            ResultSet resultado = sentencia.executeQuery();
            if (resultado.next()) {
                nombreRol = resultado.getString("nombre_rol").trim();
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

    private Integer obtenerIdRol(String nombreRolSeleccionado) {
        Integer idRol = null;
        String sql = "SELECT id_rol FROM roles WHERE nombre_rol = ?";
        try (Connection conexion = DriverManager.getConnection(dbUrl, dbUser, dbPassword); PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setString(1, nombreRolSeleccionado);
            ResultSet resultado = sentencia.executeQuery();
            if (resultado.next()) {
                idRol = resultado.getInt("id_rol");
                System.out.println("obtenerIdRol - Nombre Rol Recibido: " + nombreRolSeleccionado + ", ID Rol Encontrado: " + idRol); // Debug
            } else {
                System.out.println("obtenerIdRol - No se encontró ID para el rol: " + nombreRolSeleccionado); // Debug
                JOptionPane.showMessageDialog(this, "No se encontró el rol: " + nombreRolSeleccionado + " en la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al obtener el ID del rol: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return idRol;
    }

    private void cargarDatosUsuarios() {
        String[] nombresColumnas = {"ID", "Nombre", "Apellido", "Usuario", "Correo", "Teléfono", "Contraseña", "ID Rol"};
        DefaultTableModel modeloTabla = new DefaultTableModel(null, nombresColumnas);
        usuariosTable.setModel(modeloTabla);

        try (Connection conexion = DriverManager.getConnection(dbUrl, dbUser, dbPassword); Statement sentencia = conexion.createStatement(); ResultSet resultado = sentencia.executeQuery("SELECT * FROM usuarios")) {

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
        cargarRolesEnComboBox(); // Cargar los roles al iniciar

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
                txtcelular.setText(usuariosTable.getValueAt(filaSeleccionada, 5).toString());

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
                String celular = txtcelular.getText();
                String tipoEmpleadoSeleccionado = (String) cbxRol.getSelectedItem();
                Integer idRol = obtenerIdRol(tipoEmpleadoSeleccionado);

                if (idRol == null && !tipoEmpleadoSeleccionado.equals("Seleccionar")) {
                    JOptionPane.showMessageDialog(ControlUsuarios.this, "El tipo de empleado seleccionado no es válido.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                    return;
                } else if (tipoEmpleadoSeleccionado.equals("Seleccionar")) {
                    JOptionPane.showMessageDialog(ControlUsuarios.this, "Por favor, seleccione un tipo de empleado.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                String sql = "UPDATE usuarios SET nombre=?, apellido=?, usuario=?, correo=?, telefono=?, id_rol=? WHERE id=?";
                try (Connection conexion = DriverManager.getConnection(dbUrl, dbUser, dbPassword); PreparedStatement sentencia = conexion.prepareStatement(sql)) {
                    sentencia.setString(1, nombre);
                    sentencia.setString(2, apellido);
                    sentencia.setString(3, nombreUsuario);
                    sentencia.setString(4, correo);
                    sentencia.setString(5, celular);
                    sentencia.setInt(6, idRol); // Corregido el índice
                    sentencia.setInt(7, usuarioSeleccionadoId); // Corregido el índice

                    int filasAfectadas = sentencia.executeUpdate();

                    if (filasAfectadas > 0) {
                        JOptionPane.showMessageDialog(ControlUsuarios.this, "Usuario actualizado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                        cargarDatosUsuarios();
                        txtid.setText("");
                        txtnombre.setText("");
                        txtapellido.setText("");
                        txtusuario.setText("");
                        txtcorreo.setText("");
                        txtcelular.setText("");
                        cbxRol.setSelectedIndex(0);
                        usuarioSeleccionadoId = -1;
                    } else {
                        JOptionPane.showMessageDialog(ControlUsuarios.this, "No se pudo actualizar el usuario.", "Error", JOptionPane.ERROR_MESSAGE);
                    }

                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(ControlUsuarios.this, "Error al actualizar el usuario: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnElimianr.addActionListener(new ActionListener() { // **CORRECCIÓN:** Nombre del botón
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
                            txtcelular.setText("");
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

    private void cargarRolesEnComboBox() {
        cbxRol.removeAllItems();
        cbxRol.addItem("Seleccionar"); // Valor por defecto
        String sql = "SELECT nombre_rol FROM roles";
        try (Connection conexion = DriverManager.getConnection(dbUrl, dbUser, dbPassword); Statement sentencia = conexion.createStatement(); ResultSet resultado = sentencia.executeQuery(sql)) {
            while (resultado.next()) {
                cbxRol.addItem(resultado.getString("nombre_rol").trim());
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar los roles: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        usuariosTable = new javax.swing.JTable();
        jLabel11 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        txtid = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        txtnombre = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtapellido = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtusuario = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtcorreo = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        txtcelular = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        cbxRol = new javax.swing.JComboBox<>();
        jLabel13 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        btnEditar = new javax.swing.JButton();
        btnLimpiar = new javax.swing.JButton();
        btnElimianr = new javax.swing.JButton();
        btncrearnuevousuario = new javax.swing.JButton();
        jLabel14 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(0, 0, 0));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(0, 0, 0));
        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255), 3));

        usuariosTable.setBackground(new java.awt.Color(0, 0, 0));
        usuariosTable.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255), 2));
        usuariosTable.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        usuariosTable.setForeground(new java.awt.Color(255, 255, 255));
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

        jLabel11.setFont(new java.awt.Font("Segoe UI", 3, 36)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/icons8-añadir-usuario-masculino-30.png"))); // NOI18N
        jLabel11.setText("Usuarios");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap(27, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 714, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addGap(282, 282, 282))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 336, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21))
        );

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 170, -1, 440));

        jLabel9.setFont(new java.awt.Font("Segoe UI Black", 1, 45)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setText("GESTIÓN USUARIOS");
        jPanel1.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 30, 650, -1));

        jLabel8.setFont(new java.awt.Font("Segoe UI", 2, 25)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("Cree, modifique y elimine usuarios desde aquí.");
        jPanel1.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 100, -1, 30));

        jPanel3.setBackground(new java.awt.Color(0, 0, 0));
        jPanel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255), 3));

        jLabel7.setFont(new java.awt.Font("Segoe UI", 2, 18)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("ID:");

        txtid.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        txtid.setEnabled(false);
        txtid.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtidActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Segoe UI", 2, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Nombre:");

        txtnombre.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N

        jLabel2.setFont(new java.awt.Font("Segoe UI", 2, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Apellido:");

        txtapellido.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N

        jLabel3.setFont(new java.awt.Font("Segoe UI", 2, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Nombre de Usuario:");

        txtusuario.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N

        jLabel4.setFont(new java.awt.Font("Segoe UI", 2, 18)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Correo:");

        txtcorreo.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N

        jLabel10.setFont(new java.awt.Font("Segoe UI", 2, 18)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setText("Teléfono:");

        txtcelular.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N

        jLabel6.setFont(new java.awt.Font("Segoe UI", 2, 18)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Tipo de Empleado:");

        cbxRol.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        cbxRol.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Seleccionar", "Administrador", "Vendedor" }));

        jLabel13.setFont(new java.awt.Font("Segoe UI", 3, 36)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(255, 255, 255));
        jLabel13.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/icons8-añadir-usuario-masculino-30.png"))); // NOI18N
        jLabel13.setText(" Información Usuarios");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jLabel13)
                .addGap(106, 106, 106))
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(190, 190, 190)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtid, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel3Layout.createSequentialGroup()
                        .addGap(144, 144, 144)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtnombre))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel3Layout.createSequentialGroup()
                                .addGap(140, 140, 140)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel10)
                                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txtcelular, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 292, Short.MAX_VALUE)
                                    .addComponent(txtcorreo, javax.swing.GroupLayout.Alignment.TRAILING)))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(49, 49, 49)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addComponent(jLabel6)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(cbxRol, javax.swing.GroupLayout.PREFERRED_SIZE, 289, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addComponent(jLabel3)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtusuario, javax.swing.GroupLayout.PREFERRED_SIZE, 291, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtapellido, javax.swing.GroupLayout.PREFERRED_SIZE, 291, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(85, 85, 85))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jLabel13)
                .addGap(30, 30, 30)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtnombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtapellido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(13, 13, 13)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtusuario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtcorreo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(txtcelular, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(cbxRol, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(78, Short.MAX_VALUE))
        );

        jPanel1.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(870, 170, -1, 440));

        jPanel4.setBackground(new java.awt.Color(0, 0, 0));
        jPanel4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255), 3));

        btnEditar.setBackground(new java.awt.Color(0, 0, 0));
        btnEditar.setFont(new java.awt.Font("Segoe UI Black", 0, 18)); // NOI18N
        btnEditar.setForeground(new java.awt.Color(255, 255, 255));
        btnEditar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/íconos/usuario (2).png"))); // NOI18N
        btnEditar.setText("Editar");
        btnEditar.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255), 2));
        btnEditar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditarActionPerformed(evt);
            }
        });

        btnLimpiar.setBackground(new java.awt.Color(0, 0, 0));
        btnLimpiar.setFont(new java.awt.Font("Segoe UI Black", 0, 18)); // NOI18N
        btnLimpiar.setForeground(new java.awt.Color(255, 255, 255));
        btnLimpiar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/íconos/agregar-carpeta.png"))); // NOI18N
        btnLimpiar.setText("Nuevo");
        btnLimpiar.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255), 2));
        btnLimpiar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLimpiarActionPerformed(evt);
            }
        });

        btnElimianr.setBackground(new java.awt.Color(0, 0, 0));
        btnElimianr.setFont(new java.awt.Font("Segoe UI Black", 0, 18)); // NOI18N
        btnElimianr.setForeground(new java.awt.Color(255, 255, 255));
        btnElimianr.setIcon(new javax.swing.ImageIcon(getClass().getResource("/íconos/boton-x.png"))); // NOI18N
        btnElimianr.setText("Eliminar ");
        btnElimianr.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255), 2));
        btnElimianr.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnElimianrActionPerformed(evt);
            }
        });

        btncrearnuevousuario.setBackground(new java.awt.Color(0, 0, 0));
        btncrearnuevousuario.setFont(new java.awt.Font("Segoe UI Black", 0, 18)); // NOI18N
        btncrearnuevousuario.setForeground(new java.awt.Color(255, 255, 255));
        btncrearnuevousuario.setIcon(new javax.swing.ImageIcon(getClass().getResource("/íconos/crear-una-cuenta.png"))); // NOI18N
        btncrearnuevousuario.setText("Crear Nuevo Usuario");
        btncrearnuevousuario.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255), 2));
        btncrearnuevousuario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btncrearnuevousuarioActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addGap(91, 91, 91)
                .addComponent(btnEditar, javax.swing.GroupLayout.PREFERRED_SIZE, 234, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 95, Short.MAX_VALUE)
                .addComponent(btnLimpiar, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(84, 84, 84)
                .addComponent(btnElimianr, javax.swing.GroupLayout.PREFERRED_SIZE, 232, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(67, 67, 67)
                .addComponent(btncrearnuevousuario, javax.swing.GroupLayout.PREFERRED_SIZE, 279, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(67, 67, 67))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnLimpiar, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnElimianr, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btncrearnuevousuario, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnEditar, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(43, Short.MAX_VALUE))
        );

        jPanel1.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 660, 1390, -1));

        jLabel14.setIcon(new javax.swing.ImageIcon(getClass().getResource("/íconos/Opcion 7 (1,1).png"))); // NOI18N
        jLabel14.setText("jLabel14");
        jPanel1.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(1340, 40, 170, -1));

        jLabel12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/101851.jpg"))); // NOI18N
        jLabel12.setText("jLabel12");
        jPanel1.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, 960));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 1597, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField txtapellido;
    private javax.swing.JTextField txtcelular;
    private javax.swing.JTextField txtcorreo;
    private javax.swing.JTextField txtid;
    private javax.swing.JTextField txtnombre;
    private javax.swing.JTextField txtusuario;
    private javax.swing.JTable usuariosTable;
    // End of variables declaration//GEN-END:variables
}
