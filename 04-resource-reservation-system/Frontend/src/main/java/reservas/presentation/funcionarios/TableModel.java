package reservas.presentation.funcionarios;

import reservas.logic.Funcionario;
import reservas.presentation.AbstractTableModel;

import java.util.List;

/**
 * TableModel concreto para la tabla "Listado" de Funcionarios.
 * Define las columnas disponibles (Id, Nombre, Telefono) y como se extrae
 * cada una desde un Funcionario.
 */
public class TableModel extends AbstractTableModel<Funcionario> implements javax.swing.table.TableModel {

    public TableModel(int[] cols, List<Funcionario> rows) {
        super(cols, rows);
    }

    public static final int ID = 0;
    public static final int NOMBRE = 1;
    public static final int TELEFONO = 2;

    @Override
    protected void initColNames() {
        colNames = new String[3];
        colNames[ID] = "Id";
        colNames[NOMBRE] = "Nombre";
        colNames[TELEFONO] = "Telefono";
    }

    @Override
    protected Object getPropetyAt(Funcionario e, int col) {
        switch (cols[col]) {
            case ID:
                return e.getId();
            case NOMBRE:
                return e.getNombre();
            case TELEFONO:
                return e.getTelefono();
            default:
                return "";
        }
    }
}
