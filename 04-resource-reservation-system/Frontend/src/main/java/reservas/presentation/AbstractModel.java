package reservas.presentation;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * Clase base para los "Model" de presentacion (MVC), siguiendo el patron
 * Observer via PropertyChangeListener usado en el proyecto de referencia
 * "Personas" del profesor.
 */
public abstract class AbstractModel {
    protected PropertyChangeSupport propertyChangeSupport;

    public AbstractModel() {
        propertyChangeSupport = new PropertyChangeSupport(this);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    protected void firePropertyChange(String propertyName) {
        propertyChangeSupport.firePropertyChange(propertyName, null, null);
    }
}
