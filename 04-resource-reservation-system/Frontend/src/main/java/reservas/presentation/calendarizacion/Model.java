package reservas.presentation.calendarizacion;

import reservas.logic.Recurso;
import reservas.presentation.AbstractModel;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Model extends AbstractModel {

    public static final String MATRIZ = "matriz";

    private List<Recurso> recursos = new ArrayList<>();
    private List<LocalTime> horas = new ArrayList<>();
    private String[][] celdas = new String[0][0];

    public List<Recurso> getRecursos() {
        return recursos;
    }

    public List<LocalTime> getHoras() {
        return horas;
    }

    public String[][] getCeldas() {
        return celdas;
    }

    public void setMatriz(List<Recurso> recursos, List<LocalTime> horas, String[][] celdas) {
        this.recursos = recursos;
        this.horas = horas;
        this.celdas = celdas;
        firePropertyChange(MATRIZ);
    }
}