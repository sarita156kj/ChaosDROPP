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


public class registrodepedidos_1 extends javax.swing.JInternalFrame {

    private Map<String, Double> preciosArticulos = new HashMap<>();
    private static final String[] NOMBRES_ARTICULOS = {
        "Gorras de Verano", "Gorra ¨Not today¨", "Gorra Prospectos", "Gorra D", "Gorra de BAK con Rayas",
        "Gorra Original 1998", "Gorra 72 Negra", "Gorra 72 Blanca", "Gorra S", "Gorra de San Diego",
        "Gorra Nueva York", "Gorra Baltimore", "Gorra Cielo", "Gorra roja ¨Estilo Urbano¨", "Gorra de Estilo Antiguo",
        "Gorra Soho Nueva York", "Gorra ¨Copen¨", "Gorra WILDCAST", "Gorra Clásica", "Gorra Compañía",
        "Gorra de Brazil", "Abrigo de Gamuza", "Abrigo de Algodón", "Sudadera Scarface Negra", "Abrigo Reversible",
        "Sudadera Plumón", "Abrigo de Satén", "Abrigo (Drew)", "Abrigo Plumón", "Sudadera de Corte Asiático",
        "Sudadera Vintage", "Sudadera estilo UNC", "Sudadera Sostenible", "Abrigo XXXTentación", "Sudadera estilo Viejo",
        "Sudadera con Estampado", "Abrigo de Gamuza", "Sudadera ¨Los Angeles¨", "Sudadera estilo ¨Old Money¨",
        "Sudadera de Equipo ¨PSG¨", "Sudadera de Plumón", "Sudadera Universitaria", "Camiseta Roquera", "Camiseta Talla Extra Blanca",
        "Camiseta Guns N' Roses", "Camiseta Black Vibes", "Camiseta Angel Cat", "Camiseta Espacial", "Camiseta ROCKY",
        "Camiseta Scarface", "Camisa con Diseño", "Camiseta Estrella", "Camiseta Trythm Club", "Camiseta Básica",
        "Camiseta Esmeralda", "Camiseta DOBERMAN", "Camiseta Chanyoou", "Camiseta Metálica", "Camiseta de Angel",
        "Camiseta Ares Wings", "Camiseta STWD Records", "Camiseta Boxy Lavada Print", "Camiseta Print Manga Corta", "Pantalón Ancho Cargo",
        "Jeans de Denim Celeste", "Jeans de Denim Negros", "Jeans Azules ‘Skyline Rips", "Pantalón Gris estilo Cargo", "Jeans Celestes Desgastados",
        "Pantalón Negro Cargo", "Pantalón de Chándal Gris", "Pantalón parachute", "Pantalón Cargo Beige", "Pantalón Cargo Verde Oliva",
        "Pantalón Ancho", "Jeans Clásicos Celestes", "Jeans Morados Denim", "Jeans de Mezclilla Claros", "Pantalón Negro Clásico",
        "Pantalón Cargo Color Claro", "Pantalón Cargo Negro", "Pantalón Verde Claro", "Pantalón Oscuro Casual", "Pantalón Cargo Gris",
        "Shorts Deportivos", "Shorts Condensed", "Bermuda Cargo Lockandloas", "Shorts Urban Fray", "Bermuda Cargo Grey Utility",
        "Shorts Denim Classic Fade", "Shorts Distressed Trend", "Shorts Global BioRun", "Shorts Green Trim Flex", "Bermuda Pitillo",
        "Bermuda de Mezclilla", "Bermuda Nocturna Ajustable", "Shorts Denim Oscuro de Medianoche", "Bermuda de Explorador Urbano", "Bermuda Comando Urbano",
        "Shorts Ola Marina", "Shorts Clásico Azul Denim", "Bermuda Aventura", "Camisa de Rayas", "Camisa Hawayana",
        "Camisa Serenidad Elegante", "Camisa Urban Beach", "Camisa Clásica Moderna", "Camisa Jardín Secreto", "Camisa de Cuadros Grises Elegante",
        "Camisa Marea Nocturna", "Camisa Verde Oliva Casual", "Camisa Matices del Desierto", "Camisa Rock n’ Roll Clásico", "Camisa Estampada Tropical Noturna"
    };

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
                        String nombreArticulo = nombreArticuloConDetalle.split("\\(")[0].trim();
                        cantidadesPedido.put(nombreArticulo, cantidadesPedido.getOrDefault(nombreArticulo, 0) + cantidad);
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

