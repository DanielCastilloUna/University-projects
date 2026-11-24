package reservas.logic;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReservaServiceTest {

    @Test
    void cancelarReservaInexistenteDebeRetornarFalse() {
        boolean resultado = Service.instance().cancelarReserva("id-que-no-existe-999");
        assertFalse(resultado);
    }
}