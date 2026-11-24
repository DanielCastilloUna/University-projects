package reservas.presentation.actividad;

import com.github.lgooddatepicker.components.DatePicker;
import reservas.presentation.CeldaMatrizRenderer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

public class View implements PropertyChangeListener {

    private JPanel panelPrincipal;
    private DatePicker fechaFld;
    private JButton cargarFld;
    private JButton imprimirFld;
    private JTable matrizTable;

    Controller controller;
    Model model;

    public View() {
        $$$setupUI$$$();
        CeldaMatrizRenderer.aplicarA(matrizTable);

        fechaFld.setDateToToday();

        cargarFld.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (fechaFld.getDate() == null) {
                    JOptionPane.showMessageDialog(panelPrincipal, "Seleccione una fecha de referencia", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                controller.cargar(fechaFld.getDate());
            }
        });

        imprimirFld.addActionListener(new ActionListener() {
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

    private void $$$setupUI$$$() {
        panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(2, 4, new java.awt.Insets(10, 10, 10, 10), -1, -1));

        JLabel lblFecha = new JLabel("Fecha de referencia");
        panelPrincipal.add(lblFecha, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));

        fechaFld = new DatePicker();
        panelPrincipal.add(fechaFld, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));

        cargarFld = new JButton("Cargar");
        panelPrincipal.add(cargarFld, new com.intellij.uiDesigner.core.GridConstraints(0, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));

        imprimirFld = new JButton("Imprimir");
        panelPrincipal.add(imprimirFld, new com.intellij.uiDesigner.core.GridConstraints(0, 3, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));

        JScrollPane scrollPane = new JScrollPane();
        panelPrincipal.add(scrollPane, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 4, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));

        matrizTable = new JTable();
        scrollPane.setViewportView(matrizTable);
    }

    public JPanel getPanel() {
        return panelPrincipal;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public void refrescar() {
        if (fechaFld.getDate() == null) {
            fechaFld.setDateToToday();
        }
        controller.cargar(fechaFld.getDate());
    }

    public void mostrarFecha(LocalDate fecha) {
        fechaFld.setDate(fecha);
        refrescar();
    }

    public void setModel(Model model) {
        this.model = model;
        model.addPropertyChangeListener(this);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (Model.MATRIZ.equals(evt.getPropertyName())) {
            List<LocalDate> dias = model.getDias();
            List<Integer> horas = model.getHoras();
            String[][] celdas = model.getCeldas();

            String[] columnas = new String[dias.size() + 1];
            columnas[0] = "Hora";
            DateTimeFormatter formatoDia = DateTimeFormatter.ofPattern("dd/MM");
            for (int i = 0; i < dias.size(); i++) {
                String nombreDia = dias.get(i).getDayOfWeek().getDisplayName(TextStyle.SHORT, new Locale("es"));
                columnas[i + 1] = nombreDia + " " + dias.get(i).format(formatoDia);
            }

            Object[][] filas = new Object[horas.size()][dias.size() + 1];
            for (int f = 0; f < horas.size(); f++) {
                filas[f][0] = String.format("%02d:00", horas.get(f));
                for (int c = 0; c < dias.size(); c++) {
                    filas[f][c + 1] = celdas[f][c];
                }
            }

            matrizTable.setModel(new DefaultTableModel(filas, columnas));
        }
        this.panelPrincipal.revalidate();
    }
}