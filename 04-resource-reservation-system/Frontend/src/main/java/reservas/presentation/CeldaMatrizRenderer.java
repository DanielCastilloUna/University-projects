package reservas.presentation;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

public class CeldaMatrizRenderer extends DefaultTableCellRenderer {

    public static final Color COLOR_OCUPADO = new Color(255, 249, 196);
    public static final Color COLOR_BORDE = new Color(210, 210, 210);
    private static final Border BORDE_CELDA = new LineBorder(COLOR_BORDE);

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                    boolean hasFocus, int row, int column) {
        String texto = value == null ? "" : value.toString();
        boolean ocupada = column > 0 && !texto.isBlank();

        JLabel label = new JLabel();
        label.setOpaque(true);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setVerticalAlignment(SwingConstants.CENTER);
        label.setBorder(BORDE_CELDA);

        if (column == 0) {
            label.setText("<html><div style='text-align:center;'><b>" + texto + "</b></div></html>");
            label.setBackground(Color.WHITE);
        } else if (ocupada) {
            label.setText("<html><div style='text-align:center;'>" + texto.replace("\n", "<br>") + "</div></html>");
            label.setBackground(COLOR_OCUPADO);
        } else {
            label.setText("");
            label.setBackground(Color.WHITE);
        }
        return label;
    }

    public static void aplicarA(JTable tabla) {
        tabla.setRowHeight(48);
        tabla.setShowGrid(true);
        tabla.setGridColor(COLOR_BORDE);
        tabla.setIntercellSpacing(new Dimension(1, 1));
        tabla.getTableHeader().setFont(tabla.getTableHeader().getFont().deriveFont(Font.BOLD));
        tabla.setDefaultRenderer(Object.class, new CeldaMatrizRenderer());
    }
}
