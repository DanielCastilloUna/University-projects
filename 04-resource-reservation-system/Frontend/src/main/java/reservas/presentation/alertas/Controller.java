package reservas.presentation.alertas;

import reservas.logic.Reserva;
import reservas.logic.Service;
import reservas.presentation.SocketListener;
import reservas.presentation.SocketMonitor;

/**
 * Recibe del SocketMonitor las reservas nuevas de los demás usuarios
 * y las agrega al modelo; tambien atiende "ver" y "descartar" desde la View.
 */
public class Controller implements SocketListener {

    private final View view;
    private final Model model;
    private SocketMonitor monitor;

    public Controller(View view, Model model) {
        this.view = view;
        this.model = model;
        view.setController(this);
        view.setModel(model);
    }

    // Abre el canal asíncrono con el Backend y escucha.
    public void iniciar() throws Exception{
        monitor = new SocketMonitor(this, Service.instance().getSid());
        monitor.start();
    }

    public void stop(){
        if(monitor != null){
            monitor.stop();
        }
    }
    @Override
    public void deliver_reserva(Reserva reserva) {
        model.agregar(reserva);
    }

    public Reserva ver(int index) {
        return model.getList().get(index);
    }

    // Solo se quita de la lista local: NO se envia nada al Backend
    public void descartar(int index) {
        model.remover(index);
    }
}
