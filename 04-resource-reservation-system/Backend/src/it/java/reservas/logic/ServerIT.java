package reservas.logic;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import reservas.data.ReservaDao;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Prueba de integracion del Server/Worker: levanta el servidor real en un puerto libre
 * y le habla por el socket con el protocolo (sin pasar por el Proxy del Frontend).
 * Cada prueba crea sus propios datos y los elimina al terminar, asi que no depende
 * de los datos de ejemplo de la base.
 */
class ServerIT {

    static Socket socket;
    static ObjectOutputStream os;
    static ObjectInputStream is;

    @BeforeAll
    static void conectar() throws Exception {
        int port;
        try (ServerSocket libre = new ServerSocket(0)) {
            port = libre.getLocalPort();
        }
        System.setProperty("reservas.port", String.valueOf(port));

        Server server = new Server();
        Thread t = new Thread(server::run);
        t.setDaemon(true);
        t.start();

        socket = new Socket("localhost", port);
        os = new ObjectOutputStream(socket.getOutputStream());
        is = new ObjectInputStream(socket.getInputStream());
        handshakeSync(os, is);
    }

    // Saludo SYNC: el servidor crea el Worker y responde con el id de sesion
    private static String handshakeSync(ObjectOutputStream os, ObjectInputStream is) throws Exception {
        os.writeInt(Protocol.SYNC);
        os.flush();
        return (String) is.readObject();
    }

    // Abre el socket ASYNC de un cliente (como lo hace el proxy del Frontend)
    private static Socket abrirAsync(String sid) throws Exception {
        Socket as = new Socket("localhost", Integer.getInteger("reservas.port"));
        ObjectOutputStream aos = new ObjectOutputStream(as.getOutputStream());
        aos.writeInt(Protocol.ASYNC);
        aos.writeObject(sid);
        aos.flush();
        return as;
    }

    @AfterAll
    static void cerrar() throws Exception {
        socket.close();
    }

    // Envia una operacion y devuelve {codigo, resultado o mensaje de error}
    private static Object[] request(int operation, Object... args) throws Exception {
        os.reset();
        os.writeInt(operation);
        for (Object arg : args) {
            os.writeObject(arg);
        }
        os.flush();
        int status = is.readInt();
        Object payload = is.readObject();
        return new Object[]{status, payload};
    }

    // Crea un funcionario de prueba directamente en el Service (no por el socket)
    private static Funcionario crearFuncionarioDePrueba(String prefijo) throws Exception {
        Funcionario f = new Funcionario();
        // Id corto: la columna id admite 20 caracteres como maximo
        f.setId(prefijo + (System.nanoTime() % 100_000_000_000L));
        f.setNombre("Funcionario ServerIT");
        f.setTelefono("12345678");
        Service.instance().crearFuncionario(f);
        return f;
    }

    @Test
    @SuppressWarnings("unchecked")
    void listarFuncionariosPorSocketDebeIncluirAlFuncionarioCreado() throws Exception {
        Funcionario creado = crearFuncionarioDePrueba("SRV-L-");
        try {
            Object[] respuesta = request(Protocol.FUNCIONARIO_FINDALL);

            assertEquals(Protocol.ERROR_NO_ERROR, respuesta[0]);
            List<Funcionario> funcionarios = (List<Funcionario>) respuesta[1];
            assertTrue(funcionarios.stream().anyMatch(f -> creado.getId().equals(f.getId())));
        } finally {
            Service.instance().eliminarFuncionario(creado.getId());
        }
    }

    @Test
    void unErrorDelServiceDebeLlegarConSuMensaje() throws Exception {
        Funcionario existente = crearFuncionarioDePrueba("SRV-D-");
        try {
            Funcionario duplicado = new Funcionario();
            duplicado.setId(existente.getId());
            duplicado.setNombre("Duplicado");
            duplicado.setTelefono("1234");

            Object[] respuesta = request(Protocol.FUNCIONARIO_CREATE, duplicado);

            assertEquals(Protocol.ERROR_ERROR, respuesta[0]);
            assertEquals("Ya existe un funcionario con id " + existente.getId(), respuesta[1]);
        } finally {
            Service.instance().eliminarFuncionario(existente.getId());
        }
    }

