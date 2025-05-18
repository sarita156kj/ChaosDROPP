package ventanas;

import java.awt.Container;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.PlainDocument;

public class Seguimientodeenvios extends javax.swing.JInternalFrame {

    // Información de la conexión a la base de datos (¡Reemplaza con tus datos!)
    private static final String DB_URL = "jdbc:mysql://localhost:3306/chaos_app";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    // Opciones para el ComboBox de Estado
    private final String[] estadosPosibles = {"En almacén", "De camino", "Entregado", "Devuelto al Remitente", "Cancelado"};
    private final List<String> codigosPedido; // Para almacenar todos los códigos de pedido

    public Seguimientodeenvios() {
        initComponents();
        codigosPedido = new ArrayList<>();
        // Inicializar el TextField de Códigos de Pedido con autocompletado
        cargarCodigosPedido();
        autoCompletarTextField(txtCodigoPedido, codigosPedido);

        // Inicializar el ComboBox de Estado
        DefaultComboBoxModel<String> estadoModel = new DefaultComboBoxModel<>(estadosPosibles);
        cboxtestado.setModel(estadoModel);
        cboxtestado.setEnabled(false); // Inicialmente deshabilitado}

        // ActionListener para el botón Buscar
        buscarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String codigoPedido = txtCodigoPedido.getText().trim();
                if (!codigoPedido.isEmpty()) {
                    buscarPedido(codigoPedido);
                } else {
                    JOptionPane.showMessageDialog(Seguimientodeenvios.this, "Por favor, ingresa un código de pedido.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        // Asegúrate de que el JPanel principal esté configurado como contentPane
        setContentPane(jPanel2);
    }

    private void buscarPedido(String codigoPedido) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD); PreparedStatement pstmt = conn.prepareStatement("SELECT estado, fechaEntrega FROM pedidos WHERE Idpedido = ?")) {
            pstmt.setString(1, codigoPedido);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String estadoPedido = rs.getString("estado");
                txtfechadeentrega.setText(rs.getString("fechaEntrega")); // Asume que fechaEntrega es un String
                // Seleccionar la opción correspondiente en el ComboBox de Estado
                for (String estado : estadosPosibles) {
                    if (estado.equalsIgnoreCase(estadoPedido)) {
                        cboxtestado.setSelectedItem(estado);
                        break;
                    }
                }
                cboxtestado.setEnabled(true); // Habilitar el ComboBox de Estado después de la búsqueda
            } else {
                cboxtestado.setSelectedIndex(-1); // Deseleccionar cualquier opción
                cboxtestado.setEnabled(false); // Deshabilitar si no se encuentra el pedido
                txtfechadeentrega.setText("");
                JOptionPane.showMessageDialog(this, "No se encontró ningún pedido con el código: " + codigoPedido, "Información", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al buscar el pedido: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } finally {
            // El JTextField siempre permitirá borrar y realizar nuevas consultas
            txtCodigoPedido.requestFocusInWindow(); // Opcional: enfocar el JTextField para la siguiente entrada
        }
    }

    private void autoCompletarTextField(JTextField textField, List<String> items) {
        textField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                actualizarSugerencias();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                actualizarSugerencias();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                // No es necesario para PlainDocument
            }

