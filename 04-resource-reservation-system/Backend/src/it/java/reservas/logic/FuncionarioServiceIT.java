package reservas.logic;

import org.junit.jupiter.api.Test;
import reservas.data.FuncionarioDao;

import static org.junit.jupiter.api.Assertions.*;

class FuncionarioServiceIT {

    @Test
    void crearFuncionarioDebeQuedarGuardadoEnLaBaseDeDatos() throws Exception {
        String idPrueba = "IT-" + System.currentTimeMillis();

        Funcionario nuevo = new Funcionario();
        nuevo.setId(idPrueba);
        nuevo.setNombre("Integracion Test");
        nuevo.setTelefono("11112222");

        Service.instance().crearFuncionario(nuevo);

        assertEquals(idPrueba, nuevo.getClave());

        // Se lee directo desde la capa de datos (consulta a MySQL), sin pasar por el Service
        Funcionario guardado = new FuncionarioDao().read(idPrueba);
        assertEquals("Integracion Test", guardado.getNombre());
        assertEquals("11112222", guardado.getTelefono());
        assertEquals(idPrueba, guardado.getClave());

        Service.instance().eliminarFuncionario(idPrueba);
    }

    @Test
    void crearYEliminarFuncionarioDebeQuitarloDelListado() throws Exception {
        String idPrueba = "IT-DEL-" + System.currentTimeMillis();

        Funcionario nuevo = new Funcionario();
        nuevo.setId(idPrueba);
        nuevo.setNombre("Para Borrar");
        nuevo.setTelefono("33334444");

        Service.instance().crearFuncionario(nuevo);
        assertNotNull(Service.instance().buscarFuncionarioPorId(idPrueba));

        Service.instance().eliminarFuncionario(idPrueba);
        assertNull(Service.instance().buscarFuncionarioPorId(idPrueba));
    }

    @Test
    void crearFuncionarioDuplicadoDebeFallar() throws Exception {
        String idPrueba = "IT-DUP-" + System.currentTimeMillis();

        Funcionario nuevo = new Funcionario();
        nuevo.setId(idPrueba);
        nuevo.setNombre("Duplicado");
        nuevo.setTelefono("55556666");
        Service.instance().crearFuncionario(nuevo);

        try {
            Funcionario otro = new Funcionario();
            otro.setId(idPrueba);
            otro.setNombre("Otro");
            otro.setTelefono("77778888");
            Exception ex = assertThrows(Exception.class, () -> Service.instance().crearFuncionario(otro));
            assertEquals("Ya existe un funcionario con id " + idPrueba, ex.getMessage());
        } finally {
            Service.instance().eliminarFuncionario(idPrueba);
        }
    }
}
