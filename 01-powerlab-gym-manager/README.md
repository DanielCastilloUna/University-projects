# 🏋️ PowerLab — Gym Chain Management System

> **Course:** Programming I · Universidad Nacional de Costa Rica (UNA) · II Cycle 2025
> **Author:** Daniel Castillo Jiménez (individual project)
> **Stack:** C++17 · Visual Studio 2022 · CMake (added later)

A menu-driven console application to manage a chain of gyms. It handles **branches**, **clients**, **instructors**, **group classes**, **workout routines** and **body measurements**, and gives health recommendations based on each measurement.

This was my first large program: **17 source files and 8 classes**. It was where I learned to model a real business with classes and to manage memory by hand.

---

## Features

- **Branches (`Sucursal`):** each one has its own clients, instructors and group classes, with a configurable capacity.
- **Clients (`Cliente`):** personal data, an assigned instructor, a routine, measurement history and class enrollments.
- **Instructors (`Instructor`):** contact data and a list of specialties.
- **Group classes (`ClaseGrupal`):** room, schedule, instructor and enrolled clients, with a capacity limit.
- **Routines (`Rutina`):** exercises (`Ejercicio`) with sets, reps and target area. Exercises can be added or removed by code.
- **Body measurements (`Medicion`):** weight, height, body fat, muscle, visceral fat, metabolic age and body measurements. From these the program calculates:
  - **BMI**, classified into 8 categories (from severe thinness to morbid obesity)
  - **Recommended daily protein**, depending on sex and activity level
  - **Recommended glasses of water** per day

```
===== MENU SUCURSAL SJ01 =====
1. Agregar cliente
2. Listar clientes
3. Agregar instructor
...
IMC: 24.69 (Normal)
Proteina recomendada: 160 g/dia
```

## Concepts applied

| Concept | How |
|---|---|
| **Classes & encapsulation** | 8 classes with private state, getters/setters and `toString()` |
| **Associations between objects** | Branch → clients, instructors and classes; client → instructor, routine and measurements; class → instructor and clients |
| **Dynamic arrays of pointers** | `Cliente**`, `Medicion**`, `ClaseGrupal**`, `string**` with fixed capacity and a counter, created with `new[]` and freed in the destructors |
| **Manual memory management** | Each class decides what it owns and releases it in its destructor |
| **Forward declarations** | Break circular dependencies between headers (`class Cliente;`) |
| **Nested menus** | Main menu → branch menu → client sub-menu |

## Build & run

```bash
cmake -S . -B build
cmake --build build
./build/powerlab                            # Windows: build\powerlab.exe
```

It can also be opened in **Visual Studio** by adding the files in `src/` to an empty C++ console project.

## What this project taught me

- **Thinking in objects.** I took a real business (a gym chain) and turned it into classes and relationships. That was the jump from writing functions to designing a system.
- **Pointers and dynamic memory.** Arrays like `Cliente**` forced me to understand the difference between owning and referencing an object, and why every `new` needs exactly one `delete`.
- **Breaking a big program into files.** Separating headers from implementation, and using forward declarations to avoid circular includes.
- **Handling user input in the console:** mixing `cin >>` and `getline`, and the buffer problems that come with it.

## Fixes after the course

| Issue | Fix |
|---|---|
| `system("cls")` only works on Windows | Small helper that uses `cls` on Windows and `clear` on Linux/macOS |
| Typing a letter in a menu caused an **infinite loop** | `leerOpcion()` resets `cin` and asks again |
| `Medicion` was built with *muscle %* and *metabolic age* swapped (it only worked because setters overwrote the values afterwards) | Passed the constructor arguments in the right order and removed the redundant setters |

## Possible improvements

- Replace the fixed-capacity pointer arrays with `std::vector` and smart pointers.
- Save branches and clients to files so the data is kept between runs.
- Validate dates, emails and ID numbers.