    private void calcularMontoTotal() {
        String descripcion = txtDescripcion.getText();
        String[] lineas = descripcion.split("\n");
        double montoTotal = 0.0;

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
                            montoTotal += cantidad * preciosArticulos.get(nombreArticulo);
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
        txtsubtotal.setText(String.format("%.2f", montoTotal));
        calcularImpuesto(montoTotal); // Llamamos al método para calcular el impuesto
    }

    private void calcularImpuesto(double montoTotal) {
        String provinciaSeleccionada = (String) cmbCiudad.getSelectedItem();
        double impuesto = 0.0;
        java.util.List<String> provinciasExentas = java.util.Arrays.asList("Santo Domingo", "Puerto Plata", "Santiago", "La Vega", "Samaná");

        if (!provinciasExentas.contains(provinciaSeleccionada)) {
            // Puedes definir la lógica de tu impuesto aquí, por ejemplo, un porcentaje del monto total.
            // Para este ejemplo, estableceremos un impuesto fijo de 50 pesos.
            impuesto = 50.0;
        }

        txtimpuesto.setText(String.format("%.2f", impuesto));
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
        jPanel4 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jFechaPedido = new com.toedter.calendar.JDateChooser();
        jLabel6 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        cmbCiudad = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jFechaEntrega = new com.toedter.calendar.JDateChooser();
        jLabel1 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        CmbEstado = new javax.swing.JComboBox<>();
        jLabel13 = new javax.swing.JLabel();
        cmbTipodePago = new javax.swing.JComboBox<>();
        txtNombreC = new javax.swing.JTextField();
        txtApellidoC = new javax.swing.JTextField();
        txtTelefono = new javax.swing.JTextField();
        txtcodigo = new javax.swing.JTextField();
        txtdireccion = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtDescripcion = new javax.swing.JTextArea();
        jLabel8 = new javax.swing.JLabel();
        CboArticulo = new javax.swing.JComboBox<>();
        jLabel10 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        txtsubtotal = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        txtimpuesto = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        txtmontotoal = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        btnRegistrar = new javax.swing.JButton();
        btnLimpiar = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();
        btnCalcular = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(0, 0, 0));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel4.setBackground(new java.awt.Color(0, 0, 0));
        jPanel4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255), 6));
        jPanel4.setForeground(new java.awt.Color(255, 255, 255));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 2, 18)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("Descripción:");

        jLabel12.setFont(new java.awt.Font("Segoe UI", 2, 18)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setText("Fecha de entrega:");

        jFechaPedido.setForeground(new java.awt.Color(255, 255, 255));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 2, 18)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Tipo de pago:");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 2, 18)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Dirección:");

        jLabel7.setFont(new java.awt.Font("Segoe UI Light", 1, 13)); // NOI18N
        jLabel7.setText("Tipo");

        cmbCiudad.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        cmbCiudad.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Azua", "Baoruco", "Barahona", "Dajabón", "Distrito Nacional", "Duarte", "Elías Piña", "El Seibo", "Espaillat", "Hato Mayor", "Hermanas Mirabal   ", "Independencia", "La Altagracia", "La Romana", "La Vega", "María Trinidad Sánchez", "Monseñor Nouel", "Monte Cristi", "Monte Plata", "Pedernales", "Peravia", "Puerto Plata", "Samaná", "San Cristóbal", "San José de Ocoa", "San Juan", "San Pedro de Macorís", "Sánchez Ramírez", "Santiago", "Santiago Rodríguez   ", "Santo Domingo   ", "Valverde", " " }));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 2, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Provincia:");

        jLabel17.setFont(new java.awt.Font("Segoe UI", 2, 18)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(255, 255, 255));
        jLabel17.setText("Código pedido:");

        jLabel20.setFont(new java.awt.Font("Segoe UI", 2, 18)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(255, 255, 255));
        jLabel20.setText("Fecha:");

        jFechaEntrega.setForeground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 2, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Nombre:");

        jLabel14.setFont(new java.awt.Font("Segoe UI", 2, 18)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(255, 255, 255));
        jLabel14.setText("Apellido:");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 2, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Teléfono:");

        jLabel19.setFont(new java.awt.Font("Segoe UI", 2, 18)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(255, 255, 255));
        jLabel19.setText("Estado:");

        CmbEstado.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        CmbEstado.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "En almacén", "De camino", "Entregado", "Devuelto al Remitente", "Cancelado" }));

        jLabel13.setFont(new java.awt.Font("Segoe UI", 3, 36)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(255, 255, 255));
        jLabel13.setText("Registro de Pedidos");

        cmbTipodePago.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        cmbTipodePago.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Efectivo", "Tarjeta", "Transferencia" }));

        txtNombreC.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N

        txtApellidoC.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N

        txtTelefono.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N

        txtcodigo.setText("CH-1109");

        txtdireccion.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N

        txtDescripcion.setColumns(20);
        txtDescripcion.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        txtDescripcion.setRows(5);
        jScrollPane2.setViewportView(txtDescripcion);

        jLabel8.setFont(new java.awt.Font("Segoe UI", 2, 18)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("Artículo:");

        jLabel10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/icon-pds-360-w.jpg"))); // NOI18N
        jLabel10.setText("jLabel10");

        jLabel16.setFont(new java.awt.Font("Segoe UI", 2, 18)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(255, 255, 255));
        jLabel16.setText("Subtotal:");

        jLabel18.setFont(new java.awt.Font("Segoe UI", 2, 18)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(255, 255, 255));
        jLabel18.setText("Impuesto:");

        jLabel21.setFont(new java.awt.Font("Segoe UI", 2, 18)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(255, 255, 255));
        jLabel21.setText("Monto Total:");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(jPanel4Layout.createSequentialGroup()
                            .addGap(39, 39, 39)
                            .addComponent(jLabel3)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(txtTelefono, javax.swing.GroupLayout.PREFERRED_SIZE, 276, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                            .addGap(42, 42, 42)
                            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                                    .addComponent(jLabel1)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(txtNombreC, javax.swing.GroupLayout.PREFERRED_SIZE, 277, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                                    .addComponent(jLabel14)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(txtApellidoC, javax.swing.GroupLayout.PREFERRED_SIZE, 276, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGroup(jPanel4Layout.createSequentialGroup()
                            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jLabel17)
                                .addComponent(jLabel20))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jFechaPedido, javax.swing.GroupLayout.PREFERRED_SIZE, 276, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txtcodigo, javax.swing.GroupLayout.PREFERRED_SIZE, 276, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jFechaEntrega, javax.swing.GroupLayout.PREFERRED_SIZE, 276, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(43, 43, 43)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel4Layout.createSequentialGroup()
                            .addComponent(jLabel2)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(cmbCiudad, javax.swing.GroupLayout.PREFERRED_SIZE, 284, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel4Layout.createSequentialGroup()
                            .addComponent(jLabel4)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txtdireccion, javax.swing.GroupLayout.PREFERRED_SIZE, 283, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel5)
                            .addComponent(jLabel8))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(CboArticulo, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 283, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(33, 33, 33)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel19)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(CmbEstado, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(jPanel4Layout.createSequentialGroup()
                            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jLabel6)
                                .addComponent(jLabel16)
                                .addComponent(jLabel18))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(cmbTipodePago, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(txtsubtotal)
                                .addComponent(txtimpuesto, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(jPanel4Layout.createSequentialGroup()
                            .addComponent(jLabel21)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txtmontotoal, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel7)
                .addGap(569, 569, 569))
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(442, 442, 442)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel13)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(116, 116, 116)
                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 29, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addGap(60, 60, 60))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel4Layout.createSequentialGroup()
                                        .addGap(5, 5, 5)
                                        .addComponent(txtNombreC, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jLabel1))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(jPanel4Layout.createSequentialGroup()
                                        .addComponent(txtApellidoC, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(2, 2, 2))
                                    .addComponent(jLabel14))
                                .addGap(19, 19, 19)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(jPanel4Layout.createSequentialGroup()
                                        .addComponent(txtTelefono, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(2, 2, 2))
                                    .addComponent(jLabel3))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                                        .addComponent(jLabel17)
                                        .addGap(29, 29, 29)
                                        .addComponent(jLabel20))
                                    .addComponent(jFechaPedido, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel4Layout.createSequentialGroup()
                                        .addGap(3, 3, 3)
                                        .addComponent(txtcodigo, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel12)
                                    .addComponent(jFechaEntrega, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGap(5, 5, 5)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel4Layout.createSequentialGroup()
                                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(jLabel19)
                                            .addComponent(CmbEstado, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(jLabel6)
                                            .addComponent(cmbTipodePago, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(12, 12, 12)
                                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(txtsubtotal, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel16))
                                        .addGap(18, 18, 18)
                                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(jLabel18)
                                            .addComponent(txtimpuesto, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(18, 18, 18)
                                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(jLabel21)
                                            .addComponent(txtmontotoal, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(jPanel4Layout.createSequentialGroup()
                                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                .addComponent(txtdireccion, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(jLabel4))
                                            .addGroup(jPanel4Layout.createSequentialGroup()
                                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                    .addComponent(cmbCiudad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(jLabel2))
                                                .addGap(46, 46, 46)))
                                        .addGap(18, 18, 18)
                                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(CboArticulo, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel8))
                                        .addGap(18, 18, 18)
                                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel5)
                                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                        .addGap(57, 57, 57))))
        );

        jPanel1.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 100, 1240, 520));

        jPanel3.setBackground(new java.awt.Color(0, 0, 0));
        jPanel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255), 5));
        jPanel3.setForeground(new java.awt.Color(204, 204, 204));

        btnRegistrar.setBackground(new java.awt.Color(0, 0, 0));
        btnRegistrar.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnRegistrar.setForeground(new java.awt.Color(255, 255, 255));
        btnRegistrar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/íconos/crear-una-cuenta.png"))); // NOI18N
        btnRegistrar.setText("Registrar");
        btnRegistrar.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255), 4));
        btnRegistrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRegistrarActionPerformed(evt);
            }
        });

        btnLimpiar.setBackground(new java.awt.Color(0, 0, 0));
        btnLimpiar.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnLimpiar.setForeground(new java.awt.Color(255, 255, 255));
        btnLimpiar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/íconos/agregar-carpeta.png"))); // NOI18N
        btnLimpiar.setText("Nuevo");
        btnLimpiar.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255), 4));
        btnLimpiar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLimpiarActionPerformed(evt);
            }
        });

        btnCancelar.setBackground(new java.awt.Color(0, 0, 0));
        btnCancelar.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnCancelar.setForeground(new java.awt.Color(255, 255, 255));
        btnCancelar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/íconos/boton-x.png"))); // NOI18N
        btnCancelar.setText("Cancelar");
        btnCancelar.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255), 4));
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
        btnCalcular.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255), 4));
        btnCalcular.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCalcularActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(153, 153, 153)
                .addComponent(btnRegistrar, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(60, 60, 60)
                .addComponent(btnLimpiar, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(60, 60, 60)
                .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(38, 38, 38)
                .addComponent(btnCalcular, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(157, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnRegistrar, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnLimpiar, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCalcular, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(28, Short.MAX_VALUE))
        );

        jPanel1.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 650, 1240, 110));

        jLabel9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/Opcion 7 (1,1).png"))); // NOI18N
        jLabel9.setText("jLabel9");
        jPanel1.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, 170, -1));

        jLabel15.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/pexels-panos-and-marenia-stavrinos-106103914-9648161.jpg"))); // NOI18N
        jLabel15.setText("jLabel15");
        jPanel1.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1550, -1));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 824, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnRegistrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRegistrarActionPerformed
 // Primero, obtenemos las cantidades del pedido
    Map<String, Integer> cantidadesPedido = obtenerCantidadesPedido();
    if (cantidadesPedido == null) {
        return; // Hubo un error en el formato de la descripción
    }

    Connection cn = null;
    PreparedStatement pstCatalogo = null;
    PreparedStatement pstPedido = null;
    ResultSet rsCatalogo = null;

    try {
        cn = Conexion_Chaos.conectar();
        cn.setAutoCommit(false);

        // Verificamos el stock y actualizamos la tabla catalogo
        for (Map.Entry<String, Integer> entry : cantidadesPedido.entrySet()) {
            String nombreArticulo = entry.getKey();
            int cantidadPedida = entry.getValue();

            String consultaStock = "SELECT cantidad FROM catalogo WHERE nombre = ?";
            pstCatalogo = cn.prepareStatement(consultaStock);
            pstCatalogo.setString(1, nombreArticulo);
            rsCatalogo = pstCatalogo.executeQuery();

            if (rsCatalogo.next()) {
                int stockActual = rsCatalogo.getInt("cantidad");
                if (stockActual >= cantidadPedida) {
                    // Hay suficiente stock, procedemos a actualizar
                    int nuevoStock = stockActual - cantidadPedida;
                    String actualizarStock = "UPDATE catalogo SET cantidad = ? WHERE nombre = ?";
                    PreparedStatement pstActualizar = cn.prepareStatement(actualizarStock);
                    pstActualizar.setInt(1, nuevoStock);
                    pstActualizar.setString(2, nombreArticulo);
                    pstActualizar.executeUpdate();
                    pstActualizar.close();
                } else {
                    JOptionPane.showMessageDialog(this, "No hay suficiente stock para el artículo: " + nombreArticulo + "\nStock disponible: " + stockActual + ", Cantidad pedida: " + cantidadPedida, "Advertencia", JOptionPane.WARNING_MESSAGE);
                    cn.rollback(); // Revertimos la transacción
                    return; // Detenemos el registro del pedido
                }
            } else {
                JOptionPane.showMessageDialog(this, "El artículo: " + nombreArticulo + " no se encontró en el catálogo.", "Error", JOptionPane.ERROR_MESSAGE);
                cn.rollback(); // Revertimos la transacción
                return; // Detenemos el registro del pedido
            }
            if (pstCatalogo != null) pstCatalogo.close();
            if (rsCatalogo != null) rsCatalogo.close();
        }

        // Si llegamos aquí, significa que hay suficiente stock para todos los artículos
        // Ahora procedemos a insertar el pedido en la tabla de pedidos
        try {
            String sql = "INSERT INTO pedidos (codigo_pedido, nombre_cliente, apellido_cliente, telefono, direccion, provincia, fecha_pedido, fecha_entrega, descripcion, tipo_pago, estado, monto_total, impuesto) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
            pstPedido = cn.prepareStatement(sql);
            pstPedido.setString(1, txtcodigo.getText());
            pstPedido.setString(2, txtNombreC.getText());
            pstPedido.setString(3, txtApellidoC.getText());
            pstPedido.setString(4, txtTelefono.getText());
            pstPedido.setString(5, txtdireccion.getText());
            pstPedido.setString(6, (String) cmbCiudad.getSelectedItem());

            SimpleDateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd");
            Date fechaPedido = jFechaPedido.getDate();
            Date fechaEntrega = jFechaEntrega.getDate();

            if (fechaPedido != null) {
                pstPedido.setString(7, formatoFecha.format(fechaPedido));
            } else {
                pstPedido.setString(7, null); // O manejarlo según tus necesidades
            }

            if (fechaEntrega != null) {
                pstPedido.setString(8, formatoFecha.format(fechaEntrega));
            } else {
                pstPedido.setString(8, null); // O manejarlo según tus necesidades
            }

            pstPedido.setString(9, txtDescripcion.getText());
            pstPedido.setString(10, (String) cmbTipodePago.getSelectedItem());
            pstPedido.setString(11, (String) CmbEstado.getSelectedItem());
            pstPedido.setString(12, txtsubtotal.getText());
            pstPedido.setString(13, txtimpuesto.getText()); // Guardamos el valor del impuesto
            pstPedido.executeUpdate();
            JOptionPane.showMessageDialog(null, "Pedido registrado exitosamente.");
            limpiarFormulario();
            cn.commit(); // Confirmamos la transacción
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al registrar el pedido: " + e.getMessage());
            cn.rollback(); // Revertimos la transacción en caso de error al insertar el pedido
        } finally {
            if (pstPedido != null) try { pstPedido.close(); } catch (SQLException e) {}
        }

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error al conectar o al verificar el stock: " + e.getMessage());
        if (cn != null) {
            try {
                cn.rollback(); // Aseguramos la reversión en caso de error general
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Error al hacer rollback: " + ex.getMessage());
            }
        }
    } finally {
        if (cn != null) try { cn.setAutoCommit(true); cn.close(); } catch (SQLException e) {}
    }
}

