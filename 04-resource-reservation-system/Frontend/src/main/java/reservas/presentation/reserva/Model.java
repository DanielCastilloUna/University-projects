package reservas.presentation.reserva;

import reservas.logic.CategoriaRecurso;
import reservas.logic.Reserva;
import reservas.presentation.AbstractModel;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

public class Model extends AbstractModel { // Modelo de la pantalla de Reservas

    private Reserva current; // Reserva actual
    private List<Reserva> misReservas; // Lista de reservas
    private List<CategoriaRecurso> categorias; //Lista de categorias

    public static final String CURRENT = "current";
    public static final String MIS_RESERVAS = "misReservas";
    public static final String CATEGORIAS = "categorias";

    public Model() {
        current = new Reserva();
        misReservas = new ArrayList<>();
        categorias = new ArrayList<>();
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        super.addPropertyChangeListener(listener);
        firePropertyChange(CURRENT);
        firePropertyChange(MIS_RESERVAS);
        firePropertyChange(CATEGORIAS);
    }

    public Reserva getCurrent() { return current; }
    public void setCurrent(Reserva current) {
        this.current = current;
        firePropertyChange(CURRENT);
    }

    public List<Reserva> getMisReservas() { return misReservas; }
    public void setMisReservas(List<Reserva> misReservas) {
        this.misReservas = misReservas;
        firePropertyChange(MIS_RESERVAS);
    }

    public List<CategoriaRecurso> getCategorias() { return categorias; }
    public void setCategorias(List<CategoriaRecurso> categorias) {
        this.categorias = categorias;
        firePropertyChange(CATEGORIAS);
    }
}
