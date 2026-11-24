# Sistema de Reservas

EIF206 - Programación 3 (2026-II) - Proyecto #2: Sistema de Reserva de Recursos
Universidad Nacional - Escuela de Informática

## Integrantes del equipo
- Daniel Castillo Jiménez
- Daniela Valenciano Vargas
- Dana Pamela Rojas Lázaro

## Pasos realizados

### Paso 1: migrar de XML a base de datos (MySQL con patrón DAO)

La persistencia dejó de usar un archivo XML y pasó a una base de datos MySQL llamada `Reservas`. Cada entidad tiene su `Dao` en el paquete `data`, que es la única capa que escribe SQL (con `PreparedStatement`); la clase `Database` mantiene la conexión y lee `database.properties`. El `Service` sigue siendo el único punto de acceso desde la presentación y mantiene las mismas reglas de negocio del Proyecto #1, ahora apoyado en los DAO.

Tablas: `Administrador`, `Funcionario`, `CategoriaRecurso`, `Recurso`, `Reserva` y las tablas puente `ReservaCategoria` (categorías pedidas) y `ReservaRecurso` (recursos asignados).

Se usa `JOIN` en las consultas que reconstruyen objetos completos (Recurso con su Categoría, Reserva con su Funcionario, y los recursos/categorías de cada Reserva) y en las estadísticas (`JOIN` + `GROUP BY`).

Restricciones de integridad: no se puede eliminar un funcionario con reservas, una categoría con recursos, ni un recurso asignado a reservas.

### Paso 2: dividir el proyecto en módulos

El proyecto se dividió en tres módulos Maven, siguiendo la estructura del proyecto de referencia del curso ("Personas"):

- **Protocol**: lo que comparten Frontend y Backend (entidades y el contrato del servicio).
- **Backend**: capas de Datos y Lógica. No tiene interfaz de usuario.
- **Frontend**: capa de Presentación (MVC).

### Paso 3: comunicar Frontend y Backend por sockets (patrón Proxy)

El Frontend y el Backend ahora son dos programas independientes que se comunican por sockets. El Backend es el único que se conecta a MySQL; el Frontend no tiene ninguna referencia a la base de datos y ya no depende del módulo Backend (solo de Protocol).

    SistemaReservas
    ├── pom.xml                         -> pom agregador: compila los tres módulos en orden
    ├── reservas.sql                    -> script de la base de datos (MySQL)
    ├── Protocol                        -> lo compartido (sin dependencias)
    │   └── reservas/logic/
    │       ├── Administrador, CategoriaRecurso, EstadoReserva, Funcionario,
    │       │   Recurso, Reserva, ResultadoReserva, Rol, Usuario    entidades (Serializable)
    │       ├── IService.java           contrato del servicio: lo implementan el Service real y el proxy
    │       └── Protocol.java           servidor, puerto y codigo de cada operacion del socket
    ├── Backend                         -> capas de Datos y Lógica
    │   ├── reservas/Application.java      punto de entrada: arranca el servidor
    │   ├── reservas/data/                 Database y un DAO por entidad (JDBC)
    │   ├── reservas/logic/Service.java    Service real: usa los DAO
    │   ├── reservas/logic/Server.java     ServerSocket: acepta clientes y crea un Worker por cada uno
    │   ├── reservas/logic/Worker.java     hilo que atiende a un cliente y responde por el socket
    │   ├── src/test, src/it               pruebas unitarias (Surefire) y de integracion (Failsafe)
    │   └── src/main/resources/            database.properties.example (plantilla de conexion)
    └── Frontend                        -> capa de Presentación (MVC)
        ├── reservas/Application.java      punto de entrada (main)
        ├── reservas/logic/Service.java    PROXY: representa al Service del Backend y envia cada
        │                                  peticion por el socket (mismo nombre, asi la presentacion no cambia)
        ├── reservas/logic/IAService.java  reservas por lenguaje natural (usa una View, por eso vive aqui)
        └── reservas/presentation/         pantallas (View, Controller, Model)

Dependencias entre módulos: `Protocol` <- `Backend` y `Protocol` <- `Frontend`.

**Cómo funciona una petición:** el Controller llama a `Service.instance()` (el proxy) -> el proxy escribe en el socket el código de la operación y sus argumentos -> el `Worker` del Backend los lee, llama al `Service` real y responde con un código (`ERROR_NO_ERROR` o `ERROR_ERROR`) más el resultado, o el mensaje del error, que el Frontend le muestra al usuario.

**Varios Frontend:** el Backend crea un `Worker` (hilo) por cada Frontend conectado. Como todos comparten una sola conexión a MySQL, las operaciones del `Service` real son `synchronized` y se ejecutan de una en una.

### Paso 4: Alertas (notificaciones asincrónicas)

Cuando un usuario registra una reserva nueva, el Backend se la envía a **todos los demás** Frontend conectados, que la muestran en el **Área de Alertas** (panel a la derecha, fuera de las pestañas, siempre visible).

**Dos sockets por Frontend:**

