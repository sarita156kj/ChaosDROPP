package ventanas;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import logica.Conexion_Chaos;

public class Historialpedidos_1 extends javax.swing.JInternalFrame {

    private String idPedidoSeleccionado = null;

    public Historialpedidos_1() {
        initComponents();
        cargarHistorialPedidos();
        configurarListeners();
        // Agregar listener para cuando se selecciona una fila en la tabla
        tablaHistorialPedidos.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                int filaSeleccionada = tablaHistorialPedidos.getSelectedRow();
                if (filaSeleccionada >= 0) {
                    // Asumimos que la columna del ID del pedido es la cuarta (índice 3)
                    idPedidoSeleccionado = (String) tablaHistorialPedidos.getValueAt(filaSeleccionada, 3);
                    // Asumimos que la columna del estado es la onceava (índice 10)
                    String estadoPedido = (String) tablaHistorialPedidos.getValueAt(filaSeleccionada, 10);
                    cboxtestado.setSelectedItem(estadoPedido);
                } else {
                    idPedidoSeleccionado = null;
                    cboxtestado.setSelectedIndex(0); // Resetear el JComboBox
                }
            }
        });
    }

    private void configurarListeners() {
        btnBuscarCliente.addActionListener(evt -> {
            String textoBusqueda = txtNombreClienteBuscar.getText().trim();
            if (!textoBusqueda.isEmpty()) {
                cargarHistorialPedidosPorNombreApellido(textoBusqueda);
            } else {
                cargarHistorialPedidos();
            }
        });

        btnEliminarPedido.addActionListener(evt -> {
            int filaSeleccionada = tablaHistorialPedidos.getSelectedRow();

            if (filaSeleccionada >= 0) {
                String idPedidoEliminar = (String) tablaHistorialPedidos.getValueAt(filaSeleccionada, 3);

                int confirmacion = JOptionPane.showConfirmDialog(null, "¿Está seguro de que desea eliminar el pedido con ID: " + idPedidoEliminar + "?", "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);

                if (confirmacion == JOptionPane.YES_OPTION) {
                    eliminarPedido(idPedidoEliminar);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Por favor, seleccione un pedido para eliminar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            }
        });

        txtactualizar.addActionListener(evt -> {
            if (idPedidoSeleccionado != null) {
                String nuevoEstado = (String) cboxtestado.getSelectedItem();
                if (!nuevoEstado.equals("Seleccionar")) {
                } else {
                    JOptionPane.showMessageDialog(null, "Por favor, seleccione un estado para actualizar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Por favor, seleccione un pedido de la tabla para actualizar su estado.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            }
        });
    }

    private void cargarHistorialPedidos() {
        DefaultTableModel modeloTabla = new DefaultTableModel();
        modeloTabla.addColumn("Nombre Cliente");
        modeloTabla.addColumn("Apellido Cliente");
        modeloTabla.addColumn("Teléfono");
        modeloTabla.addColumn("ID Pedido");
        modeloTabla.addColumn("Fecha Pedido");
        modeloTabla.addColumn("Fecha Entrega");
        modeloTabla.addColumn("Provincia");
        modeloTabla.addColumn("Dirección");
        modeloTabla.addColumn("Descripción");
        modeloTabla.addColumn("Tipo Pago");
        modeloTabla.addColumn("Estado");
        modeloTabla.addColumn("Monto Total");

        tablaHistorialPedidos.setModel(modeloTabla);

        String query = "SELECT * FROM pedidos";

        try (Connection con = Conexion_Chaos.conectar(); PreparedStatement pstmt = con.prepareStatement(query); ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Object[] fila = new Object[12];
                fila[0] = rs.getString("nombreCliente");
                fila[1] = rs.getString("apellidoCliente");
                fila[2] = rs.getString("telefonoCliente");
                fila[3] = rs.getString("idPedido");
                fila[4] = rs.getString("fechaPedido");
                fila[5] = rs.getString("fechaEntrega");
                fila[6] = rs.getString("provincia");
                fila[7] = rs.getString("direccion");
                fila[8] = rs.getString("descripcion");
                fila[9] = rs.getString("tipoPago");
                fila[10] = rs.getString("estado");
                fila[11] = rs.getString("total_pago");

                modeloTabla.addRow(fila);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al cargar el historial de pedidos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void cargarHistorialPedidosPorNombreApellido(String textoBusqueda) {
        DefaultTableModel modeloTabla = new DefaultTableModel();
        modeloTabla.addColumn("Nombre Cliente");
        modeloTabla.addColumn("Apellido Cliente");
        modeloTabla.addColumn("Teléfono");
        modeloTabla.addColumn("ID Pedido");
        modeloTabla.addColumn("Fecha Pedido");
        modeloTabla.addColumn("Fecha Entrega");
        modeloTabla.addColumn("Provincia");
        modeloTabla.addColumn("Dirección");
        modeloTabla.addColumn("Descripción");
        modeloTabla.addColumn("Tipo Pago");
        modeloTabla.addColumn("Estado");
        modeloTabla.addColumn("Monto Total");

        tablaHistorialPedidos.setModel(modeloTabla);

        // Modificamos la consulta SQL para buscar por nombre O apellido (insensible a mayúsculas/minúsculas)
        String query = "SELECT * FROM pedidos WHERE LOWER(nombreCliente) LIKE ? OR LOWER(apellidoCliente) LIKE ?";

        try (Connection con = Conexion_Chaos.conectar(); PreparedStatement pstmt = con.prepareStatement(query)) {

            // Convertimos el texto de búsqueda a minúsculas y agregamos el comodín '%'
            String busquedaLower = "%" + textoBusqueda.toLowerCase() + "%";
            pstmt.setString(1, busquedaLower);
            pstmt.setString(2, busquedaLower);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Object[] fila = new Object[12];
                fila[0] = rs.getString("nombreCliente");
                fila[1] = rs.getString("apellidoCliente");
                fila[2] = rs.getString("telefonoCliente");
                fila[3] = rs.getString("idPedido");
                fila[4] = rs.getString("fechaPedido");
                fila[5] = rs.getString("fechaEntrega");
                fila[6] = rs.getString("provincia");
                fila[7] = rs.getString("direccion");
                fila[8] = rs.getString("descripcion");
                fila[9] = rs.getString("tipoPago");
                fila[10] = rs.getString("estado");
                fila[11] = rs.getString("total_pago");
                modeloTabla.addRow(fila);
            }

            if (modeloTabla.getRowCount() == 0) {
                JOptionPane.showMessageDialog(null, "No se encontraron pedidos para el cliente con el nombre o apellido: " + textoBusqueda, "Información", JOptionPane.INFORMATION_MESSAGE);
                cargarHistorialPedidos(); // Recargar todo el historial si no se encuentran resultados
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al buscar pedidos por nombre o apellido: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void eliminarPedido(String idPedido) {
        String query = "DELETE FROM pedidos WHERE idPedido = ?";

        try (Connection con = Conexion_Chaos.conectar(); PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setString(1, idPedido);
            int filasAfectadas = pstmt.executeUpdate();

            if (filasAfectadas > 0) {
                JOptionPane.showMessageDialog(null, "Pedido con ID: " + idPedido + " eliminado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                cargarHistorialPedidos();
            } else {
                JOptionPane.showMessageDialog(null, "No se encontró ningún pedido con el ID: " + idPedido + ".", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al eliminar el pedido: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
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

        jPanel2 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaHistorialPedidos = new javax.swing.JTable();
        txtNombreClienteBuscar = new javax.swing.JTextField();
        btnEliminarPedido = new javax.swing.JButton();
        btnBuscarCliente = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        btnLimpiar = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        cboxtestado = new javax.swing.JComboBox<>();
        txtactualizar = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();

        setClosable(true);
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setIconifiable(true);
        setMaximizable(true);

        jPanel2.setBackground(new java.awt.Color(0, 0, 0));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(0, 0, 0));
        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255), 4));

        tablaHistorialPedidos.setBackground(new java.awt.Color(0, 0, 0));
        tablaHistorialPedidos.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255), 2));
        tablaHistorialPedidos.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        tablaHistorialPedidos.setForeground(new java.awt.Color(255, 255, 255));
        tablaHistorialPedidos.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tablaHistorialPedidos);

        btnEliminarPedido.setBackground(new java.awt.Color(0, 0, 0));
        btnEliminarPedido.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnEliminarPedido.setForeground(new java.awt.Color(255, 255, 255));
        btnEliminarPedido.setIcon(new javax.swing.ImageIcon(getClass().getResource("/íconos/boton-x.png"))); // NOI18N
        btnEliminarPedido.setText(" Eliminar ");
        btnEliminarPedido.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255), 3));
        btnEliminarPedido.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarPedidoActionPerformed(evt);
            }
        });

        btnBuscarCliente.setBackground(new java.awt.Color(0, 0, 0));
        btnBuscarCliente.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnBuscarCliente.setForeground(new java.awt.Color(255, 255, 255));
        btnBuscarCliente.setIcon(new javax.swing.ImageIcon(getClass().getResource("/íconos/buscar 2.png"))); // NOI18N
        btnBuscarCliente.setText("Buscar ");
        btnBuscarCliente.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255), 3));
        btnBuscarCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarClienteActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Segoe UI", 2, 20)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Nombre del Cliente:");

        btnLimpiar.setBackground(new java.awt.Color(0, 0, 0));
        btnLimpiar.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnLimpiar.setForeground(new java.awt.Color(255, 255, 255));
        btnLimpiar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/íconos/Limpiar.png"))); // NOI18N
        btnLimpiar.setText("Limpiar");
        btnLimpiar.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255), 3));
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
                .addGap(22, 22, 22)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(txtNombreClienteBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 334, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnBuscarCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnLimpiar, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel1))
                        .addGap(18, 18, 18)
                        .addComponent(btnEliminarPedido, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1337, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(22, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtNombreClienteBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBuscarCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnLimpiar, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnEliminarPedido, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 309, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(27, Short.MAX_VALUE))
        );

        jPanel2.add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 140, -1, 460));

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Segoe UI Black", 1, 48)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Historial Pedidos");
        jPanel2.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 30, -1, 60));

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Segoe UI", 2, 24)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Bienvenido al historial de pedidos - ¿Qué deseas ver? ");
        jPanel2.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 90, -1, -1));

        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/Opcion 7 (1,1).png"))); // NOI18N
        jLabel5.setText("jLabel5");
        jPanel2.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(1350, 20, 180, -1));

        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/íconos/icons8-historia-de-la-actividad-100.png"))); // NOI18N
        jLabel6.setText("jLabel6");
        jPanel2.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 30, 100, -1));

        jPanel3.setBackground(new java.awt.Color(0, 0, 0));
        jPanel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255), 5));

        jLabel7.setFont(new java.awt.Font("Segoe UI", 2, 18)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("Estado:");

        cboxtestado.setFont(new java.awt.Font("Segoe UI", 2, 16)); // NOI18N
        cboxtestado.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Seleccionar", "En almacén", "De camino", "Entregado", "Devuelto al Remitente", "Cancelado" }));

        txtactualizar.setBackground(new java.awt.Color(0, 0, 0));
        txtactualizar.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        txtactualizar.setForeground(new java.awt.Color(255, 255, 255));
        txtactualizar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/íconos/histo.png"))); // NOI18N
        txtactualizar.setText("Actualizar");
        txtactualizar.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255), 4));
        txtactualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtactualizarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(cboxtestado, javax.swing.GroupLayout.PREFERRED_SIZE, 261, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 99, Short.MAX_VALUE)
                        .addComponent(txtactualizar, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(47, 47, 47))))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cboxtestado, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(txtactualizar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(1, 1, 1)))
                .addGap(26, 26, 26))
        );

        jPanel2.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 620, 610, 130));

        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/empresas-courier-hacer-envios.jpg"))); // NOI18N
        jLabel4.setText("jLabel4");
        jPanel2.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(-10, -30, 1540, 830));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnBuscarClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarClienteActionPerformed
        String textoBusqueda = txtNombreClienteBuscar.getText().trim();
        if (!textoBusqueda.isEmpty()) {
            cargarHistorialPedidosPorNombreApellido(textoBusqueda);
        } else {
            JOptionPane.showMessageDialog(null, "Por favor, ingrese parte del nombre o apellido del cliente a buscar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            cargarHistorialPedidos(); // Recargar todo el historial si no hay búsqueda
        }
    }//GEN-LAST:event_btnBuscarClienteActionPerformed

    private void btnEliminarPedidoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarPedidoActionPerformed
        int filaSeleccionada = tablaHistorialPedidos.getSelectedRow();

        if (filaSeleccionada >= 0) {
            // Asumimos que la columna del ID del pedido es la cuarta (índice 3)
            String idPedidoEliminar = (String) tablaHistorialPedidos.getValueAt(filaSeleccionada, 3);

            int confirmacion = JOptionPane.showConfirmDialog(null, "¿Está seguro de que desea eliminar el pedido con ID: " + idPedidoEliminar + "?", "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);

            if (confirmacion == JOptionPane.YES_OPTION) {
                eliminarPedido(idPedidoEliminar);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Por favor, seleccione un pedido para eliminar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_btnEliminarPedidoActionPerformed

    private void txtactualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtactualizarActionPerformed
if (idPedidoSeleccionado != null) {
String nuevoEstado = (String) cboxtestado.getSelectedItem();
if (!nuevoEstado.equals("Seleccionar")) {
String query = "UPDATE pedidos SET estado = ? WHERE idPedido = ?";
try (Connection con = Conexion_Chaos.conectar(); PreparedStatement pstmt = con.prepareStatement(query)) {
pstmt.setString(1, nuevoEstado);
pstmt.setString(2, idPedidoSeleccionado);

int filasAfectadas = pstmt.executeUpdate();

if (filasAfectadas > 0) {
JOptionPane.showMessageDialog(null, "El estado del pedido con ID: " + idPedidoSeleccionado + " se ha actualizado a: " + nuevoEstado + ".", "Éxito", JOptionPane.INFORMATION_MESSAGE);
cargarHistorialPedidos(); // Recargar la tabla para mostrar los cambios
} else {
JOptionPane.showMessageDialog(null, "No se pudo actualizar el estado del pedido con ID: " + idPedidoSeleccionado + ".", "Error", JOptionPane.ERROR_MESSAGE);
}

} catch (SQLException e) {
JOptionPane.showMessageDialog(null, "Error al actualizar el estado del pedido: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
e.printStackTrace();
} finally {
idPedidoSeleccionado = null; // Resetear el ID seleccionado después de la actualización
cboxtestado.setSelectedIndex(0); // Resetear el JComboBox después de la actualización
}
} else {
JOptionPane.showMessageDialog(null, "Por favor, seleccione un estado para actualizar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
}
} else {
JOptionPane.showMessageDialog(null, "Por favor, seleccione un pedido de la tabla para actualizar su estado.", "Advertencia", JOptionPane.WARNING_MESSAGE);
}
    }//GEN-LAST:event_txtactualizarActionPerformed

    private void btnLimpiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimpiarActionPerformed
        // TODO add your handling code here:
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
            java.util.logging.Logger.getLogger(Historialpedidos_1.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Historialpedidos_1.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Historialpedidos_1.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Historialpedidos_1.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Historialpedidos_1().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBuscarCliente;
    private javax.swing.JButton btnEliminarPedido;
    private javax.swing.JButton btnLimpiar;
    private javax.swing.JComboBox<String> cboxtestado;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tablaHistorialPedidos;
    private javax.swing.JTextField txtNombreClienteBuscar;
    private javax.swing.JButton txtactualizar;
    // End of variables declaration//GEN-END:variables
}