            private void actualizarSugerencias() {
                String textoIngresado = textField.getText();
                if (textoIngresado.isEmpty()) {
                    // No mostrar sugerencias si el campo está vacío
                    return;
                }
                List<String> sugerenciasFiltradas = new ArrayList<>();
                for (String item : items) {
                    if (item.toLowerCase().startsWith(textoIngresado.toLowerCase())) {
                        sugerenciasFiltradas.add(item);
                    }
                }
                mostrarSugerencias(textField, sugerenciasFiltradas);
            }
        });
    }

    private void mostrarSugerencias(JTextField textField, List<String> sugerencias) {
        if (sugerencias.isEmpty()) {
            return;
        }

        JList<String> listaSugerencias = new JList<>(sugerencias.toArray(new String[0]));
        JPopupMenu popupMenu = new JPopupMenu();
        popupMenu.setBorder(BorderFactory.createLineBorder(java.awt.Color.BLACK));
        popupMenu.add(new JScrollPane(listaSugerencias));
        popupMenu.show(textField, 0, textField.getHeight());

        listaSugerencias.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                textField.setText(listaSugerencias.getSelectedValue());
                popupMenu.setVisible(false);
            }
        });

        // Cerrar el popup si se pierde el foco del textField
        textField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                popupMenu.setVisible(false);
                textField.removeFocusListener(this); // Evitar múltiples listeners
            }
        });
    }

    private List<String> getSortedItems(JComboBox<String> comboBox) {
        List<String> items = new ArrayList<>();
        for (int i = 0; i < comboBox.getItemCount(); i++) {
            items.add(comboBox.getItemAt(i));
        }
        Collections.sort(items, String.CASE_INSENSITIVE_ORDER);
        return items;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // </editor-fold>
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txtfechadeentrega = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        buscarButton = new javax.swing.JButton();
        cboxtestado = new javax.swing.JComboBox<>();
        txtCodigoPedido = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();

        setClosable(true);
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setIconifiable(true);
        setMaximizable(true);

        jPanel1.setBackground(new java.awt.Color(0, 0, 0));
        jPanel1.setPreferredSize(new java.awt.Dimension(1550, 920));

        jPanel2.setBackground(new java.awt.Color(0, 0, 0));
        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255), 6));

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Segoe UI", 3, 36)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Seguimiento de envíos");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 2, 18)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Administra la información de tu pedido ");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 2, 18)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("Código del pedido:");

        jLabel6.setFont(new java.awt.Font("Segoe UI", 2, 20)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Estado:");

        txtfechadeentrega.setFont(new java.awt.Font("Segoe UI", 2, 16)); // NOI18N

        jLabel7.setFont(new java.awt.Font("Segoe UI", 2, 20)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("Fecha de entrega:");

        buscarButton.setBackground(new java.awt.Color(0, 0, 0));
        buscarButton.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        buscarButton.setForeground(new java.awt.Color(255, 255, 255));
        buscarButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/íconos/buscar 2.png"))); // NOI18N
        buscarButton.setText("Buscar");
        buscarButton.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255), 3));
        buscarButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buscarButtonActionPerformed(evt);
            }
        });

        cboxtestado.setFont(new java.awt.Font("Segoe UI", 2, 16)); // NOI18N
        cboxtestado.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "En almacén", "De camino", "Entregado", "Devuelto al Remitente", "Cancelado" }));
        cboxtestado.setEnabled(false);

        txtCodigoPedido.setFont(new java.awt.Font("Segoe UI", 2, 16)); // NOI18N
        txtCodigoPedido.setText("CH-1109");
        txtCodigoPedido.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCodigoPedidoActionPerformed(evt);
            }
        });

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/envio-removebg-preview.png"))); // NOI18N
        jLabel3.setText("jLabel3");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(50, 50, 50)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel5)
                                    .addComponent(txtCodigoPedido, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(buscarButton, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGap(316, 316, 316))
                                        .addComponent(cboxtestado, javax.swing.GroupLayout.PREFERRED_SIZE, 518, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtfechadeentrega, javax.swing.GroupLayout.PREFERRED_SIZE, 518, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 17, Short.MAX_VALUE))))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(127, 127, 127)
                        .addComponent(jLabel2))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(160, 160, 160)
                        .addComponent(jLabel4)))
                .addGap(68, 68, 68))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(252, 252, 252)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addGap(18, 18, 18)
                .addComponent(jLabel4)
                .addGap(27, 27, 27)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtCodigoPedido, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(buscarButton, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cboxtestado, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27)
                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtfechadeentrega, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(58, Short.MAX_VALUE))
        );

        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/Opcion 7 (1,1).png"))); // NOI18N
        jLabel8.setText("jLabel8");

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/1259.jpg"))); // NOI18N
        jLabel1.setText("jLabel1");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(75, 75, 75)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 77, Short.MAX_VALUE)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 856, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(281, 281, 281))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(jLabel8)
                .addGap(45, 45, 45)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 1954, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 6, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void buscarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buscarButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_buscarButtonActionPerformed

    private void txtCodigoPedidoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCodigoPedidoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCodigoPedidoActionPerformed

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
            java.util.logging.Logger.getLogger(Seguimientodeenvios.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Seguimientodeenvios.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Seguimientodeenvios.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Seguimientodeenvios.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Seguimientodeenvios().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buscarButton;
    private javax.swing.JComboBox<String> cboxtestado;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JTextField txtCodigoPedido;
    private javax.swing.JTextField txtfechadeentrega;
    // End of variables declaration//GEN-END:variables

private void cargarCodigosPedido() {
    codigosPedido.clear(); // Clear the list before re-populating
    try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD); 
         Statement stmt = conn.createStatement(); 
         ResultSet rs = stmt.executeQuery("SELECT Idpedido FROM pedidos")) {
        while (rs.next()) {
            codigosPedido.add(rs.getString("Idpedido"));
        }
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this, "Error al cargar los códigos de pedido: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        ex.printStackTrace();
    }
}

}
