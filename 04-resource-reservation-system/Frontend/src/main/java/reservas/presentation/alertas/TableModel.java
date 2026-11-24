package reservas.presentation.alertas;

import reservas.logic.Reserva;
import reservas.presentation.AbstractTableModel;

import javax.swing.Icon;
import java.util.List;

/**TableModel (MVC) para la tabla del Area de Alertas.
 * Cada fila es una reserva de otro usuario: un resumen y los iconos de ver y descartar.
 *
 */
public class TableModel extends AbstractTableModel<Reserva> implements javax.swing.table.TableModel {

    public TableModel(int[] cols, List<Reserva> rows) {
        super(cols, rows);
    }

    public static final int ALERTA = 0;
    public static final int VER = 1;
    public static final int DESCARTAR = 2;

    @Override
    protected void initColNames() {
        colNames = new String[3];
        colNames[ALERTA] = "Actividad";
        colNames[VER] = "Ver";
        colNames[DESCARTAR] = "Desc.";
    }

    @Override
    protected Object getPropetyAt(Reserva r, int col) {
        switch (cols[col]) {
            case ALERTA:
                String nombre = r.getFuncionario() != null ? r.getFuncionario().getNombre() : "";
                return nombre + " - " + r.getActividad();
            case VER:
                return Iconos.ver();
            case DESCARTAR:
                return Iconos.descartar();
            default:
                return "";
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (cols[columnIndex]) {
            case VER:
            case DESCARTAR:
                return Icon.class;
            default:
                return String.class;
        }
    }
}
