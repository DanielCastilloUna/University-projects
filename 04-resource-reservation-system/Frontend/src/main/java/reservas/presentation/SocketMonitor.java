package reservas.presentation;

import reservas.logic.Protocol;
import reservas.logic.Reserva;

import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/*** Abre el socket ASYNC y mantiene un hilo que espera las reservas que crean los demás usuarios
 * El hilo no toca la interfaz: le pasa cada reserva al listener en el hilo de Swing.
 *
 */
public class SocketMonitor {

    SocketListener listener;
    String sid;
    Socket as;
    ObjectOutputStream aos;
    ObjectInputStream ais;

    volatile boolean condition = true;
    private Thread t;

    public SocketMonitor(SocketListener listener, String sid) throws Exception {
        this.listener = listener;
        this.sid = sid;

        String host = System.getProperty("reservas.server", Protocol.SERVER);
        int port = Integer.getInteger("reservas.port", Protocol.PORT);

        as = new Socket(host, port);
        aos = new ObjectOutputStream(as.getOutputStream());
        ais = new ObjectInputStream(as.getInputStream());
        aos.writeInt(Protocol.ASYNC);
        aos.writeObject(sid);
        aos.flush();
    }
    public void start(){
        t = new Thread(this::listen);
        t.setDaemon(true);
        condition = true;
        t.start();
    }

    public void stop(){
    condition = false;
    try{
        as.close();
    } catch (IOException e) {}
    }
    public void listen(){
        while (condition) {
            try {
                int method = ais.readInt();
                if (method == Protocol.DELIVER_RESERVA) {
                    Reserva reserva = (Reserva) ais.readObject();
                    SwingUtilities.invokeLater(() -> listener.deliver_reserva(reserva));
                }
            } catch (IOException | ClassNotFoundException ex) {
                condition = false;   // conexion cerrada (por stop() o porque el Backend se cayo)
            }
        }
        try {
            as.shutdownOutput();
            as.close();
        } catch (IOException e) {}
    }
}