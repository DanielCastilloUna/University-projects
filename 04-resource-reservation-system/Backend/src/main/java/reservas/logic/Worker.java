package reservas.logic;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

/**
 * Atiende a UN cliente (Frontend): lee el codigo de la operacion y sus argumentos,
 * llama al Service real y responde por el socket.
 * Respuesta: ERROR_NO_ERROR + resultado (null si la operacion no devuelve nada),
 * o ERROR_ERROR + el mensaje del error.
 * Ademas tiene un segundo socket (ASYNC) por donde el Server le envia al cliente
 * las reservas nuevas que hacen los demas usuarios.
 */
public class Worker {
    Server srv;
    Socket s;
    ObjectOutputStream os;
    ObjectInputStream is;
    String sid;
    IService service;

    Socket as;
    ObjectOutputStream aos;
    ObjectInputStream ais;

    public Worker(Server srv, Socket s, ObjectOutputStream os, ObjectInputStream is, String sid, IService service) {
        this.srv=srv;
        this.s=s;
        this.os=os;
        this.is=is;
        this.sid=sid;
        this.service=service;
    }

    public String getSid() {
        return sid;
    }

    public synchronized void setAs(Socket as, ObjectOutputStream aos, ObjectInputStream ais) {
        this.as=as;
        this.aos=aos;
        this.ais=ais;
    }

    // Lo llama el Server desde el hilo de OTRO Worker: synchronized para que dos
    // notificaciones no se mezclen en el mismo stream.
    public synchronized void deliver(Reserva reserva) {
        if (aos == null) return; // el cliente aun no abre su socket ASYNC
        try {
            aos.reset();
            aos.writeInt(Protocol.DELIVER_RESERVA);
            aos.writeObject(reserva);
            aos.flush();
        } catch (IOException ex) {
            System.out.println("No se pudo notificar al cliente " + sid + ": " + ex);
        }
    }

    boolean continuar;
    public void start(){
        try {
            System.out.println("Worker atendiendo peticiones...");
            Thread t = new Thread(new Runnable(){
                public void run(){
                    listen();
                }
            });
            continuar = true;
            t.start();
        } catch (Exception ex) { }
    }

    public void stop(){
        continuar=false;
        try { s.close(); } catch (IOException ex) { }
        synchronized(this) {
            try { if (as != null) as.close(); } catch (IOException ex) { }
        }
        System.out.println("Conexion cerrada...");
    }

    // Respuesta exitosa: el resultado va despues del codigo (null si no hay resultado).
    // reset(): evita reenviar referencias de objetos ya enviados antes por el mismo stream.
    private void ok(Object result) throws IOException {
        os.reset();
        os.writeInt(Protocol.ERROR_NO_ERROR);
        os.writeObject(result);
    }

    // Respuesta con error: se envia el mensaje para que el Frontend lo muestre al usuario.
    private void fail(Exception ex) throws IOException {
        os.reset();
        os.writeInt(Protocol.ERROR_ERROR);
        os.writeObject(ex.getMessage() != null ? ex.getMessage() : ex.toString());
    }

