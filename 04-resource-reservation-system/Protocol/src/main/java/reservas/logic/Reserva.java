package reservas.logic;

import java.io.Serializable;


import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Reserva implements Serializable {

    private String id;
    private Funcionario funcionario;
    private String actividad;
    private LocalDate fecha;
    private LocalTime horaInicio;
    private LocalTime horaFin;

    private List<CategoriaRecurso> categoriasRequeridas = new ArrayList<>();

    private List<Recurso> recursosAsignados = new ArrayList<>();

    private EstadoReserva estado;

    public Reserva() {
    }

    public Reserva(String id, Funcionario funcionario, String actividad, LocalDate fecha,
                   LocalTime horaInicio, LocalTime horaFin) {
        this.id = id;
        this.funcionario = funcionario;
        this.actividad = actividad;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.estado = EstadoReserva.ACTIVA;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Funcionario getFuncionario() {
        return funcionario;
    }

    public void setFuncionario(Funcionario funcionario) {
        this.funcionario = funcionario;
    }

    public String getActividad() {
        return actividad;
    }

    public void setActividad(String actividad) {
        this.actividad = actividad;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public LocalTime getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(LocalTime horaFin) {
        this.horaFin = horaFin;
    }

    public List<CategoriaRecurso> getCategoriasRequeridas() {
        return categoriasRequeridas;
    }

    public void setCategoriasRequeridas(List<CategoriaRecurso> categoriasRequeridas) {
        this.categoriasRequeridas = categoriasRequeridas;
    }

    public List<Recurso> getRecursosAsignados() {
        return recursosAsignados;
    }

    public void setRecursosAsignados(List<Recurso> recursosAsignados) {
        this.recursosAsignados = recursosAsignados;
    }

    public EstadoReserva getEstado() {
        return estado;
    }

    public void setEstado(EstadoReserva estado) {
        this.estado = estado;
    }
}