private void limpiarFormulario() {
    txtNombreC.setText("");
    txtApellidoC.setText("");
    txtTelefono.setText("");
    txtdireccion.setText("");
    txtDescripcion.setText("");
    txtsubtotal.setText("");
    txtimpuesto.setText(""); // También limpiamos el campo de impuesto
    jFechaPedido.setDate(null);
    jFechaEntrega.setDate(null);
    cmbCiudad.setSelectedIndex(0);
    cmbTipodePago.setSelectedIndex(0);
    CmbEstado.setSelectedIndex(0);
    

    }//GEN-LAST:event_btnRegistrarActionPerformed

    private void btnLimpiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimpiarActionPerformed
        limpiarCamposPedido();
    }//GEN-LAST:event_btnLimpiarActionPerformed

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        int opcion = JOptionPane.showConfirmDialog(this, "¿Está seguro de que desea cancelar el registro?", "Confirmar Cancelación", JOptionPane.YES_NO_OPTION);
        if (opcion == JOptionPane.YES_OPTION) {
            this.dispose();

        }
    }//GEN-LAST:event_btnCancelarActionPerformed

    private void btnCalcularActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCalcularActionPerformed
    calcularMontoTotal();
    }//GEN-LAST:event_btnCalcularActionPerformed

    private void limpiarCamposPedido() {
        txtNombreC.setText("");
        txtApellidoC.setText("");
        txtTelefono.setText("");
        txtcodigo.setText("");
        jFechaPedido.setDate(null);; // Limpiar JDateChooser
        jFechaEntrega.setDate(null); // Establecer JDateChooser a null
        cmbCiudad.setSelectedIndex(0); // Restablecer JComboBox de provincia al primer elemento
        txtdireccion.setText("");
        txtDescripcion.setText(""); // Corregí la doble ".."
        cmbTipodePago.setSelectedIndex(0); // Restablecer JComboBox de tipo de pago al primer elemento
        CmbEstado.setSelectedIndex(0); // Restablecer JComboBox de estado al primer elemento    
    }

    private void CargarArticulos() {
        CboArticulo.setModel(new DefaultComboBoxModel<>(NOMBRES_ARTICULOS));
    }

  private void CargarSugerencias() {
        AutoCompleteDecorator.decorate(CboArticulo);
    }