- **SYNC**: petición-respuesta, como en el Paso 3. Al conectarse, el Frontend envía `Protocol.SYNC` y el `Server` crea su `Worker` y le responde con un id de sesión (`sid`). El proxy (`logic/Service`) guarda ese `sid` y lo expone con `getSid()`.
- **ASYNC**: el Frontend abre una segunda conexión, envía `Protocol.ASYNC` y su `sid`, y el `Server` la une a su `Worker`. Por este socket solo escribe el Backend.

**Flujo de una alerta:** el `Worker` atiende `RESERVA_CREATE` -> si la reserva se registró con éxito llama a `Server.deliver(reserva, this)` -> el `Server` recorre los demás `Worker` y cada uno escribe `DELIVER_RESERVA` + la reserva en su socket ASYNC -> en el Frontend, el hilo del `SocketMonitor` la lee y la pasa con `SwingUtilities.invokeLater` a `deliver_reserva` del `alertas.Controller` (que implementa `SocketListener`) -> el `Model` la agrega y la `View` se actualiza.

**Quién escucha en el Frontend:** el hilo que escucha vive en su propia clase, `presentation/SocketMonitor`, y no dentro del proxy (el proxy solo se ocupa del canal SYNC). `presentation/SocketListener` es la interfaz con `deliver_reserva(Reserva)`. El `alertas.Controller` abre el canal con `iniciar()` (crea y arranca el `SocketMonitor` usando el `sid` del proxy) y lo cierra con `stop()`. `Application` llama a `iniciar()` al abrir la ventana principal y a `stop()` al cerrarla. El monitor no se crea en el constructor del `Controller`, para poder probar el `Controller` sin Backend.

**En la pantalla:** el módulo `presentation/alertas` (View.form + View, Model, Controller y TableModel, como los demás) muestra una tabla donde cada fila tiene actividad, funcionario, fecha y horario, y dos íconos: ver el detalle en una ventana emergente y descartarla (pide confirmación). Descartar solo la quita de la lista local; no envía nada al Backend.

Pruebas: `Frontend/src/test/.../alertas/AlertasTest` (unitaria: agregar con `deliver_reserva`, ver, descartar, `stop()` sin haber iniciado) y `ServerIT.unaReservaNuevaDebeNotificarseALosDemasClientesPeroNoAlQueLaCreo` (integración: dos clientes por socket).

## Configuración de la base de datos

1. Tener MySQL instalado y corriendo.
2. Crear la base de datos y cargar los datos de ejemplo con el script de la raíz del proyecto:

        mysql -u root -p < reservas.sql

   (el script **borra y recrea** la base `Reservas`).
3. Copiar `Backend/src/main/resources/database.properties.example` a `Backend/src/main/resources/database.properties` y editar ese archivo con el usuario y la clave de MySQL de cada integrante:

        cp Backend/src/main/resources/database.properties.example Backend/src/main/resources/database.properties

   `database.properties` está en `.gitignore` (cada integrante tiene su propia clave local) y `database.properties.example` es la plantilla, que sí se versiona.

Usuarios de ejemplo cargados por el script (la clave es igual al id):

| Rol | ID |
|---|---|
| Administrador | `admin` |
| Funcionario | `111`, `222`, `333`, `444` |

Si no existe ningún administrador en la base de datos, el sistema crea `admin`/`admin` al arrancar.

## Cómo ejecutar

El Backend debe arrancar **primero**, y después el Frontend (puede haber varios Frontend conectados al mismo Backend). Con MySQL corriendo, la base `Reservas` cargada y `database.properties` configurado, en IntelliJ:

1. Abrir `Backend/src/main/java/reservas/Application.java` y ejecutar el `main` (flecha verde). La consola debe mostrar `Servidor iniciado en el puerto 1234...`.
2. Abrir `Frontend/src/main/java/reservas/Application.java` y ejecutar el `main`.

Si el Frontend no encuentra el Backend, muestra un mensaje de error y se cierra.

**En máquinas distintas:** el Backend queda escuchando en el puerto `1234` (`Protocol.PORT`). Para que el Frontend se conecte a otra máquina, ejecutarlo con la dirección del servidor:

    -Dreservas.server=IP_DEL_BACKEND

Para cambiar el puerto se usa `-Dreservas.port=XXXX` (en el Backend y en el Frontend).

Para importar el proyecto, abrir la carpeta raíz `SistemaReservas` y, si IntelliJ no detecta Maven, hacer clic derecho sobre el `pom.xml` de la raíz y elegir **Add as Maven Project**. Es necesario tener activada la opción **"Delegate IDE build/run actions to Maven"** en Settings > Build, Execution, Deployment > Build Tools > Maven > Runner, para evitar el error `ClassFormatError: Duplicate method $$$getFont$$$`.

## Cómo correr las pruebas

Las pruebas están en el módulo `Backend` y usan la base de datos, así que MySQL debe estar corriendo y con `reservas.sql` cargado. Desde la carpeta raíz:

    # Solo pruebas unitarias (Surefire, *Test.java)
    mvn test

    # Pruebas unitarias + de integración (Failsafe, *IT.java)
    mvn verify

Las pruebas de integración crean sus propios datos y los eliminan al terminar. `ServerIT` levanta el servidor en un puerto libre y le habla por el socket, así que no necesita que el Backend esté corriendo aparte.
