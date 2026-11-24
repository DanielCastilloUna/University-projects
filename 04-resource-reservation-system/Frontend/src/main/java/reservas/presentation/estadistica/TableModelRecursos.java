package reservas.presentation.estadistica;
import reservas.presentation.AbstractTableModel;

import java.util.List;
import java.util.Map;

/**
 * TableModel (MVC) para la tabla de listado (Recursos por Categoría) de la pantalla de Estadísticas.
 * Define que columnas mostrar (Categoría, Cantidad) y de donde sacar cada valor
 */

public class TableModelRecursos extends AbstractTableModel<Map.Entry<String, Integer>> implements javax.swing.table.TableModel {

    public TableModelRecursos(int [] cols, List<Map.Entry<String, Integer>> rows){
        super(cols, rows);
    }
    public static final int CATEGORIA = 0;
    public static final int CANTIDAD = 1;

    @Override
    protected  void initColNames(){
        colNames = new String[2];
        colNames[CATEGORIA] = "Categoría";
        colNames[CANTIDAD] = "Cantidad";
    }

    @Override
    protected Object getPropetyAt(Map.Entry<String, Integer> entry, int col){
        switch (col){
            case CATEGORIA:
                return entry.getKey();
            case CANTIDAD:
                return entry.getValue();
            default:
                return "";
        }

    }
}