    @SuppressWarnings("unchecked")
    public void listen(){
        int method;
        while (continuar) {
            try {
                method = is.readInt();
                System.out.println("Operacion: "+method);
                switch(method) {
                    case Protocol.FUNCIONARIO_CREATE: {
                        Funcionario funcionario = (Funcionario) is.readObject();
                        try {
                            service.crearFuncionario(funcionario);
                        } catch (Exception ex) { fail(ex); break; }
                        ok(null);
                        break;
                    }
                    case Protocol.FUNCIONARIO_READ: {
                        String id = (String) is.readObject();
                        Funcionario result;
                        try {
                            result = service.buscarFuncionarioPorId(id);
                        } catch (Exception ex) { fail(ex); break; }
                        ok(result);
                        break;
                    }
                    case Protocol.FUNCIONARIO_UPDATE: {
                        Funcionario funcionario = (Funcionario) is.readObject();
                        try {
                            service.modificarFuncionario(funcionario);
                        } catch (Exception ex) { fail(ex); break; }
                        ok(null);
                        break;
                    }
                    case Protocol.FUNCIONARIO_DELETE: {
                        String id = (String) is.readObject();
                        try {
                            service.eliminarFuncionario(id);
                        } catch (Exception ex) { fail(ex); break; }
                        ok(null);
                        break;
                    }
                    case Protocol.FUNCIONARIO_FINDALL: {
                        List<Funcionario> result;
                        try {
                            result = service.listarFuncionarios();
                        } catch (Exception ex) { fail(ex); break; }
                        ok(result);
                        break;
                    }
                    case Protocol.FUNCIONARIO_FIND_BY_NOMBRE: {
                        String nombre = (String) is.readObject();
                        List<Funcionario> result;
                        try {
                            result = service.buscarFuncionariosPorNombre(nombre);
                        } catch (Exception ex) { fail(ex); break; }
                        ok(result);
                        break;
                    }
                    case Protocol.CATEGORIA_CREATE: {
                        CategoriaRecurso categoria = (CategoriaRecurso) is.readObject();
                        try {
                            service.crearCategoria(categoria);
                        } catch (Exception ex) { fail(ex); break; }
                        ok(null);
                        break;
                    }
                    case Protocol.CATEGORIA_READ: {
                        String id = (String) is.readObject();
                        CategoriaRecurso result;
                        try {
                            result = service.buscarCategoriaPorId(id);
                        } catch (Exception ex) { fail(ex); break; }
                        ok(result);
                        break;
                    }
                    case Protocol.CATEGORIA_UPDATE: {
                        CategoriaRecurso categoria = (CategoriaRecurso) is.readObject();
                        try {
                            service.modificarCategoria(categoria);
                        } catch (Exception ex) { fail(ex); break; }
                        ok(null);
                        break;
                    }
                    case Protocol.CATEGORIA_DELETE: {
                        String id = (String) is.readObject();
                        try {
                            service.eliminarCategoria(id);
                        } catch (Exception ex) { fail(ex); break; }
                        ok(null);
                        break;
                    }
                    case Protocol.CATEGORIA_FINDALL: {
                        List<CategoriaRecurso> result;
                        try {
                            result = service.listarCategorias();
                        } catch (Exception ex) { fail(ex); break; }
                        ok(result);
                        break;
                    }
                    case Protocol.CATEGORIA_FIND_BY_DESCRIPCION: {
                        String descripcion = (String) is.readObject();
                        List<CategoriaRecurso> result;
                        try {
                            result = service.buscarCategoriasPorDescripcion(descripcion);
                        } catch (Exception ex) { fail(ex); break; }
                        ok(result);
                        break;
                    }
                    case Protocol.RECURSO_CREATE: {
                        Recurso recurso = (Recurso) is.readObject();
                        try {
                            service.crearRecurso(recurso);
                        } catch (Exception ex) { fail(ex); break; }
                        ok(null);
                        break;
                    }
                    case Protocol.RECURSO_READ: {
                        String id = (String) is.readObject();
                        Recurso result;
                        try {
                            result = service.leerRecurso(id);
                        } catch (Exception ex) { fail(ex); break; }
                        ok(result);
                        break;
                    }
                    case Protocol.RECURSO_UPDATE: {
                        Recurso recurso = (Recurso) is.readObject();
                        try {
                            service.modificarRecurso(recurso);
                        } catch (Exception ex) { fail(ex); break; }
                        ok(null);
                        break;
                    }
                    case Protocol.RECURSO_DELETE: {
                        String id = (String) is.readObject();
                        try {
                            service.eliminarRecurso(id);
                        } catch (Exception ex) { fail(ex); break; }
                        ok(null);
                        break;
                    }
                    case Protocol.RECURSO_SEARCH: {
                        CategoriaRecurso categoria = (CategoriaRecurso) is.readObject();
                        String descripcion = (String) is.readObject();
                        List<Recurso> result;
                        try {
                            result = service.buscarRecursos(categoria, descripcion);
                        } catch (Exception ex) { fail(ex); break; }
                        ok(result);
                        break;
                    }
                    case Protocol.ADMINISTRADOR_CREATE: {
                        Administrador administrador = (Administrador) is.readObject();
                        try {
                            service.crearAdministrador(administrador);
                        } catch (Exception ex) { fail(ex); break; }
                        ok(null);
                        break;
                    }
                    case Protocol.USUARIO_READ: {
                        String id = (String) is.readObject();
                        Usuario result;
                        try {
                            result = service.buscarUsuarioPorId(id);
                        } catch (Exception ex) { fail(ex); break; }
                        ok(result);
                        break;
                    }
                    case Protocol.USUARIO_CAMBIAR_CLAVE: {
                        Usuario usuario = (Usuario) is.readObject();
                        String claveNueva = (String) is.readObject();
                        try {
                            service.cambiarClave(usuario, claveNueva);
                        } catch (Exception ex) { fail(ex); break; }
                        ok(null);
                        break;
                    }
                    case Protocol.RESERVA_CREATE: {
                        Funcionario funcionario = (Funcionario) is.readObject();
                        String actividad = (String) is.readObject();
                        LocalDate fecha = (LocalDate) is.readObject();
                        LocalTime horaInicio = (LocalTime) is.readObject();
                        LocalTime horaFin = (LocalTime) is.readObject();
                        List<CategoriaRecurso> categoriasRequeridas = (List<CategoriaRecurso>) is.readObject();
                        ResultadoReserva result;
                        try {
                            result = service.intentarReservar(funcionario, actividad, fecha, horaInicio, horaFin, categoriasRequeridas);
                        } catch (Exception ex) { fail(ex); break; }
                        ok(result);
                        // Reserva nueva registrada: avisar a los demas Frontend (alertas)
                        if (result.isExito()) {
                            srv.deliver(result.getReserva(), this);
                        }
                        break;
                    }
                    case Protocol.RESERVA_UPDATE: {
                        String idOriginal = (String) is.readObject();
                        String actividad = (String) is.readObject();
                        LocalDate fecha = (LocalDate) is.readObject();
                        LocalTime horaInicio = (LocalTime) is.readObject();
                        LocalTime horaFin = (LocalTime) is.readObject();
                        List<CategoriaRecurso> categoriasRequeridas = (List<CategoriaRecurso>) is.readObject();
                        ResultadoReserva result;
                        try {
                            result = service.modificarReserva(idOriginal, actividad, fecha, horaInicio, horaFin, categoriasRequeridas);
                        } catch (Exception ex) { fail(ex); break; }
                        ok(result);
                        break;
                    }
                    case Protocol.RESERVA_CANCEL: {
                        String idReserva = (String) is.readObject();
                        boolean result;
                        try {
                            result = service.cancelarReserva(idReserva);
                        } catch (Exception ex) { fail(ex); break; }
                        ok(result);
                        break;
                    }
                    case Protocol.RESERVA_FINDALL: {
                        List<Reserva> result;
                        try {
                            result = service.listarReservas();
                        } catch (Exception ex) { fail(ex); break; }
                        ok(result);
                        break;
                    }
                    case Protocol.RESERVA_FIND_BY_FUNCIONARIO: {
                        Funcionario funcionario = (Funcionario) is.readObject();
                        List<Reserva> result;
                        try {
                            result = service.listarReservasDe(funcionario);
                        } catch (Exception ex) { fail(ex); break; }
                        ok(result);
                        break;
                    }
                    case Protocol.ESTADISTICA_RECURSOS_POR_CATEGORIA: {
                        LocalDate desde = (LocalDate) is.readObject();
                        LocalDate hasta = (LocalDate) is.readObject();
                        Map<String, Integer> result;
                        try {
                            result = service.estadisticaRecursosPorCategoria(desde, hasta);
                        } catch (Exception ex) { fail(ex); break; }
                        ok(result);
                        break;
                    }
                    case Protocol.ESTADISTICA_ACTIVIDADES_POR_SEMANA: {
                        LocalDate desde = (LocalDate) is.readObject();
                        LocalDate hasta = (LocalDate) is.readObject();
                        Map<String, Integer> result;
                        try {
                            result = service.estadisticaActividadesPorSemana(desde, hasta);
                        } catch (Exception ex) { fail(ex); break; }
                        ok(result);
                        break;
                    }
                    case Protocol.DISCONNECT:
                        stop();
                        srv.remove(this);
                        return;
                }
                os.flush();
            } catch (IOException | ClassNotFoundException e) {
                if (continuar) {
                    stop();
                    srv.remove(this);
                }
            }
        }
    }
}