private void cargarPreciosDesdeCatalogo() {
        Connection conexion = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conexion = Conexion_Chaos.conectar();
            System.out.println("Conexión a la base de datos exitosa."); // <-- Añade esta línea
            String sql = "SELECT nombre, precio FROM catalogo";
            pstmt = conexion.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                String nombreArticulo = rs.getString("nombre");
                double precio = rs.getDouble("precio");
                preciosArticulos.put(nombreArticulo, precio);
                System.out.println("Artículo cargado: " + nombreArticulo + " - Precio: " + precio); // <-- Añade esta línea
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar los precios del catálogo: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) {}
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) {}
            try { if (conexion != null) conexion.close(); } catch (SQLException e) {}
        }
    }
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
            java.util.logging.Logger.getLogger(registrodepedidos_1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(registrodepedidos_1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(registrodepedidos_1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(registrodepedidos_1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new registrodepedidos_1().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> CboArticulo;
    private javax.swing.JComboBox<String> CmbEstado;
    private javax.swing.JButton btnCalcular;
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnLimpiar;
    private javax.swing.JButton btnRegistrar;
    private javax.swing.JComboBox<String> cmbCiudad;
    private javax.swing.JComboBox<String> cmbTipodePago;
    private com.toedter.calendar.JDateChooser jFechaEntrega;
    private com.toedter.calendar.JDateChooser jFechaPedido;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextField txtApellidoC;
    private javax.swing.JTextArea txtDescripcion;
    private javax.swing.JTextField txtNombreC;
    private javax.swing.JTextField txtTelefono;
    private javax.swing.JTextField txtcodigo;
    private javax.swing.JTextField txtdireccion;
    private javax.swing.JTextField txtimpuesto;
    private javax.swing.JTextField txtmontotoal;
    private javax.swing.JTextField txtsubtotal;
    // End of variables declaration//GEN-END:variables


    }


