package logica;

import java.awt.event.ActionEvent;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import ventanas.Catalogopedidos;

public class VCatalogopedidos {

    private static JTable tablaCatalogo;
    private final JTextField txtnarticulo;
    private final JTextField txtprecio;
    private final JTextField txtstock;
    private final JTextField txtbuscar;
    private final JTextField txtcodigoarticulo;
    private final JComboBox<String> jComboCategoria;
    private final JButton btnNuevo;
    private final JButton btnGuardar;
    private final JButton btnEditar;
    private final JButton btnBuscar;
    private final JButton btnEliminar;
    private final JButton btnSalir;

    private int idProductoSeleccionado = -1;

    public VCatalogopedidos(javax.swing.JTextField txtbuscar) {
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
        btnGuardar.addActionListener(e -> {
            try {
                btnGuardarActionPerformed(e);
            } catch (SQLException ex) {
                Logger.getLogger(VCatalogopedidos.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        btnEditar.addActionListener(e -> btnEditarActionPerformed(e));
        btnBuscar.addActionListener(e -> btnBuscarActionPerformed(e));
        btnEliminar.addActionListener(e -> btnEliminarActionPerformed(e));
        btnSalir.addActionListener(e -> btnSalirActionPerformed(e));

        mostrarProductos(); // Cargar productos al iniciar
        this.txtbuscar = txtbuscar;
    }

    public static void setTabla(JTable tabla) {
        tablaCatalogo = tabla;
    }

    public static void mostrarProductos() {
        DefaultTableModel model = new DefaultTableModel();

        model.addColumn("ID");
        model.addColumn("Nombre");
        model.addColumn("Precio");
        model.addColumn("Categoría");
        model.addColumn("Stock");
        model.addColumn("Código Artículo");

        String query = "SELECT * FROM catalogo";

        try (Connection con = Conexion_Chaos.conectar(); PreparedStatement stmt = con.prepareStatement(query); ResultSet rs = stmt.executeQuery()) {

            model.setRowCount(0); // Limpiar filas anteriores

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

            if (tablaCatalogo != null) {
                tablaCatalogo.setModel(model);
            } else {
                JOptionPane.showMessageDialog(null, "Error: No se pudo encontrar el JTable. Asegúrate de haberlo asignado con setTabla().");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al cargar los productos: " + e.getMessage());
        }
    }

    public void btnNuevoActionPerformed(ActionEvent evt) {
        limpiarCampos();
        idProductoSeleccionado = -1;
    }

    private void btnGuardarActionPerformed(ActionEvent evt) throws SQLException {
        String nombre = txtnarticulo.getText().trim();
        String codigoArticulo = txtcodigoarticulo.getText().trim();
        String categoria = jComboCategoria.getSelectedItem().toString();
        String precioStr = txtprecio.getText().trim();
        String stockStr = txtstock.getText().trim();

        // Validar que todos los campos estén llenos
        if (nombre.isEmpty() || codigoArticulo.isEmpty() || precioStr.isEmpty() || stockStr.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Por favor, completa todos los campos.");
            return; // Sale del método si hay campos vacíos
        }

        try {
            // Validar que el precio sea un número válido
            double precio = Double.parseDouble(precioStr);
            // Validar que el stock sea un número entero válido
            int stock = Integer.parseInt(stockStr);

            // Consulta SQL para verificar si el artículo ya existe por nombre o código de artículo
            String checkQuery = "SELECT COUNT(*) FROM catalogo WHERE nombre = ? OR codigo_articulo = ?";
            // Consulta SQL para insertar un nuevo producto
            String insertQuery = "INSERT INTO catalogo (nombre, codigo_articulo, categoria, precio, stock) VALUES (?, ?, ?, ?, ?)";

            try (Connection con = Conexion_Chaos.conectar(); PreparedStatement checkStmt = con.prepareStatement(checkQuery);) {

                checkStmt.setString(1, nombre);
                checkStmt.setString(2, codigoArticulo);
                ResultSet rs = checkStmt.executeQuery();
                rs.next();
                int count = rs.getInt(1);

                if (count > 0) {
                    JOptionPane.showMessageDialog(null, "El artículo ya existe. No se puede registrar.");
                    return; // Sale del método si el artículo ya existe
                }
            }

            try (Connection con = Conexion_Chaos.conectar(); PreparedStatement ps = con.prepareStatement(insertQuery)) {

                ps.setString(1, nombre);
                ps.setString(2, codigoArticulo);
                ps.setString(3, categoria);
                ps.setDouble(4, precio);
                ps.setInt(5, stock);

                int filasAfectadas = ps.executeUpdate();
                if (filasAfectadas > 0) {
                    JOptionPane.showMessageDialog(null, "Artículo registrado exitosamente.");
                    limpiarCampos();
                    mostrarProductos(); // Recargar la tabla
                } else {
                    JOptionPane.showMessageDialog(null, "Error al guardar el artículo.");
                }

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Error al conectar o ejecutar la consulta: " + e.getMessage());
                e.printStackTrace();
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Por favor, ingrese valores numéricos válidos para el precio y el stock.");
        }
    }

    private void btnEditarActionPerformed(ActionEvent evt) {
        int row = tablaCatalogo.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(null, "Por favor, seleccione un producto de la tabla para editar.");
            return;
        }

        idProductoSeleccionado = (int) tablaCatalogo.getValueAt(row, 0);
        txtnarticulo.setText((String) tablaCatalogo.getValueAt(row, 1));
        txtcodigoarticulo.setText((String) tablaCatalogo.getValueAt(row, 5)); // Asumiendo que código_articulo está en la columna 5
        jComboCategoria.setSelectedItem((String) tablaCatalogo.getValueAt(row, 3));
        txtprecio.setText(String.valueOf(tablaCatalogo.getValueAt(row, 2))); // Asumiendo que precio está en la columna 2
        txtstock.setText(String.valueOf(tablaCatalogo.getValueAt(row, 4))); // Asumiendo que stock está en la columna 4
    }

    private void btnBuscarActionPerformed(ActionEvent evt) {
        String busqueda = txtbuscar.getText().trim();
        System.out.println("Término de búsqueda: " + busqueda);

        if (busqueda.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Por favor ingrese un término de búsqueda.");
            mostrarProductos(); // Mostrar todos los productos si la búsqueda está vacía
            return;
        }

        String query = "SELECT * FROM catalogo WHERE nombre LIKE ?";

        try (Connection con = Conexion_Chaos.conectar(); PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, "%" + busqueda + "%");

            try (ResultSet rs = ps.executeQuery()) {
                DefaultTableModel model = new DefaultTableModel();
                model.addColumn("ID");
                model.addColumn("Nombre");
                model.addColumn("Código Artículo");
                model.addColumn("Categoría");
                model.addColumn("Precio");
                model.addColumn("Stock");

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

                tablaCatalogo.setModel(model);

                if (model.getRowCount() == 0) {
                    JOptionPane.showMessageDialog(null, "No se encontraron productos con el nombre: " + busqueda);
                }

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Error al realizar la búsqueda: " + ex.getMessage());
                ex.printStackTrace();
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error de conexión con la base de datos: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void btnEliminarActionPerformed(ActionEvent evt) {
        if (idProductoSeleccionado == -1) {
            JOptionPane.showMessageDialog(null, "Debes seleccionar un producto en la tabla para eliminar.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(null, "¿Estás seguro de que deseas eliminar este producto?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection con = Conexion_Chaos.conectar(); PreparedStatement ps = con.prepareStatement("DELETE FROM catalogo WHERE id=?")) {

                ps.setInt(1, idProductoSeleccionado);
                int filasAfectadas = ps.executeUpdate();

                if (filasAfectadas > 0) {
                    JOptionPane.showMessageDialog(null, "Producto eliminado correctamente.");
                    limpiarCampos();
                    mostrarProductos();
                } else {
                    JOptionPane.showMessageDialog(null, "No se pudo eliminar el producto.");
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
}
