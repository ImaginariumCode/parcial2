import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class TemperaturaVentana extends JFrame {
    private final List<RegistroTemperatura> registros;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private PanelGraficoBarras panelGrafico;
    private JTextField txtFechaInicio;
    private JTextField txtFechaFin;
    private JTextField txtFechaConsulta;
    private JTextArea lblResultado;

    public TemperaturaVentana() {
        setTitle("Temperaturas por Ciudad y Fecha");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 800);
        setLocationRelativeTo(null);

        registros = TemperaturaApp.cargarRegistros("Temperaturas.csv");
        List<RegistroTemperatura> registrosOrdenados = registros.stream()
                .sorted(Comparator.comparing(RegistroTemperatura::getCiudad)
                        .thenComparing(RegistroTemperatura::getFecha))
                .collect(Collectors.toList());

        // Panel de la tabla
        String[] columnas = {"Ciudad", "Fecha", "Temperatura"};
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0);
        for (RegistroTemperatura r : registrosOrdenados) {
            Object[] fila = {
                    r.getCiudad(),
                    r.getFecha().format(formatter),
                    r.getTemperatura()
            };
            modelo.addRow(fila);
        }
        JTable tabla = new JTable(modelo);
        JScrollPane scrollPane = new JScrollPane(tabla);

        // Panel del gráfico de barras con controles de fecha
        JPanel panelControles = new JPanel();
        panelControles.add(new JLabel("Fecha inicio (dd/MM/yyyy):"));
        txtFechaInicio = new JTextField(8);
        panelControles.add(txtFechaInicio);
        panelControles.add(new JLabel("Fecha fin (dd/MM/yyyy):"));
        txtFechaFin = new JTextField(8);
        panelControles.add(txtFechaFin);
        JButton btnActualizar = new JButton("Actualizar gráfica");
        panelControles.add(btnActualizar);

        panelGrafico = new PanelGraficoBarras(calcularPromedios(registros));
        JPanel panelGraficaCompleto = new JPanel(new BorderLayout());
        panelGraficaCompleto.add(panelControles, BorderLayout.NORTH);
        panelGraficaCompleto.add(panelGrafico, BorderLayout.CENTER);

        btnActualizar.addActionListener((ActionEvent e) -> actualizarGrafico());

        // Panel de consulta de ciudad más y menos calurosa
        JPanel panelConsulta = new JPanel();
        panelConsulta.add(new JLabel("Fecha (dd/MM/yyyy):"));
        txtFechaConsulta = new JTextField(8);
        panelConsulta.add(txtFechaConsulta);
        JButton btnConsultar = new JButton("Consultar");
        panelConsulta.add(btnConsultar);
        lblResultado = new JTextArea(6, 40);
        lblResultado.setEditable(false);
        lblResultado.setLineWrap(true);
        lblResultado.setWrapStyleWord(true);
        JScrollPane resultadoScroll = new JScrollPane(lblResultado);
        resultadoScroll.setPreferredSize(new Dimension(600, 120));
        panelConsulta.add(resultadoScroll);
        btnConsultar.addActionListener((ActionEvent e) -> consultarCalurosa());

        JPanel panelTablaCompleto = new JPanel(new BorderLayout());
        panelTablaCompleto.add(scrollPane, BorderLayout.CENTER);
        panelTablaCompleto.add(panelConsulta, BorderLayout.SOUTH);

        // Pestañas con títulos personalizados
        JLabel tabTituloTabla = new JLabel("Tabla");
        tabTituloTabla.setForeground(new Color(218, 165, 32)); // Dorado

        JLabel tabTituloGrafica = new JLabel("Gráfica");
        tabTituloGrafica.setForeground(new Color(128, 0, 128)); // Morado

        JTabbedPane pestañas = new JTabbedPane();
        pestañas.addTab(null, panelTablaCompleto);
        pestañas.setTabComponentAt(0, tabTituloTabla);
        pestañas.addTab(null, panelGraficaCompleto);
        pestañas.setTabComponentAt(1, tabTituloGrafica);

        add(pestañas, BorderLayout.CENTER);
    }

    private Map<String, Double> calcularPromedios(List<RegistroTemperatura> lista) {
        return lista.stream()
                .collect(Collectors.groupingBy(
                        RegistroTemperatura::getCiudad,
                        Collectors.averagingDouble(RegistroTemperatura::getTemperatura)
                ));
    }

    private void actualizarGrafico() {
        try {
            LocalDate inicio = LocalDate.parse(txtFechaInicio.getText(), formatter);
            LocalDate fin = LocalDate.parse(txtFechaFin.getText(), formatter);
            List<RegistroTemperatura> filtrados = registros.stream()
                    .filter(r -> !r.getFecha().isBefore(inicio) && !r.getFecha().isAfter(fin))
                    .collect(Collectors.toList());
            panelGrafico.setDatos(calcularPromedios(filtrados));
            panelGrafico.repaint();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Fechas inválidas. Usa el formato dd/MM/yyyy (ejemplo: 07/01/2024)", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void consultarCalurosa() {
        try {
            LocalDate fecha = LocalDate.parse(txtFechaConsulta.getText(), formatter);
            List<RegistroTemperatura> filtrados = registros.stream()
                    .filter(r -> r.getFecha().equals(fecha))
                    .collect(Collectors.toList());
            if (filtrados.isEmpty()) {
                lblResultado.setText("No hay datos para esa fecha");
                return;
            }
            RegistroTemperatura max = filtrados.get(0);
            RegistroTemperatura min = filtrados.get(0);
            for (RegistroTemperatura r : filtrados) {
                if (r.getTemperatura() > max.getTemperatura()) {
                    max = r;
                }
                if (r.getTemperatura() < min.getTemperatura()) {
                    min = r;
                }
            }
            lblResultado.setText("Más calurosa: " + max.getCiudad() + " (" + max.getTemperatura() + "°C)\n" +
                "Menos calurosa: " + min.getCiudad() + " (" + min.getTemperatura() + "°C)");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Fecha inválida. Usa el formato dd/MM/yyyy (ejemplo: 07/01/2024)", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
} 