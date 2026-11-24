package reservas.data;

import reservas.logic.Funcionario;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FuncionarioDao {
    Database db;

    public FuncionarioDao() {
        db = Database.instance();
    }

    public void create(Funcionario f) throws Exception {
        String sql = "insert into Funcionario (id, clave, nombre, telefono) values(?,?,?,?)";
        PreparedStatement stm = db.prepareStatement(sql);
        stm.setString(1, f.getId());
        stm.setString(2, f.getClave());
        stm.setString(3, f.getNombre());
        stm.setString(4, f.getTelefono());
        int count = db.executeUpdate(stm);
        if (count == 0) {
            throw new Exception("Funcionario ya existe");
        }
    }

    public Funcionario read(String id) throws Exception {
        String sql = "select * from Funcionario f where f.id=?";
        PreparedStatement stm = db.prepareStatement(sql);
        stm.setString(1, id);
        ResultSet rs = db.executeQuery(stm);
        if (rs.next()) {
            return from(rs, "f");
        } else {
            throw new Exception("Funcionario no existe");
        }
    }

    /** Actualiza nombre y telefono. La clave se cambia con updateClave. */
    public void update(Funcionario f) throws Exception {
        String sql = "update Funcionario set nombre=?, telefono=? where id=?";
        PreparedStatement stm = db.prepareStatement(sql);
        stm.setString(1, f.getNombre());
        stm.setString(2, f.getTelefono());
        stm.setString(3, f.getId());
        int count = db.executeUpdate(stm);
        if (count == 0) {
            throw new Exception("Funcionario no existe");
        }
    }

    public void updateClave(String id, String claveNueva) throws Exception {
        String sql = "update Funcionario set clave=? where id=?";
        PreparedStatement stm = db.prepareStatement(sql);
        stm.setString(1, claveNueva);
        stm.setString(2, id);
        int count = db.executeUpdate(stm);
        if (count == 0) {
            throw new Exception("Funcionario no existe");
        }
    }

    public void delete(String id) throws Exception {
        String sql = "delete from Funcionario where id=?";
        PreparedStatement stm = db.prepareStatement(sql);
        stm.setString(1, id);
        int count = db.executeUpdate(stm);
        if (count == 0) {
            throw new Exception("No se puede eliminar el funcionario: tiene reservas asociadas");
        }
    }

    public List<Funcionario> findAll() {
        List<Funcionario> resultado = new ArrayList<Funcionario>();
        try {
            String sql = "select * from Funcionario f order by f.id";
            PreparedStatement stm = db.prepareStatement(sql);
            ResultSet rs = db.executeQuery(stm);
            while (rs.next()) {
                resultado.add(from(rs, "f"));
            }
        } catch (SQLException ex) {
        }
        return resultado;
    }

    public List<Funcionario> findByNombre(String nombre) {
        List<Funcionario> resultado = new ArrayList<Funcionario>();
        try {
            String sql = "select * from Funcionario f where f.nombre like ? order by f.id";
            PreparedStatement stm = db.prepareStatement(sql);
            stm.setString(1, "%" + nombre + "%");
            ResultSet rs = db.executeQuery(stm);
            while (rs.next()) {
                resultado.add(from(rs, "f"));
            }
        } catch (SQLException ex) {
        }
        return resultado;
    }

    public Funcionario from(ResultSet rs, String alias) {
        try {
            Funcionario f = new Funcionario();
            f.setId(rs.getString(alias + ".id"));
            f.setClave(rs.getString(alias + ".clave"));
            f.setNombre(rs.getString(alias + ".nombre"));
            f.setTelefono(rs.getString(alias + ".telefono"));
            return f;
        } catch (SQLException ex) {
            return null;
        }
    }
}
