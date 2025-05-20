package ventanas;

import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;
import javax.swing.*;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import javax.swing.JOptionPane;
import logica.Conexion_Chaos;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Registro_de_Pedidos extends javax.swing.JInternalFrame {

    private Map<String, Double> preciosArticulos = new HashMap<>();
    private final Ventanamultiple Ventanamultiple;
    private Set<String> articulosSeleccionados = new HashSet<>();

    public Registro_de_Pedidos(Ventanamultiple ventanas) {
        initComponents();
        this.Ventanamultiple = ventanas; // Recibes la instancia existente de VentanaMultiple
        cargarNombresArticulos();
        AutoCompleteDecorator.decorate(cbx_Articulo);
        cargarPreciosArticulos();
        txtmontototal.setEditable(false);
        txtimpuesto.setEditable(false);
        txtSubtotal.setEditable(false);
        cbx_Articulo.setSelectedIndex(-1); // Inicialmente no seleccionar nada

        cbx_Articulo.addActionListener(evt -> {
            String articuloSeleccionado = (String) cbx_Articulo.getSelectedItem();
            if (articuloSeleccionado != null && !articuloSeleccionado.isEmpty() && !articulosSeleccionados.contains(articuloSeleccionado)) {
                String textoActual = txtDescripcion.getText();
                if (!textoActual.isEmpty()) {
                    txtDescripcion.append("\n1 x " + articuloSeleccionado + " (Talla)");
                } else {
                    txtDescripcion.setText("1 x " + articuloSeleccionado + " (Talla)");
                }
                articulosSeleccionados.add(articuloSeleccionado);
                cbx_Articulo.setSelectedIndex(-1); // Deseleccionar después de añadir
            } else if (articuloSeleccionado != null && !articuloSeleccionado.isEmpty() && articulosSeleccionados.contains(articuloSeleccionado)) {
                // Si ya está seleccionado, no hacer nada para evitar duplicados en la lista interna
                cbx_Articulo.setSelectedIndex(-1); // Deseleccionar después de la selección
            } else if (articuloSeleccionado != null && !articuloSeleccionado.isEmpty()) {
                cbx_Articulo.setSelectedIndex(-1); // Deseleccionar si no se cumple la condición de no duplicado
            }
        });

        btnCalcular.addActionListener(evt -> calcularMontoTotalPedido());
    }

    private void cargarNombresArticulos() {
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        model.addElement(""); // Añadir una opción vacía o "Seleccionar" al inicio
        try (Connection con = Conexion_Chaos.conectar(); PreparedStatement pstmt = con.prepareStatement("SELECT nombre FROM catalogo"); ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                model.addElement(rs.getString("nombre"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar los nombres de los artículos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        cbx_Articulo.setModel(model);
        cbx_Articulo.setSelectedIndex(-1); // Deseleccionar la opción por defecto
    }

    private void cargarPreciosArticulos() {
        try (Connection con = Conexion_Chaos.conectar(); PreparedStatement pstmt = con.prepareStatement("SELECT nombre, precio FROM catalogo"); ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                preciosArticulos.put(rs.getString("nombre"), rs.getDouble("precio"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar los precios de los artículos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private Map<String, Integer> obtenerCantidadesPedido() {
        Map<String, Integer> cantidadesPedido = new HashMap<>();
        String descripcion = txtDescripcion.getText();
        String[] lineas = descripcion.split("\n");

        for (String linea : lineas) {
            linea = linea.trim();
            if (!linea.isEmpty()) {
                String[] partes = linea.split("x");
                if (partes.length == 2) {
                    try {
                        int cantidad = Integer.parseInt(partes[0].trim());
                        String nombreArticuloConDetalle = partes[1].trim();
                        String[] nombrePartes = nombreArticuloConDetalle.split("\\(");
                        String nombreArticulo = nombrePartes[0].trim();

                        // Verificar si el nombre del artículo ya existe en el mapa
                        if (cantidadesPedido.containsKey(nombreArticulo)) {
                            JOptionPane.showMessageDialog(this, "El artículo '" + nombreArticulo + "' ya ha sido especificado. Por favor, combine las cantidades en una sola línea.", "Error", JOptionPane.ERROR_MESSAGE);
                            return null; // Indica un error: artículo duplicado
                        }

                        cantidadesPedido.put(nombreArticulo, cantidad);

                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(this, "Formato de cantidad incorrecto en la descripción: " + linea, "Error", JOptionPane.ERROR_MESSAGE);
                        return null; // Indica un error en el formato
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Formato de descripción de artículo incorrecto: " + linea + "\nDebe ser como 'Cantidad x Nombre del Artículo (Talla)'", "Error", JOptionPane.ERROR_MESSAGE);
                    return null; // Indica un error en el formato
                }
            }
        }
        return cantidadesPedido;
    }

    private void calcularMontoTotalPedido() {
        String descripcion = txtDescripcion.getText();
        String[] lineas = descripcion.split("\n");
        double subtotal = 0.0;

        for (String linea : lineas) {
            linea = linea.trim();
            if (!linea.isEmpty()) {
                String[] partes = linea.split("x");
                if (partes.length == 2) {
                    try {
                        int cantidad = Integer.parseInt(partes[0].trim());
                        String nombreArticuloConDetalle = partes[1].trim();
                        String nombreArticulo = nombreArticuloConDetalle.split("\\(")[0].trim();

                        if (preciosArticulos.containsKey(nombreArticulo)) {
                            subtotal += cantidad * preciosArticulos.get(nombreArticulo);
                        } else {
                            JOptionPane.showMessageDialog(this, "No se encontró el precio para el artículo: " + nombreArticulo, "Advertencia", JOptionPane.WARNING_MESSAGE);
                            return;
                        }

                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(this, "Formato de cantidad incorrecto en la descripción: " + linea, "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Formato de descripción de artículo incorrecto: " + linea + "\nDebe ser como 'Cantidad x Nombre del Artículo (Talla)'", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        }
        txtSubtotal.setText(String.format("%.2f", subtotal));
        calcularImpuesto(subtotal); // Llamamos al método para calcular el impuesto
    }

    private void calcularImpuesto(double subtotal) {
        String provinciaSeleccionada = (String) cbx_Provincia.getSelectedItem();
        double impuesto = 0.0;
        java.util.List<String> provinciasExentas = java.util.Arrays.asList("Santo Domingo", "Puerto Plata", "Santiago", "La Vega", "Samaná");

        if (provinciaSeleccionada != null && !provinciasExentas.contains(provinciaSeleccionada)) {
            impuesto = subtotal * 0.18;
        }

        txtimpuesto.setText(String.format("%.2f", impuesto));
        calcularMontoTotalFinal(subtotal, impuesto); // Calcular el monto total después del impuesto
    }

    private void calcularMontoTotalFinal(double subtotal, double impuesto) {
        double montoTotal = subtotal + impuesto;
        txtmontototal.setText(String.format("%.2f", montoTotal));
    }

    private boolean verificarStock() {
        Map<String, Integer> cantidadesPedido = obtenerCantidadesPedido();
        if (cantidadesPedido == null) {
            return false; // Hubo un error en el formato de la descripción
        }

        try (Connection con = Conexion_Chaos.conectar(); PreparedStatement pstmt = con.prepareStatement("SELECT nombre, stock FROM catalogo WHERE nombre = ?")) {
            Set<String> nombresArticulosPedido = cantidadesPedido.keySet();
            for (String nombreArticulo : nombresArticulosPedido) {
                pstmt.setString(1, nombreArticulo);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    int stockActual = rs.getInt("stock");
                    int cantidadPedido = cantidadesPedido.get(nombreArticulo);
                    if (cantidadPedido > 10) {
                        JOptionPane.showMessageDialog(this, "No se pueden pedir más de 10 unidades del artículo '" + nombreArticulo + "' en un solo pedido.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                        return false;
                    } else if (stockActual == 10 && cantidadPedido > 0) {
                        JOptionPane.showMessageDialog(this, "Advertencia: El stock del artículo '" + nombreArticulo + "' es de 10 unidades. Considere actualizar el stock.", "Advertencia de Stock", JOptionPane.WARNING_MESSAGE);
                        // No impedimos el registro, solo advertimos. Si quieres impedirlo, retorna false aquí.
                    } else if (cantidadPedido > stockActual) {
                        JOptionPane.showMessageDialog(this, "Error: No hay suficiente stock para el artículo '" + nombreArticulo + "'. Stock actual: " + stockActual + ", Cantidad pedida: " + cantidadPedido, "Error de Stock", JOptionPane.ERROR_MESSAGE);
                        return false; // No hay suficiente stock para registrar el pedido
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Error: No se encontró el artículo '" + nombreArticulo + "' en el stock.", "Error de Artículo", JOptionPane.ERROR_MESSAGE);
                    return false; // El artículo no se encontró en el stock
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al verificar el stock: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        }
        return true; // Stock verificado correctamente
    }

    private boolean actualizarStockEnBaseDeDatos() {
        Map<String, Integer> cantidadesPedido = obtenerCantidadesPedido();
        if (cantidadesPedido == null) {
            return false; // Error al obtener las cantidades del pedido
        }

        try (Connection con = Conexion_Chaos.conectar(); PreparedStatement pstmt = con.prepareStatement("UPDATE catalogo SET stock = stock - ? WHERE nombre = ?")) {
            for (Map.Entry<String, Integer> entry : cantidadesPedido.entrySet()) {
                String nombreArticulo = entry.getKey();
                int cantidadPedido = entry.getValue();
                pstmt.setInt(1, cantidadPedido);
                pstmt.setString(2, nombreArticulo);
                pstmt.executeUpdate();
            }
            return true;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al actualizar el stock en la base de datos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
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

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        Fecha_de_Entrega = new com.toedter.calendar.JDateChooser();
        Fecha_de_Pedido = new com.toedter.calendar.JDateChooser();
        jLabel6 = new javax.swing.JLabel();
        txtcodigo = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txt_telefono = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtapellido = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtnombre = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtDescripcion = new javax.swing.JTextArea();
        jLabel11 = new javax.swing.JLabel();
        cbx_Articulo = new javax.swing.JComboBox<>();
        jLabel10 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        txtdireccion = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        txtmontototal = new javax.swing.JTextField();
        Impuesto = new javax.swing.JLabel();
        txtimpuesto = new javax.swing.JTextField();
        txtSubtotal = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        cbx_TipodePago = new javax.swing.JComboBox<>();
        cbx_Estado = new javax.swing.JComboBox<>();
        jLabel12 = new javax.swing.JLabel();
        cbx_Provincia = new javax.swing.JComboBox<>();
        jLabel8 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        btnRegistrar = new javax.swing.JButton();
        btnNuevo = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();
        btnCalcular = new javax.swing.JButton();
        jLabel15 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();

        setClosable(true);
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setIconifiable(true);
        setMaximizable(true);

        jPanel1.setBackground(new java.awt.Color(0, 0, 0));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(0, 0, 0));
        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255), 3));

        jLabel7.setFont(new java.awt.Font("Segoe UI", 2, 18)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("Fecha de Entrega:");

        jLabel6.setFont(new java.awt.Font("Segoe UI", 2, 18)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Fecha:");

        txtcodigo.setText("CH-1109");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 2, 18)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("Código de Pedido:");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 2, 18)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Teléfono:");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 2, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Apellido:");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 2, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Nombre:");

        txtDescripcion.setColumns(20);
        txtDescripcion.setRows(5);
        jScrollPane1.setViewportView(txtDescripcion);

        jLabel11.setFont(new java.awt.Font("Segoe UI", 2, 18)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setText("Descripción:");

        jLabel10.setFont(new java.awt.Font("Segoe UI", 2, 18)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setText("Artículo:");

        jLabel9.setFont(new java.awt.Font("Segoe UI", 2, 18)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setText("Dirección:");

        jLabel16.setFont(new java.awt.Font("Segoe UI", 2, 18)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(255, 255, 255));
        jLabel16.setText("Montal Total:");

        txtmontototal.setEnabled(false);

        Impuesto.setFont(new java.awt.Font("Segoe UI", 2, 18)); // NOI18N
        Impuesto.setForeground(new java.awt.Color(255, 255, 255));
        Impuesto.setText("Impuesto:");

        txtimpuesto.setEnabled(false);
        txtimpuesto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtimpuestoActionPerformed(evt);
            }
        });

        txtSubtotal.setEnabled(false);

        jLabel14.setFont(new java.awt.Font("Segoe UI", 2, 18)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(255, 255, 255));
        jLabel14.setText("Subtotal:");

        jLabel13.setFont(new java.awt.Font("Segoe UI", 2, 18)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(255, 255, 255));
        jLabel13.setText("Tipo de Pago:");

        cbx_TipodePago.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Seleccionar", "Efectivo", "Tarjeta", "Transferencia" }));

        cbx_Estado.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Seleccionar", "En almacén", "De camino", "Entregado", "Devuelto al Remitente", "Cancelado" }));

        jLabel12.setFont(new java.awt.Font("Segoe UI", 2, 18)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setText("Estado:");

        cbx_Provincia.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Seleccionar", "Azua", "Baoruco", "Barahona", "Dajabón", "Distrito Nacional", "Duarte", "Elías Piña", "El Seibo", "Espaillat", "Hato Mayor", "Hermanas Mirabal   ", "Independencia", "La Altagracia", "La Romana", "La Vega", "María Trinidad Sánchez", "Monseñor Nouel", "Monte Cristi", "Monte Plata", "Pedernales", "Peravia", "Puerto Plata", "Samaná", "San Cristóbal", "San José de Ocoa", "San Juan", "San Pedro de Macorís", "Sánchez Ramírez", "Santiago", "Santiago Rodríguez   ", "Santo Domingo   ", "Valverde" }));

        jLabel8.setFont(new java.awt.Font("Segoe UI", 2, 18)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("Provincia:");

        jLabel1.setFont(new java.awt.Font("Segoe UI", 3, 40)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Registro de Pedidos");

        jLabel19.setIcon(new javax.swing.ImageIcon(getClass().getResource("/íconos/icon-pds-360-w.jpg"))); // NOI18N
        jLabel19.setText("jLabel19");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(41, 41, 41)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel5)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(jLabel2)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txt_telefono)
                    .addComponent(txtapellido)
                    .addComponent(txtnombre)
                    .addComponent(cbx_Provincia, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtcodigo, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(33, 33, 33)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addGroup(jPanel2Layout.createSequentialGroup()
                                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                            .addComponent(jLabel11)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 219, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                            .addGap(71, 71, 71)
                                            .addComponent(jLabel10)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(cbx_Articulo, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                .addComponent(jLabel7)
                                                .addComponent(jLabel9))
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(txtdireccion, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(Fecha_de_Entrega, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                    .addGap(27, 27, 27))
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                    .addComponent(jLabel6)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(Fecha_de_Pedido, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(26, 26, 26)))
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jLabel12)
                                .addComponent(jLabel13)
                                .addComponent(jLabel14)
                                .addComponent(Impuesto)
                                .addComponent(jLabel16))
                            .addGap(12, 12, 12)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(txtimpuesto)
                                    .addComponent(txtSubtotal)
                                    .addComponent(cbx_TipodePago, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(cbx_Estado, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addComponent(txtmontototal, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGap(0, 0, Short.MAX_VALUE))
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addComponent(jLabel1)
                            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGap(211, 211, 211)
                        .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(549, 549, 549))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(jLabel19)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 36, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(txtnombre, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel3)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(txtapellido, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(1, 1, 1)))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txt_telefono, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cbx_Provincia, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(4, 4, 4)
                                .addComponent(txtcodigo, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel12)
                                .addComponent(cbx_Estado, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addGap(51, 51, 51)
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel13)
                                            .addComponent(cbx_TipodePago, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(22, 22, 22))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(Fecha_de_Pedido, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel6))
                                        .addGap(18, 18, 18)
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(Fecha_de_Entrega, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel7))
                                        .addGap(18, 18, 18)))
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(txtdireccion, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel9)
                                    .addComponent(jLabel14)
                                    .addComponent(txtSubtotal, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(cbx_Articulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel10)
                                    .addComponent(Impuesto)
                                    .addComponent(txtimpuesto, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(txtmontototal, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel16))
                            .addComponent(jLabel11)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(35, 35, 35))
        );

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 90, 1200, 520));

        jPanel3.setBackground(new java.awt.Color(0, 0, 0));
        jPanel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255), 3));

        btnRegistrar.setBackground(new java.awt.Color(0, 0, 0));
        btnRegistrar.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnRegistrar.setForeground(new java.awt.Color(255, 255, 255));
        btnRegistrar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/íconos/crear-una-cuenta.png"))); // NOI18N
        btnRegistrar.setText("Registrar");
        btnRegistrar.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255), 2));
        btnRegistrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRegistrarActionPerformed(evt);
            }
        });

        btnNuevo.setBackground(new java.awt.Color(0, 0, 0));
        btnNuevo.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnNuevo.setForeground(new java.awt.Color(255, 255, 255));
        btnNuevo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/íconos/agregar-carpeta.png"))); // NOI18N
        btnNuevo.setText("Nuevo");
        btnNuevo.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255), 2));
        btnNuevo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNuevoActionPerformed(evt);
            }
        });

        btnCancelar.setBackground(new java.awt.Color(0, 0, 0));
        btnCancelar.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnCancelar.setForeground(new java.awt.Color(255, 255, 255));
        btnCancelar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/íconos/boton-x.png"))); // NOI18N
        btnCancelar.setText("Cancelar");
        btnCancelar.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255), 2));
        btnCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarActionPerformed(evt);
            }
        });

        btnCalcular.setBackground(new java.awt.Color(0, 0, 0));
        btnCalcular.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnCalcular.setForeground(new java.awt.Color(255, 255, 255));
        btnCalcular.setIcon(new javax.swing.ImageIcon(getClass().getResource("/íconos/calculadora.png"))); // NOI18N
        btnCalcular.setText("Calcular");
        btnCalcular.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255), 2));
        btnCalcular.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCalcularActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap(135, Short.MAX_VALUE)
                .addComponent(btnRegistrar, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(112, 112, 112)
                .addComponent(btnNuevo, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(120, 120, 120)
                .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(89, 89, 89)
                .addComponent(btnCalcular, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(95, 95, 95))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(39, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnRegistrar, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnNuevo, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCalcular, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(28, 28, 28))
        );

        jPanel1.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 620, 1200, 120));

        jLabel15.setText("jLabel15");
        jPanel1.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(799, 78, -1, -1));

        jLabel18.setIcon(new javax.swing.ImageIcon(getClass().getResource("/íconos/Opcion 7 (1,1).png"))); // NOI18N
        jLabel18.setText("jLabel18");
        jPanel1.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, 170, -1));

        jLabel17.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/pexels-panos-and-marenia-stavrinos-106103914-9648161.jpg"))); // NOI18N
        jLabel17.setText("jLabel17");
        jPanel1.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1520, 960));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCalcularActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCalcularActionPerformed
        calcularMontoTotalPedido();
    }//GEN-LAST:event_btnCalcularActionPerformed

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        this.dispose();
        Ventanamultiple.setVisible(true);
    }//GEN-LAST:event_btnCancelarActionPerformed

    private void btnNuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNuevoActionPerformed
        limpiarCampos();
    }//GEN-LAST:event_btnNuevoActionPerformed

    private void btnRegistrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRegistrarActionPerformed
        String nombreCliente = txtnombre.getText().trim();
        String apellidoCliente = txtapellido.getText().trim();

        // 1. Verificar campos obligatorios
        if (nombreCliente.isEmpty() || apellidoCliente.isEmpty() ||
            txtdireccion.getText().trim().isEmpty() || txtDescripcion.getText().trim().isEmpty() ||
            Fecha_de_Pedido.getDate() == null || Fecha_de_Entrega.getDate() == null ||
            txtcodigo.getText().trim().isEmpty() || txtSubtotal.getText().trim().isEmpty() ||
            txtimpuesto.getText().trim().isEmpty() || txtmontototal.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, complete todos los campos obligatorios.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 2. Verificar si el cliente existe en la tabla de clientes
        if (!clienteExiste(nombreCliente, apellidoCliente)) {
            JOptionPane.showMessageDialog(this, "El cliente no fue encontrado. Por favor, registre al cliente.", "Error", JOptionPane.ERROR_MESSAGE);
            return; // No registrar el pedido si el cliente no está registrado
        }

        // 3. Verificar stock
        if (!verificarStock()) {
            return; // La verificación de stock falló, el método verificarStock() ya mostró el mensaje de error.
        }

        // 4. Actualizar stock en la base de datos
        if (actualizarStockEnBaseDeDatos()) {
            System.out.println("Stock actualizado correctamente.");
        } else {
            JOptionPane.showMessageDialog(this, "Error al actualizar el stock.", "Error", JOptionPane.ERROR_MESSAGE);
            return; // No continuar con el registro si falla la actualización del stock
        }

        // 5. Guardar los datos del pedido en la base de datos
        try (Connection con = Conexion_Chaos.conectar();
             PreparedStatement pstmt = con.prepareStatement("INSERT INTO pedidos (idPedido, nombreCliente, apellidoCliente, telefonoCliente, direccion, provincia, fechaPedido, fechaEntrega, tipoPago, estado, descripcion, total_pago, impuesto, montoTotal) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            pstmt.setString(1, txtcodigo.getText());
            pstmt.setString(2, nombreCliente); // Usar las variables
            pstmt.setString(3, apellidoCliente); // Usar las variables
            pstmt.setString(4, txt_telefono.getText());
            pstmt.setString(5, txtdireccion.getText());
            pstmt.setString(6, (String) cbx_Provincia.getSelectedItem());
            pstmt.setString(7, sdf.format(Fecha_de_Pedido.getDate()));
            pstmt.setString(8, sdf.format(Fecha_de_Entrega.getDate()));
            pstmt.setString(9, (String) cbx_TipodePago.getSelectedItem());
            pstmt.setString(10, (String) cbx_Estado.getSelectedItem());
            pstmt.setString(11, txtDescripcion.getText());
            pstmt.setDouble(12, Double.parseDouble(txtSubtotal.getText()));
            pstmt.setDouble(13, Double.parseDouble(txtimpuesto.getText()));
            pstmt.setDouble(14, Double.parseDouble(txtmontototal.getText()));

            int filasAfectadas = pstmt.executeUpdate();

            if (filasAfectadas > 0) {
                JOptionPane.showMessageDialog(this, "Pedido registrado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                limpiarCampos(); // Opcional: limpiar los campos después del registro
                // **** OPCIONAL: GENERAR REPORTE PDF AQUÍ SI LO DESEAS ****
            } else {
                JOptionPane.showMessageDialog(this, "Error al registrar el pedido.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al conectar o guardar el pedido: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private boolean clienteExiste(String nombre, String apellido) {
        try (Connection con = Conexion_Chaos.conectar();
             PreparedStatement pstmt = con.prepareStatement("SELECT COUNT(*) FROM clientes WHERE (nombre LIKE ? AND apellido LIKE ?) OR (nombre LIKE ? AND apellido LIKE ?)")) {

            // Permite coincidencia si el usuario ingresa parte del nombre o apellido
            pstmt.setString(1, "%" + nombre + "%");
            pstmt.setString(2, "%" + apellido + "%");
            pstmt.setString(3, "%" + apellido + "%"); // Invertir orden para cubrir casos como "Apellido Nombre"
            pstmt.setString(4, "%" + nombre + "%");

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0; // Devuelve true si se encontró al menos un cliente coincidente
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al verificar el cliente: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        return false; // Error o cliente no encontrado
    }

    private void limpiarCampos() {
        txtnombre.setText("");
        txtapellido.setText("");
        txt_telefono.setText("");
        txtdireccion.setText("");
        txtDescripcion.setText("");
        txtcodigo.setText("");
        txtSubtotal.setText("");
        txtimpuesto.setText("");
        txtmontototal.setText("");
        Fecha_de_Pedido.setDate(null);
        Fecha_de_Entrega.setDate(null);
        cbx_Provincia.setSelectedIndex(0);
        cbx_TipodePago.setSelectedIndex(0);
        cbx_Estado.setSelectedIndex(0);
        cbx_Articulo.setSelectedIndex(-1);
    }//GEN-LAST:event_btnRegistrarActionPerformed

    private void txtimpuestoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtimpuestoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtimpuestoActionPerformed

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
            java.util.logging.Logger.getLogger(Registro_de_Pedidos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Registro_de_Pedidos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Registro_de_Pedidos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Registro_de_Pedidos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.toedter.calendar.JDateChooser Fecha_de_Entrega;
    private com.toedter.calendar.JDateChooser Fecha_de_Pedido;
    private javax.swing.JLabel Impuesto;
    private javax.swing.JButton btnCalcular;
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnNuevo;
    private javax.swing.JButton btnRegistrar;
    private javax.swing.JComboBox<String> cbx_Articulo;
    private javax.swing.JComboBox<String> cbx_Estado;
    private javax.swing.JComboBox<String> cbx_Provincia;
    private javax.swing.JComboBox<String> cbx_TipodePago;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea txtDescripcion;
    private javax.swing.JTextField txtSubtotal;
    private javax.swing.JTextField txt_telefono;
    private javax.swing.JTextField txtapellido;
    private javax.swing.JTextField txtcodigo;
    private javax.swing.JTextField txtdireccion;
    private javax.swing.JTextField txtimpuesto;
    private javax.swing.JTextField txtmontototal;
    private javax.swing.JTextField txtnombre;
    // End of variables declaration//GEN-END:variables

}
