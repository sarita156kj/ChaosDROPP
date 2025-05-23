/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package ventanas;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import conexion.Conexion_DB;
import java.awt.Cursor;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import java.sql.ResultSet;
import java.sql.SQLException;

public class InicioSesion extends javax.swing.JFrame {

    /**
     * Creates new form InicioSesion
     */
    public InicioSesion() {
        initComponents();
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
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        lblRegistrate = new javax.swing.JLabel();
        btnAcceder = new javax.swing.JButton();
        txtContraseña1 = new javax.swing.JPasswordField();
        jLabel43 = new javax.swing.JLabel();
        txtcorreo1 = new javax.swing.JTextField();
        jLabel41 = new javax.swing.JLabel();
        txtusuario1 = new javax.swing.JTextField();
        jLabel40 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setForeground(new java.awt.Color(255, 255, 255));

        jLabel22.setFont(new java.awt.Font("Segoe UI Black", 1, 30)); // NOI18N
        jLabel22.setText("INICIA SESIÓN");

        jLabel23.setFont(new java.awt.Font("Segoe UI", 2, 18)); // NOI18N
        jLabel23.setText("¡Nos alegra tenerte de vuelta, amigo!");

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/Imagen salvaje IA.png"))); // NOI18N
        jLabel1.setText("jLabel1");

        lblRegistrate.setFont(new java.awt.Font("Segoe UI", 2, 16)); // NOI18N
        lblRegistrate.setText("¿No tienes cuenta? Regístrate.");
        lblRegistrate.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblRegistrateMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                lblRegistrateMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                lblRegistrateMouseExited(evt);
            }
        });

        btnAcceder.setBackground(new java.awt.Color(0, 0, 0));
        btnAcceder.setFont(new java.awt.Font("Segoe UI Black", 0, 16)); // NOI18N
        btnAcceder.setForeground(new java.awt.Color(255, 255, 255));
        btnAcceder.setText("Acceder");
        btnAcceder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAccederActionPerformed(evt);
            }
        });

        txtContraseña1.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        jLabel43.setFont(new java.awt.Font("Segoe UI", 3, 17)); // NOI18N
        jLabel43.setText("Contraseña:");

        txtcorreo1.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        jLabel41.setFont(new java.awt.Font("Segoe UI", 3, 17)); // NOI18N
        jLabel41.setText("Correo:");

        txtusuario1.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        jLabel40.setFont(new java.awt.Font("Segoe UI", 3, 17)); // NOI18N
        jLabel40.setText("Nombre de Usuario:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(56, 56, 56)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 303, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel22)
                    .addComponent(jLabel43)
                    .addComponent(txtContraseña1, javax.swing.GroupLayout.PREFERRED_SIZE, 252, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel40)
                    .addComponent(txtusuario1, javax.swing.GroupLayout.PREFERRED_SIZE, 252, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel41)
                    .addComponent(txtcorreo1, javax.swing.GroupLayout.PREFERRED_SIZE, 252, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(lblRegistrate)
                        .addGap(58, 58, 58)
                        .addComponent(btnAcceder, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 82, Short.MAX_VALUE)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 866, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(86, 86, 86)
                .addComponent(jLabel22)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel23)
                .addGap(59, 59, 59)
                .addComponent(jLabel40)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtusuario1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(31, 31, 31)
                .addComponent(jLabel41)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtcorreo1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(38, 38, 38)
                .addComponent(jLabel43)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtContraseña1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(70, 70, 70)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblRegistrate)
                    .addComponent(btnAcceder, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addGap(0, 40, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnAccederActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAccederActionPerformed
        // Obtener valores de las cajas de texto
        String usuario = txtusuario1.getText().trim();
        String correo = txtcorreo1.getText().trim();
        String contrasena = new String(txtContraseña1.getPassword()).trim();

        // Validar que no haya campos vacíos
        if (usuario.isEmpty() || correo.isEmpty() || contrasena.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, completa todos los campos.");
            return;
        }

        // Conectar a la base de datos y verificar credenciales
        try (Connection conn = (Connection) Conexion_DB.conectar()) {
            String sql = "SELECT * FROM usuarios WHERE usuario = ? AND correo = ? AND contrasena = ?";
            PreparedStatement ps = (PreparedStatement) conn.prepareStatement(sql);
            ps.setString(1, usuario);
            ps.setString(2, correo);
            ps.setString(3, contrasena);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                JOptionPane.showMessageDialog(this, "Inicio de sesión exitoso. ¡Bienvenido/a!");
                this.dispose(); // Cierra la ventana actual de login

// Abre la ventana principal
                new Ventanamultiple().setVisible(true);// Si encuentra un registro, el inicio de sesión es exitoso
                JOptionPane.showMessageDialog(this, "Inicio de sesión exitoso. ¡Bienvenido/a!");
                this.dispose(); // Cierra la ventana de inicio de sesión

                // Aquí puedes abrir tu menú principal o dashboard
                // new MenuPrincipal().setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Credenciales incorrectas o usuario no registrado.");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al conectar con la base de datos: " + e.getMessage());
        }

    }//GEN-LAST:event_btnAccederActionPerformed

    private void lblRegistrateMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblRegistrateMouseClicked
        // TODO add your handling code here:
        RegistroUsuarios inicio = new RegistroUsuarios();
        inicio.setVisible(true);
    }//GEN-LAST:event_lblRegistrateMouseClicked

    private void lblRegistrateMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblRegistrateMouseEntered
        // TODO add your handling code here:
        lblRegistrate.setText("<html><u><b>¿No tienes cuenta? Regístrate.</b></u></html>");
        lblRegistrate.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }//GEN-LAST:event_lblRegistrateMouseEntered

    private void lblRegistrateMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblRegistrateMouseExited
        // TODO add your handling code here:
        lblRegistrate.setText("<html><u>¿No tienes cuenta? Regístrate.</u></html>");
    }//GEN-LAST:event_lblRegistrateMouseExited

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
            java.util.logging.Logger.getLogger(InicioSesion.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(InicioSesion.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(InicioSesion.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(InicioSesion.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new InicioSesion().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAcceder;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lblRegistrate;
    private javax.swing.JPasswordField txtContraseña1;
    private javax.swing.JTextField txtcorreo1;
    private javax.swing.JTextField txtusuario1;
    // End of variables declaration//GEN-END:variables
}
