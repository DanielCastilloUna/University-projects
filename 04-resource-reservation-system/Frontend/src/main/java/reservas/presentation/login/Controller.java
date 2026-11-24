package reservas.presentation.login;

import reservas.logic.Service;
import reservas.logic.Usuario;
import reservas.presentation.Sesion;

public class Controller {

    private final View view;
    private final Model model;

    public Controller(View view, Model model) {
        this.view = view;
        this.model = model;
        view.setController(this);
        view.setModel(model);
    }

    public void login(String id, String clave) throws Exception {
        Usuario encontrado = Service.instance().buscarUsuarioPorId(id);
        if (encontrado == null || !encontrado.getClave().equals(clave)) {
            throw new Exception("Usuario o clave incorrectos");
        }
        Sesion.setUsuario(encontrado);
    }

    public boolean cambiarClave(String id, String claveActual, String claveNueva) throws Exception {
        Usuario usuario = Service.instance().buscarUsuarioPorId(id);
        if (usuario == null) {
            throw new Exception("El usuario no existe");
        }
        if (!usuario.getClave().equals(claveActual)) {
            throw new Exception("La clave actual no es correcta");
        }
        Service.instance().cambiarClave(usuario, claveNueva);
        return true;
    }
}