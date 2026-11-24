package reservas.logic;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Server {
    ServerSocket srv;
    List<Worker> workers;
    public Server() {
        // El puerto se puede cambiar con -Dreservas.port=XXXX (por defecto Protocol.PORT)
        int port = Integer.getInteger("reservas.port", Protocol.PORT);
        try {
            srv = new ServerSocket(port);
            workers = new ArrayList<Worker>();
            System.out.println("Servidor iniciado en el puerto " + port + "...");
        } catch (IOException ex) {
            System.out.println("No se pudo iniciar el servidor en el puerto " + port + ": " + ex);
            System.exit(-1);
        }
    }

    // Cada Frontend se conecta dos veces:
    //  1) SYNC: se crea su Worker y se le responde con un id de sesion (sid).
    //  2) ASYNC: envia ese sid para que el socket se una a su Worker (por ahi le llegan las notificaciones).
    public void run() {
        IService service = Service.instance();
        boolean continuar = true;
        Socket s;
        ObjectOutputStream os;
        ObjectInputStream is;
        while (continuar) {
            try {
                s = srv.accept();
                os = new ObjectOutputStream(s.getOutputStream());
                is = new ObjectInputStream(s.getInputStream());
                int type = is.readInt();
                switch (type) {
                    case Protocol.SYNC: {
                        System.out.println("Conexion Establecida (SYNC)...");
                        String sid = UUID.randomUUID().toString();
                        Worker worker = new Worker(this, s, os, is, sid, service);
                        this.add(worker);
                        os.writeObject(sid);
                        os.flush();
                        worker.start();
                        break;
                    }
                    case Protocol.ASYNC: {
                        System.out.println("Conexion Establecida (ASYNC)...");
                        String sid = (String) is.readObject();
                        join(s, os, is, sid);
                        break;
                    }
                    default:
                        s.close();
                }
            } catch (Exception ex) { System.out.println(ex);}
        }
    }

    public void add(Worker w) {
        synchronized(workers) {
            workers.add(w);
        }
        System.out.println("Quedan: " +workers.size());
    }

    public void remove(Worker w) {
        synchronized(workers) {
            workers.remove(w);
            System.out.println("Quedan: " + workers.size());
        }
    }

    // Une el socket ASYNC al Worker que tiene ese sid
    private void join(Socket as, ObjectOutputStream aos, ObjectInputStream ais, String sid) throws IOException {
        synchronized(workers) {
            for (Worker w : workers) {
                if (w.getSid().equals(sid)) {
                    w.setAs(as, aos, ais);
                    return;
                }
            }
        }
        as.close(); // sid desconocido
    }

    // Envia la reserva nueva a todos los Frontend conectados, menos al que la creo
    public void deliver(Reserva reserva, Worker from) {
        synchronized(workers) {
            for (Worker w : workers) {
                if (w != from) {
                    w.deliver(reserva);
                }
            }
        }
    }
}
