package ventanas;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import logica.Conexion_Chaos;

public class Historialpedidos_1 extends javax.swing.JInternalFrame {

    public Historialpedidos_1() {
        initComponents();
        cargarHistorialPedidos();
        configurarListeners();
    }

    private void configurarListeners() {
        btnBuscarCliente.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                String textoBusqueda = txtNombreClienteBuscar.getText().trim();
                if (!textoBusqueda.isEmpty()) {
                    cargarHistorialPedidosPorNombreApellido(textoBusqueda);
                } else {
                    cargarHistorialPedidos();
                }
            }
        });

        btnEliminarPedido.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
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
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel2.setBackground(new java.awt.Color(0, 0, 0));

        jPanel1.setBackground(new java.awt.Color(0, 0, 0));
        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255), 4));

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
        btnEliminarPedido.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        btnEliminarPedido.setForeground(new java.awt.Color(255, 255, 255));
        btnEliminarPedido.setText("Eliminar pedido");
        btnEliminarPedido.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarPedidoActionPerformed(evt);
            }
        });

        btnBuscarCliente.setBackground(new java.awt.Color(0, 0, 0));
        btnBuscarCliente.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        btnBuscarCliente.setForeground(new java.awt.Color(255, 255, 255));
        btnBuscarCliente.setText("Buscar ");
        btnBuscarCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarClienteActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Segoe UI", 2, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Nombre del Cliente:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(txtNombreClienteBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 254, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnBuscarCliente)
                        .addGap(18, 18, 18)
                        .addComponent(btnEliminarPedido)
                        .addGap(44, 44, 44))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1385, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(23, Short.MAX_VALUE))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtNombreClienteBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBuscarCliente)
                    .addComponent(btnEliminarPedido))
                .addGap(38, 38, 38)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 491, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(21, Short.MAX_VALUE))
        );

        jLabel2.setFont(new java.awt.Font("Segoe UI Black", 1, 48)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Historial Pedidos");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 2, 24)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Bienvenido al historial de pedidos - ¿Qué deseas ver? ");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(55, 55, 55)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(jLabel2)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(228, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addGap(18, 18, 18)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(158, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 6, Short.MAX_VALUE))
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
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tablaHistorialPedidos;
    private javax.swing.JTextField txtNombreClienteBuscar;
    // End of variables declaration//GEN-END:variables
}
