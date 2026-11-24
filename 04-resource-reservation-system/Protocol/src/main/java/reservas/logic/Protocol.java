package reservas.logic;

/**
 * Constantes compartidas por el Frontend (Proxy) y el Backend (Server/Worker):
 * donde esta el servidor y los codigos de cada operacion que viaja por el socket.
 */
public class Protocol {
    public static final String SERVER = "localhost";
    public static final int PORT = 1234;

    // FUNCIONARIOS
    public static final int FUNCIONARIO_CREATE=101;
    public static final int FUNCIONARIO_READ=102;
    public static final int FUNCIONARIO_UPDATE=103;
    public static final int FUNCIONARIO_DELETE=104;
    public static final int FUNCIONARIO_FINDALL=105;
    public static final int FUNCIONARIO_FIND_BY_NOMBRE=106;

    // CATEGORIAS DE RECURSO
    public static final int CATEGORIA_CREATE=201;
    public static final int CATEGORIA_READ=202;
    public static final int CATEGORIA_UPDATE=203;
    public static final int CATEGORIA_DELETE=204;
    public static final int CATEGORIA_FINDALL=205;
    public static final int CATEGORIA_FIND_BY_DESCRIPCION=206;

    // RECURSOS
    public static final int RECURSO_CREATE=301;
    public static final int RECURSO_READ=302;
    public static final int RECURSO_UPDATE=303;
    public static final int RECURSO_DELETE=304;
    public static final int RECURSO_SEARCH=305;

    // USUARIOS (login y cambio de clave)
    public static final int ADMINISTRADOR_CREATE=401;
    public static final int USUARIO_READ=402;
    public static final int USUARIO_CAMBIAR_CLAVE=403;

    // RESERVAS
    public static final int RESERVA_CREATE=501;
    public static final int RESERVA_UPDATE=503;
    public static final int RESERVA_CANCEL=504;
    public static final int RESERVA_FINDALL=505;
    public static final int RESERVA_FIND_BY_FUNCIONARIO=506;

    // ESTADISTICAS
    public static final int ESTADISTICA_RECURSOS_POR_CATEGORIA=601;
    public static final int ESTADISTICA_ACTIVIDADES_POR_SEMANA=602;

    // TIPO DE CONEXION: cada Frontend abre dos sockets.
    // SYNC: peticion-respuesta (el Frontend pregunta y el Backend contesta).
    // ASYNC: solo el Backend escribe, para enviar notificaciones sin que se las pidan.
    public static final int SYNC=10;
    public static final int ASYNC=11;

    // NOTIFICACIONES (Backend -> Frontend por el socket ASYNC)
    public static final int DELIVER_RESERVA=701;

    public static final int ERROR_NO_ERROR=0;
    public static final int ERROR_ERROR=1;

    public static final int DISCONNECT=99;
}
