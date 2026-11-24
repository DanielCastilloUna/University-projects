package reservas.presentation.calendarizacion;

import com.github.lgooddatepicker.components.DatePicker;
import reservas.logic.CategoriaRecurso;
import reservas.logic.Recurso;
import reservas.logic.Service;
import reservas.presentation.CeldaMatrizRenderer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class View implements PropertyChangeListener {

    private JPanel panelPrincipal;
    private DatePicker fechaFld;
    private JComboBox categoriaFld;
    private JButton cargarFld;
    private JButton imprimirFld;
    private JTable matrizTable;

    Controller controller;
    Model model;

    public View() {
        $$$setupUI$$$();
        CeldaMatrizRenderer.aplicarA(matrizTable);

        List<CategoriaRecurso> categorias = Service.instance().listarCategorias();
        categoriaFld.setModel(new DefaultComboBoxModel<>(categorias.toArray()));
        fechaFld.setDateToToday();

        cargarFld.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (fechaFld.getDate() == null || categoriaFld.getSelectedItem() == null) {
                    JOptionPane.showMessageDialog(panelPrincipal, "Seleccione fecha y categoría", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                CategoriaRecurso categoria = (CategoriaRecurso) categoriaFld.getSelectedItem();
                controller.cargar(fechaFld.getDate(), categoria);
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
        panelPrincipal.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(3, 5, new java.awt.Insets(10, 10, 10, 10), -1, -1));

        JLabel lblFecha = new JLabel("Fecha");
        panelPrincipal.add(lblFecha, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));

        fechaFld = new DatePicker();
        panelPrincipal.add(fechaFld, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));

        JLabel lblCategoria = new JLabel("Categoría");
        panelPrincipal.add(lblCategoria, new com.intellij.uiDesigner.core.GridConstraints(0, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));

        categoriaFld = new JComboBox();
        panelPrincipal.add(categoriaFld, new com.intellij.uiDesigner.core.GridConstraints(0, 3, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));

        cargarFld = new JButton("Cargar");
        panelPrincipal.add(cargarFld, new com.intellij.uiDesigner.core.GridConstraints(1, 3, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));

        imprimirFld = new JButton("Imprimir");
        panelPrincipal.add(imprimirFld, new com.intellij.uiDesigner.core.GridConstraints(1, 4, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));

        JScrollPane scrollPane = new JScrollPane();
        panelPrincipal.add(scrollPane, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 5, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));

        matrizTable = new JTable();
        scrollPane.setViewportView(matrizTable);
    }

    public JPanel getPanel() {
        return panelPrincipal;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public void mostrarFecha(java.time.LocalDate fecha, CategoriaRecurso categoriaSugerida) {
        fechaFld.setDate(fecha);
        if (categoriaSugerida != null) {
            for (int i = 0; i < categoriaFld.getItemCount(); i++) {
                CategoriaRecurso c = (CategoriaRecurso) categoriaFld.getItemAt(i);
                if (c != null && c.getId().equals(categoriaSugerida.getId())) {
                    categoriaFld.setSelectedItem(c);
                    break;
                }
            }
        }
        refrescar();
    }

    public void refrescar() {
        Object seleccionActual = categoriaFld.getSelectedItem();
        List<CategoriaRecurso> categorias = Service.instance().listarCategorias();
        categoriaFld.setModel(new DefaultComboBoxModel<>(categorias.toArray()));
        if (seleccionActual != null && categorias.stream().anyMatch(c -> c.getId().equals(((CategoriaRecurso) seleccionActual).getId()))) {
            categoriaFld.setSelectedItem(seleccionActual);
        }
        if (fechaFld.getDate() == null) {
            fechaFld.setDateToToday();
        }
        CategoriaRecurso categoria = (CategoriaRecurso) categoriaFld.getSelectedItem();
        if (categoria != null) {
            controller.cargar(fechaFld.getDate(), categoria);
        }
    }

    public void setModel(Model model) {
        this.model = model;
        model.addPropertyChangeListener(this);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (Model.MATRIZ.equals(evt.getPropertyName())) {
            List<Recurso> recursos = model.getRecursos();
            List<LocalTime> horas = model.getHoras();
            String[][] celdas = model.getCeldas();

            String[] columnas = new String[recursos.size() + 1];
            columnas[0] = "Hora";
            for (int i = 0; i < recursos.size(); i++) {
                columnas[i + 1] = recursos.get(i).getDescripcion();
            }

            DateTimeFormatter formato = DateTimeFormatter.ofPattern("HH:mm");
            Object[][] filas = new Object[horas.size()][recursos.size() + 1];
            for (int f = 0; f < horas.size(); f++) {
                filas[f][0] = horas.get(f).format(formato);
                for (int c = 0; c < recursos.size(); c++) {
                    filas[f][c + 1] = celdas[f][c];
                }
            }

            matrizTable.setModel(new DefaultTableModel(filas, columnas));
        }
        this.panelPrincipal.revalidate();
    }
}