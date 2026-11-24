package reservas.presentation;

import reservas.logic.Reserva;

/*** Quien quiera recibir las notificaciones asincrónicas del Backend (por ejemplo,
 * el Controller de Alertas) implementa esta interfaz.
 *
 */
public interface SocketListener {
    void deliver_reserva(Reserva reserva);
}
