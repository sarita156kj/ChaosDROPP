package logica;

import java.awt.event.ActionEvent;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class Catalogopedidos {

    private static JTable tablaCatalogo;
    private final JTextField txtnarticulo;
    private final JTextField txtprecio;
    private final JTextField txtstock;
    private final JTextField txtcodigoarticulo; // Nuevo campo para código de artículo
    private final JComboBox<String> jComboCategoria;
    private final JButton btnNuevo;
    private final JButton btnGuardar;
    private final JButton btnEditar;
    private final JButton btnBuscar;
    private final JButton btnEliminar;
    private final JButton btnSalir;

    private int idProductoSeleccionado = -1;

    public Catalogopedidos() {

        tablaCatalogo = new JTable();
        txtnarticulo = new JTextField();
        txtcodigoarticulo = new JTextField();
        txtprecio = new JTextField();
        txtstock = new JTextField();
        jComboCategoria = new JComboBox<>();
        btnNuevo = new JButton("Nuevo");
        btnGuardar = new JButton("Guardar");
        btnEditar = new JButton("Editar");
        btnBuscar = new JButton("Buscar");
        btnEliminar = new JButton("Eliminar");
        btnSalir = new JButton("Salir");

        btnNuevo.addActionListener(e -> btnNuevoActionPerformed(e));
        btnGuardar.addActionListener(e -> btnGuardarActionPerformed(e));
        btnEditar.addActionListener(e -> btnEditarActionPerformed(e));
        btnBuscar.addActionListener(e -> btnBuscarActionPerformed(e));
        btnEliminar.addActionListener(e -> btnEliminarActionPerformed(e));
        btnSalir.addActionListener(e -> btnSalirActionPerformed(e));
    }

    // Método para mostrar los productos en la tabla
public static void mostrarProductos() {
    // Crear un modelo para la tabla
    DefaultTableModel model = new DefaultTableModel();
    model.addColumn("ID");
    model.addColumn("Nombre");
    model.addColumn("Código Artículo");
    model.addColumn("Categoría");
    model.addColumn("Precio");
    model.addColumn("Stock");

    // Consulta SQL para obtener los productos activos
    String query = "SELECT * FROM catalogo WHERE estado = 1"; // Filtrar solo los productos activos

    // Abrir la conexión correctamente
    try (Connection con = Conexion_DB.conectar(); 
         PreparedStatement stmt = con.prepareStatement(query); 
         ResultSet rs = stmt.executeQuery()) {

        // Limpiar las filas previas del modelo para evitar duplicados
        model.setRowCount(0);

        // Iterar sobre el ResultSet para llenar el modelo
        while (rs.next()) {
            model.addRow(new Object[]{
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("codigo_articulo"),
                rs.getString("categoria"),
                rs.getDouble("precio"),
                rs.getInt("stock")
            });
        }

        // Asignar el modelo a la tabla para mostrar los productos
        tablaCatalogo.setModel(model);

    } catch (SQLException e) {
        // Manejo de excepciones
        JOptionPane.showMessageDialog(null, "Error al cargar los productos: " + e.getMessage());
    }
}

    // Método para limpiar los campos para un nuevo producto
    private void btnNuevoActionPerformed(ActionEvent evt) {
        limpiarCampos();
        idProductoSeleccionado = -1; // Resetear el id seleccionado
    }

    // Método para guardar un producto
    private void btnGuardarActionPerformed(ActionEvent evt) {
        String nombre = txtnarticulo.getText();
        String codigoArticulo = txtcodigoarticulo.getText(); // Obtener el código de artículo
        String categoria = jComboCategoria.getSelectedItem().toString();
        String precioStr = txtprecio.getText();
        String stockStr = txtstock.getText();

        // Validación de campos vacíos
        if (nombre.isEmpty() || codigoArticulo.isEmpty() || precioStr.isEmpty() || stockStr.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Todos los campos deben estar completos.");
            return;
        }

        try {
            double precio = Double.parseDouble(precioStr);
            int stock = Integer.parseInt(stockStr);

            String query = "INSERT INTO catalogo (nombre, codigo_articulo, categoria, precio, stock, estado) VALUES (?, ?, ?, ?, ?, 1)";

            try (Connection con = Conexion_DB.getConexion(); PreparedStatement ps = con.prepareStatement(query)) {

                ps.setString(1, nombre);
                ps.setString(2, codigoArticulo); // Establecer el código de artículo
                ps.setString(3, categoria);
                ps.setDouble(4, precio);
                ps.setInt(5, stock);

                ps.executeUpdate();
                JOptionPane.showMessageDialog(null, "Producto guardado correctamente.");
                limpiarCampos();
                mostrarProductos(); // Actualizar la tabla

            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "El precio o el stock no son válidos.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al guardar el producto: " + e.getMessage());
        }
    }

    // Método para editar un producto
    private void btnEditarActionPerformed(ActionEvent evt) {
        if (idProductoSeleccionado == -1) {
            JOptionPane.showMessageDialog(null, "Debes buscar un producto primero.");
            return;
        }

        String nombre = txtnarticulo.getText();
        String codigoArticulo = txtcodigoarticulo.getText(); // Obtener el código de artículo
        String categoria = jComboCategoria.getSelectedItem().toString();
        String precioStr = txtprecio.getText();
        String stockStr = txtstock.getText();

        if (nombre.isEmpty() || codigoArticulo.isEmpty() || precioStr.isEmpty() || stockStr.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Todos los campos deben estar completos.");
            return;
        }

        try {
            double precio = Double.parseDouble(precioStr);
            int stock = Integer.parseInt(stockStr);

            String query = "UPDATE catalogo SET nombre=?, codigo_articulo=?, categoria=?, precio=?, stock=? WHERE id=?";

            try (Connection con = Conexion_DB.getConexion(); PreparedStatement ps = con.prepareStatement(query)) {

                ps.setString(1, nombre);
                ps.setString(2, codigoArticulo); // Establecer el código de artículo
                ps.setString(3, categoria);
                ps.setDouble(4, precio);
                ps.setInt(5, stock);
                ps.setInt(6, idProductoSeleccionado);

                ps.executeUpdate();
                JOptionPane.showMessageDialog(null, "Producto actualizado correctamente.");
                limpiarCampos();
                mostrarProductos(); // Actualizar la tabla

            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "El precio o el stock no son válidos.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al actualizar el producto: " + e.getMessage());
        }
    }

    // Método para buscar un producto
    private void btnBuscarActionPerformed(ActionEvent evt) {
        String nombre = txtnarticulo.getText();

        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(null, "El nombre del producto no puede estar vacío.");
            return;
        }

        try (Connection con = Conexion_DB.getConexion()) {
            String query = "SELECT * FROM catalogo WHERE nombre=?";
            try (PreparedStatement ps = con.prepareStatement(query)) {
                ps.setString(1, nombre);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        idProductoSeleccionado = rs.getInt("id");
                        txtnarticulo.setText(rs.getString("nombre"));
                        txtcodigoarticulo.setText(rs.getString("codigo_articulo")); // Rellenar el código de artículo
                        jComboCategoria.setSelectedItem(rs.getString("categoria"));
                        txtprecio.setText(String.valueOf(rs.getDouble("precio")));
                        txtstock.setText(String.valueOf(rs.getInt("stock")));
                    } else {
                        JOptionPane.showMessageDialog(null, "Producto no encontrado.");
                    }
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al buscar el producto: " + e.getMessage());
        }
    }

    // Método para eliminar un producto
    private void btnEliminarActionPerformed(ActionEvent evt) {
        if (idProductoSeleccionado == -1) {
            JOptionPane.showMessageDialog(null, "Debes buscar un producto primero.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(null, "¿Estás seguro de que deseas eliminar este producto?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection con = Conexion_DB.getConexion()) {
                String query = "DELETE FROM catalogo WHERE id=?";
                try (PreparedStatement ps = con.prepareStatement(query)) {
                    ps.setInt(1, idProductoSeleccionado);
                    ps.executeUpdate();
                    JOptionPane.showMessageDialog(null, "Producto eliminado correctamente.");
                    limpiarCampos();
                    mostrarProductos(); // Actualizar la tabla
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Error al eliminar el producto: " + e.getMessage());
            }
        }
    }

    // Método para salir (cerrar la ventana)
    private void btnSalirActionPerformed(ActionEvent evt) {
        System.exit(0);  // Esto cerrará la aplicación
    }

    // Método para limpiar los campos
    private void limpiarCampos() {
        txtnarticulo.setText("");
        txtcodigoarticulo.setText(""); // Limpiar el campo del código de artículo
        txtprecio.setText("");
        txtstock.setText("");
        jComboCategoria.setSelectedIndex(0);
        idProductoSeleccionado = -1;
    }

    // Método para obtener la conexión con la base de datos
    public static class Conexion_DB {

        public static Connection getConexion() throws SQLException {
            // Asegúrate de que este método devuelva una conexión válida a tu base de datos
            return null; // Reemplaza con la implementación real
        }

        private static Connection conectar() {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }
    }
}
