package reservas.logic;

/**
 * Estados posibles de una Reserva.
 */
public enum EstadoReserva {
    CONFIRMADA,
    ACTIVA,
    CANCELADA
    // TODO: evaluar si se requiere un estado adicional (ej. FINALIZADA) para reservas pasadas
}
