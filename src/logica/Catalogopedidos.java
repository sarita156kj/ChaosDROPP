
package logica;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class Catalogopedidos extends javax.swing.JFrame {
    // Componentes gráficos (botones, campos de texto, JComboBox, JTable)
    private javax.swing.JButton btnNuevo;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnEditar;
    private javax.swing.JButton btnBuscar;
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnSalir;
    private javax.swing.JComboBox<String> jComboCategoria;
    private javax.swing.JTextField txtnarticulo;
    private javax.swing.JTextField txtprecio;
    private javax.swing.JTextField txtstock;
    private javax.swing.JTable tablaCatalogo;
    private javax.swing.JTextField txtbuscar;

    private int idProductoSeleccionado = -1;

    public Catalogopedidos() {
        initComponents();
        mostrarProductos(); // Llamada a la función para cargar los productos
    }

    private void initComponents() {
        // Aquí debes configurar y crear los componentes, como los botones, campos de texto, etc.

        // Asigna los listeners de los botones
        btnGuardar.addActionListener(this::btnGuardarActionPerformed);
        btnNuevo.addActionListener(this::btnNuevoActionPerformed);
        btnEditar.addActionListener(this::btnEditarActionPerformed);
        btnBuscar.addActionListener(this::btnBuscarActionPerformed);
        btnCancelar.addActionListener(this::btnCancelarActionPerformed);
        btnSalir.addActionListener(this::btnSalirActionPerformed);

        // Configura la JTable para mostrar los productos
        tablaCatalogo = new javax.swing.JTable();
        JScrollPane scrollPane = new JScrollPane(tablaCatalogo);
        tablaCatalogo.setModel(new DefaultTableModel(
            new Object [][] {},
            new String [] {"ID", "Nombre", "Categoría", "Precio", "Stock", "Estado"}
        ));
        // Agregar el JScrollPane que contiene la tabla al panel del formulario
    }

    private void mostrarProductos() {
        DefaultTableModel modelo = (DefaultTableModel) tablaCatalogo.getModel();
        modelo.setRowCount(0); // Limpiar la tabla

        try (Connection con = Conexion_DB.getConexion()) {
            String query = "SELECT * FROM catalogo";
            PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Object[] row = {
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getString("categoria"),
                    rs.getDouble("precio"),
                    rs.getInt("stock"),
                    rs.getBoolean("estado")
                };
                modelo.addRow(row); // Agregar cada producto a la tabla
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar los productos: " + e.getMessage());
        }
    }

    private void limpiarCampos() {
        txtnarticulo.setText("");
        txtprecio.setText("");
        txtstock.setText("");
        jComboCategoria.setSelectedIndex(0); // Establecer la primera categoría como predeterminada
    }

    // 1. Función para el botón "Nuevo"
    private void btnNuevoActionPerformed(java.awt.event.ActionEvent evt) {
        limpiarCampos();  // Limpiar los campos para ingresar nuevo producto
    }

    // 2. Función para el botón "Guardar"
    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {
        String nombre = txtnarticulo.getText();
        String categoria = jComboCategoria.getSelectedItem().toString();
        String precioStr = txtprecio.getText();
        String stockStr = txtstock.getText();

        // Validación de campos vacíos
        if (nombre.isEmpty() || precioStr.isEmpty() || stockStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos los campos deben estar completos.");
            return;
        }

        try {
            // Convertir los datos a los tipos correctos
            double precio = Double.parseDouble(precioStr);
            int stock = Integer.parseInt(stockStr);

            // Si idProductoSeleccionado es -1, es un nuevo producto, si no, se actualizará el existente
            if (idProductoSeleccionado == -1) {
                // Insertar nuevo producto
                Connection con = Conexion_DB.getConexion();
                String query = "INSERT INTO catalogo (nombre, categoria, precio, stock, estado) VALUES (?, ?, ?, ?, 1)";
                PreparedStatement ps = con.prepareStatement(query);
                ps.setString(1, nombre);
                ps.setString(2, categoria);
                ps.setDouble(3, precio);
                ps.setInt(4, stock);
                ps.executeUpdate();

                JOptionPane.showMessageDialog(this, "Producto guardado correctamente.");
            } else {
                // Actualizar producto existente
                Connection con = Conexion_DB.getConexion();
                String query = "UPDATE catalogo SET nombre=?, categoria=?, precio=?, stock=? WHERE id=?";
                PreparedStatement ps = con.prepareStatement(query);
                ps.setString(1, nombre);
                ps.setString(2, categoria);
                ps.setDouble(3, precio);
                ps.setInt(4, stock);
                ps.setInt(5, idProductoSeleccionado);
                ps.executeUpdate();

                JOptionPane.showMessageDialog(this, "Producto actualizado correctamente.");
            }

            // Limpiar campos y recargar la tabla
            limpiarCampos();
            mostrarProductos();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Precio o stock inválidos.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al guardar el producto: " + e.getMessage());
        }
    }

    // 3. Función para el botón "Editar"
    private void btnEditarActionPerformed(java.awt.event.ActionEvent evt) {
        int filaSeleccionada = tablaCatalogo.getSelectedRow();
        if (filaSeleccionada != -1) {
            // Obtener los datos de la fila seleccionada
            idProductoSeleccionado = (int) tablaCatalogo.getValueAt(filaSeleccionada, 0);
            String nombre = (String) tablaCatalogo.getValueAt(filaSeleccionada, 1);
            String categoria = (String) tablaCatalogo.getValueAt(filaSeleccionada, 2);
            double precio = (double) tablaCatalogo.getValueAt(filaSeleccionada, 3);
            int stock = (int) tablaCatalogo.getValueAt(filaSeleccionada, 4);

            // Rellenar los campos con los datos seleccionados
            txtnarticulo.setText(nombre);
            jComboCategoria.setSelectedItem(categoria);
            txtprecio.setText(String.valueOf(precio));
            txtstock.setText(String.valueOf(stock));
        } else {
            JOptionPane.showMessageDialog(this, "Por favor, selecciona un producto para editar.");
        }
    }

    // 4. Función para el botón "Buscar"
    private void btnBuscarActionPerformed(java.awt.event.ActionEvent evt) {
        String buscarTexto = txtbuscar.getText().trim();
        DefaultTableModel modelo = (DefaultTableModel) tablaCatalogo.getModel();
        modelo.setRowCount(0); // Limpiar la tabla

        if (!buscarTexto.isEmpty()) {
            try (Connection con = Conexion_DB.getConexion()) {
                String query = "SELECT * FROM catalogo WHERE nombre LIKE ?";
                PreparedStatement ps = con.prepareStatement(query);
                ps.setString(1, "%" + buscarTexto + "%");
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    Object[] row = {
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("categoria"),
                        rs.getDouble("precio"),
                        rs.getInt("stock"),
                        rs.getBoolean("estado")
                    };
                    modelo.addRow(row); // Agregar fila de resultados a la tabla
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al buscar productos: " + e.getMessage());
            }
        } else {
            mostrarProductos(); // Mostrar todos los productos si no hay texto de búsqueda
        }
    }

    // 5. Función para el botón "Cancelar"
    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {
        limpiarCampos();
        idProductoSeleccionado = -1; // Resetear el id del producto seleccionado
    }

    // 6. Función para el botón "Salir"
    private void btnSalirActionPerformed(java.awt.event.ActionEvent evt) {
        System.exit(0); // Cerrar la aplicación
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> new Catalogopedidos().setVisible(true));
    }
    
}
