package reservas.logic;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

/**
 * Contrato del servicio. Lo implementan el Service real (Backend) y el Service proxy (Frontend),
 * que reenvia cada llamada por el socket al Service real.
 */
public interface IService {
    // =============== FUNCIONARIOS ===============
    void crearFuncionario(Funcionario funcionario) throws Exception;
    Funcionario buscarFuncionarioPorId(String id);
    void modificarFuncionario(Funcionario funcionario) throws Exception;
    void eliminarFuncionario(String id) throws Exception;
    List<Funcionario> listarFuncionarios();
    List<Funcionario> buscarFuncionariosPorNombre(String nombre);

    // =============== CATEGORIAS DE RECURSO ===============
    void crearCategoria(CategoriaRecurso categoria) throws Exception;
    CategoriaRecurso buscarCategoriaPorId(String id);
    void modificarCategoria(CategoriaRecurso categoria) throws Exception;
    void eliminarCategoria(String id) throws Exception;
    List<CategoriaRecurso> listarCategorias();
    List<CategoriaRecurso> buscarCategoriasPorDescripcion(String descripcion);

    // =============== RECURSOS ===============
    void crearRecurso(Recurso recurso) throws Exception;
    Recurso leerRecurso(String id) throws Exception;
    void modificarRecurso(Recurso recurso) throws Exception;
    void eliminarRecurso(String id) throws Exception;
    List<Recurso> buscarRecursos(CategoriaRecurso categoria, String descripcion);

    // =============== USUARIOS (login) ===============
    Usuario buscarUsuarioPorId(String id);
    void cambiarClave(Usuario usuario, String claveNueva) throws Exception;
    void crearAdministrador(Administrador administrador) throws Exception;

    // =============== RESERVAS ===============
    ResultadoReserva intentarReservar(Funcionario funcionario, String actividad, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin, List<CategoriaRecurso> categoriasRequeridas);
    ResultadoReserva modificarReserva(String idOriginal, String actividad, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin, List<CategoriaRecurso> categoriasRequeridas) throws Exception;
    List<Reserva> listarReservasDe(Funcionario funcionario);
    List<Reserva> listarReservas();
    boolean cancelarReserva(String idReserva);

    // =============== ESTADISTICAS ===============
    Map<String, Integer> estadisticaRecursosPorCategoria(LocalDate desde, LocalDate hasta);
    Map<String, Integer> estadisticaActividadesPorSemana(LocalDate desde, LocalDate hasta);

    void stop();
}
