package logica;

import java.awt.event.ActionEvent;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    private final JButton btnBuscar;
    private final JButton btnEliminar;
    private final JButton btnSalir;

    private int idArticuloSeleccionado = -1;

    public VCatalogopedidos(javax.swing.JTextField txtbuscar, javax.swing.JTextField txtnarticulo,
            javax.swing.JTextField txtcodigoarticulo, javax.swing.JComboBox<String> jComboCategoria,
            javax.swing.JTextField txtprecio, javax.swing.JTextField txtstock,
            javax.swing.JButton btnNuevo, javax.swing.JButton btnGuardar,
            javax.swing.JButton btnBuscar, javax.swing.JButton btnEliminar, javax.swing.JButton btnSalir) {
        this.txtnarticulo = txtnarticulo;
        this.txtcodigoarticulo = txtcodigoarticulo;
        this.txtprecio = txtprecio;
        this.txtstock = txtstock;
        this.jComboCategoria = jComboCategoria;
        this.btnNuevo = btnNuevo;
        this.btnGuardar = btnGuardar;
        this.btnBuscar = btnBuscar;
        this.btnEliminar = btnEliminar;
        this.btnSalir = btnSalir;
        this.txtbuscar = txtbuscar;

        btnNuevo.addActionListener(e -> btnNuevoActionPerformed(e));
        btnGuardar.addActionListener(e -> {
            try {
                btnGuardarActionPerformed(e);
            } catch (SQLException ex) {
                Logger.getLogger(VCatalogopedidos.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        btnBuscar.addActionListener(e -> btnBuscarActionPerformed(e));
        btnEliminar.addActionListener(e -> btnEliminarActionPerformed(e));
        btnSalir.addActionListener(e -> btnSalirActionPerformed(e));

        mostrarProductos(); // Cargar productos al iniciar

    }

    public static void setTabla(JTable tabla) {
        tablaCatalogo = tabla;
    }

    public void cargarDatosParaEdicion(int fila) {
        if (fila >= 0) {
            try {
                idArticuloSeleccionado = (int) tablaCatalogo.getValueAt(fila, 0);
                txtnarticulo.setText(tablaCatalogo.getValueAt(fila, 1).toString());
                txtprecio.setText(tablaCatalogo.getValueAt(fila, 2).toString());
                jComboCategoria.setSelectedItem(tablaCatalogo.getValueAt(fila, 3).toString());
                txtcodigoarticulo.setText(tablaCatalogo.getValueAt(fila, 4).toString());
                txtstock.setText(tablaCatalogo.getValueAt(fila, 5).toString());

                btnGuardar.setText("Actualizar");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Error al cargar datos para edición: " + e.getMessage());
            }
        }
    }

    public static void mostrarProductos() {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("ID");
        model.addColumn("Nombre");
        model.addColumn("Precio");
        model.addColumn("Categoría");
        model.addColumn("Código Artículo");
        model.addColumn("Stock");

        String query = "SELECT * FROM catalogo";

        try (Connection con = Conexion_Chaos.conectar(); PreparedStatement stmt = con.prepareStatement(query); ResultSet rs = stmt.executeQuery()) {
            model.setRowCount(0); // Limpiar filas anteriores
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getDouble("precio"),
                    rs.getString("categoria"),
                    rs.getString("codigo_articulo"),
                    rs.getInt("stock")
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
        idArticuloSeleccionado = -1;
        btnGuardar.setText("Guardar");
    }

    private void btnGuardarActionPerformed(ActionEvent evt) throws SQLException {
        String nombre = txtnarticulo.getText().trim();
        String codigoArticulo = txtcodigoarticulo.getText().trim();
        String categoria = jComboCategoria.getSelectedItem().toString();
        String precioStr = txtprecio.getText().trim();
        String stockStr = txtstock.getText().trim();

        if (btnGuardar.getText().equals("Guardar")) { // Modo Guardar Nuevo
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
                String checkQuery = "SELECT COUNT(*) FROM catalogo WHERE (nombre = ? OR codigo_articulo = ?) AND id_articulo != ?";
                // Consulta SQL para insertar un nuevo producto
                String insertQuery = "INSERT INTO catalogo (nombre, codigo_articulo, categoria, precio, stock) VALUES (?, ?, ?, ?, ?)";

                try (Connection con = Conexion_Chaos.conectar(); PreparedStatement checkStmt = con.prepareStatement(checkQuery);) {
                    checkStmt.setString(1, nombre);
                    checkStmt.setString(2, codigoArticulo);
                    checkStmt.setInt(3, -1); // Indicamos que no estamos excluyendo ningún ID en la verificación de existencia
                    ResultSet rs = checkStmt.executeQuery();
                    rs.next();
                    int count = rs.getInt(1);

                    if (count > 0) {
                        JOptionPane.showMessageDialog(null, "Ya existe un artículo con ese nombre o código.");
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
                        btnGuardar.setText("Guardar"); // Asegurarse de que el botón vuelva a decir "Guardar"
                        idArticuloSeleccionado = -1; // Resetear el ID seleccionado
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
        } else if (btnGuardar.getText().equals("Actualizar")) { // Modo Editar Existente
            // Consulta SQL para actualizar el producto existente
            String updateQuery = "UPDATE catalogo SET nombre = ?, codigo_articulo = ?, categoria = ?, precio = ?, stock = ? WHERE id_articulo = ?";

            try (Connection con = Conexion_Chaos.conectar(); PreparedStatement ps = con.prepareStatement(updateQuery)) {
                ps.setString(1, nombre);
                ps.setString(2, codigoArticulo);
                ps.setString(3, categoria);
                ps.setDouble(4, Double.parseDouble(precioStr));
                ps.setInt(5, Integer.parseInt(stockStr));
                ps.setInt(6, idArticuloSeleccionado);

                int filasAfectadas = ps.executeUpdate();
                if (filasAfectadas > 0) {
                    JOptionPane.showMessageDialog(null, "Artículo actualizado exitosamente.");
                    limpiarCampos();
                    mostrarProductos(); // Recargar la tabla
                    btnGuardar.setText("Guardar"); // Volver a la funcionalidad de guardar
                    idArticuloSeleccionado = -1; // Resetear el ID seleccionado
                } else {
                    JOptionPane.showMessageDialog(null, "Error al actualizar el artículo.");
                }

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Error al conectar o ejecutar la consulta: " + e.getMessage());
                e.printStackTrace();
            }
        }
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
                model.addColumn("Precio");
                model.addColumn("Categoría");
                model.addColumn("Código Artículo");
                model.addColumn("Stock");

                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getInt("id_articulo"),
                        rs.getString("nombre"),
                        rs.getDouble("precio"),
                        rs.getString("categoria"),
                        rs.getString("codigo_articulo"),
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
        int filaSeleccionada = tablaCatalogo.getSelectedRow();

        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(null, "Debes seleccionar un producto en la tabla para eliminar.");
            return;
        }

        int idEliminar = (int) tablaCatalogo.getValueAt(filaSeleccionada, 0);

        int confirm = JOptionPane.showConfirmDialog(null, "¿Estás seguro de que deseas eliminar este producto?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection con = Conexion_Chaos.conectar(); PreparedStatement ps = con.prepareStatement("DELETE FROM catalogo WHERE id_articulo=?")) {
                ps.setInt(1, idEliminar);
                int filasAfectadas = ps.executeUpdate();

                if (filasAfectadas > 0) {
                    JOptionPane.showMessageDialog(null, "Producto eliminado correctamente.");
                    limpiarCampos();
                    mostrarProductos();
                    idArticuloSeleccionado = -1; // Resetear el ID seleccionado
                    btnGuardar.setText("Guardar");
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
        jComboCategoria.setSelectedIndex(0); // Puedes ajustar el índice inicial si es necesario
        idArticuloSeleccionado = -1;
    }

    public void setTablaCatalogo(JTable tabla) {
        tablaCatalogo = tabla;
        // Agregar MouseListener a la tabla para manejar los clics de fila para la edición
        tablaCatalogo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) { // Detecta doble clic
                    int fila = tablaCatalogo.rowAtPoint(evt.getPoint());
                    if (fila >= 0) {
                        cargarDatosParaEdicion(fila);
                    }
                }
            }
        });
    }
}
