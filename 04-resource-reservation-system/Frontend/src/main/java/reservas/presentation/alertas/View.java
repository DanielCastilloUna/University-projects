package reservas.presentation.alertas;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import reservas.logic.Recurso;
import reservas.logic.Reserva;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.format.DateTimeFormatter;

/**
 * Area de Alertas: panel fijo a la derecha de la ventana principal (fuera de las pestanas).
 * Cada fila es una reserva de otro usuario; al hacer click en Ver se ve el detalle
 * y en Descartar se quita la alerta (con confirmacion).
 */
public class View implements PropertyChangeListener {

    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");

    private JPanel panelPrincipal;
    private JLabel lblTitulo;
    private JPanel alertasPanel;
    private JTable tablaAlertas;

    Controller controller;
    Model model;

    public View() {
        $$$setupUI$$$();

        lblTitulo.setFont(lblTitulo.getFont().deriveFont(Font.BOLD, 14f));
        tablaAlertas.setRowHeight(28);
        tablaAlertas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaAlertas.setRowSelectionAllowed(true);
        tablaAlertas.getTableHeader().setReorderingAllowed(false);
        tablaAlertas.setToolTipText("Ver detalle / Descartar");

        tablaAlertas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tablaAlertas.rowAtPoint(e.getPoint());
                int col = tablaAlertas.columnAtPoint(e.getPoint());
                if (row < 0 || col < 0) return;
                tablaAlertas.setRowSelectionInterval(row, row);
                switch (col) {
                    case TableModel.VER:
                        verDetalle(controller.ver(row));
                        break;
                    case TableModel.DESCARTAR:
                        descartar(row);
                        break;
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
            case Model.LIST:
                int[] cols = {TableModel.ALERTA, TableModel.VER, TableModel.DESCARTAR};
                tablaAlertas.setModel(new TableModel(cols, model.getList()));
                ajustarColumnas();
                break;
        }
        this.panelPrincipal.revalidate();
    }

    // Columnas de los iconos angostas y centradas; el resumen ocupa el resto
    private void ajustarColumnas() {
        DefaultTableCellRenderer iconoCentrado = new DefaultTableCellRenderer() {
            @Override
            protected void setValue(Object value) {
                setText("");
                setIcon(value instanceof Icon ? (Icon) value : null);
            }
        };
        iconoCentrado.setHorizontalAlignment(SwingConstants.CENTER);
        TableColumnModel columnas = tablaAlertas.getColumnModel();
        for (int col : new int[]{TableModel.VER, TableModel.DESCARTAR}) {
            columnas.getColumn(col).setCellRenderer(iconoCentrado);
            columnas.getColumn(col).setMinWidth(44);
            columnas.getColumn(col).setMaxWidth(44);
        }
    }

    private void descartar(int row) {
        Reserva reserva = controller.ver(row);
        int opcion = JOptionPane.showConfirmDialog(panelPrincipal,
                reserva.getActividad(), "Descartar?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (opcion == JOptionPane.YES_OPTION) {
            controller.descartar(row);
        }
    }

    private void verDetalle(Reserva reserva) {
        JPanel panelDetalle = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(3, 5, 3, 5);
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.HORIZONTAL;

        String funcionario = reserva.getFuncionario() == null ? "" : reserva.getFuncionario().getNombre();
        agregarFila(panelDetalle, constraints, 0, "Encargado:", funcionario);
        agregarFila(panelDetalle, constraints, 1, "Fecha:", reserva.getFecha().toString());
        agregarFila(panelDetalle, constraints, 2, "Desde:", reserva.getHoraInicio().format(HORA));
        agregarFila(panelDetalle, constraints, 3, "Hasta:", reserva.getHoraFin().format(HORA));

        DefaultListModel<String> recursosModel = new DefaultListModel<>();
        if (reserva.getRecursosAsignados() != null) {
            for (Recurso recurso : reserva.getRecursosAsignados()) {
                recursosModel.addElement(recurso.getDescripcion());
            }
        }
        JList<String> listaRecursos = new JList<>(recursosModel);
        JScrollPane scrollRecursos = new JScrollPane(listaRecursos);
        scrollRecursos.setPreferredSize(new Dimension(200, 120));
        constraints.gridx = 0;
        constraints.gridy = 4;
        constraints.weightx = 0;
        panelDetalle.add(new JLabel("Recursos"), constraints);
        constraints.gridx = 1;
        constraints.weightx = 1;
        panelDetalle.add(scrollRecursos, constraints);

        JOptionPane.showMessageDialog(panelPrincipal, panelDetalle, reserva.getActividad(), JOptionPane.PLAIN_MESSAGE);
    }

    private void agregarFila(JPanel panel, GridBagConstraints constraints, int fila, String etiqueta, String valor) {
        constraints.gridy = fila;
        constraints.gridx = 0;
        constraints.weightx = 0;
        panel.add(new JLabel(etiqueta), constraints);
        constraints.gridx = 1;
        constraints.weightx = 1;
        panel.add(new JLabel(valor), constraints);
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
        panelPrincipal.setLayout(new GridLayoutManager(2, 1, new Insets(10, 10, 10, 10), -1, 10));

        lblTitulo = new JLabel();
        lblTitulo.setText("Alertas");
        panelPrincipal.add(lblTitulo, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));

        alertasPanel = new JPanel();
        alertasPanel.setLayout(new GridLayoutManager(1, 1, new Insets(5, 5, 5, 5), -1, -1));
        panelPrincipal.add(alertasPanel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        alertasPanel.setBorder(BorderFactory.createEtchedBorder());

        final JScrollPane scrollPane1 = new JScrollPane();
        alertasPanel.add(scrollPane1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(250, -1), null, 0, false));
        tablaAlertas = new JTable();
        scrollPane1.setViewportView(tablaAlertas);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panelPrincipal;
    }
}
