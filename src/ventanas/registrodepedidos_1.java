package ventanas;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

    private void calcularMontoTotal() {
        String descripcion = txtDescripcion.getText();
        String[] lineas = descripcion.split("\n");
        double montoTotal = 0.0;

        for (String linea : lineas) {
            linea = linea.trim();
            if (!linea.isEmpty()) {
                // Asumimos un formato como "Cantidad x Nombre del Artículo (Talla)"
                // Necesitas adaptar esta lógica según el formato en que el administrador ingresará la descripción
                String[] partes = linea.split("x");
                if (partes.length == 2) {
                    try {
                        int cantidad = Integer.parseInt(partes[0].trim());
                        String nombreArticuloConDetalle = partes[1].trim();
                        // Extraer solo el nombre del artículo para buscar el precio
                        String nombreArticulo = nombreArticuloConDetalle.split("\\(")[0].trim();

                        if (preciosArticulos.containsKey(nombreArticulo)) {
                            montoTotal += cantidad * preciosArticulos.get(nombreArticulo);
                        } else {
                            JOptionPane.showMessageDialog(this, "No se encontró el precio para el artículo: " + nombreArticulo, "Advertencia", JOptionPane.WARNING_MESSAGE);
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

        txtmontototal.setText(String.format("%.2f", montoTotal));
    }

    public registrodepedidos_1() {
        initComponents();

        CargarSugerencias();
        CargarArticulos();

        CboArticulo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String articuloSeleccionado = (String) CboArticulo.getSelectedItem();
                if (articuloSeleccionado != null && !articuloSeleccionado.isEmpty()) {
                    // Verificar si el artículo ya está en el JTextArea
                    if (!txtDescripcion.getText().contains(articuloSeleccionado)) {
                        txtDescripcion.append(articuloSeleccionado + "\n");
                    }
                    CboArticulo.setSelectedIndex(-1); // Deseleccionar el artículo después de añadirlo
                }
            }
        });
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
        txtmontototal = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        btnRegistrar = new javax.swing.JButton();
        btnLimpiar = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();
        btnCalcular = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
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
        jLabel16.setText("Monto Total;");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(104, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGap(59, 59, 59)
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cmbCiudad, javax.swing.GroupLayout.PREFERRED_SIZE, 284, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel4)
                                    .addComponent(jLabel5)
                                    .addComponent(jLabel12)
                                    .addComponent(jLabel8))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jFechaEntrega, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(txtdireccion)
                                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(CboArticulo, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 283, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(182, 182, 182))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel4Layout.createSequentialGroup()
                            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                                    .addComponent(jLabel3)
                                    .addGap(9, 9, 9))
                                .addGroup(jPanel4Layout.createSequentialGroup()
                                    .addComponent(jLabel17)
                                    .addGap(9, 9, 9))
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                                    .addComponent(jLabel14)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED))
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                                    .addComponent(jLabel20)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jFechaPedido, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(txtcodigo, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(txtTelefono, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(txtApellidoC, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 276, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGap(723, 723, 723))
                        .addGroup(jPanel4Layout.createSequentialGroup()
                            .addGap(50, 50, 50)
                            .addComponent(jLabel1)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txtNombreC)
                            .addGap(513, 513, 513)))))
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(381, 381, 381)
                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel13))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(219, 219, 219)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                                .addGap(168, 168, 168)
                                .addComponent(jLabel16)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtmontototal, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel19)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(CmbEstado, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(45, 45, 45)
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cmbTipodePago, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(281, 281, 281)))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel13)
                    .addComponent(jLabel10))
                .addGap(55, 55, 55)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGap(5, 5, 5)
                                .addComponent(txtNombreC, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel1))
                        .addGap(21, 21, 21)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtApellidoC, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel14))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGap(4, 4, 4)
                                .addComponent(txtTelefono, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel17)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(txtcodigo, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel20)
                            .addComponent(jFechaPedido, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 32, Short.MAX_VALUE)
                        .addComponent(jLabel7))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel12)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGap(3, 3, 3)
                                .addComponent(jFechaEntrega, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(cmbCiudad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtdireccion, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4))
                        .addGap(17, 17, 17)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(CboArticulo, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 27, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(CmbEstado, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19)
                    .addComponent(jLabel6)
                    .addComponent(cmbTipodePago, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16)
                    .addComponent(txtmontototal, javax.swing.GroupLayout.DEFAULT_SIZE, 37, Short.MAX_VALUE))
                .addGap(45, 45, 45))
        );

        jPanel1.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 110, 1240, 510));

        jPanel3.setBackground(new java.awt.Color(0, 0, 0));
        jPanel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255), 5));
        jPanel3.setForeground(new java.awt.Color(204, 204, 204));

        btnRegistrar.setBackground(new java.awt.Color(0, 0, 0));
        btnRegistrar.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnRegistrar.setForeground(new java.awt.Color(255, 255, 255));
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
        btnLimpiar.setText("Limpiar");
        btnLimpiar.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255), 4));
        btnLimpiar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLimpiarActionPerformed(evt);
            }
        });

        btnCancelar.setBackground(new java.awt.Color(0, 0, 0));
        btnCancelar.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnCancelar.setForeground(new java.awt.Color(255, 255, 255));
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
                .addComponent(btnRegistrar, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(117, 117, 117)
                .addComponent(btnLimpiar, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(109, 109, 109)
                .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(115, 115, 115)
                .addComponent(btnCalcular, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(213, Short.MAX_VALUE))
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
                .addContainerGap(18, Short.MAX_VALUE))
        );

        jPanel1.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 650, 1240, 100));

        jLabel11.setText("jLabel11");
        jPanel1.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 10, -1, -1));

        jLabel9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/Opcion 7 (1,1).png"))); // NOI18N
        jLabel9.setText("jLabel9");
        jPanel1.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, 170, -1));

        jLabel15.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/b6c187a41f26ce3a29012f271ab820e2.jpg"))); // NOI18N
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
        // 1. Obtener los datos de los campos
        String nombreCliente = txtNombreC.getText().trim();
        String apellidoCliente = txtApellidoC.getText().trim();
        String telefonoCliente = txtTelefono.getText().trim();
        String idPedido = txtcodigo.getText().trim();

        Date fechaPedidoSeleccionada = jFechaPedido.getDate();
        Date fechaEntregaSeleccionada = jFechaEntrega.getDate();

        String provincia = (String) cmbCiudad.getSelectedItem();
        String direccion = txtdireccion.getText().trim();
        String descripcion = txtDescripcion.getText(); // Aquí obtenemos todos los artículos
        String tipoPago = (String) cmbTipodePago.getSelectedItem();
        String estado = (String) CmbEstado.getSelectedItem();

        // 2. Validar que todos los campos obligatorios estén llenos (incluyendo que se haya seleccionado al menos un artículo)
        if (nombreCliente.isEmpty() || apellidoCliente.isEmpty() || telefonoCliente.isEmpty()
                || idPedido.isEmpty() || fechaPedidoSeleccionada == null || provincia.equals("Seleccionar")
                || direccion.isEmpty() || tipoPago.equals("Seleccionar") || estado.equals("Seleccionar")
                || descripcion.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, complete todos los campos obligatorios y seleccione al menos un artículo.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 3. Verificar si el cliente existe en la tabla 'clientes' (INLINE)
        String queryCliente = "SELECT COUNT(*) FROM clientes WHERE nombre = ? AND apellido = ?";
        Connection conCliente = null;
        PreparedStatement pstmtCliente = null;
        ResultSet rsCliente = null;
        boolean clienteExiste = false;
        try {
            conCliente = Conexion_Chaos.conectar();
            if (conCliente != null) {
                pstmtCliente = conCliente.prepareStatement(queryCliente);
                pstmtCliente.setString(1, nombreCliente);
                pstmtCliente.setString(2, apellidoCliente);
                rsCliente = pstmtCliente.executeQuery();
                if (rsCliente.next()) {
                    clienteExiste = rsCliente.getInt(1) > 0;
                }
            } else {
                JOptionPane.showMessageDialog(this, "Error: No se pudo establecer la conexión a la base de datos para verificar el cliente.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al verificar el cliente: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return;
        } finally {
            try {
                if (rsCliente != null) {
                    rsCliente.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (pstmtCliente != null) {
                    pstmtCliente.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (conCliente != null) {
                    conCliente.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (!clienteExiste) {
            JOptionPane.showMessageDialog(this, "El cliente no está registrado. Por favor, registre al cliente primero.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 4. Validar el formato de las fechas
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String fechaPedido = sdf.format(fechaPedidoSeleccionada);
        String fechaEntrega = (fechaEntregaSeleccionada != null) ? sdf.format(fechaEntregaSeleccionada) : null;

        // 5. Insertar los datos del pedido en la tabla 'pedidos'
        String queryPedido = "INSERT INTO pedidos (nombreCliente, apellidoCliente, telefonoCliente, idPedido, fechaPedido, fechaEntrega, provincia, direccion, descripcion, tipoPago, estado) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conPedido = null;
        PreparedStatement pstmtPedido = null;
        try {
            conPedido = Conexion_Chaos.conectar();
            if (conPedido != null) {
                pstmtPedido = conPedido.prepareStatement(queryPedido);
                pstmtPedido.setString(1, nombreCliente);
                pstmtPedido.setString(2, apellidoCliente.isEmpty() ? null : apellidoCliente);
                pstmtPedido.setString(3, telefonoCliente);
                pstmtPedido.setString(4, idPedido);
                pstmtPedido.setString(5, fechaPedido);
                pstmtPedido.setString(6, fechaEntrega);
                pstmtPedido.setString(7, provincia);
                pstmtPedido.setString(8, direccion);
                pstmtPedido.setString(9, descripcion);
                pstmtPedido.setString(10, tipoPago);
                pstmtPedido.setString(11, estado);

                int filasPedidoAfectadas = pstmtPedido.executeUpdate();

                if (filasPedidoAfectadas > 0) {
                    JOptionPane.showMessageDialog(this, "Pedido registrado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    limpiarCamposPedido();
                } else {
                    JOptionPane.showMessageDialog(this, "Error al registrar el pedido.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Error: No se pudo establecer la conexión a la base de datos para registrar el pedido.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error de base de datos al registrar el pedido: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            try {
                if (pstmtPedido != null) {
                    pstmtPedido.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (conPedido != null) {
                    conPedido.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
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
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
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
    private javax.swing.JTextField txtmontototal;
    // End of variables declaration//GEN-END:variables

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
    
    }

}
