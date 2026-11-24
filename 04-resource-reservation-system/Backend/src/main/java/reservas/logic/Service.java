package reservas.logic;

import reservas.data.AdministradorDao;
import reservas.data.CategoriaRecursoDao;
import reservas.data.Database;
import reservas.data.FuncionarioDao;
import reservas.data.RecursoDao;
import reservas.data.ReservaDao;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Service implements IService {

    private static Service theInstance;

    public static synchronized IService instance() {
        if (theInstance == null) theInstance = new Service();
        return theInstance;
    }

    private FuncionarioDao funcionarioDao;
    private AdministradorDao administradorDao;
    private CategoriaRecursoDao categoriaDao;
    private RecursoDao recursoDao;
    private ReservaDao reservaDao;

    private static final int MAX_ID_FUNCIONARIO = 20;
    private static final int MAX_NOMBRE = 100;
    private static final int MAX_TELEFONO = 30;
    private static final int MAX_CLAVE = 50;
    private static final int MAX_DESC_CATEGORIA = 100;
    private static final int MAX_DESC_RECURSO = 150;
    private static final int MAX_ACTIVIDAD = 200;

    private Service() {
        try {
            funcionarioDao = new FuncionarioDao();
            administradorDao = new AdministradorDao();
            categoriaDao = new CategoriaRecursoDao();
            recursoDao = new RecursoDao();
            reservaDao = new ReservaDao();

            // Semilla: si no existe ningun administrador, crea uno por defecto
            // para poder entrar al sistema por primera vez, ya que el enunciado no define
            // una funcionalidad para crear el primer administrador.
            if (administradorDao.count() == 0) {
                administradorDao.create(new Administrador("admin", "admin"));
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }
    }

    @Override
    public synchronized void stop() {
        try {
            Database.instance().close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // =============== FUNCIONARIOS ===============

    @Override
    public synchronized void crearFuncionario(Funcionario funcionario) throws Exception {
        if (funcionario == null) {
            throw new Exception("El funcionario no puede ser nulo");
        }
        if (funcionario.getId() == null || funcionario.getId().isBlank()) {
            throw new Exception("El id es obligatorio");
        }
        if (funcionario.getNombre() == null || funcionario.getNombre().isBlank()) {
            throw new Exception("El nombre es obligatorio");
        }
        if (funcionario.getTelefono() == null || funcionario.getTelefono().isBlank()) {
            throw new Exception("El telefono es obligatorio");
        }
        validarLargosFuncionario(funcionario);
        if (buscarFuncionarioPorId(funcionario.getId()) != null) {
            throw new Exception("Ya existe un funcionario con id " + funcionario.getId());
        }


        funcionario.setClave(funcionario.getId());
        funcionarioDao.create(funcionario);
    }

    @Override
    public synchronized void modificarFuncionario(Funcionario funcionario) throws Exception {
        if (funcionario.getNombre() == null || funcionario.getNombre().isBlank()) {
            throw new Exception("El nombre es obligatorio");
        }
        if (funcionario.getTelefono() == null || funcionario.getTelefono().isBlank()) {
            throw new Exception("El telefono es obligatorio");
        }

        validarLargosFuncionario(funcionario);
        Funcionario existente = buscarFuncionarioPorId(funcionario.getId());
        if (existente == null) {
            throw new Exception("No existe un funcionario con id " + funcionario.getId());
        }

        funcionario.setClave(existente.getClave());
        funcionarioDao.update(funcionario);
    }

    @Override
    public synchronized void eliminarFuncionario(String id) throws Exception {
        Funcionario existente = buscarFuncionarioPorId(id);
        if (existente == null) {
            throw new Exception("No existe un funcionario con id " + id);
        }
        funcionarioDao.delete(id);
    }

    @Override
    public synchronized Funcionario buscarFuncionarioPorId(String id) {
        if (id == null || id.isBlank()) {
            return null;
        }
        try {
            return funcionarioDao.read(id);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public synchronized List<Funcionario> buscarFuncionariosPorNombre(String nombre) {
        return funcionarioDao.findByNombre(nombre == null ? "" : nombre);
    }

    @Override
    public synchronized List<Funcionario> listarFuncionarios() {
        return funcionarioDao.findAll();
    }

    private void validarLargo(String valor, int max, String campo) throws Exception{
        if(valor != null && valor.length() > max){
            throw new Exception(String.format("%s no puede tener más de %d caracteres", campo,max));

        }
    }

    private void validarLargosFuncionario(Funcionario funcionario) throws Exception{
        validarLargo(funcionario.getId(), MAX_ID_FUNCIONARIO, "El id");
        validarLargo(funcionario.getNombre(), MAX_NOMBRE, "El nombre");
        validarLargo(funcionario.getTelefono(), MAX_TELEFONO, "El teléfono");
    }
    // =============== CATEGORIAS DE RECURSO ===============

    @Override
    public synchronized void crearCategoria(CategoriaRecurso categoria) throws Exception {
        validarDatosCategoria(categoria);

        String nuevoId = String.format("CAT-%06d", categoriaDao.maxNumero() + 1);
        categoria.setId(nuevoId);

        categoriaDao.create(categoria);
    }

    @Override
    public synchronized void modificarCategoria(CategoriaRecurso categoria) throws Exception {
        validarDatosCategoria(categoria);
        validarIdCategoria(categoria.getId());

        CategoriaRecurso existente = buscarCategoriaPorId(categoria.getId());
        if (existente == null) {
            throw new Exception("No existe una categoria con id " + categoria.getId());
        }

        categoriaDao.update(categoria);
    }

    @Override
    public synchronized void eliminarCategoria(String id) throws Exception {
        validarIdCategoria(id);
        CategoriaRecurso existente = buscarCategoriaPorId(id);
        if (existente == null) {
            throw new Exception("No existe una categoria con id " + id);
        }
        categoriaDao.delete(id);
    }

    @Override
    public synchronized CategoriaRecurso buscarCategoriaPorId(String id) {
        if (id == null || id.isBlank()) {
            return null;
        }
        try {
            return categoriaDao.read(id);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public synchronized List<CategoriaRecurso> buscarCategoriasPorDescripcion(String descripcion) {
        return categoriaDao.findByDescripcion(descripcion == null ? "" : descripcion);
    }

    @Override
    public synchronized List<CategoriaRecurso> listarCategorias() {
        return categoriaDao.findAll();
    }

    private void validarDatosCategoria(CategoriaRecurso categoria) throws Exception {
        if (categoria == null) {
            throw new Exception("La categoria no puede ser nula");
        }
        if (categoria.getDescripcion() == null || categoria.getDescripcion().isBlank()) {
            throw new Exception("La descripcion es obligatoria");
        }
        validarLargo(categoria.getDescripcion(), MAX_DESC_CATEGORIA, "La descripción");
    }

    private void validarIdCategoria(String id) throws Exception {
        if (id == null || id.isBlank()) {
            throw new Exception("El id es obligatorio");
        }
    }

    // =============== RECURSOS ===============

    @Override
    public synchronized void crearRecurso(Recurso recurso) throws Exception {
        validarDatosRecurso(recurso);
        recurso.setId(generarIdRecurso());
        recursoDao.create(recurso);
    }

    private String generarIdRecurso() {
        return String.format("R-%03d", recursoDao.maxNumero() + 1);
    }

    @Override
    public synchronized Recurso leerRecurso(String id) throws Exception {
        return recursoDao.read(id);
    }

    @Override
    public synchronized void modificarRecurso(Recurso recurso) throws Exception {
        validarDatosRecurso(recurso);
        recursoDao.read(recurso.getId()); // lanza "Recurso no existe" si no esta
        recursoDao.update(recurso);
    }

    @Override
    public synchronized void eliminarRecurso(String id) throws Exception {
        recursoDao.read(id); // lanza "Recurso no existe" si no esta
        recursoDao.delete(id);
    }

    @Override
    public synchronized List<Recurso> buscarRecursos(CategoriaRecurso categoria, String descripcion) {
        return recursoDao.search(categoria, descripcion);
    }

    private void validarDatosRecurso(Recurso recurso) throws Exception {
        if (recurso == null) {
            throw new Exception("El recurso no puede ser nulo");
        }
        if (recurso.getCategoria() == null) {
            throw new Exception("La categoria es obligatoria");
        }
        if (recurso.getDescripcion() == null || recurso.getDescripcion().isBlank()) {
            throw new Exception("La descripcion es obligatoria");
        }
        validarLargo(recurso.getDescripcion(), MAX_DESC_RECURSO, "La descripción");
    }

    // =============== USUARIOS (login) ===============

    @Override
    public synchronized Usuario buscarUsuarioPorId(String id) {
        if (id == null || id.isBlank()) {
            return null;
        }
        Usuario encontrado = buscarFuncionarioPorId(id);
        if (encontrado != null) {
            return encontrado;
        }
        try {
            return administradorDao.read(id);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public synchronized void cambiarClave(Usuario usuario, String claveNueva) throws Exception {
        if (claveNueva == null || claveNueva.isBlank()) {
            throw new Exception("La clave nueva es obligatoria");
        }
        validarLargo(claveNueva, MAX_CLAVE, "La clave");
        if (usuario instanceof Funcionario) {
            funcionarioDao.updateClave(usuario.getId(), claveNueva);
        } else {
            administradorDao.updateClave(usuario.getId(), claveNueva);
        }
        usuario.setClave(claveNueva);
    }

    @Override
    public synchronized void crearAdministrador(Administrador administrador) throws Exception {
        if (administrador == null) {
            throw new Exception("El administrador no puede ser nulo");
        }
        if (buscarUsuarioPorId(administrador.getId()) != null) {
            throw new Exception("Ya existe un usuario con id " + administrador.getId());
        }
        administradorDao.create(administrador);
    }

    // =============== RESERVAS ===============

    @Override
    public synchronized ResultadoReserva intentarReservar(Funcionario funcionario, String actividad, LocalDate fecha,
                                             LocalTime horaInicio, LocalTime horaFin,
                                             List<CategoriaRecurso> categoriasRequeridas) {
        return intentarReservarInterno(funcionario, actividad, fecha, horaInicio, horaFin, categoriasRequeridas, null);
    }

    @Override
    public synchronized ResultadoReserva modificarReserva(String idOriginal, String actividad, LocalDate fecha,
                                             LocalTime horaInicio, LocalTime horaFin,
                                             List<CategoriaRecurso> categoriasRequeridas) throws Exception {
        Reserva existente;
        try {
            existente = reservaDao.read(idOriginal);
        } catch (Exception e) {
            throw new Exception("No existe una reserva con id " + idOriginal);
        }
        return intentarReservarInterno(existente.getFuncionario(), actividad, fecha, horaInicio, horaFin,
                categoriasRequeridas, idOriginal);
    }

    private ResultadoReserva intentarReservarInterno(Funcionario funcionario, String actividad, LocalDate fecha,
                                                     LocalTime horaInicio, LocalTime horaFin,
                                                     List<CategoriaRecurso> categoriasRequeridas, String idAReutilizar) {

        validarReserva(funcionario, actividad, fecha, horaInicio,horaFin);
        ResultadoReserva resultado = new ResultadoReserva();
        List<Recurso> recursosAsignados = new ArrayList<>();
        List<CategoriaRecurso> noDisponibles = new ArrayList<>();

        // Reservas vigentes de ese dia (si se esta modificando una, se ignora a si misma)
        List<Reserva> reservasExistentes = new ArrayList<>();
        for (Reserva r : reservaDao.findByFecha(fecha)) {
            boolean vigente = r.getEstado() == EstadoReserva.CONFIRMADA || r.getEstado() == EstadoReserva.ACTIVA;
            boolean esLaMisma = idAReutilizar != null && idAReutilizar.equals(r.getId());
            if (vigente && !esLaMisma) {
                reservasExistentes.add(r);
            }
        }

        for (CategoriaRecurso cat : categoriasRequeridas) {
            List<Recurso> recursosDeCat = buscarRecursos(cat, "");
            Recurso libreEncontrado = null;

            for (Recurso rec : recursosDeCat) {
                // Un mismo recurso no se asigna dos veces dentro de la misma reserva
                boolean yaAsignado = recursosAsignados.stream().anyMatch(a -> a.getId().equals(rec.getId()));
                if (!yaAsignado && estaDisponibleRecurso(rec, horaInicio, horaFin, reservasExistentes)) {
                    libreEncontrado = rec;
                    break;
                }
            }

            if (libreEncontrado != null) {
                recursosAsignados.add(libreEncontrado);
            } else {
                noDisponibles.add(cat);
            }
        }

        if (!noDisponibles.isEmpty()) {
            resultado.setExito(false);
            resultado.setCategoriasNoDisponibles(noDisponibles);
            return resultado;
        }

        Reserva nueva = new Reserva();
        nueva.setId(idAReutilizar != null ? idAReutilizar : generarIdReserva());
        nueva.setFuncionario(funcionario);
        nueva.setActividad(actividad);
        nueva.setFecha(fecha);
        nueva.setHoraInicio(horaInicio);
        nueva.setHoraFin(horaFin);
        nueva.setCategoriasRequeridas(categoriasRequeridas);
        nueva.setRecursosAsignados(recursosAsignados);
        nueva.setEstado(EstadoReserva.CONFIRMADA);

        try {
            if (idAReutilizar != null) {
                reservaDao.update(nueva);
            } else {
                reservaDao.create(nueva);
            }
        } catch (Exception e) {
            // Se propaga como error de ejecucion para que la vista muestre el motivo real
            throw new IllegalStateException(e.getMessage(), e);
        }

        resultado.setExito(true);
        resultado.setReserva(nueva);
        return resultado;
    }

    private String generarIdReserva() {
        return String.format("RES-%04d", reservaDao.maxNumero() + 1);
    }

    @Override
    public synchronized List<Reserva> listarReservasDe(Funcionario funcionario) {
        if (funcionario == null) return new ArrayList<>();
        return reservaDao.findByFuncionario(funcionario.getId());
    }

    @Override
    public synchronized List<Reserva> listarReservas() {
        return reservaDao.findAll();
    }

    @Override
    public synchronized boolean cancelarReserva(String idReserva) {
        Reserva r;
        try {
            r = reservaDao.read(idReserva);
        } catch (Exception e) {
            return false;
        }

        LocalDate hoy = LocalDate.now();
        LocalTime ahora = LocalTime.now();

        boolean esFutura = r.getFecha().isAfter(hoy) ||
                (r.getFecha().isEqual(hoy) && r.getHoraInicio().isAfter(ahora));

        if (!esFutura) {
            return false;
        }

        return reservaDao.updateEstado(idReserva, EstadoReserva.CANCELADA) > 0;
    }

    private boolean estaDisponibleRecurso(Recurso recurso, LocalTime hInicio, LocalTime hFin, List<Reserva> reservasFecha) {
        for (Reserva res : reservasFecha) {
            boolean asignado = res.getRecursosAsignados().stream()
                    .anyMatch(r -> r.getId().equals(recurso.getId()));

            if (asignado) {
                boolean traslape = hInicio.isBefore(res.getHoraFin()) && hFin.isAfter(res.getHoraInicio());
                if (traslape) {
                    return false;
                }
            }
        }
        return true;
    }

    private void validarReserva(Funcionario funcionario, String actividad, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin){
        if(funcionario == null){
            throw new IllegalArgumentException("El funcionario es obligatorio");
        }
        if(fecha == null){
            throw new IllegalArgumentException("La fecha es obligatoria");
        }
        if(horaInicio == null){
            throw new IllegalArgumentException("La hora de inicio es obligatoria");
        }
        if(horaFin == null){
            throw new IllegalArgumentException("La hora de fin es obligatoria");
        }
        if(actividad == null || actividad.isBlank()){
            throw new IllegalArgumentException("La actividad es obligatoria");
        }
        if(actividad.length() > MAX_ACTIVIDAD ){
            throw new IllegalArgumentException( String.format("La actividad no puede tener más de %d caracteres", MAX_ACTIVIDAD));
        }
        if(!horaFin.isAfter(horaInicio)){
            throw new IllegalArgumentException("La hora de fin debe ser posterior a la hora de inicio");
        }

    }

    // =============== ESTADISTICAS ===============

    @Override
    public synchronized Map<String, Integer> estadisticaRecursosPorCategoria(LocalDate desde, LocalDate hasta) {
        validarRango(desde, hasta);
        return reservaDao.recursosPorCategoria(desde, hasta);
    }

    @Override
    public synchronized Map<String, Integer> estadisticaActividadesPorSemana(LocalDate desde, LocalDate hasta) {
        validarRango(desde, hasta);
        return reservaDao.actividadesPorSemana(desde, hasta);
    }

    private void validarRango(LocalDate desde, LocalDate hasta) {
        if (desde == null || hasta == null) {
            throw new IllegalArgumentException("Debe indicar ambas fechas.");
        }
        if (desde.isAfter(hasta)) {
            throw new IllegalArgumentException("La fecha 'desde' no puede ser posterior a 'hasta'");
        }
    }

}
