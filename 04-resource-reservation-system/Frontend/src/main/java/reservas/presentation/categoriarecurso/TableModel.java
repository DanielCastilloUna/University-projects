package reservas.presentation.categoriarecurso;

import reservas.logic.CategoriaRecurso;
import reservas.presentation.AbstractTableModel;

import java.util.List;

/**TableModel (MVC) para la tabla de listado de la pantalla de Categorías.
 * Define que columnas mostrar (ID, Descripción) y de donde sacar cada valor.
 *
 */
public class TableModel extends AbstractTableModel<CategoriaRecurso> implements javax.swing.table.TableModel{

    public  TableModel(int [] cols, List<CategoriaRecurso> rows){
        super(cols, rows);
    }
    public static final int ID = 0;
    public static final int DESCRIPCION = 1;

    @Override
    protected void initColNames(){
        colNames = new String[2];
        colNames[ID] = "Id";
        colNames[DESCRIPCION] = "Descripcion";
    }

    @Override
    protected Object getPropetyAt(CategoriaRecurso c, int col){
        switch (col){
            case ID:
                return c.getId();
            case DESCRIPCION:
                return c.getDescripcion();
            default:
                return "";
        }

    }
}
