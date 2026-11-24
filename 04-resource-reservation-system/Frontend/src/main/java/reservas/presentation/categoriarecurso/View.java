package reservas.presentation.categoriarecurso;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import reservas.Application;
import reservas.logic.CategoriaRecurso;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;


public class View implements PropertyChangeListener {

    Controller controller;
    Model model;

    private JPanel panelPrincipal;

    private JLabel DescripcionBusqueda;
    private JButton btnBuscar;
    private JButton btnImprimi;

    private JTable tablaListado;

    private JTextField txtBuscarDescripcion;
    private JTextField txtDescripcion;

    private JButton btnGuardar;
    private JButton btnBorrar;
    private JButton btnLimpiar;

    private JLabel ID;
    private JLabel Descripcion;
    private JTextField txtId;

    private JPanel busquedaPanel;
    private JPanel categoriaPanel;
    private JPanel botonesPanel;
    private JPanel listadoPanel;

    public View() {
        $$$setupUI$$$();

        // el id es autogenerado, el usuario no debe editarlo
        txtId.setEditable(false);

        btnGuardar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (validate()) {
                    CategoriaRecurso c = take();
                    try {
                        controller.guardar(c);
                        JOptionPane.showMessageDialog(panelPrincipal, "Categoria de recurso guardado correctamente.", "Categorias", JOptionPane.INFORMATION_MESSAGE);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(panelPrincipal, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        btnBorrar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String id = txtId.getText().trim();
                try {
                    controller.borrar(id);
                    JOptionPane.showMessageDialog(panelPrincipal, "Categoría eliminada correctamente.", "Categorías", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(panelPrincipal, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        btnLimpiar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtDescripcion.setText("");
                controller.clear();
            }
        });
        btnBuscar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.buscarPorDescripcion(txtBuscarDescripcion.getText().trim());
            }
        });

        btnImprimi.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    controller.print();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(panelPrincipal, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        tablaListado.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tablaListado.getSelectedRow();
                if (row >= 0) {
                    controller.edit(row);
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
                int[] cols = {TableModel.ID, TableModel.DESCRIPCION};
                tablaListado.setModel(new TableModel(cols, model.getList()));
                break;
            case Model.CURRENT:
                txtId.setText(model.getCurrent().getId());
                txtDescripcion.setText(model.getCurrent().getDescripcion());

                txtDescripcion.setBackground(null);
                txtDescripcion.setToolTipText(null);
        }
        this.panelPrincipal.revalidate();
    }

    private CategoriaRecurso take() {
        CategoriaRecurso c = new CategoriaRecurso();
        c.setId(txtId.getText().trim());
        c.setDescripcion(txtDescripcion.getText().trim());
        return c;
    }


    private boolean validate() {
        boolean valid = true;

        if (txtDescripcion.getText().isBlank()) {
            valid = false;
            txtDescripcion.setBackground(Application.BACKGROUND_ERROR);
            txtDescripcion.setToolTipText("Descripcion requerida");
        } else {
            txtDescripcion.setBackground(null);
            txtDescripcion.setToolTipText(null);
        }
        return valid;
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
        panelPrincipal.setLayout(new GridLayoutManager(3, 1, new Insets(10, 10, 10, 10), -1, 10));

        busquedaPanel = new JPanel();
        busquedaPanel.setLayout(new GridLayoutManager(1, 4, new Insets(5, 5, 5, 5), -1, -1));
        panelPrincipal.add(busquedaPanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        busquedaPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Búsqueda"));

        DescripcionBusqueda = new JLabel();
        DescripcionBusqueda.setText("Descripción");
        busquedaPanel.add(DescripcionBusqueda, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));

        txtBuscarDescripcion = new JTextField();
        busquedaPanel.add(txtBuscarDescripcion, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));

        btnBuscar = new JButton();
        btnBuscar.setText("Buscar");
        busquedaPanel.add(btnBuscar, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));

        btnImprimi = new JButton();
        btnImprimi.setText("Imprimir PDF");
        busquedaPanel.add(btnImprimi, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));

        categoriaPanel = new JPanel();
        categoriaPanel.setLayout(new GridLayoutManager(3, 2, new Insets(5, 5, 5, 5), -1, -1));
        panelPrincipal.add(categoriaPanel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        categoriaPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Categoría"));

        ID = new JLabel();
        ID.setText("ID");
        categoriaPanel.add(ID, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));

        txtId = new JTextField();
        categoriaPanel.add(txtId, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));

        Descripcion = new JLabel();
        Descripcion.setText("Descripción");
        categoriaPanel.add(Descripcion, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));

        txtDescripcion = new JTextField();
        categoriaPanel.add(txtDescripcion, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));

        botonesPanel = new JPanel();
        botonesPanel.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        categoriaPanel.add(botonesPanel, new GridConstraints(2, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));

        btnGuardar = new JButton();
        btnGuardar.setText("Guardar");
        botonesPanel.add(btnGuardar, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));

        btnBorrar = new JButton();
        btnBorrar.setText("Borrar");
        botonesPanel.add(btnBorrar, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));

        btnLimpiar = new JButton();
        btnLimpiar.setText("Limpiar");
        botonesPanel.add(btnLimpiar, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));

        listadoPanel = new JPanel();
        listadoPanel.setLayout(new GridLayoutManager(1, 1, new Insets(5, 5, 5, 5), -1, -1));
        panelPrincipal.add(listadoPanel, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        listadoPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Listado"));

        final JScrollPane scrollPane1 = new JScrollPane();
        listadoPanel.add(scrollPane1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        tablaListado = new JTable();
        scrollPane1.setViewportView(tablaListado);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panelPrincipal;
    }
}
