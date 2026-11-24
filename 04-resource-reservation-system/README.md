# 🗓️ Resource Reservation System — Java Client-Server with MySQL

> **Course:** EIF206 Programming III · Universidad Nacional de Costa Rica (UNA) · II Cycle 2026
> **Team:** Daniel Castillo Jiménez, Daniela Valenciano Vargas & Dana Pamela Rojas Lázaro
> **Stack:** Java 17 · Maven (multi-module) · MySQL · JDBC · TCP sockets · Swing (MVC) · JUnit 5 · iText · JFreeChart · Groq LLM API

A desktop system for booking shared resources in an organization: meeting rooms, audiovisual equipment, vehicles and laptops. An employee asks for the **categories** of resources they need for an activity, and the server automatically assigns a **free resource of each category** for that date and time slot, or explains which categories are not available.

The application is split into **two independent programs** that talk over **TCP sockets**: a backend server that is the only part connected to the database, and a Swing desktop client. Several clients can be connected at once, and when one of them books something, **all the others get a real-time alert**.

```
 ┌──────────── Frontend (Swing, MVC) ─────────────┐                ┌──────────────── Backend ────────────────┐
 │                                                │   SYNC socket  │                                         │
 │  View ─► Controller ─► Service (proxy) ────────┼──── request ──►│  Server ─► Worker (1 thread per client) │
 │                          implements IService ◄─┼─── response ───│              │                          │
 │                                                │                │              ▼                          │
 │  Alerts panel ◄─ SocketMonitor (own thread) ◄──┼── ASYNC socket │  Service (real, synchronized)           │
 │                                                │   (push only)  │              │                          │
 └────────────────────────────────────────────────┘                │              ▼                          │
                         ▲                                          │  DAO layer (JDBC, PreparedStatement)    │
                         │                                          │              │                          │
            Protocol module: entities + IService                    │              ▼                          │
            + operation codes, shared by both sides                 │           MySQL                         │
                                                                    └─────────────────────────────────────────┘
```

---

## Features

- **Two roles with login:** administrators manage employees, resource categories and resources; employees create, edit and cancel their own reservations.
- **Automatic resource assignment:** for each requested category the server looks for a resource that is not used by an active reservation overlapping that time slot. A resource is never assigned twice within the same booking.
- **Real-time alerts:** a new reservation is pushed to every other connected client and shown in an alerts panel that is always visible.
- **Natural-language booking:** the user can type a sentence in Spanish, such as *"reunión mañana de 9 a 11 con proyector"* ("meeting tomorrow from 9 to 11 with a projector"). The client sends it to an LLM (Groq API) to extract the activity, date, times and categories. If there is no API key, no internet or the API fails, it **falls back to a rule-based parser with regular expressions**.
- **Calendar view** of reservations, and **PDF reports** exported with iText from the calendar, the lists and the statistics screens.
- **Statistics with charts** (JFreeChart): resources used per category and activities per week, calculated in SQL with `JOIN` + `GROUP BY`.
- **Integrity rules:** an employee with reservations, a category with resources or a resource assigned to reservations cannot be deleted.

## Architecture

The project is a **Maven multi-module build** with three modules:

| Module | Contents | Depends on |
|---|---|---|
| `Protocol` | Shared entities (`Serializable`), the `IService` contract and the socket operation codes | — |
| `Backend` | Data layer (`Database` + one DAO per entity) and logic layer (`Service`, `Server`, `Worker`). No UI | `Protocol`, MySQL Connector/J |
| `Frontend` | Presentation layer: one MVC package per screen (`View`, `Controller`, `Model`, `TableModel`) and the service proxy | `Protocol` only |

Key design decisions:

- **Proxy pattern:** the client has its own `Service` class that implements the same `IService` interface as the real one, but sends every call through the socket. The controllers never know whether they are talking to a local object or to a server.
- **DAO pattern:** only the `data` package writes SQL, always with `PreparedStatement`. The `Service` keeps the business rules.
- **Two sockets per client:** a synchronous one for request/response, and an asynchronous one where only the server writes, used for alerts. They are linked by a session id (UUID) issued by the server.
- **Concurrency:** the server creates one `Worker` thread per client. All of them share one database connection, so the methods of the real `Service` are `synchronized`. On the client, alerts arrive on a background thread and are handed to Swing with `SwingUtilities.invokeLater`.
- **Database:** 7 tables, including two many-to-many link tables (`ReservaCategoria` for the categories requested and `ReservaRecurso` for the resources assigned).

## What this project taught me

This was my first project with a **real database**, **networking** and **more than one program running at the same time**. It changed how I think about software:

