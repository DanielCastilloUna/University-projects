package reservas.logic;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FuncionarioServiceTest {

    @Test
    void crearFuncionarioSinNombreDebeFallar() {
        Funcionario f = new Funcionario();
        f.setId("999test");
        f.setTelefono("88888888");

        Exception ex = assertThrows(Exception.class, () -> {
            Service.instance().crearFuncionario(f);
        });

        assertEquals("El nombre es obligatorio", ex.getMessage());
    }

    @Test
    void crearFuncionarioSinTelefonoDebeFallar() {
        Funcionario f = new Funcionario();
        f.setId("999test2");
        f.setNombre("Prueba Test");

        Exception ex = assertThrows(Exception.class, () -> {
            Service.instance().crearFuncionario(f);
        });

        assertEquals("El telefono es obligatorio", ex.getMessage());
    }

    @Test
    void crearFuncionarioSinIdDebeFallar() {
        Funcionario f = new Funcionario();
        f.setNombre("Prueba Test");
        f.setTelefono("88888888");

        Exception ex = assertThrows(Exception.class, () -> {
            Service.instance().crearFuncionario(f);
        });

        assertEquals("El id es obligatorio", ex.getMessage());
    }

    @Test
    void buscarFuncionarioConIdVacioDebeRetornarNull() {
        Funcionario resultado = Service.instance().buscarFuncionarioPorId("");
        assertNull(resultado);
    }

    @Test
    void buscarFuncionarioInexistenteDebeRetornarNull() {
        Funcionario resultado = Service.instance().buscarFuncionarioPorId("id-que-no-existe-999");
        assertNull(resultado);
    }
}