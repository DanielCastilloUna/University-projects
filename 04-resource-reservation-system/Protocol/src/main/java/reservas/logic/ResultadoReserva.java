package reservas.logic;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.List;

public class ResultadoReserva implements Serializable {
    private boolean exito;
    private Reserva reserva;
    private List<CategoriaRecurso> categoriasNoDisponibles = new ArrayList<>();

    public boolean isExito() {
        return exito;
    }

    public void setExito(boolean exito) {
        this.exito = exito;
    }

    public Reserva getReserva() {
        return reserva;
    }

    public void setReserva(Reserva reserva) {
        this.reserva = reserva;
    }

    public List<CategoriaRecurso> getCategoriasNoDisponibles() {
        return categoriasNoDisponibles;
    }

    public void setCategoriasNoDisponibles(List<CategoriaRecurso> categoriasNoDisponibles) {
        this.categoriasNoDisponibles = categoriasNoDisponibles;
    }
}
