package ventanas;

import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;
import javax.swing.*;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JOptionPane;
import logica.Conexion_Chaos;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Registro_de_Pedidos extends javax.swing.JInternalFrame {

    private Map<String, Double> preciosArticulos = new HashMap<>();
    private final Ventanamultiple Ventanamultiple;

    public Registro_de_Pedidos(Ventanamultiple ventanas) {
        initComponents();
        this.Ventanamultiple = ventanas; // Recibes la instancia existente de VentanaMultiple
        cargarNombresArticulos();
        AutoCompleteDecorator.decorate(cbx_Articulo);
        cargarPreciosArticulos();
        txtmontototal.setEditable(false);

        cbx_Articulo.addActionListener(evt -> {
            String articuloSeleccionado = (String) cbx_Articulo.getSelectedItem();
            if (articuloSeleccionado != null && !articuloSeleccionado.isEmpty()) {
                String textoActual = txtDescripcion.getText();
                if (!textoActual.isEmpty()) {
                    txtDescripcion.append("\n1 x " + articuloSeleccionado + " (Talla)");
                } else {
                    txtDescripcion.setText("1 x " + articuloSeleccionado + " (Talla)");
                }
                cbx_Articulo.setSelectedIndex(-1);
            }
        });
    }

    private void cargarNombresArticulos() {
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        try (Connection con = Conexion_Chaos.conectar(); PreparedStatement pstmt = con.prepareStatement("SELECT nombre FROM catalogo"); ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                model.addElement(rs.getString("nombre"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar los nombres de los artículos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        cbx_Articulo.setModel(model);
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

        if (!provinciasExentas.contains(provinciaSeleccionada)) {
            // Puedes definir la lógica de tu impuesto aquí, por ejemplo, un porcentaje del subtotal.
            // Para este ejemplo, estableceremos un impuesto del 18% (ITBIS en RD).
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
                    if (stockActual <= 20) {
                        JOptionPane.showMessageDialog(this, "Advertencia: El artículo '" + nombreArticulo + "' tiene un stock de " + stockActual + " unidades. Considere actualizar el stock.", "Advertencia de Stock", JOptionPane.WARNING_MESSAGE);
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

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txtnombre = new javax.swing.JTextField();
        txtapellido = new javax.swing.JTextField();
        txt_telefono = new javax.swing.JTextField();
        txtcodigo = new javax.swing.JTextField();
        Fecha_de_Pedido = new com.toedter.calendar.JDateChooser();
        Fecha_de_Entrega = new com.toedter.calendar.JDateChooser();
        jLabel8 = new javax.swing.JLabel();
        cbx_Provincia = new javax.swing.JComboBox<>();
        jLabel9 = new javax.swing.JLabel();
        txtdireccion = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        cbx_Articulo = new javax.swing.JComboBox<>();
        jLabel11 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtDescripcion = new javax.swing.JTextArea();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        Impuesto = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        txtimpuesto = new javax.swing.JTextField();
        txtmontototal = new javax.swing.JTextField();
        txtSubtotal = new javax.swing.JTextField();
        cbx_TipodePago = new javax.swing.JComboBox<>();
        cbx_Estado = new javax.swing.JComboBox<>();
        btnRegistrar = new javax.swing.JButton();
        btnNuevo = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();
        btnCalcular = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(0, 0, 0));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 3, 40)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Registro de Pedidos");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 2, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Nombre:");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 2, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Apellido:");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 2, 18)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Teléfono:");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 2, 18)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("Código de Pedido:");

        jLabel6.setFont(new java.awt.Font("Segoe UI", 2, 18)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Fecha:");

        jLabel7.setFont(new java.awt.Font("Segoe UI", 2, 18)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("Fecha de Entrega:");

        txtcodigo.setText("CH-1109");

        jLabel8.setFont(new java.awt.Font("Segoe UI", 2, 18)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("Provincia:");

        cbx_Provincia.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Seleccionar", "Azua", "Baoruco", "Barahona", "Dajabón", "Distrito Nacional", "Duarte", "Elías Piña", "El Seibo", "Espaillat", "Hato Mayor", "Hermanas Mirabal   ", "Independencia", "La Altagracia", "La Romana", "La Vega", "María Trinidad Sánchez", "Monseñor Nouel", "Monte Cristi", "Monte Plata", "Pedernales", "Peravia", "Puerto Plata", "Samaná", "San Cristóbal", "San José de Ocoa", "San Juan", "San Pedro de Macorís", "Sánchez Ramírez", "Santiago", "Santiago Rodríguez   ", "Santo Domingo   ", "Valverde" }));

        jLabel9.setFont(new java.awt.Font("Segoe UI", 2, 18)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setText("Dirección:");

        jLabel10.setFont(new java.awt.Font("Segoe UI", 2, 18)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setText("Artículo:");

        jLabel11.setFont(new java.awt.Font("Segoe UI", 2, 18)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setText("Descripción:");

        txtDescripcion.setColumns(20);
        txtDescripcion.setRows(5);
        jScrollPane1.setViewportView(txtDescripcion);

        jLabel12.setFont(new java.awt.Font("Segoe UI", 2, 18)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setText("Estado:");

        jLabel13.setFont(new java.awt.Font("Segoe UI", 2, 18)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(255, 255, 255));
        jLabel13.setText("Tipo de Pago:");

        jLabel14.setFont(new java.awt.Font("Segoe UI", 2, 18)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(255, 255, 255));
        jLabel14.setText("Subtotal:");

        Impuesto.setFont(new java.awt.Font("Segoe UI", 2, 18)); // NOI18N
        Impuesto.setForeground(new java.awt.Color(255, 255, 255));
        Impuesto.setText("Impuesto:");

        jLabel16.setFont(new java.awt.Font("Segoe UI", 2, 18)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(255, 255, 255));
        jLabel16.setText("Montal Total:");

        txtimpuesto.setEnabled(false);

        txtmontototal.setEnabled(false);

        txtSubtotal.setEnabled(false);

        cbx_TipodePago.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Seleccionar", "Efectivo", "Tarjeta", "Transferencia" }));

        cbx_Estado.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Seleccionar", "En almacén", "De camino", "Entregado", "Devuelto al Remitente", "Cancelado" }));

        btnRegistrar.setText("Registrar");
        btnRegistrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRegistrarActionPerformed(evt);
            }
        });

        btnNuevo.setText("Nuevo");
        btnNuevo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNuevoActionPerformed(evt);
            }
        });

        btnCancelar.setText("Cancelar");
        btnCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarActionPerformed(evt);
            }
        });

        btnCalcular.setText("Calcular");
        btnCalcular.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCalcularActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnRegistrar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(110, 110, 110)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(jLabel5)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(txtcodigo))
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel4)
                                        .addComponent(jLabel2))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(txt_telefono, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(txtapellido, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(txtnombre, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addComponent(jLabel7)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(Fecha_de_Entrega, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(jLabel6)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(Fecha_de_Pedido, javax.swing.GroupLayout.PREFERRED_SIZE, 229, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                .addGap(120, 120, 120)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel8)
                    .addComponent(jLabel9)
                    .addComponent(jLabel10)
                    .addComponent(jLabel11)
                    .addComponent(btnNuevo, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(cbx_Provincia, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtdireccion, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cbx_Articulo, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(198, 198, 198)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel12)
                    .addComponent(jLabel13)
                    .addComponent(jLabel14)
                    .addComponent(Impuesto)
                    .addComponent(jLabel16))
                .addGap(12, 12, 12)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(txtmontototal)
                    .addComponent(txtimpuesto)
                    .addComponent(txtSubtotal)
                    .addComponent(cbx_TipodePago, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cbx_Estado, javax.swing.GroupLayout.Alignment.LEADING, 0, 210, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(599, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addGap(128, 128, 128)
                .addComponent(btnCalcular, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(293, 293, 293))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(113, 113, 113)
                .addComponent(jLabel1)
                .addGap(64, 64, 64)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtnombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2)
                            .addComponent(jLabel12)
                            .addComponent(cbx_Estado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(29, 29, 29)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtapellido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3)
                            .addComponent(jLabel13)
                            .addComponent(cbx_TipodePago, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(32, 32, 32)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(txt_telefono, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel14)
                            .addComponent(txtSubtotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(29, 29, 29)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(txtcodigo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel5))
                                .addGap(32, 32, 32)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel6)
                                    .addComponent(Fecha_de_Pedido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(37, 37, 37)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(Impuesto)
                                    .addComponent(txtimpuesto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(35, 35, 35)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel16)
                                    .addComponent(txtmontototal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel8)
                            .addComponent(cbx_Provincia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(29, 29, 29)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel9)
                            .addComponent(txtdireccion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(32, 32, 32)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel10)
                            .addComponent(cbx_Articulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(29, 29, 29)
                                .addComponent(jLabel11))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(37, 37, 37)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addGap(14, 14, 14)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(Fecha_de_Entrega, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(115, 115, 115)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnRegistrar)
                    .addComponent(btnNuevo)
                    .addComponent(btnCancelar)
                    .addComponent(btnCalcular))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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

        // 4. Guardar los datos del pedido en la base de datos
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
        Fecha_de_Pedido.setDate(new Date());
        Fecha_de_Entrega.setDate(new Date());
        cbx_Provincia.setSelectedIndex(0);
        cbx_TipodePago.setSelectedIndex(0);
        cbx_Estado.setSelectedIndex(0);
        cbx_Articulo.setSelectedIndex(-1);
    }//GEN-LAST:event_btnRegistrarActionPerformed

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
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
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
