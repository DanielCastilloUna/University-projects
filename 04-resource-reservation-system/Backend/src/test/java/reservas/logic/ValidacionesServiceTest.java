package reservas.logic;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/** Una prueba por cada regla de validacion del Service del Backend. */
class ValidacionesServiceTest {

    private static final LocalDate FECHA = LocalDate.of(2026, 11, 2);
    private static final LocalTime INICIO = LocalTime.of(8, 0);
    private static final LocalTime FIN = LocalTime.of(9, 0);

    private final IService service = Service.instance();

    private static String texto(int largo) {
        return "x".repeat(largo);
    }

    private static Funcionario funcionarioValido() {
        Funcionario f = new Funcionario();
        f.setId("val-test");
        f.setNombre("Prueba");
        f.setTelefono("88888888");
        return f;
    }

    private static void assertFalla(Class<? extends Exception> tipo, String mensaje, Executable accion) {
        Exception ex = assertThrows(tipo, accion);
        assertEquals(mensaje, ex.getMessage());
    }

    private void reservar(Funcionario f, String actividad, LocalDate fecha,
                          LocalTime inicio, LocalTime fin) {
        service.intentarReservar(f, actividad, fecha, inicio, fin, new ArrayList<>());
    }

    // ---------------- reservas (IllegalArgumentException) ----------------
    @Test
    void reservaSinFuncionarioDebeFallar() {
        assertFalla(IllegalArgumentException.class, "El funcionario es obligatorio",
                () -> reservar(null, "Reunion", FECHA, INICIO, FIN));
    }

    @Test
    void reservaSinFechaDebeFallar() {
        assertFalla(IllegalArgumentException.class, "La fecha es obligatoria",
                () -> reservar(funcionarioValido(), "Reunion", null, INICIO, FIN));
    }

    @Test
    void reservaSinHoraInicioDebeFallar() {
        assertFalla(IllegalArgumentException.class, "La hora de inicio es obligatoria",
                () -> reservar(funcionarioValido(), "Reunion", FECHA, null, FIN));
    }

    @Test
    void reservaSinHoraFinDebeFallar() {
        assertFalla(IllegalArgumentException.class, "La hora de fin es obligatoria",
                () -> reservar(funcionarioValido(), "Reunion", FECHA, INICIO, null));
    }

    @Test
    void reservaSinActividadDebeFallar() {
        assertFalla(IllegalArgumentException.class, "La actividad es obligatoria",
                () -> reservar(funcionarioValido(), null, FECHA, INICIO, FIN));
    }

    @Test
    void reservaConActividadEnBlancoDebeFallar() {
        assertFalla(IllegalArgumentException.class, "La actividad es obligatoria",
                () -> reservar(funcionarioValido(), "   ", FECHA, INICIO, FIN));
    }

    @Test
    void reservaConActividadMayorA200CaracteresDebeFallar() {
        assertFalla(IllegalArgumentException.class,
                "La actividad no puede tener más de 200 caracteres",
                () -> reservar(funcionarioValido(), texto(201), FECHA, INICIO, FIN));
    }

    @Test
    void reservaConHoraFinAnteriorAInicioDebeFallar() {
        assertFalla(IllegalArgumentException.class,
                "La hora de fin debe ser posterior a la hora de inicio",
                () -> reservar(funcionarioValido(), "Reunion", FECHA, FIN, INICIO));
    }

    @Test
    void reservaConHoraFinIgualAInicioDebeFallar() {
        assertFalla(IllegalArgumentException.class,
                "La hora de fin debe ser posterior a la hora de inicio",
                () -> reservar(funcionarioValido(), "Reunion", FECHA, INICIO, INICIO));
    }

    // ---------------- funcionario (Exception) ----------------
    @Test
    void crearFuncionarioConIdMayorA20CaracteresDebeFallar() {
        Funcionario f = funcionarioValido();
        f.setId(texto(21));
        assertFalla(Exception.class, "El id no puede tener más de 20 caracteres",
                () -> service.crearFuncionario(f));
    }

    @Test
    void crearFuncionarioConNombreMayorA100CaracteresDebeFallar() {
        Funcionario f = funcionarioValido();
        f.setNombre(texto(101));
        assertFalla(Exception.class, "El nombre no puede tener más de 100 caracteres",
                () -> service.crearFuncionario(f));
    }

    @Test
    void crearFuncionarioConTelefonoMayorA30CaracteresDebeFallar() {
        Funcionario f = funcionarioValido();
        f.setTelefono(texto(31));
        assertFalla(Exception.class, "El teléfono no puede tener más de 30 caracteres",
                () -> service.crearFuncionario(f));
    }

    @Test
    void modificarFuncionarioConIdMayorA20CaracteresDebeFallar() {
        Funcionario f = funcionarioValido();
        f.setId(texto(21));
        assertFalla(Exception.class, "El id no puede tener más de 20 caracteres",
                () -> service.modificarFuncionario(f));
    }

    @Test
    void modificarFuncionarioConNombreMayorA100CaracteresDebeFallar() {
        Funcionario f = funcionarioValido();
        f.setNombre(texto(101));
        assertFalla(Exception.class, "El nombre no puede tener más de 100 caracteres",
                () -> service.modificarFuncionario(f));
    }

    @Test
    void modificarFuncionarioConTelefonoMayorA30CaracteresDebeFallar() {
        Funcionario f = funcionarioValido();
        f.setTelefono(texto(31));
        assertFalla(Exception.class, "El teléfono no puede tener más de 30 caracteres",
                () -> service.modificarFuncionario(f));
    }

    // ---------------- clave ----------------
    @Test
    void cambiarClaveMayorA50CaracteresDebeFallar() {
        assertFalla(Exception.class, "La clave no puede tener más de 50 caracteres",
                () -> service.cambiarClave(funcionarioValido(), texto(51)));
    }

    // ---------------- categoria y recurso ----------------
    @Test
    void crearCategoriaConDescripcionMayorA100CaracteresDebeFallar() {
        CategoriaRecurso c = new CategoriaRecurso();
        c.setDescripcion(texto(101));
        assertFalla(Exception.class, "La descripción no puede tener más de 100 caracteres",
                () -> service.crearCategoria(c));
    }

    @Test
    void crearRecursoConDescripcionMayorA150CaracteresDebeFallar() {
        Recurso r = new Recurso();
        r.setCategoria(new CategoriaRecurso());
        r.setDescripcion(texto(151));
        assertFalla(Exception.class, "La descripción no puede tener más de 150 caracteres",
                () -> service.crearRecurso(r));
    }
}