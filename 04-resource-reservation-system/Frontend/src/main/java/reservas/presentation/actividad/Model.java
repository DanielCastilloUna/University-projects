package reservas.presentation.actividad;

import reservas.presentation.AbstractModel;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Model extends AbstractModel {

    public static final String MATRIZ = "matriz";

    private List<LocalDate> dias = new ArrayList<>();
    private List<Integer> horas = new ArrayList<>();
    private String[][] celdas = new String[0][0];

    public List<LocalDate> getDias() {
        return dias;
    }

    public List<Integer> getHoras() {
        return horas;
    }

    public String[][] getCeldas() {
        return celdas;
    }

    public void setMatriz(List<LocalDate> dias, List<Integer> horas, String[][] celdas) {
        this.dias = dias;
        this.horas = horas;
        this.celdas = celdas;
        firePropertyChange(MATRIZ);
    }
}