    @Test
    void unaOperacionConResultadoNuloDebeResponderSinError() throws Exception {
        Object[] respuesta = request(Protocol.FUNCIONARIO_READ, "id-que-no-existe-999");

        assertEquals(Protocol.ERROR_NO_ERROR, respuesta[0]);
        assertNull(respuesta[1]);
    }

    @Test
    void desconectarDebeCerrarLaConexion() throws Exception {
        Socket otro = new Socket("localhost", Integer.getInteger("reservas.port"));
        ObjectOutputStream otroOs = new ObjectOutputStream(otro.getOutputStream());
        ObjectInputStream otroIs = new ObjectInputStream(otro.getInputStream());
        handshakeSync(otroOs, otroIs);

        otroOs.writeInt(Protocol.DISCONNECT);
        otroOs.flush();

        // El servidor cierra su lado: la lectura termina con error (fin de la conexion)
        assertThrows(IOException.class, otroIs::readInt);
        otro.close();
    }

    @Test
    void unaReservaNuevaDebeNotificarseALosDemasClientesPeroNoAlQueLaCreo() throws Exception {
        int port = Integer.getInteger("reservas.port");

        // Cliente A: solo escucha (SYNC + ASYNC)
        Socket syncA = new Socket("localhost", port);
        ObjectOutputStream osA = new ObjectOutputStream(syncA.getOutputStream());
        ObjectInputStream isA = new ObjectInputStream(syncA.getInputStream());
        Socket asyncA = abrirAsync(handshakeSync(osA, isA));
        asyncA.setSoTimeout(5000);
        ObjectInputStream aisA = new ObjectInputStream(asyncA.getInputStream());

        // Cliente B: crea la reserva (SYNC + ASYNC)
        Socket syncB = new Socket("localhost", port);
        ObjectOutputStream osB = new ObjectOutputStream(syncB.getOutputStream());
        ObjectInputStream isB = new ObjectInputStream(syncB.getInputStream());
        Socket asyncB = abrirAsync(handshakeSync(osB, isB));
        asyncB.setSoTimeout(1000);
        ObjectInputStream aisB = new ObjectInputStream(asyncB.getInputStream());

        Funcionario f = crearFuncionarioDePrueba("SRV-A-");
        String idReserva = null;
        try {
            osB.writeInt(Protocol.RESERVA_CREATE);
            osB.writeObject(f);
            osB.writeObject("Reunion ServerIT");
            osB.writeObject(LocalDate.now().plusDays(90));
            osB.writeObject(LocalTime.of(8, 0));
            osB.writeObject(LocalTime.of(9, 0));
            osB.writeObject(new ArrayList<CategoriaRecurso>());
            osB.flush();
            assertEquals(Protocol.ERROR_NO_ERROR, isB.readInt());
            ResultadoReserva resultado = (ResultadoReserva) isB.readObject();
            assertTrue(resultado.isExito());
            idReserva = resultado.getReserva().getId();

            // A recibe la alerta con la reserva
            assertEquals(Protocol.DELIVER_RESERVA, aisA.readInt());
            Reserva notificada = (Reserva) aisA.readObject();
            assertEquals(idReserva, notificada.getId());
            assertEquals("Reunion ServerIT", notificada.getActividad());

            // B (quien la creo) no recibe nada
            assertThrows(SocketTimeoutException.class, aisB::readInt);
        } finally {
            if (idReserva != null) new ReservaDao().delete(idReserva);
            Service.instance().eliminarFuncionario(f.getId());
            syncA.close(); asyncA.close();
            syncB.close(); asyncB.close();
        }
    }
}