package reservas.data;

import reservas.logic.Administrador;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdministradorDao {
    Database db;

    public AdministradorDao() {
        db = Database.instance();
    }

    public void create(Administrador a) throws Exception {
        String sql = "insert into Administrador (id, clave) values(?,?)";
        PreparedStatement stm = db.prepareStatement(sql);
        stm.setString(1, a.getId());
        stm.setString(2, a.getClave());
        int count = db.executeUpdate(stm);
        if (count == 0) {
            throw new Exception("Administrador ya existe");
        }
    }

    public Administrador read(String id) throws Exception {
        String sql = "select * from Administrador a where a.id=?";
        PreparedStatement stm = db.prepareStatement(sql);
        stm.setString(1, id);
        ResultSet rs = db.executeQuery(stm);
        if (rs.next()) {
            return from(rs, "a");
        } else {
            throw new Exception("Administrador no existe");
        }
    }

    public void updateClave(String id, String claveNueva) throws Exception {
        String sql = "update Administrador set clave=? where id=?";
        PreparedStatement stm = db.prepareStatement(sql);
        stm.setString(1, claveNueva);
        stm.setString(2, id);
        int count = db.executeUpdate(stm);
        if (count == 0) {
            throw new Exception("Administrador no existe");
        }
    }

    public int count() {
        try {
            PreparedStatement stm = db.prepareStatement("select count(*) from Administrador");
            ResultSet rs = db.executeQuery(stm);
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException ex) {
        }
        return 0;
    }

    public Administrador from(ResultSet rs, String alias) {
        try {
            Administrador a = new Administrador();
            a.setId(rs.getString(alias + ".id"));
            a.setClave(rs.getString(alias + ".clave"));
            return a;
        } catch (SQLException ex) {
            return null;
        }
    }
}
