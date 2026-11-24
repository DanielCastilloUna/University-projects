# 🎓 University Projects — C++, Java & Software Design

**Daniel Castillo Jiménez** · Information Systems Engineering student at the **Universidad Nacional de Costa Rica (UNA)**

This repository collects my main course projects from **Programming I, II and III**. The first three are C++ console applications built to master the fundamentals: **logic, object-oriented design, design patterns, memory management, algorithms and software architecture**. The fourth is a **Java client-server desktop system with a MySQL database**, where those fundamentals are applied to networking, concurrency and persistence.

Each project has its own README explaining what it does, how it is built and, most importantly, **what it taught me**.

---

## Projects

| # | Project | Course | What it shows |
|---|---|---|---|
| 01 | [**PowerLab — Gym Chain Manager**](01-powerlab-gym-manager) | Programming I · 2025 | Classes, encapsulation, object associations, dynamic arrays of pointers, manual memory management |
| 02 | [**Maintenance Planning System**](02-maintenance-planning-system) | Programming II · 2026 | **Strategy pattern**, inheritance & polymorphism, **QuickSort + binary search implemented from scratch**, custom exceptions, 30-day simulation |
| 03 | [**Adventures & Dungeons**](03-adventures-and-dungeons) | Programming II · 2026 | **Layered architecture**, abstract classes, explicit ownership, data-driven world loaded from files, turn-based combat, logging & reports |
| 04 | [**Resource Reservation System**](04-resource-reservation-system) | Programming III · 2026 | **Java client-server over TCP sockets**, **MySQL + DAO/JDBC**, Proxy & MVC patterns, multithreading, real-time push alerts, LLM integration, JUnit integration tests |

## Learning path

```
Programming I                     Programming II                                                Programming III
─────────────                     ──────────────                                                ───────────────
01 PowerLab               ──►     02 Maintenance Planner     ──►  03 Adventures & Dungeons  ──►  04 Reservation System
 • Model a business                • Abstract classes &             • Layers: domain /              • 3 Maven modules
   with classes                      polymorphism                     simulation / output           • MySQL, SQL, DAO
 • Pointers & new/delete           • Strategy pattern               • UML before coding             • Sockets, Proxy, MVC
 • Multi-file programs             • Own sort & search              • Ownership rules               • Threads, push alerts
 • Console menus & input           • Exceptions, random sim         • Files, logging, reports       • Unit + integration tests
```

Each project builds on the previous one: first **objects**, then **behaviour that varies through polymorphism and patterns**, then **a whole system organized into layers**, and finally **a distributed system with a database, where the layers run as separate programs**.

## Skills demonstrated

- **Languages & tools:** C++17/20, STL (`vector`, `deque`, `fstream`, `sstream`, `<random>`), Java 17, Maven, CMake, IntelliJ IDEA, Visual Studio, CLion, Git/GitHub
- **Databases:** MySQL, SQL (`JOIN`, `GROUP BY`, many-to-many relations, integrity rules), JDBC with `PreparedStatement`
- **Networking & concurrency:** TCP sockets, object serialization, a custom request/response protocol, one thread per client, `synchronized`, Swing threading
- **GUI & APIs:** Java Swing (MVC), iText (PDF), JFreeChart, LLM REST API (Groq) with a regex fallback
- **Object-oriented design:** encapsulation, inheritance, polymorphism, abstract classes, `dynamic_cast`, forward declarations
- **Design patterns:** Strategy, Factory Method, Proxy, DAO, MVC, Singleton
- **Memory:** raw pointers, ownership transfer, destructors, deleted copy operations, debugging with AddressSanitizer
- **Algorithms:** QuickSort, binary search, bubble sort
- **Software engineering:** layered and multi-module architecture, UML, input validation, exception handling, file I/O, unit & integration testing (JUnit 5), secrets kept out of Git, teamwork with Git

## Build the C++ projects (01–03)

Requirements: a C++20 compiler (GCC 10+, Clang 12+ or MSVC 2019+) and CMake ≥ 3.16.

```bash
git clone <this-repo-url>
cd university-projects
cmake -S . -B build
cmake --build build
```

Executables:

| Project | Run from | Executable |
|---|---|---|
| PowerLab | `build/01-powerlab-gym-manager/` | `powerlab` |
| Maintenance Planner | `build/02-maintenance-planning-system/` | `maintenance_planner` |
| Adventures & Dungeons | `build/03-adventures-and-dungeons/` | `adventures_and_dungeons` |

> Projects 02 and 03 read their data files from the folder they run in, so `cd` into that build folder first.

## Run the Java project (04)

Project 04 needs JDK 17+, Maven, MySQL 8 and IntelliJ IDEA. Follow the steps in [its README](04-resource-reservation-system#how-to-run-it): load `reservas.sql`, create your local `database.properties` from the template, start the backend and then one or more frontends.

## Reviewed after the course

When I put this portfolio together, I went back to each C++ project, compiled it on Linux with **AddressSanitizer** and fixed the bugs I found. Among them were a double `delete` that crashed one game on exit, a build that was broken by an unfinished rename, and a menu that looped forever on invalid input.

The Git history keeps the **original submission as the first commit**, followed by one commit per fix. Each project's README has a **"Fixes after the course"** section explaining the cause and the solution, because learning to find and fix my own mistakes is part of what these projects taught me.

## Acknowledgements

Projects 02 and 03 were built together with my teammate **[Saúl Sáenz Vargas](https://github.com/SaulSaenz483)**. The original team repositories are on his GitHub.

Project 04 was built together with **Daniela Valenciano Vargas** and **Dana Pamela Rojas Lázaro**. Its README lists the limitations I would improve next.

---

*The source code and console output are in Spanish, the language of the courses. All documentation is in English.*
