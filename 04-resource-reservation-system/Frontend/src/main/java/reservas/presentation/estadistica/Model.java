package reservas.presentation.estadistica;


import reservas.presentation.AbstractModel;

import java.beans.PropertyChangeListener;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

public class Model extends AbstractModel {

    private LocalDate desdeRecursos;
    private LocalDate hastaRecursos;
    private Map<String, Integer> estadisticasRecursos;
    public static final String DESDE_RECURSOS = "desdeRecursos";
    public static final String HASTA_RECURSOS = "hastaRecursos";
    public static final String ESTADISTICAS_RECURSOS = "estadisticasRecursos";

    private LocalDate desdeActividades;
    private LocalDate hastaActividades;
    private Map<String, Integer> estadisticasActividades;
    public static final String DESDE_ACTIVIDADES = "desdeActividades";
    public static final String HASTA_ACTIVIDADES = "hastaActividades";
    public static final String ESTADISTICAS_ACTIVIDADES = "estadisticasActividades";


    public Model(){
        estadisticasActividades = new LinkedHashMap<>();
        estadisticasRecursos = new LinkedHashMap<>();
    }
    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener){
        super.addPropertyChangeListener(listener);
        firePropertyChange(HASTA_ACTIVIDADES);
        firePropertyChange(HASTA_RECURSOS);
        firePropertyChange(DESDE_ACTIVIDADES);
        firePropertyChange(DESDE_RECURSOS);
        firePropertyChange(ESTADISTICAS_ACTIVIDADES);
        firePropertyChange(ESTADISTICAS_RECURSOS);
    }

    public Map<String, Integer> getEstadisticasRecursos(){
        return estadisticasRecursos;
    }

    public void setEstadisticasRecursos(Map<String, Integer> estadisticasRecursos) {
        this.estadisticasRecursos = estadisticasRecursos;
        firePropertyChange(ESTADISTICAS_RECURSOS);

    }

    public LocalDate getHastaRecursos(){
        return hastaRecursos;
    }

    public void setHastaRecursos(LocalDate hastaRecursos){
        this.hastaRecursos = hastaRecursos;
        firePropertyChange(HASTA_RECURSOS);
    }
    public LocalDate getDesdeRecursos() {
        return desdeRecursos;
    }

    public void setDesdeRecursos(LocalDate desdeRecursos){
        this.desdeRecursos = desdeRecursos;
        firePropertyChange(DESDE_RECURSOS);

    }
    public Map<String, Integer> getEstadisticasActividades(){
        return estadisticasActividades;
    }

    public void setEstadisticasActividades(Map<String, Integer> estadisticasActividades) {
        this.estadisticasActividades = estadisticasActividades;
        firePropertyChange(ESTADISTICAS_ACTIVIDADES);
    }

    public LocalDate getDesdeActividades() {
        return desdeActividades;
    }
    public void setDesdeActividades(LocalDate desdeActividades){
        this.desdeActividades = desdeActividades;
        firePropertyChange(DESDE_ACTIVIDADES);
    }

    public LocalDate getHastaActividades() {
        return hastaActividades;
    }

    public void setHastaActividades(LocalDate hastaActividades) {
        this.hastaActividades = hastaActividades;
        firePropertyChange(HASTA_ACTIVIDADES);
    }

}
