package reservas.presentation;

import java.util.List;

/**
 * Clase base generica para los TableModel de cada pantalla (MVC), siguiendo
 * el patron del proyecto de referencia "Personas": permite que una JTable
 * muestre cualquier tipo de entidad, dejando que cada pantalla decida
 * QUE columnas mostrar y en QUE orden (via el array "cols" recibido en el
 * constructor), sin tener que reescribir la logica de tabla cada vez.
 *
 * @param <E> tipo de entidad que se muestra en la tabla (ej. Funcionario)
 */
public abstract class AbstractTableModel<E> extends javax.swing.table.AbstractTableModel implements javax.swing.table.TableModel {
    protected List<E> rows;
    protected int[] cols;
    protected String[] colNames;

    public AbstractTableModel(int[] cols, List<E> rows) {
        this.cols = cols;
        this.rows = rows;
        initColNames();
    }

    public int getColumnCount() {
        return cols.length;
    }

    public String getColumnName(int col) {
        return colNames[cols[col]];
    }

    public Class<?> getColumnClass(int col) {
        switch (cols[col]) {
            default:
                return super.getColumnClass(col);
        }
    }

    public int getRowCount() {
        return rows.size();
    }

    public Object getValueAt(int row, int col) {
        E e = rows.get(row);
        return getPropetyAt(e, col);
    }

    protected abstract Object getPropetyAt(E e, int col);

    public E getRowAt(int row) {
        return rows.get(row);
    }

    protected abstract void initColNames();
}
