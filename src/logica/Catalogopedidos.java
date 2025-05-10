package logica;

import java.awt.event.ActionEvent;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.sql.SQLException;

public class Catalogopedidos extends javax.swing.JFrame {

    // Componentes gráficos (botones, campos de texto, JComboBox, JTable)
    private javax.swing.JButton btnNuevo;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnEditar;
    private javax.swing.JButton btnBuscar;
    private javax.swing.JButton btnEliminar;
    private javax.swing.JComboBox<String> jComboCategoria;
    private javax.swing.JTextField txtnarticulo;
    private javax.swing.JTextField txtcodigoarticulo;
    private javax.swing.JTextField txtprecio;
    private javax.swing.JTextField txtstock;
    private javax.swing.JTable tablaCatalogo;
    private javax.swing.JTextField txtbuscar; // Campo para la búsqueda

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
        btnEliminar.addActionListener(this::btnEliminarActionPerformed);

        // Configura la JTable para mostrar los productos
        tablaCatalogo = new javax.swing.JTable();
        JScrollPane scrollPane = new JScrollPane(tablaCatalogo);
        tablaCatalogo.setModel(new DefaultTableModel(
                new Object[][]{},
                new String[]{
                    "ID", "Nombre", "Categoría", "Precio", "Stock", "Código_articulo"
                }
        ));

        // Agregar el JScrollPane que contiene la tabla al panel del formulario
    }

    private void mostrarProductos() {
        DefaultTableModel modelo = (DefaultTableModel) tablaCatalogo.getModel();
        modelo.setRowCount(0); // Limpiar la tabla antes de llenarla

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
                    rs.getString("codigo_articulo") // Asegúrate de que el nombre de la columna es correcto
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
        String codigoArticulo = txtcodigoarticulo.getText();

        // Validación de campos vacíos
        if (nombre.isEmpty() || precioStr.isEmpty() || stockStr.isEmpty() || codigoArticulo.isEmpty()) {
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
                String query = "INSERT INTO catalogo (nombre, categoria, precio, stock, codigo_articulo) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement ps = con.prepareStatement(query);
                ps.setString(1, nombre);
                ps.setString(2, categoria);
                ps.setDouble(3, precio);
                ps.setInt(4, stock);
                ps.setString(5, codigoArticulo);
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
            String codigoArticulo = (String) tablaCatalogo.getValueAt(filaSeleccionada, 5);

            // Rellenar los campos con los datos seleccionados
            txtnarticulo.setText(nombre);
            jComboCategoria.setSelectedItem(categoria);
            txtprecio.setText(String.valueOf(precio));
            txtstock.setText(String.valueOf(stock));
            txtcodigoarticulo.setText(codigoArticulo);
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
                        rs.getString("codigo_articulo")
                    };
                    modelo.addRow(row); // Agregar fila de resultados a la tabla
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al buscar productos: " + e.getMessage());
            }
        }
    }

    // 5. Función para el botón "Eliminar"
    private void btnEliminarActionPerformed(ActionEvent e) {
        if (idProductoSeleccionado == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto para eliminar.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "¿Estás seguro de eliminar este producto?", "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try (Connection con = Conexion_DB.getConexion()) {
            String query = "DELETE FROM catalogo WHERE id=?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, idProductoSeleccionado);
            int filasAfectadas = ps.executeUpdate();

            if (filasAfectadas > 0) {
                JOptionPane.showMessageDialog(this, "Producto eliminado correctamente.");
                limpiarCampos();
                idProductoSeleccionado = -1;
                mostrarProductos(); // Refrescar la tabla
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo eliminar el producto.");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al eliminar el producto: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> new Catalogopedidos().setVisible(true));
    }
}
