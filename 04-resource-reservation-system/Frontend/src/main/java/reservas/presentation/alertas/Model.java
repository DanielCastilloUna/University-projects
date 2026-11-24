package reservas.presentation.alertas;

import reservas.logic.Reserva;
import reservas.presentation.AbstractModel;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Lista de alertas: reservas que hicieron otros usuarios durante la sesion actual.
 * Solo vive en memoria del Frontend; descartar una alerta no toca el Backend.
 */
public class Model extends AbstractModel {

    private List<Reserva> list;

    public static final String LIST = "list";

    public Model() {
        list = new ArrayList<>();
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        super.addPropertyChangeListener(listener);
        firePropertyChange(LIST);
    }

    public List<Reserva> getList() {
        return list;
    }

    // La alerta mas reciente queda de primera
    public void agregar(Reserva reserva) {
        list.add(0, reserva);
        firePropertyChange(LIST);
    }

    public void remover(int index) {
        list.remove(index);
        firePropertyChange(LIST);
    }
}
