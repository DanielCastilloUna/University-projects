package reservas.presentation.login;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class View extends JDialog implements PropertyChangeListener {

    private JPanel panelPrincipal;
    private JTextField idFld;
    private JPasswordField claveFld;
    private JButton ingresarFld;
    private JButton cancelarFld;
    private JButton cambiarFld;

    Controller controller;
    Model model;

    public View() {
        $$$setupUI$$$();
        setContentPane(panelPrincipal);
        setModal(true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        pack();

        ingresarFld.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String id = idFld.getText().trim();
                    String clave = new String(claveFld.getPassword());
                    controller.login(id, clave);
                    dispose();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(panelPrincipal, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        cancelarFld.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        cambiarFld.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String id = idFld.getText().trim();
                if (id.isBlank()) {
                    JOptionPane.showMessageDialog(panelPrincipal, "Escriba su ID primero", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String claveActual = JOptionPane.showInputDialog(panelPrincipal, "Clave actual:");
                if (claveActual == null) return;
                String claveNueva = JOptionPane.showInputDialog(panelPrincipal, "Clave nueva:");
                if (claveNueva == null) return;
                try {
                    controller.cambiarClave(id, claveActual, claveNueva);
                    JOptionPane.showMessageDialog(panelPrincipal, "Clave cambiada correctamente");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(panelPrincipal, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    private void $$$setupUI$$$() {
        panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(4, 2, new java.awt.Insets(20, 30, 20, 30), -1, -1));
        panelPrincipal.setBackground(new Color(240, 244, 248));

        JLabel lblTitulo = new JLabel("🔐 Sistema de Reservas");
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        panelPrincipal.add(lblTitulo, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(-1, 40), null, 0, false));

        JLabel lblId = new JLabel("ID:");
        lblId.setFont(new Font("SansSerif", Font.PLAIN, 13));
        panelPrincipal.add(lblId, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));

        idFld = new JTextField();
        idFld.setPreferredSize(new Dimension(200, 28));
        panelPrincipal.add(idFld, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));

        JLabel lblClave = new JLabel("Clave:");
        lblClave.setFont(new Font("SansSerif", Font.PLAIN, 13));
        panelPrincipal.add(lblClave, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));

        claveFld = new JPasswordField();
        claveFld.setPreferredSize(new Dimension(200, 28));
        panelPrincipal.add(claveFld, new com.intellij.uiDesigner.core.GridConstraints(2, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));

        JPanel botones = new JPanel();
        botones.setLayout(new java.awt.FlowLayout());
        botones.setBackground(new Color(240, 244, 248));
        panelPrincipal.add(botones, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));

        ingresarFld = new JButton("✔ Ingresar");
        botones.add(ingresarFld);

        cancelarFld = new JButton("✖ Cancelar");
        botones.add(cancelarFld);

        cambiarFld = new JButton("🔑 Cambiar Clave");
        botones.add(cambiarFld);
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
    }
}