1. **Separating a system into layers and modules.** Moving from one program to three Maven modules forced us to decide what is shared (`Protocol`), what is server-only and what is client-only. The compiler started enforcing the architecture: the frontend simply cannot import the database code.
2. **SQL and the DAO pattern.** We migrated the persistence of the previous project from an XML file to MySQL. I learned to design tables with primary keys and many-to-many relations, rebuild full objects with `JOIN`s, calculate statistics with `GROUP BY`, and protect against SQL injection with `PreparedStatement`.
3. **Client-server communication with sockets.** I learned how to design a small protocol (an operation code, then the arguments, then a status code and the result or an error message), serialize objects over the network, and send server errors back to the client so the user sees the real reason.
4. **Design patterns with a real purpose.** The Proxy pattern let us turn a local app into a networked one without rewriting the screens, and MVC kept each screen's logic testable without a GUI.
5. **Threads and concurrency.** One thread per client, a separate listener thread for push notifications, `synchronized` sections around shared resources, and the rule that Swing must only be updated from its own thread.
6. **Integrating an AI API safely.** The API key is read from an environment variable (`LLM_API_KEY`) and never written in the code, and the feature degrades gracefully to a regex parser when the API is not available.
7. **Testing beyond unit tests.** Unit tests run with Surefire, and integration tests run with Failsafe against a real database. One integration test starts the server on a free port, connects two clients and checks that a new reservation notifies the other client but not the one that created it.
8. **Working in a team of three with Git**, keeping secrets out of the repository: each member had their own local `database.properties`, which is ignored by Git, and only a template is committed.

## Project structure

```
04-resource-reservation-system/
├── pom.xml                  Aggregator POM: builds Protocol → Backend → Frontend
├── reservas.sql             Database script (drops and recreates the "Reservas" database, with sample data)
├── Protocol/                Shared entities, IService, Protocol (port and operation codes)
├── Backend/
│   ├── src/main/java/reservas/data/    Database + DAOs (JDBC)
│   ├── src/main/java/reservas/logic/   Service, Server, Worker
│   ├── src/main/resources/             database.properties.example
│   ├── src/test/                       Unit tests (JUnit 5, Surefire)
│   └── src/it/                         Integration tests (Failsafe)
└── Frontend/
    ├── src/main/java/reservas/logic/          Service proxy, IAService (natural language)
    └── src/main/java/reservas/presentation/   login, reserva, calendarizacion, actividad,
                                               funcionarios, categoriarecurso, recurso,
                                               estadistica, alertas (MVC per screen)
```

## How to run it

**Requirements:** JDK 17+, Maven 3.8+, MySQL 8, and IntelliJ IDEA (the screens use IntelliJ GUI Designer `.form` files).

1. Create the database with the sample data. **Careful:** the script drops and recreates the `Reservas` database.
   ```bash
   mysql -u root -p < reservas.sql
   ```
2. Create your local connection file from the template and put your own MySQL user and password in it. This file is ignored by Git.
   ```bash
   cp Backend/src/main/resources/database.properties.example Backend/src/main/resources/database.properties
   ```
3. Open the folder in IntelliJ as a Maven project and enable **Settings → Build Tools → Maven → Runner → "Delegate IDE build/run actions to Maven"**.
4. Run `Backend/.../reservas/Application.java` first. It should print `Servidor iniciado en el puerto 1234...`.
5. Run `Frontend/.../reservas/Application.java`. You can start several frontends to see the alerts.
   - To connect to a backend on another machine: `-Dreservas.server=<backend-ip>`
   - To change the port (on both sides): `-Dreservas.port=<port>`
6. *(Optional)* To use the LLM for natural-language booking, set the environment variable `LLM_API_KEY` with a Groq API key. Without it, the rule-based parser is used.

Sample users (the password is the same as the id): administrator `admin`, employees `111`, `222`, `333`, `444`.

**Tests** (MySQL running with `reservas.sql` loaded):
```bash
mvn test      # unit tests
mvn verify    # unit + integration tests
```

## Known limitations

These are the things I would improve next. I am writing them down because knowing a system's weak points is part of understanding it:

- **Passwords are stored in plain text.** A production version should store salted hashes (for example, bcrypt).
- **A single shared database connection,** serialized with `synchronized`. A connection pool (such as HikariCP) and database transactions would allow real parallel requests.
- **Java serialization over raw sockets** works for a course project, but it is not encrypted or versioned. A real system would use TLS and a format such as JSON over HTTP, or gRPC.
- **The GUI depends on IntelliJ's form designer,** so building the client outside IntelliJ needs extra setup.

---

*The source code, the user interface and the original course README ([`README.es.md`](README.es.md)) are in Spanish, the language of the course. This documentation is in English.*
