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
    private final JTextField txtcodigoarticulo;
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

        mostrarProductos(); // Cargar productos al iniciar
    }

    public static void mostrarProductos() {
       DefaultTableModel model = new DefaultTableModel();

        // Agrega las columnas a la tabla
        model.addColumn("ID");
        model.addColumn("Nombre");
        model.addColumn("Precio");
        model.addColumn("Categoría");
        model.addColumn("Stock");
        model.addColumn("Código Artículo");

        String query = "SELECT * FROM catalogo";  // Asegúrate de que el nombre de la tabla sea correcto

        try (Connection con = Conexion_DB.getConexion()) {

            if (con == null) {
                JOptionPane.showMessageDialog(null, "Error: No se pudo conectar a la base de datos.");
                return;
            }

            try (PreparedStatement stmt = con.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {

                model.setRowCount(0);  // Limpiar filas anteriores

                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getDouble("precio"),
                        rs.getString("categoria"),
                        rs.getInt("stock"),
                        rs.getString("codigo_articulo")
                    });
                }
            }

            // Aquí usas el JTable de tu formulario (ya debería estar arrastrado del diseñador)
            if (tablaCatalogo != null) {
                tablaCatalogo.setModel(model);  // Asigna el modelo a la tabla
            } else {
                JOptionPane.showMessageDialog(null, "Error: No se pudo encontrar el JTable.");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al cargar los productos: " + e.getMessage());
        }
    }

    // Método para inicializar los componentes gráficos del formulario (NetBeans lo genera automáticamente)
    private void initComponents() {
        // Aquí va el código generado por NetBeans para inicializar los componentes gráficos
        // Asegúrate de que la tablaCatalogo sea un componente de tu JFrame
    }


    private void btnNuevoActionPerformed(ActionEvent evt) {
        limpiarCampos();
        idProductoSeleccionado = -1;
    }

    private void btnGuardarActionPerformed(ActionEvent evt) {
        String nombre = txtnarticulo.getText();
        String codigoArticulo = txtcodigoarticulo.getText();
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

            String query = "INSERT INTO catalogo (nombre, codigo_articulo, categoria, precio, stock) VALUES (?, ?, ?, ?, ?)";

            try (Connection con = Conexion_DB.getConexion(); PreparedStatement ps = con.prepareStatement(query)) {

                ps.setString(1, nombre);
                ps.setString(2, codigoArticulo);
                ps.setString(3, categoria);
                ps.setDouble(4, precio);
                ps.setInt(5, stock);

                ps.executeUpdate();
                JOptionPane.showMessageDialog(null, "Producto guardado correctamente.");
                limpiarCampos();
                mostrarProductos();

            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "El precio o el stock no son válidos.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al guardar el producto: " + e.getMessage());
        }
    }

    private void btnEditarActionPerformed(ActionEvent evt) {
        if (idProductoSeleccionado == -1) {
            JOptionPane.showMessageDialog(null, "Debes buscar un producto primero.");
            return;
        }

        String nombre = txtnarticulo.getText();
        String codigoArticulo = txtcodigoarticulo.getText();
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
                ps.setString(2, codigoArticulo);
                ps.setString(3, categoria);
                ps.setDouble(4, precio);
                ps.setInt(5, stock);
                ps.setInt(6, idProductoSeleccionado);

                ps.executeUpdate();
                JOptionPane.showMessageDialog(null, "Producto actualizado correctamente.");
                limpiarCampos();
                mostrarProductos();

            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "El precio o el stock no son válidos.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al actualizar el producto: " + e.getMessage());
        }
    }

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
                        txtcodigoarticulo.setText(rs.getString("codigo_articulo"));
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
                    mostrarProductos();
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Error al eliminar el producto: " + e.getMessage());
            }
        }
    }

    private void btnSalirActionPerformed(ActionEvent evt) {
        System.exit(0);
    }

    private void limpiarCampos() {
        txtnarticulo.setText("");
        txtcodigoarticulo.setText("");
        txtprecio.setText("");
        txtstock.setText("");
        jComboCategoria.setSelectedIndex(0);
        idProductoSeleccionado = -1;
    }

    public static class Conexion_DB {
        public static Connection getConexion() throws SQLException {
            // Sustituir con tu configuración real:
            // return DriverManager.getConnection("jdbc:mysql://localhost:3306/tu_base", "usuario", "contraseña");
            return null;
        }
    }
}
