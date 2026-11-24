package reservas.data;

import reservas.logic.CategoriaRecurso;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategoriaRecursoDao {
    Database db;

    public CategoriaRecursoDao() {
        db = Database.instance();
    }

    public void create(CategoriaRecurso c) throws Exception {
        String sql = "insert into CategoriaRecurso (id, descripcion) values(?,?)";
        PreparedStatement stm = db.prepareStatement(sql);
        stm.setString(1, c.getId());
        stm.setString(2, c.getDescripcion());
        int count = db.executeUpdate(stm);
        if (count == 0) {
            throw new Exception("Categoria ya existe");
        }
    }

    public CategoriaRecurso read(String id) throws Exception {
        String sql = "select * from CategoriaRecurso c where c.id=?";
        PreparedStatement stm = db.prepareStatement(sql);
        stm.setString(1, id);
        ResultSet rs = db.executeQuery(stm);
        if (rs.next()) {
            return from(rs, "c");
        } else {
            throw new Exception("Categoria no existe");
        }
    }

    public void update(CategoriaRecurso c) throws Exception {
        String sql = "update CategoriaRecurso set descripcion=? where id=?";
        PreparedStatement stm = db.prepareStatement(sql);
        stm.setString(1, c.getDescripcion());
        stm.setString(2, c.getId());
        int count = db.executeUpdate(stm);
        if (count == 0) {
            throw new Exception("Categoria no existe");
        }
    }

    public void delete(String id) throws Exception {
        String sql = "delete from CategoriaRecurso where id=?";
        PreparedStatement stm = db.prepareStatement(sql);
        stm.setString(1, id);
        int count = db.executeUpdate(stm);
        if (count == 0) {
            throw new Exception("No se puede eliminar la categoria: tiene recursos o reservas asociados");
        }
    }

    public List<CategoriaRecurso> findAll() {
        List<CategoriaRecurso> resultado = new ArrayList<CategoriaRecurso>();
        try {
            String sql = "select * from CategoriaRecurso c order by c.id";
            PreparedStatement stm = db.prepareStatement(sql);
            ResultSet rs = db.executeQuery(stm);
            while (rs.next()) {
                resultado.add(from(rs, "c"));
            }
        } catch (SQLException ex) {
        }
        return resultado;
    }

    public List<CategoriaRecurso> findByDescripcion(String descripcion) {
        List<CategoriaRecurso> resultado = new ArrayList<CategoriaRecurso>();
        try {
            String sql = "select * from CategoriaRecurso c where c.descripcion like ? order by c.id";
            PreparedStatement stm = db.prepareStatement(sql);
            stm.setString(1, "%" + descripcion + "%");
            ResultSet rs = db.executeQuery(stm);
            while (rs.next()) {
                resultado.add(from(rs, "c"));
            }
        } catch (SQLException ex) {
        }
        return resultado;
    }

    /** Mayor numero usado en los ids "CAT-000001" (0 si no hay categorias). */
    public int maxNumero() {
        try {
            String sql = "select ifnull(max(cast(substring(id, 5) as unsigned)), 0) from CategoriaRecurso where id like 'CAT-%'";
            PreparedStatement stm = db.prepareStatement(sql);
            ResultSet rs = db.executeQuery(stm);
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException ex) {
        }
        return 0;
    }

    public CategoriaRecurso from(ResultSet rs, String alias) {
        try {
            CategoriaRecurso c = new CategoriaRecurso();
            c.setId(rs.getString(alias + ".id"));
            c.setDescripcion(rs.getString(alias + ".descripcion"));
            return c;
        } catch (SQLException ex) {
            return null;
        }
    }
}
