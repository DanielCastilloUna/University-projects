package reservas.logic;

import org.junit.jupiter.api.Test;
import reservas.data.CategoriaRecursoDao;
import reservas.data.RecursoDao;
import reservas.data.ReservaDao;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas de integracion de Reservas contra MySQL.
 * Cada prueba crea sus propios datos y los borra al terminar.
 */
class ReservaServiceIT {

    private Funcionario crearFuncionarioDePrueba() throws Exception {
        Funcionario f = new Funcionario();
        f.setId("IT-R-" + (System.nanoTime() % 100_000_000_000L));
        f.setNombre("Funcionario IT");
        f.setTelefono("12345678");
        Service.instance().crearFuncionario(f);
        return f;
    }

    @Test
    void reservaSinCategoriasDebeGuardarseYPoderLeerse() throws Exception {
        Funcionario f = crearFuncionarioDePrueba();
        String idReserva = null;
        try {
            ResultadoReserva res = Service.instance().intentarReservar(
                    f, "Actividad de prueba", LocalDate.now().plusDays(30),
                    LocalTime.of(10, 0), LocalTime.of(11, 0), new ArrayList<>());

            assertTrue(res.isExito());
            idReserva = res.getReserva().getId();
            assertTrue(res.getReserva().getRecursosAsignados().isEmpty());

            List<Reserva> mias = Service.instance().listarReservasDe(f);
            assertEquals(1, mias.size());
            assertEquals(idReserva, mias.get(0).getId());
            assertEquals("Actividad de prueba", mias.get(0).getActividad());
            assertEquals(f.getId(), mias.get(0).getFuncionario().getId());
            assertEquals(EstadoReserva.CONFIRMADA, mias.get(0).getEstado());
        } finally {
            if (idReserva != null) new ReservaDao().delete(idReserva);
            Service.instance().eliminarFuncionario(f.getId());
        }
    }

    @Test
    void reservaConCategoriaAsignaRecursoYBloqueaElTraslape() throws Exception {
        Funcionario f = crearFuncionarioDePrueba();
        CategoriaRecurso cat = new CategoriaRecurso();
        cat.setDescripcion("Categoria IT " + System.nanoTime());
        Service.instance().crearCategoria(cat);

        Recurso rec = new Recurso();
        rec.setCategoria(cat);
        rec.setDescripcion("Recurso IT unico");
        Service.instance().crearRecurso(rec);

        LocalDate fecha = LocalDate.now().plusDays(60);
        List<String> reservasCreadas = new ArrayList<>();
        try {
            // 1) Primera reserva 09:00-11:00: debe asignar el unico recurso de la categoria
            ResultadoReserva r1 = Service.instance().intentarReservar(
                    f, "Primera", fecha, LocalTime.of(9, 0), LocalTime.of(11, 0), List.of(cat));
            assertTrue(r1.isExito());
            reservasCreadas.add(r1.getReserva().getId());
            assertEquals(1, r1.getReserva().getRecursosAsignados().size());
            assertEquals(rec.getId(), r1.getReserva().getRecursosAsignados().get(0).getId());

            // 2) Reserva que se traslapa (10:00-12:00): el recurso ya no esta disponible
            ResultadoReserva r2 = Service.instance().intentarReservar(
                    f, "Traslapada", fecha, LocalTime.of(10, 0), LocalTime.of(12, 0), List.of(cat));
            assertFalse(r2.isExito());
            assertEquals(1, r2.getCategoriasNoDisponibles().size());

            // 3) Reserva pegada pero sin traslape (11:00-12:00): si se puede
            ResultadoReserva r3 = Service.instance().intentarReservar(
                    f, "Seguida", fecha, LocalTime.of(11, 0), LocalTime.of(12, 0), List.of(cat));
            assertTrue(r3.isExito());
            reservasCreadas.add(r3.getReserva().getId());

            // 4) El detalle (categoria y recurso con sus datos) se reconstruye con los JOIN
            Reserva leida = new ReservaDao().read(r1.getReserva().getId());
            assertEquals(cat.getId(), leida.getCategoriasRequeridas().get(0).getId());
            assertEquals(cat.getDescripcion(), leida.getRecursosAsignados().get(0).getCategoria().getDescripcion());

            // 5) Modificar la primera reserva a otro horario sin chocar consigo misma
            ResultadoReserva mod = Service.instance().modificarReserva(
                    r1.getReserva().getId(), "Primera modificada", fecha,
                    LocalTime.of(9, 30), LocalTime.of(10, 30), List.of(cat));
            assertTrue(mod.isExito());
            assertEquals("Primera modificada", new ReservaDao().read(r1.getReserva().getId()).getActividad());

            // 6) Estadistica por categoria: 2 reservas confirmadas usan la categoria
            Map<String, Integer> porCategoria = Service.instance().estadisticaRecursosPorCategoria(fecha, fecha);
            assertEquals(2, porCategoria.get(cat.getDescripcion()));

            // 7) Estadistica por semana: las 2 reservas caen en la misma semana
            Map<String, Integer> porSemana = Service.instance().estadisticaActividadesPorSemana(fecha, fecha);
            assertEquals(1, porSemana.size());
            assertEquals(2, porSemana.values().iterator().next());

            // 8) Cancelar (es futura) y ver el estado
            assertTrue(Service.instance().cancelarReserva(r3.getReserva().getId()));
            assertEquals(EstadoReserva.CANCELADA, new ReservaDao().read(r3.getReserva().getId()).getEstado());

            // 9) Un recurso asignado a reservas no se puede eliminar
            assertThrows(Exception.class, () -> Service.instance().eliminarRecurso(rec.getId()));
        } finally {
            for (String id : reservasCreadas) new ReservaDao().delete(id);
            new RecursoDao().delete(rec.getId());
            new CategoriaRecursoDao().delete(cat.getId());
            Service.instance().eliminarFuncionario(f.getId());
        }
    }

    @Test
    void cambiarClaveDebeQuedarGuardada() throws Exception {
        Funcionario f = crearFuncionarioDePrueba();
        try {
            Service.instance().cambiarClave(f, "nueva123");
            assertEquals("nueva123", Service.instance().buscarUsuarioPorId(f.getId()).getClave());
        } finally {
            Service.instance().eliminarFuncionario(f.getId());
        }
    }
}
