package reservas.logic;

import java.io.Serializable;

import java.util.Objects;

public class CategoriaRecurso implements Serializable {

    private String id;
    private String descripcion;

    public CategoriaRecurso() {
    }

    public CategoriaRecurso(String id, String descripcion) {
        this.id = id;
        this.descripcion = descripcion;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public boolean equals(Object o){
        if(this==o) return true;
        if(!(o instanceof CategoriaRecurso)) return false;
        CategoriaRecurso that = (CategoriaRecurso) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode(){
        return Objects.hash(getId());
    }

    @Override
    public String toString(){
        return descripcion;
    }
}
