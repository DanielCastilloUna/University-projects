package reservas.presentation.estadistica;

import com.github.lgooddatepicker.components.DatePicker;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Vista (MVC) de la funcionalidad 8: Estadisticas.
 * Disenada con IntelliJ GUI Designer (ver View.form).
 */
public class View implements PropertyChangeListener {

    Controller controller;
    Model model;

    private JPanel panelPrincipal;
    private JTable tablaRecursos;
    private JTable tablaActividades;

    private JButton btnCargarR;
    private JButton btnCargarA;
    private JButton btnImprimir;

    private DatePicker desdeRecursos;
    private DatePicker hastaRecursos;

    private JScrollPane panelRecursos;
    private JScrollPane panelActividades;

    private DatePicker desdeActividades;
    private DatePicker hastaActividades;

    private JPanel panelGraficoRecursos;
    private JPanel panelGraficoActividades;
    private JLabel txtFechas;
    private JLabel textoFechas;
    private JLabel textoRecursos;
    private JLabel textoActividades;
    private JLabel textoEstadisticas;
    private JLabel txtEstadisticas;
    private JLabel textoGrafico;
    private JLabel txtGrafico;


    public View() {

        $$$setupUI$$$();
        btnCargarR.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    controller.cargarEstadisticaRecursos(desdeRecursos.getDate(), hastaRecursos.getDate());
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(panelPrincipal, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }

            }


        });
        btnCargarA.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    controller.cargarEstadisticaActividades(desdeActividades.getDate(), hastaActividades.getDate());
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(panelPrincipal, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnImprimir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    controller.print();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(panelPrincipal, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    public JPanel getPanel() {
        return panelPrincipal;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public void setModel(Model model) {
        this.model = model;
        model.addPropertyChangeListener(this);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case Model.ESTADISTICAS_RECURSOS:
                actualizarTablaRecursos();
                actualizarGraficoRecursos();
                break;
            case Model.ESTADISTICAS_ACTIVIDADES:
                actualizarTablaActividades();
                actualizarGraficoActividades();
                break;
        }
        panelPrincipal.revalidate();
    }

    private void actualizarTablaRecursos() {
        List<Map.Entry<String, Integer>> filas = new ArrayList<>(model.getEstadisticasRecursos().entrySet());
        int[] cols = {TableModelRecursos.CATEGORIA, TableModelRecursos.CANTIDAD};
        tablaRecursos.setModel(new TableModelRecursos(cols, filas));
    }

    private void actualizarTablaActividades() {
        List<Map.Entry<String, Integer>> filas = new ArrayList<>(model.getEstadisticasActividades().entrySet());
        int[] cols = {TableModelActividades.SEMANA, TableModelActividades.CANTIDAD};
        tablaActividades.setModel(new TableModelActividades(cols, filas));
    }

    private void actualizarGraficoRecursos() {
        DefaultCategoryDataset vertical_chart_dataset = new DefaultCategoryDataset();
        for (Map.Entry<String, Integer> entry : model.getEstadisticasRecursos().entrySet()) {
            vertical_chart_dataset.addValue(entry.getValue(), "Recurso", entry.getKey());
        }
        JFreeChart chart = ChartFactory.createBarChart("Recursos Usados", "", "Cantidad",
                vertical_chart_dataset, PlotOrientation.VERTICAL, true, true, false);

        estilizarGrafico(chart, new Color(255, 0, 0));

        panelGraficoRecursos.removeAll();
        panelGraficoRecursos.add(new ChartPanel(chart), new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW,
                null, null, null, 0, false));
        panelGraficoRecursos.revalidate();
        panelGraficoRecursos.repaint();
    }

    private void actualizarGraficoActividades() {
        DefaultCategoryDataset vertical_chart_dataset = new DefaultCategoryDataset();
        for (Map.Entry<String, Integer> entry : model.getEstadisticasActividades().entrySet()) {
            vertical_chart_dataset.addValue(entry.getValue(), "Semana", entry.getKey());
        }
        JFreeChart chart = ChartFactory.createBarChart("Actividades Realizadas", "", "Cantidad",
                vertical_chart_dataset, PlotOrientation.VERTICAL, true, true, false);

        estilizarGrafico(chart, new Color(70, 130, 180));

        panelGraficoActividades.removeAll();
        panelGraficoActividades.add(new ChartPanel(chart), new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW,
                null, null, null, 0, false));
        panelGraficoActividades.revalidate();
        panelGraficoActividades.repaint();

    }

    private void estilizarGrafico(JFreeChart chart, Color color) {
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(new Color(220, 220, 220));
        plot.setDomainGridlinesVisible(false);
        plot.setOutlineVisible(false);

        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, color);
        renderer.setShadowVisible(false);
        renderer.setMaximumBarWidth(0.12);
        renderer.setDrawBarOutline(false);

        chart.setBackgroundPaint(Color.WHITE);
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new GridLayoutManager(7, 7, new Insets(10, 10, 10, 10), -1, -1));
        panelRecursos = new JScrollPane();
        panelPrincipal.add(panelRecursos, new GridConstraints(4, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        tablaRecursos = new JTable();
        panelRecursos.setViewportView(tablaRecursos);
        panelActividades = new JScrollPane();
        panelPrincipal.add(panelActividades, new GridConstraints(4, 3, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        tablaActividades = new JTable();
        panelActividades.setViewportView(tablaActividades);
        panelGraficoRecursos = new JPanel();
        panelGraficoRecursos.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panelPrincipal.add(panelGraficoRecursos, new GridConstraints(6, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panelGraficoActividades = new JPanel();
        panelGraficoActividades.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panelPrincipal.add(panelGraficoActividades, new GridConstraints(6, 3, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        desdeActividades = new DatePicker();
        panelPrincipal.add(desdeActividades, new GridConstraints(2, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        desdeRecursos = new DatePicker();
        panelPrincipal.add(desdeRecursos, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        hastaActividades = new DatePicker();
        panelPrincipal.add(hastaActividades, new GridConstraints(2, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        hastaRecursos = new DatePicker();
        panelPrincipal.add(hastaRecursos, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        btnCargarR = new JButton();
        btnCargarR.setText("Cargar");
        panelPrincipal.add(btnCargarR, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnCargarA = new JButton();
        btnCargarA.setText("Cargar");
        panelPrincipal.add(btnCargarA, new GridConstraints(2, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnImprimir = new JButton();
        btnImprimir.setText("Imprimir PDF");
        panelPrincipal.add(btnImprimir, new GridConstraints(2, 6, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        textoRecursos = new JLabel();
        textoRecursos.setFont(textoRecursos.getFont().deriveFont(Font.BOLD));
        textoRecursos.setText("Recursos");
        panelPrincipal.add(textoRecursos, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        textoActividades = new JLabel();
        textoActividades.setFont(textoActividades.getFont().deriveFont(Font.BOLD));
        textoActividades.setText("Actividades");
        panelPrincipal.add(textoActividades, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        txtFechas = new JLabel();
        txtFechas.setText("Fechas Desde y Hasta");
        panelPrincipal.add(txtFechas, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        textoFechas = new JLabel();
        textoFechas.setText("Fechas Desde y Hasta");
        panelPrincipal.add(textoFechas, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        textoGrafico = new JLabel();
        textoGrafico.setFont(textoGrafico.getFont().deriveFont(Font.BOLD));
        textoGrafico.setText("Gráfico");
        panelPrincipal.add(textoGrafico, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        txtGrafico = new JLabel();
        txtGrafico.setFont(txtGrafico.getFont().deriveFont(Font.BOLD));
        txtGrafico.setText("Gráfico");
        panelPrincipal.add(txtGrafico, new GridConstraints(5, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        textoEstadisticas = new JLabel();
        textoEstadisticas.setFont(textoEstadisticas.getFont().deriveFont(Font.BOLD));
        textoEstadisticas.setText("Estadísticas");
        panelPrincipal.add(textoEstadisticas, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        txtEstadisticas = new JLabel();
        txtEstadisticas.setFont(txtEstadisticas.getFont().deriveFont(Font.BOLD));
        txtEstadisticas.setText("Estadísticas");
        panelPrincipal.add(txtEstadisticas, new GridConstraints(3, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panelPrincipal;
    }

}
