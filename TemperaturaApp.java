import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class TemperaturaApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> mostrarTabla());
    }

    public static void mostrarTabla() {
        List<RegistroTemperatura> registros = cargarRegistros("Temperaturas.csv");
        List<RegistroTemperatura> registrosOrdenados = registros.stream()
                .sorted(Comparator.comparing(RegistroTemperatura::getCiudad)
                        .thenComparing(RegistroTemperatura::getFecha))
                .collect(Collectors.toList());

        String[] columnas = {"Ciudad", "Fecha", "Temperatura"};
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
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
        JFrame frame = new JFrame("Temperaturas por Ciudad y Fecha");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(scrollPane);
        frame.setSize(500, 400);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static List<RegistroTemperatura> cargarRegistros(String archivo) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        try {
            return Files.lines(Paths.get(archivo))
                .skip(1)
                .map(linea -> {
                    String[] partes = linea.split(",");
                    return new RegistroTemperatura(
                        partes[0],
                        LocalDate.parse(partes[1], formatter),
                        Double.parseDouble(partes[2])
                    );
                })
                .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
} 