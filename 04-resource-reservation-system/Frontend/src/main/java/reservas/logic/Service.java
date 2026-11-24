package reservas.logic;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

/**
 * Proxy del Service: representa localmente al Service del Backend.
 * Cada metodo envia la peticion por el socket y devuelve la respuesta del servidor remoto.
 * El Frontend no conoce la base de datos: todo pasa por aqui.
 */
public class Service implements IService {
    private static Service theInstance;

    public static synchronized Service instance() {
        if (theInstance == null) theInstance = new Service();
        return theInstance;
    }

    String host;
    int port;

    Socket s;
    ObjectOutputStream os;
    ObjectInputStream is;
    String sid; // id de sesion que asigna el Backend; une el socket ASYNC con este cliente

    private Service() {
        // El servidor se puede cambiar con -Dreservas.server=IP y -Dreservas.port=XXXX
        host = System.getProperty("reservas.server", Protocol.SERVER);
        port = Integer.getInteger("reservas.port", Protocol.PORT);
        try {
            s = new Socket(host, port);
            os = new ObjectOutputStream(s.getOutputStream());
            is = new ObjectInputStream(s.getInputStream());
            os.writeInt(Protocol.SYNC);
            os.flush();
            sid = (String) is.readObject();
        } catch (Exception e) {
            String msg = "No se pudo conectar con el servidor " + host + ":" + port
                    + ". Verifique que el Backend este corriendo.";
            System.err.println(msg);
            try {
                JOptionPane.showMessageDialog(null, msg, "Error de conexion", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) { }
            System.exit(-1);
        }
    }

    public String getSid(){
        return sid;
    }
    // Envia la operacion y sus argumentos, y espera la respuesta.
    // synchronized: peticion y respuesta van juntas, nadie mas usa el socket entre las dos.
    // reset(): evita que el stream reenvie referencias de objetos ya enviados (aunque hayan cambiado).
    private synchronized Object call(int operation, Object... args) throws Exception {
        try {
            os.reset();
            os.writeInt(operation);
            for (Object arg : args) {
                os.writeObject(arg);
            }
            os.flush();
            if (is.readInt() == Protocol.ERROR_NO_ERROR) {
                return is.readObject();
            }
            // ERROR_ERROR: el Backend envia el mensaje del error
            throw new Exception((String) is.readObject());
        } catch (IOException ex) {
            throw new Exception("Se perdio la conexion con el servidor: " + ex.getMessage());
        }
    }

    // Para los metodos que no declaran "throws Exception"
    private Object callUnchecked(int operation, Object... args) {
        try {
            return call(operation, args);
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    // =============== FUNCIONARIOS ===============
    @Override
    public void crearFuncionario(Funcionario funcionario) throws Exception {
        call(Protocol.FUNCIONARIO_CREATE, funcionario);
    }

    @Override
    public Funcionario buscarFuncionarioPorId(String id) {
        return (Funcionario) callUnchecked(Protocol.FUNCIONARIO_READ, id);
    }

    @Override
    public void modificarFuncionario(Funcionario funcionario) throws Exception {
        call(Protocol.FUNCIONARIO_UPDATE, funcionario);
    }

    @Override
    public void eliminarFuncionario(String id) throws Exception {
        call(Protocol.FUNCIONARIO_DELETE, id);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Funcionario> listarFuncionarios() {
        return (List<Funcionario>) callUnchecked(Protocol.FUNCIONARIO_FINDALL);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Funcionario> buscarFuncionariosPorNombre(String nombre) {
        return (List<Funcionario>) callUnchecked(Protocol.FUNCIONARIO_FIND_BY_NOMBRE, nombre);
    }

    // =============== CATEGORIAS DE RECURSO ===============
    @Override
    public void crearCategoria(CategoriaRecurso categoria) throws Exception {
        call(Protocol.CATEGORIA_CREATE, categoria);
    }

    @Override
    public CategoriaRecurso buscarCategoriaPorId(String id) {
        return (CategoriaRecurso) callUnchecked(Protocol.CATEGORIA_READ, id);
    }

    @Override
    public void modificarCategoria(CategoriaRecurso categoria) throws Exception {
        call(Protocol.CATEGORIA_UPDATE, categoria);
    }

    @Override
    public void eliminarCategoria(String id) throws Exception {
        call(Protocol.CATEGORIA_DELETE, id);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<CategoriaRecurso> listarCategorias() {
        return (List<CategoriaRecurso>) callUnchecked(Protocol.CATEGORIA_FINDALL);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<CategoriaRecurso> buscarCategoriasPorDescripcion(String descripcion) {
        return (List<CategoriaRecurso>) callUnchecked(Protocol.CATEGORIA_FIND_BY_DESCRIPCION, descripcion);
    }

    // =============== RECURSOS ===============
    @Override
    public void crearRecurso(Recurso recurso) throws Exception {
        call(Protocol.RECURSO_CREATE, recurso);
    }

    @Override
    public Recurso leerRecurso(String id) throws Exception {
        return (Recurso) call(Protocol.RECURSO_READ, id);
    }

    @Override
    public void modificarRecurso(Recurso recurso) throws Exception {
        call(Protocol.RECURSO_UPDATE, recurso);
    }

    @Override
    public void eliminarRecurso(String id) throws Exception {
        call(Protocol.RECURSO_DELETE, id);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Recurso> buscarRecursos(CategoriaRecurso categoria, String descripcion) {
        return (List<Recurso>) callUnchecked(Protocol.RECURSO_SEARCH, categoria, descripcion);
    }

    // =============== USUARIOS (login) ===============
    @Override
    public Usuario buscarUsuarioPorId(String id) {
        return (Usuario) callUnchecked(Protocol.USUARIO_READ, id);
    }

    @Override
    public void cambiarClave(Usuario usuario, String claveNueva) throws Exception {
        call(Protocol.USUARIO_CAMBIAR_CLAVE, usuario, claveNueva);
        // El Backend modifica su copia del usuario; aqui se actualiza el objeto local (la sesion).
        usuario.setClave(claveNueva);
    }

    @Override
    public void crearAdministrador(Administrador administrador) throws Exception {
        call(Protocol.ADMINISTRADOR_CREATE, administrador);
    }

    // =============== RESERVAS ===============
    @Override
    public ResultadoReserva intentarReservar(Funcionario funcionario, String actividad, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin, List<CategoriaRecurso> categoriasRequeridas) {
        return (ResultadoReserva) callUnchecked(Protocol.RESERVA_CREATE, funcionario, actividad, fecha, horaInicio, horaFin, categoriasRequeridas);
    }

    @Override
    public ResultadoReserva modificarReserva(String idOriginal, String actividad, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin, List<CategoriaRecurso> categoriasRequeridas) throws Exception {
        return (ResultadoReserva) call(Protocol.RESERVA_UPDATE, idOriginal, actividad, fecha, horaInicio, horaFin, categoriasRequeridas);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Reserva> listarReservasDe(Funcionario funcionario) {
        return (List<Reserva>) callUnchecked(Protocol.RESERVA_FIND_BY_FUNCIONARIO, funcionario);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Reserva> listarReservas() {
        return (List<Reserva>) callUnchecked(Protocol.RESERVA_FINDALL);
    }

    @Override
    public boolean cancelarReserva(String idReserva) {
        return (Boolean) callUnchecked(Protocol.RESERVA_CANCEL, idReserva);
    }

    // =============== ESTADISTICAS ===============
    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Integer> estadisticaRecursosPorCategoria(LocalDate desde, LocalDate hasta) {
        return (Map<String, Integer>) callUnchecked(Protocol.ESTADISTICA_RECURSOS_POR_CATEGORIA, desde, hasta);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Integer> estadisticaActividadesPorSemana(LocalDate desde, LocalDate hasta) {
        return (Map<String, Integer>) callUnchecked(Protocol.ESTADISTICA_ACTIVIDADES_POR_SEMANA, desde, hasta);
    }

    @Override
    public void stop() {
        try {
            disconnect();
        } catch (Exception e) {
            // el programa se esta cerrando: no hay nada mas que hacer
        }
    }

    private synchronized void disconnect() throws Exception {
        os.writeInt(Protocol.DISCONNECT);
        os.flush();
        s.shutdownOutput();
        s.close();
    }
}
