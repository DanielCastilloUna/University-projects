package reservas.logic;

import java.io.Serializable;


public class Recurso implements Serializable {

    private String id;
    private CategoriaRecurso categoria;
    private String descripcion;

    public Recurso() {
    }

    public Recurso(String id, CategoriaRecurso categoria, String descripcion) {
        this.id = id;
        this.categoria = categoria;
        this.descripcion = descripcion;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public CategoriaRecurso getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaRecurso categoria) {
        this.categoria = categoria;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    // TODO: equals/hashCode/toString
}
