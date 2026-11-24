package reservas.presentation.reserva;

import com.github.lgooddatepicker.components.DatePicker;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import reservas.logic.CategoriaRecurso;
import reservas.logic.Funcionario;
import reservas.logic.Recurso;
import reservas.logic.Reserva;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class View implements PropertyChangeListener {

    private JPanel panelPrincipal;
    private JTextField promptIAFld;
    private JButton extraerAIBtn;

    private JTextField actividadFld;
    private DatePicker fechaFld;
    private JComboBox<LocalTime> horaInicioFld;
    private JComboBox<LocalTime> horaFinFld;

    private JList<CategoriaRecurso> categoriasList;
    private JButton reservarBtn;
    private JButton cancelarBtn;
    private JButton limpiarBtn;
    private JTable misReservasTable;
    private JButton imprimirBtn;

    private JPanel iaPanel;
    private JPanel nuevaReservaPanel;
    private JPanel formularioPanel;
    private JPanel datos;
    private JPanel categoriasPanel;
    private JPanel botonesReserva;
    private JPanel reservasPanel;
    private JPanel botonesReservas;

    private Controller controller;
    private Model model;
    private Funcionario funcionarioLogueado; // Inyectado al iniciar sesión
    private java.util.function.Consumer<Reserva> onReservaCreada;
    private String idEnEdicion; // != null mientras se está modificando una reserva existente

    public View() {
        $$$setupUI$$$();

        categoriasList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        habilitarSeleccionPorClic(categoriasList);

        extraerAIBtn.addActionListener(e -> {
            controller.extraerDatosDesdeIA(promptIAFld.getText());
        });

        reservarBtn.addActionListener(e -> {
            try {
                String actividad = actividadFld.getText();
                LocalDate fecha = fechaFld.getDate();
                LocalTime hInicio = (LocalTime) horaInicioFld.getSelectedItem();
                LocalTime hFin = (LocalTime) horaFinFld.getSelectedItem();
                if (fecha == null) {
                    throw new Exception("Seleccione la fecha de la reserva.");
                }
                if (hInicio == null || hFin == null) {
                    throw new Exception("Seleccione hora de inicio y hora de fin.");
                }
                List<CategoriaRecurso> seleccionadas = categoriasList.getSelectedValuesList();

                Reserva creada;
                if (idEnEdicion != null) {
                    creada = controller.modificar(idEnEdicion, funcionarioLogueado, actividad, fecha, hInicio, hFin, seleccionadas);
                    JOptionPane.showMessageDialog(panelPrincipal, "Reserva modificada exitosamente.");
                } else {
                    creada = controller.reservar(funcionarioLogueado, actividad, fecha, hInicio, hFin, seleccionadas);
                    JOptionPane.showMessageDialog(panelPrincipal, "Reserva realizada exitosamente.");
                }
                if (onReservaCreada != null && creada != null) {
                    onReservaCreada.accept(creada);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(panelPrincipal, ex.getMessage(), "Error al reservar", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelarBtn.addActionListener(e -> {
            int row = misReservasTable.getSelectedRow();
            if (row >= 0) {
                Reserva seleccionada = model.getMisReservas().get(row);
                try {
                    controller.cancelar(seleccionada.getId(), funcionarioLogueado);
                    JOptionPane.showMessageDialog(panelPrincipal, "Reserva cancelada.");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(panelPrincipal, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        limpiarBtn.addActionListener(e -> limpiarCampos());

        misReservasTable.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            int row = misReservasTable.getSelectedRow();
            if (row >= 0 && model.getMisReservas() != null && row < model.getMisReservas().size()) {
                cargarParaEdicion(model.getMisReservas().get(row));
            }
        });


        imprimirBtn.addActionListener(e -> {
            try {
                controller.print();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(panelPrincipal, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void createUIComponents() {
        horaInicioFld = new JComboBox<>(generarHorasDelDia());
        horaFinFld = new JComboBox<>(generarHorasDelDia());
        javax.swing.ListCellRenderer<Object> renderHora12 = (list, value, index, isSelected, cellHasFocus) -> {
            JLabel label = new JLabel(value instanceof LocalTime ? formatearHora12((LocalTime) value) : "");
            label.setOpaque(true);
            label.setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());
            label.setForeground(isSelected ? list.getSelectionForeground() : list.getForeground());
            return label;
        };
        horaInicioFld.setRenderer(renderHora12);
        horaFinFld.setRenderer(renderHora12);
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new GridLayoutManager(3, 1, new Insets(10, 10, 10, 10), -1, 10));

        iaPanel = new JPanel();
        iaPanel.setLayout(new GridLayoutManager(1, 3, new Insets(5, 5, 5, 5), -1, -1));
        panelPrincipal.add(iaPanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        iaPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Asistente de reserva"));

        final JLabel lblFrase = new JLabel();
        lblFrase.setText("Frase para IA:");
        iaPanel.add(lblFrase, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));

        promptIAFld = new JTextField();
        iaPanel.add(promptIAFld, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));

        extraerAIBtn = new JButton();
        extraerAIBtn.setText("Extraer con IA");
        iaPanel.add(extraerAIBtn, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));

        nuevaReservaPanel = new JPanel();
        nuevaReservaPanel.setLayout(new GridLayoutManager(2, 1, new Insets(5, 5, 5, 5), -1, 5));
        panelPrincipal.add(nuevaReservaPanel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        nuevaReservaPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Nueva reserva"));

        formularioPanel = new JPanel();
        formularioPanel.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), 10, -1));
        nuevaReservaPanel.add(formularioPanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));

        datos = new JPanel();
        datos.setLayout(new GridLayoutManager(4, 2, new Insets(5, 5, 5, 5), -1, -1));
        formularioPanel.add(datos, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        datos.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Datos de la reserva"));

        final JLabel lblActividad = new JLabel();
        lblActividad.setText("Actividad:");
        datos.add(lblActividad, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));

        actividadFld = new JTextField();
        datos.add(actividadFld, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));

        final JLabel lblFecha = new JLabel();
        lblFecha.setText("Fecha:");
        datos.add(lblFecha, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));

        fechaFld = new DatePicker();
        datos.add(fechaFld, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));

        final JLabel lblHoraInicio = new JLabel();
        lblHoraInicio.setText("Hora inicio:");
        datos.add(lblHoraInicio, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        datos.add(horaInicioFld, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));

        final JLabel lblHoraFin = new JLabel();
        lblHoraFin.setText("Hora fin:");
        datos.add(lblHoraFin, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        datos.add(horaFinFld, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));

        categoriasPanel = new JPanel();
        categoriasPanel.setLayout(new GridLayoutManager(1, 1, new Insets(5, 5, 5, 5), -1, -1));
        formularioPanel.add(categoriasPanel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        categoriasPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Categorias requeridas (selección múltiple)"));

        final JScrollPane scrollPaneCategorias = new JScrollPane();
        categoriasPanel.add(scrollPaneCategorias, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        categoriasList = new JList();
        scrollPaneCategorias.setViewportView(categoriasList);

        botonesReserva = new JPanel();
        botonesReserva.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        nuevaReservaPanel.add(botonesReserva, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));

        reservarBtn = new JButton();
        reservarBtn.setText("Reservar");
        botonesReserva.add(reservarBtn, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));

        cancelarBtn = new JButton();
        cancelarBtn.setText("Cancelar reserva seleccionada");
        botonesReserva.add(cancelarBtn, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));

        limpiarBtn = new JButton();
        limpiarBtn.setText("Limpiar");
        botonesReserva.add(limpiarBtn, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));

        reservasPanel = new JPanel();
        reservasPanel.setLayout(new GridLayoutManager(2, 1, new Insets(5, 5, 5, 5), -1, 5));
        panelPrincipal.add(reservasPanel, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(-1, 220), null, 0, false));
        reservasPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Mis reservas"));

        final JScrollPane scrollPaneReservas = new JScrollPane();
        reservasPanel.add(scrollPaneReservas, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        misReservasTable = new JTable();
        scrollPaneReservas.setViewportView(misReservasTable);

        botonesReservas = new JPanel();
        botonesReservas.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        reservasPanel.add(botonesReservas, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));

        imprimirBtn = new JButton();
        imprimirBtn.setText("Imprimir PDF");
        botonesReservas.add(imprimirBtn, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panelPrincipal;
    }

    private void habilitarSeleccionPorClic(JList<?> lista) {
        for (java.awt.event.MouseListener ml : lista.getMouseListeners()) {
            lista.removeMouseListener(ml);
        }
        lista.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int index = lista.locationToIndex(e.getPoint());
                if (index < 0) return;
                if (lista.isSelectedIndex(index)) {
                    lista.removeSelectionInterval(index, index);
                } else {
                    lista.addSelectionInterval(index, index);
                }
            }
        });
    }

    // Debe coincidir con el horario visible en Calendarización y Actividades (8:00 a 22:00),
    // para que ninguna reserva quede fuera de esas pantallas.
    private static final int HORA_INICIO_DIA = 8;
    private static final int HORA_FIN_DIA = 22;

    private static LocalTime[] generarHorasDelDia() {
        List<LocalTime> horas = new java.util.ArrayList<>();
        for (int hora = HORA_INICIO_DIA; hora <= HORA_FIN_DIA; hora++) {
            horas.add(LocalTime.of(hora, 0));
        }
        return horas.toArray(new LocalTime[0]);
    }

    private static String formatearHora12(LocalTime t) {
        int hora12 = t.getHour() % 12;
        if (hora12 == 0) hora12 = 12;
        String ampm = t.getHour() < 12 ? "a.m." : "p.m.";
        return String.format("%d:%02d %s", hora12, t.getMinute(), ampm);
    }

    private static void seleccionarHoraCercana(JComboBox<LocalTime> combo, LocalTime deseada) {
        if (deseada == null) return;
        LocalTime mejor = null;
        long mejorDif = Long.MAX_VALUE;
        for (int i = 0; i < combo.getItemCount(); i++) {
            LocalTime candidata = combo.getItemAt(i);
            long dif = Math.abs(java.time.Duration.between(candidata, deseada).toMinutes());
            if (dif < mejorDif) {
                mejorDif = dif;
                mejor = candidata;
            }
        }
        combo.setSelectedItem(mejor);
    }

    public void setController(Controller controller) { this.controller = controller; }

    public void setModel(Model model) {
        this.model = model;
        model.addPropertyChangeListener(this);
    }

    public JPanel getPanel() { return panelPrincipal; }

    public void setOnReservaCreada(java.util.function.Consumer<Reserva> callback) {
        this.onReservaCreada = callback;
    }

    public void setFuncionarioLogueado(Funcionario f) {
        this.funcionarioLogueado = f;
        if (controller != null) {
            controller.refrescarReservas(f);
        }
    }

    public void limpiarCampos() {
        promptIAFld.setText("");
        actividadFld.setText("");
        fechaFld.setDate(null);
        horaInicioFld.setSelectedIndex(-1);
        horaFinFld.setSelectedIndex(-1);
        categoriasList.clearSelection();
        idEnEdicion = null;
        reservarBtn.setText("Reservar");
        misReservasTable.clearSelection();
    }

    private void cargarParaEdicion(Reserva r) {
        idEnEdicion = r.getId();
        reservarBtn.setText("Guardar cambios");
        actividadFld.setText(r.getActividad());
        fechaFld.setDate(r.getFecha());
        seleccionarHoraCercana(horaInicioFld, r.getHoraInicio());
        seleccionarHoraCercana(horaFinFld, r.getHoraFin());

        List<Integer> indices = new java.util.ArrayList<>();
        ListModel<CategoriaRecurso> modeloActual = categoriasList.getModel();
        for (int i = 0; i < modeloActual.getSize(); i++) {
            CategoriaRecurso candidata = modeloActual.getElementAt(i);
            boolean usada = r.getRecursosAsignados() != null && r.getRecursosAsignados().stream()
                    .anyMatch(rec -> rec.getCategoria() != null && rec.getCategoria().getId().equals(candidata.getId()));
            if (usada) {
                indices.add(i);
            }
        }
        categoriasList.setSelectedIndices(indices.stream().mapToInt(Integer::intValue).toArray());
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case Model.CATEGORIAS:
                DefaultListModel<CategoriaRecurso> listModel = new DefaultListModel<>();
                for (CategoriaRecurso c : model.getCategorias()) {
                    listModel.addElement(c);
                }
                categoriasList.setModel(listModel);
                break;

            case Model.MIS_RESERVAS:
                String[] columnas = {"ID", "Actividad", "Fecha", "Horario", "Recursos", "Estado"};
                javax.swing.table.DefaultTableModel tableModel = new javax.swing.table.DefaultTableModel(columnas, 0);

                if (model.getMisReservas() != null) {
                    for (Reserva r : model.getMisReservas()) {
                        String horario = (r.getHoraInicio() != null ? r.getHoraInicio() : "")
                                + " - " + (r.getHoraFin() != null ? r.getHoraFin() : "");
                        Object[] fila = {
                                r.getId(),
                                r.getActividad(),
                                r.getFecha(),
                                horario,
                                recursosComoTexto(r.getRecursosAsignados()),
                                r.getEstado() != null ? r.getEstado() : "CONFIRMADA"
                        };
                        tableModel.addRow(fila);
                    }
                }
                misReservasTable.setModel(tableModel);
                break;
        }
    }

    private String recursosComoTexto(List<Recurso> recursos) {
        if (recursos == null || recursos.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < recursos.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(recursos.get(i).getDescripcion());
        }
        return sb.toString();
    }

    // Métodos para que el IAService autocomplete la interfaz

    public void setDatosExtraidosIA(String actividad, String fecha, String hInicio, String hFin,
                                    List<CategoriaRecurso> categoriasSugeridas) {
        if (actividad != null) actividadFld.setText(actividad);
        if (fecha != null) fechaFld.setDate(LocalDate.parse(fecha));
        if (hInicio != null) seleccionarHoraCercana(horaInicioFld, LocalTime.parse(hInicio));
        if (hFin != null) seleccionarHoraCercana(horaFinFld, LocalTime.parse(hFin));

        if (categoriasSugeridas != null && !categoriasSugeridas.isEmpty()) {
            ListModel<CategoriaRecurso> modeloActual = categoriasList.getModel();
            List<Integer> indices = new java.util.ArrayList<>();
            for (int i = 0; i < modeloActual.getSize(); i++) {
                CategoriaRecurso candidata = modeloActual.getElementAt(i);
                boolean sugerida = categoriasSugeridas.stream()
                        .anyMatch(c -> c.getId().equals(candidata.getId()));
                if (sugerida) {
                    indices.add(i);
                }
            }
            int[] indicesArray = indices.stream().mapToInt(Integer::intValue).toArray();
            categoriasList.setSelectedIndices(indicesArray);
        }
    }


}