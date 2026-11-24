package reservas.presentation.reserva;

import reservas.logic.Reserva;
import reservas.presentation.AbstractTableModel;

import java.util.List;

public class TableModel extends AbstractTableModel<Reserva>  {

    public static final int ID = 0;
    public static final int ACTIVIDAD = 1;
    public static final int FECHA = 2;
    public static final int HORA_INICIO = 3;
    public static final int HORA_FIN = 4;
    public static final int ESTADO = 5;

    public TableModel(int[] cols, List<Reserva> rows) {
        super(cols, rows);
    }

    @Override
    protected void initColNames() {
        colNames = new String[6];
        colNames[ID] = "ID";
        colNames[ACTIVIDAD] = "Actividad";
        colNames[FECHA] = "Fecha";
        colNames[HORA_INICIO] = "Hora Inicio";
        colNames[HORA_FIN] = "Hora Fin";
        colNames[ESTADO] = "Estado";
    }

    @Override
    protected Object getPropetyAt(Reserva r, int col) {
        switch (col) {
            case ID: return r.getId();
            case ACTIVIDAD: return r.getActividad();
            case FECHA: return (r.getFecha() != null) ? r.getFecha().toString() : "";
            case HORA_INICIO: return (r.getHoraInicio() != null) ? r.getHoraInicio().toString() : "";
            case HORA_FIN: return (r.getHoraFin() != null) ? r.getHoraFin().toString() : "";
            case ESTADO: return (r.getEstado() != null) ? r.getEstado().toString() : "";
            default: return "";
        }
    }
}