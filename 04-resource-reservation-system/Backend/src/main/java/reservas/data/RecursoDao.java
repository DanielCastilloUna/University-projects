package reservas.data;

import reservas.logic.CategoriaRecurso;
import reservas.logic.Recurso;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RecursoDao {
    Database db;

    public RecursoDao() {
        db = Database.instance();
    }

    public void create(Recurso r) throws Exception {
        String sql = "insert into Recurso (id, categoria, descripcion) values(?,?,?)";
        PreparedStatement stm = db.prepareStatement(sql);
        stm.setString(1, r.getId());
        stm.setString(2, r.getCategoria().getId());
        stm.setString(3, r.getDescripcion());
        int count = db.executeUpdate(stm);
        if (count == 0) {
            throw new Exception("Recurso ya existe o la categoria no existe");
        }
    }

    public Recurso read(String id) throws Exception {
        String sql = "select * from Recurso r " +
                "inner join CategoriaRecurso c on r.categoria=c.id " +
                "where r.id=?";
        PreparedStatement stm = db.prepareStatement(sql);
        stm.setString(1, id);
        ResultSet rs = db.executeQuery(stm);
        CategoriaRecursoDao cd = new CategoriaRecursoDao();
        if (rs.next()) {
            Recurso r = from(rs, "r");
            r.setCategoria(cd.from(rs, "c"));
            return r;
        } else {
            throw new Exception("Recurso no existe");
        }
    }

    public void update(Recurso r) throws Exception {
        String sql = "update Recurso set categoria=?, descripcion=? where id=?";
        PreparedStatement stm = db.prepareStatement(sql);
        stm.setString(1, r.getCategoria().getId());
        stm.setString(2, r.getDescripcion());
        stm.setString(3, r.getId());
        int count = db.executeUpdate(stm);
        if (count == 0) {
            throw new Exception("Recurso no existe");
        }
    }

    public void delete(String id) throws Exception {
        String sql = "delete from Recurso where id=?";
        PreparedStatement stm = db.prepareStatement(sql);
        stm.setString(1, id);
        int count = db.executeUpdate(stm);
        if (count == 0) {
            throw new Exception("No se puede eliminar el recurso: esta asignado a reservas");
        }
    }

    /** Busca por categoria (null = todas) y por texto contenido en la descripcion. */
    public List<Recurso> search(CategoriaRecurso categoria, String descripcion) {
        List<Recurso> resultado = new ArrayList<Recurso>();
        try {
            String sql = "select * from Recurso r " +
                    "inner join CategoriaRecurso c on r.categoria=c.id " +
                    "where r.descripcion like ? " +
                    (categoria != null ? "and r.categoria=? " : "") +
                    "order by r.id";
            PreparedStatement stm = db.prepareStatement(sql);
            stm.setString(1, "%" + (descripcion == null ? "" : descripcion) + "%");
            if (categoria != null) {
                stm.setString(2, categoria.getId());
            }
            ResultSet rs = db.executeQuery(stm);
            CategoriaRecursoDao cd = new CategoriaRecursoDao();
            while (rs.next()) {
                Recurso r = from(rs, "r");
                r.setCategoria(cd.from(rs, "c"));
                resultado.add(r);
            }
        } catch (SQLException ex) {
        }
        return resultado;
    }

    public List<Recurso> findAll() {
        return search(null, "");
    }

    /** Mayor numero usado en los ids "R-001" (0 si no hay recursos). */
    public int maxNumero() {
        try {
            String sql = "select ifnull(max(cast(substring(id, 3) as unsigned)), 0) from Recurso where id like 'R-%'";
            PreparedStatement stm = db.prepareStatement(sql);
            ResultSet rs = db.executeQuery(stm);
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException ex) {
        }
        return 0;
    }

    public Recurso from(ResultSet rs, String alias) {
        try {
            Recurso r = new Recurso();
            r.setId(rs.getString(alias + ".id"));
            r.setDescripcion(rs.getString(alias + ".descripcion"));
            return r;
        } catch (SQLException ex) {
            return null;
        }
    }
}
