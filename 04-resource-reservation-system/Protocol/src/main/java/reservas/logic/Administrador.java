package reservas.logic;

/**
 * Usuario de tipo administrador.
 * Puede gestionar Funcionarios, Categorias de recurso y Recursos.
 */
public class Administrador extends Usuario {

    public Administrador() {
        super();
        setRol(Rol.ADMINISTRADOR);
    }

    public Administrador(String id, String clave) {
        super(id, clave, Rol.ADMINISTRADOR);
    }

    // TODO: atributos/comportamiento adicional si aplica
}
