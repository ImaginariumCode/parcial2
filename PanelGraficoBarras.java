import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class PanelGraficoBarras extends JPanel {
    private Map<String, Double> datos;

    public PanelGraficoBarras(Map<String, Double> datos) {
        this.datos = datos;
    }

    public void setDatos(Map<String, Double> datos) {
        this.datos = datos;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int width = getWidth();
        int height = getHeight();
        int n = datos.size();
        if (n == 0) return;
        int barWidth = width / (n * 2);

        double max = datos.values().stream().mapToDouble(Double::doubleValue).max().orElse(1);

        int i = 0;
        for (Map.Entry<String, Double> entry : datos.entrySet()) {
            int barHeight = (int) ((entry.getValue() / max) * (height - 50));
            int x = (i * 2 + 1) * barWidth;
            int y = height - barHeight - 30;
            g.setColor(Color.BLUE);
            g.fillRect(x, y, barWidth, barHeight);
            g.setColor(Color.BLACK);
            g.drawRect(x, y, barWidth, barHeight);
            g.drawString(entry.getKey(), x, height - 10);
            g.drawString(String.format("%.1f", entry.getValue()), x, y - 5);
            i++;
        }
    }
} 