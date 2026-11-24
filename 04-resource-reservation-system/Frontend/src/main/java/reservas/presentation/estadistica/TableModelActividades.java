package reservas.presentation.estadistica;
import reservas.presentation.AbstractTableModel;

import java.util.List;
import java.util.Map;

/**
 * TableModel (MVC) para la tabla de listado (Estadísticas por Semana) de la pantalla de Estadísticas.
 * Define que columnas mostrar (Semana, Cantidad) y de donde sacar cada valor
 */

public class TableModelActividades extends AbstractTableModel<Map.Entry<String, Integer>> implements javax.swing.table.TableModel {

    public TableModelActividades(int [] cols, List<Map.Entry<String, Integer>> rows){
        super(cols, rows);
    }
    public static final int SEMANA = 0;
    public static final int CANTIDAD = 1;

    @Override
    protected  void initColNames(){
        colNames = new String[2];
        colNames[SEMANA] = "Semana";
        colNames[CANTIDAD] = "Cantidad";
    }

    @Override
    protected Object getPropetyAt(Map.Entry<String, Integer> entry, int col){
        switch (col){
            case SEMANA:
                return entry.getKey();
            case CANTIDAD:
                return entry.getValue();
            default:
                return "";
        }

    }
}
