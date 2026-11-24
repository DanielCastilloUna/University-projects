# 🎓 University Projects — C++ & Object-Oriented Design

**Daniel Castillo Jiménez** · Information Systems Engineering student at the **Universidad Nacional de Costa Rica (UNA)**

This repository collects my main course projects from **Programming I** and **Programming II**. They are console applications, so there is no GUI. Their purpose was to master the fundamentals: **logic, syntax, object-oriented design, design patterns, memory management, algorithms and software architecture**.

Each project has its own README explaining what it does, how it is built and, most importantly, **what it taught me**.

---

## Projects

| # | Project | Course | What it shows |
|---|---|---|---|
| 01 | [**PowerLab — Gym Chain Manager**](01-powerlab-gym-manager) | Programming I · 2025 | Classes, encapsulation, object associations, dynamic arrays of pointers, manual memory management |
| 02 | [**Maintenance Planning System**](02-maintenance-planning-system) | Programming II · 2026 | **Strategy pattern**, inheritance & polymorphism, hand-written **QuickSort + binary search**, custom exceptions, 30-day simulation |
| 03 | [**Adventures & Dungeons**](03-adventures-and-dungeons) | Programming II · 2026 | **Layered architecture**, abstract classes, explicit ownership, data-driven world loaded from files, turn-based combat, logging & reports |

## Learning path

```
Programming I                          Programming II
─────────────                          ──────────────
01 PowerLab                    ──►     02 Maintenance Planner          ──►     03 Adventures & Dungeons
 • Model a business with classes        • Abstract classes & polymorphism        • Layers: domain / simulation / output
 • Pointers & new/delete                • Strategy design pattern                • UML designed before coding
 • Multi-file programs                  • Own sorting & searching algorithms     • Ownership rules, non-copyable types
 • Console menus & input                • Exception hierarchy, random sim        • File parsing, logging, reports
```

Each project builds on the previous one: first **objects**, then **behaviour that varies through polymorphism and patterns**, and finally **a whole system organized into layers**.

## Skills demonstrated

- **Languages & tools:** C++17/20, STL (`vector`, `deque`, `fstream`, `sstream`, `<random>`), CMake, Visual Studio, CLion, Git/GitHub
- **Object-oriented design:** encapsulation, inheritance, polymorphism, abstract classes, `dynamic_cast`, forward declarations
- **Design patterns:** Strategy, Factory Method
- **Memory:** raw pointers, ownership transfer, destructors, deleted copy operations, debugging with AddressSanitizer
- **Algorithms:** QuickSort, binary search, bubble sort
- **Software engineering:** layered architecture, UML, input validation, exception handling, file I/O, teamwork with Git

## Build everything

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

## Reviewed after the course

When I put this portfolio together, I went back to each project, compiled it on Linux with **AddressSanitizer** and fixed the bugs I found. Among them were a double `delete` that crashed one game on exit, a build that was broken by an unfinished rename, and a menu that looped forever on invalid input.

The Git history keeps the **original submission as the first commit**, followed by one commit per fix. Each project's README has a **"Fixes after the course"** section explaining the cause and the solution, because learning to find and fix my own mistakes is part of what these projects taught me.

## Acknowledgements

Projects 02 and 03 were built together with my teammate **[Saúl Sáenz Vargas](https://github.com/SaulSaenz483)**. The original team repositories are on his GitHub.

---

*The source code and console output are in Spanish, the language of the courses. All documentation is in English.*
