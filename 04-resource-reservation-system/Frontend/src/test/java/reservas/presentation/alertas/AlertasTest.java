package reservas.presentation.alertas;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reservas.logic.Funcionario;
import reservas.logic.Reserva;

import javax.swing.Icon;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias del area de Alertas (Model + Controller).
 * No necesitan Backend: se simula la llegada de reservas llamando a deliver_reserva().
 */
class AlertasTest {

    Model model;
    Controller controller;

    @BeforeEach
    void setUp() {
        model = new Model();
        controller = new Controller(new View(), model);
    }

    private Reserva reserva(String id, String actividad) {
        Funcionario f = new Funcionario();
        f.setId("F1");
        f.setNombre("Ana");
        return new Reserva(id, f, actividad, LocalDate.of(2026, 11, 2), LocalTime.of(8, 0), LocalTime.of(9, 0));
    }

    @Test
    void alIniciarNoHayAlertas() {
        assertTrue(model.getList().isEmpty());
    }

    @Test
    void deliverAgregaLaAlertaYLaMasRecienteQuedaDePrimera() {
        controller.deliver_reserva(reserva("R1", "Primera"));
        controller.deliver_reserva(reserva("R2", "Segunda"));

        assertEquals(2, model.getList().size());
        assertEquals("R2", model.getList().get(0).getId());
        assertEquals("R1", model.getList().get(1).getId());
    }

    @Test
    void verDevuelveLaReservaDeEsaPosicion() {
        controller.deliver_reserva(reserva("R1", "Primera"));
        controller.deliver_reserva(reserva("R2", "Segunda"));

        assertEquals("Primera", controller.ver(1).getActividad());
    }

    @Test
    void descartarQuitaSoloEsaAlerta() {
        controller.deliver_reserva(reserva("R1", "Primera"));
        controller.deliver_reserva(reserva("R2", "Segunda"));

        controller.descartar(0);

        assertEquals(1, model.getList().size());
        assertEquals("R1", model.getList().get(0).getId());
    }

    @Test
    void laTablaMuestraElResumenYLosIconosDeVerYDescartar() {
        controller.deliver_reserva(reserva("R1", "Reunion"));
        int[] cols = {TableModel.ALERTA, TableModel.VER, TableModel.DESCARTAR};
        TableModel tabla = new TableModel(cols, model.getList());

        assertEquals(1, tabla.getRowCount());
        assertEquals(3, tabla.getColumnCount());
        String resumen = (String) tabla.getValueAt(0, TableModel.ALERTA);
        assertEquals("Actividad", tabla.getColumnName(TableModel.ALERTA));
        assertEquals("Ver", tabla.getColumnName(TableModel.VER));
        assertEquals("Desc.", tabla.getColumnName(TableModel.DESCARTAR));
        assertEquals("Ana - Reunion", resumen);
        assertNotNull(tabla.getValueAt(0, TableModel.VER));
        assertInstanceOf(Icon.class, tabla.getValueAt(0, TableModel.VER));
        assertNotNull(tabla.getValueAt(0, TableModel.DESCARTAR));
        assertInstanceOf(Icon.class, tabla.getValueAt(0, TableModel.DESCARTAR));
    }

    @Test
    void cadaCambioDeLaListaNotificaALaVista() {
        int[] avisos = {0};
        model.addPropertyChangeListener(evt -> {
            if (Model.LIST.equals(evt.getPropertyName())) avisos[0]++;
        });
        avisos[0] = 0; // el aviso inicial al registrarse no cuenta

        controller.deliver_reserva(reserva("R1", "Primera"));
        controller.descartar(0);

        assertEquals(2, avisos[0]);
    }
    @Test
    void stopSinHaberLlamadoAIniciarNoFalla(){
        assertDoesNotThrow(() -> controller.stop());
    }
}
