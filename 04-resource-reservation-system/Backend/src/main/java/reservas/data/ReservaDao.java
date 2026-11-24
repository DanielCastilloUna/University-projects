package reservas.data;

import reservas.logic.CategoriaRecurso;
import reservas.logic.EstadoReserva;
import reservas.logic.Funcionario;
import reservas.logic.Recurso;
import reservas.logic.Reserva;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ReservaDao {
    Database db;

    // Consulta base: la reserva junto con su funcionario (JOIN)
    private static final String SELECT_RESERVA =
            "select * from Reserva r " +
            "inner join Funcionario f on r.funcionario=f.id ";

    public ReservaDao() {
        db = Database.instance();
    }

    // La fecha y la hora se pasan como texto (yyyy-MM-dd / HH:mm:ss): asi no dependen
    // de la zona horaria de la JVM ni del servidor.

    public void create(Reserva r) throws Exception {
        try {
            db.beginTransaction();
            String sql = "insert into Reserva (id, funcionario, actividad, fecha, horaInicio, horaFin, estado) " +
                    "values(?,?,?,?,?,?,?)";
            PreparedStatement stm = db.prepareStatement(sql);
            stm.setString(1, r.getId());
            stm.setString(2, r.getFuncionario().getId());
            stm.setString(3, r.getActividad());
            stm.setString(4, r.getFecha().toString());
            stm.setString(5, r.getHoraInicio().toString());
            stm.setString(6, r.getHoraFin().toString());
            stm.setString(7, r.getEstado().name());
            stm.executeUpdate();
            insertarDetalle(r);
            db.commit();
        } catch (Exception e) {
            db.rollback();
            throw new Exception("No se pudo guardar la reserva: " + e.getMessage());
        }
    }

    /** Modifica actividad, fecha, horas, estado, categorias y recursos (el funcionario no cambia). */
    public void update(Reserva r) throws Exception {
        try {
            db.beginTransaction();
            String sql = "update Reserva set actividad=?, fecha=?, horaInicio=?, horaFin=?, estado=? where id=?";
            PreparedStatement stm = db.prepareStatement(sql);
            stm.setString(1, r.getActividad());
            stm.setString(2, r.getFecha().toString());
            stm.setString(3, r.getHoraInicio().toString());
            stm.setString(4, r.getHoraFin().toString());
            stm.setString(5, r.getEstado().name());
            stm.setString(6, r.getId());
            if (stm.executeUpdate() == 0) {
                throw new Exception("Reserva no existe");
            }
            borrarDetalle(r.getId());
            insertarDetalle(r);
            db.commit();
        } catch (Exception e) {
            db.rollback();
            throw new Exception("No se pudo modificar la reserva: " + e.getMessage());
        }
    }

    public int updateEstado(String id, EstadoReserva estado) {
        try {
            PreparedStatement stm = db.prepareStatement("update Reserva set estado=? where id=?");
            stm.setString(1, estado.name());
            stm.setString(2, id);
            return db.executeUpdate(stm);
        } catch (SQLException ex) {
            return 0;
        }
    }

    /** Las filas de ReservaCategoria y ReservaRecurso se borran en cascada. */
    public void delete(String id) throws Exception {
        String sql = "delete from Reserva where id=?";
        PreparedStatement stm = db.prepareStatement(sql);
        stm.setString(1, id);
        int count = db.executeUpdate(stm);
        if (count == 0) {
            throw new Exception("Reserva no existe");
        }
    }

    public Reserva read(String id) throws Exception {
        PreparedStatement stm = db.prepareStatement(SELECT_RESERVA + "where r.id=?");
        stm.setString(1, id);
        ResultSet rs = db.executeQuery(stm);
        if (rs.next()) {
            Reserva r = from(rs, "r");
            r.setFuncionario(new FuncionarioDao().from(rs, "f"));
            cargarDetalle(r);
            return r;
        } else {
            throw new Exception("Reserva no existe");
        }
    }

    public List<Reserva> findAll() {
        return consultar(SELECT_RESERVA + "order by r.id", null);
    }

    public List<Reserva> findByFuncionario(String idFuncionario) {
        return consultar(SELECT_RESERVA + "where r.funcionario=? order by r.id", idFuncionario);
    }

    public List<Reserva> findByFecha(LocalDate fecha) {
        return consultar(SELECT_RESERVA + "where r.fecha=? order by r.horaInicio", fecha.toString());
    }

    private List<Reserva> consultar(String sql, String parametro) {
        List<Reserva> resultado = new ArrayList<Reserva>();
        try {
            PreparedStatement stm = db.prepareStatement(sql);
            if (parametro != null) {
                stm.setString(1, parametro);
            }
            ResultSet rs = db.executeQuery(stm);
            FuncionarioDao fd = new FuncionarioDao();
            while (rs.next()) {
                Reserva r = from(rs, "r");
                r.setFuncionario(fd.from(rs, "f"));
                resultado.add(r);
            }
            for (Reserva r : resultado) {
                cargarDetalle(r);
            }
        } catch (SQLException ex) {
        }
        return resultado;
    }

    // ---------------- Estadisticas (JOIN + GROUP BY) ----------------

    /** Cantidad de recursos usados por categoria en el rango de fechas. */
    public Map<String, Integer> recursosPorCategoria(LocalDate desde, LocalDate hasta) {
        Map<String, Integer> resultado = new LinkedHashMap<String, Integer>();
        try {
            String sql = "select c.descripcion as descripcion, count(*) as cantidad " +
                    "from Reserva r " +
                    "inner join ReservaRecurso rr on rr.reserva=r.id " +
                    "inner join Recurso re on rr.recurso=re.id " +
                    "inner join CategoriaRecurso c on re.categoria=c.id " +
                    "where r.fecha between ? and ? " +
                    "group by c.descripcion " +
                    "order by cantidad desc, c.descripcion";
            PreparedStatement stm = db.prepareStatement(sql);
            stm.setString(1, desde.toString());
            stm.setString(2, hasta.toString());
            ResultSet rs = db.executeQuery(stm);
            while (rs.next()) {
                resultado.put(rs.getString("descripcion"), rs.getInt("cantidad"));
            }
        } catch (SQLException ex) {
        }
        return resultado;
    }

    /** Cantidad de actividades (reservas) por semana; la clave es el lunes de la semana. */
    public Map<String, Integer> actividadesPorSemana(LocalDate desde, LocalDate hasta) {
        Map<String, Integer> resultado = new LinkedHashMap<String, Integer>();
        try {
            String sql = "select date_format(date_sub(r.fecha, interval weekday(r.fecha) day), '%Y-%m-%d') as lunes, " +
                    "count(*) as cantidad " +
                    "from Reserva r " +
                    "where r.fecha between ? and ? " +
                    "group by lunes " +
                    "order by lunes";
            PreparedStatement stm = db.prepareStatement(sql);
            stm.setString(1, desde.toString());
            stm.setString(2, hasta.toString());
            ResultSet rs = db.executeQuery(stm);
            while (rs.next()) {
                resultado.put(rs.getString("lunes"), rs.getInt("cantidad"));
            }
        } catch (SQLException ex) {
        }
        return resultado;
    }

    /** Mayor numero usado en los ids "RES-0001" (0 si no hay reservas). */
    public int maxNumero() {
        try {
            String sql = "select ifnull(max(cast(substring(id, 5) as unsigned)), 0) from Reserva where id like 'RES-%'";
            PreparedStatement stm = db.prepareStatement(sql);
            ResultSet rs = db.executeQuery(stm);
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException ex) {
        }
        return 0;
    }

    // ---------------- Detalle: categorias pedidas y recursos asignados ----------------

    private void insertarDetalle(Reserva r) throws SQLException {
        if (r.getCategoriasRequeridas() != null) for (CategoriaRecurso c : r.getCategoriasRequeridas()) {
            PreparedStatement stm = db.prepareStatement(
                    "insert into ReservaCategoria (reserva, categoria) values(?,?)");
            stm.setString(1, r.getId());
            stm.setString(2, c.getId());
            stm.executeUpdate();
        }
        if (r.getRecursosAsignados() != null) for (Recurso re : r.getRecursosAsignados()) {
            PreparedStatement stm = db.prepareStatement(
                    "insert into ReservaRecurso (reserva, recurso) values(?,?)");
            stm.setString(1, r.getId());
            stm.setString(2, re.getId());
            stm.executeUpdate();
        }
    }

    private void borrarDetalle(String idReserva) throws SQLException {
        PreparedStatement stm = db.prepareStatement("delete from ReservaCategoria where reserva=?");
        stm.setString(1, idReserva);
        stm.executeUpdate();
        stm = db.prepareStatement("delete from ReservaRecurso where reserva=?");
        stm.setString(1, idReserva);
        stm.executeUpdate();
    }

    private void cargarDetalle(Reserva r) throws SQLException {
        CategoriaRecursoDao cd = new CategoriaRecursoDao();
        RecursoDao rd = new RecursoDao();

        PreparedStatement stm = db.prepareStatement(
                "select * from ReservaCategoria rc " +
                "inner join CategoriaRecurso c on rc.categoria=c.id " +
                "where rc.reserva=? order by c.id");
        stm.setString(1, r.getId());
        ResultSet rs = db.executeQuery(stm);
        List<CategoriaRecurso> categorias = new ArrayList<CategoriaRecurso>();
        while (rs.next()) {
            categorias.add(cd.from(rs, "c"));
        }
        r.setCategoriasRequeridas(categorias);

        stm = db.prepareStatement(
                "select * from ReservaRecurso rr " +
                "inner join Recurso re on rr.recurso=re.id " +
                "inner join CategoriaRecurso c on re.categoria=c.id " +
                "where rr.reserva=? order by re.id");
        stm.setString(1, r.getId());
        rs = db.executeQuery(stm);
        List<Recurso> recursos = new ArrayList<Recurso>();
        while (rs.next()) {
            Recurso re = rd.from(rs, "re");
            re.setCategoria(cd.from(rs, "c"));
            recursos.add(re);
        }
        r.setRecursosAsignados(recursos);
    }

    private Reserva from(ResultSet rs, String alias) {
        try {
            Reserva r = new Reserva();
            r.setId(rs.getString(alias + ".id"));
            r.setActividad(rs.getString(alias + ".actividad"));
            r.setFecha(LocalDate.parse(rs.getString(alias + ".fecha")));
            r.setHoraInicio(LocalTime.parse(rs.getString(alias + ".horaInicio")));
            r.setHoraFin(LocalTime.parse(rs.getString(alias + ".horaFin")));
            r.setEstado(EstadoReserva.valueOf(rs.getString(alias + ".estado")));
            return r;
        } catch (SQLException ex) {
            return null;
        }
    }
}
