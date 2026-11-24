# 🛠️ Maintenance Planning System — 30-Day Simulation

> **Course:** EIF204 Programming II · Universidad Nacional de Costa Rica (UNA) · I Cycle 2026
> **Team:** Daniel Castillo Jiménez & [Saúl Sáenz Vargas](https://github.com/SaulSaenz483)
> **Stack:** C++20 · CMake · STL · CLion

A console simulation of an industrial maintenance department. It loads an inventory of **100 machines**, simulates **30 days** of wear and random incidents, ranks the machines by priority every day and sends the **top 3** to maintenance. It writes one report file per day.

The focus was on **design patterns**, **polymorphism**, and **sorting and searching algorithms implemented from scratch** (no `std::sort` or `std::find`).

```
--- Dia 1 ---
  [Incidencias generadas hoy: 10 | Total historial: 10]
    -> Aplicado Mantenimiento PREVENTIVO a EQ-006
->Calibrando motor...
Top prioridad : EQ-006 (5) , EQ-003 (5) , EQ-009 (5)
Asignados : EQ-006 , EQ-003 , EQ-009
Backlog pendiente : 7
Riesgo global : MEDIO
```

> The code and the console output are in Spanish because that was the course language. This README explains everything in English.

---

## How the simulation works

Each day runs the same pipeline (`Sistema::procesarDia`):

1. **Degrade:** every machine loses condition. *Critical* machines degrade 2.5× faster than *standard* ones (polymorphic `degradar()`).
2. **Incidents:** new incidents (minor, medium or severe) are generated at random and spread out so there are exactly **300** by day 30.
3. **Prioritize:** machines are sorted with a **custom QuickSort** by
   `priority = 0.5·criticality + 0.3·incident weight + 0.2·idle days`
4. **Maintain:** the top 3 receive maintenance, and the **strategy** is picked at runtime:

   | Condition | Strategy | Effect |
   |---|---|---|
   | condition < 30% or > 5 incidents | Emergency | Restore to 100% |
   | condition < 60% | Corrective | +30% |
   | otherwise | Preventive | +15% |

5. **Report:** the program writes `reporte_dia_XX.txt` with the top priorities, the backlog and the global risk level.

## Concepts applied

| Concept | Where |
|---|---|
| **Strategy pattern** | `EstrategiaMantenimiento` interface with `MantenimientoPreventivo`, `Correctivo` and `Emergencia`. `Equipo::aplicarMantenimiento()` delegates to the current strategy |
| **Inheritance & polymorphism** | Abstract `Equipo` with pure virtual `degradar()`, implemented by `EquipoCritico` and `EquipoEstandar` |
| **Safe downcasting** | `dynamic_cast<EquipoCritico*>` calls `calibrarMotor()` only on critical machines |
| **Factory methods** | `fabricarEquipo()` and `fabricarIncidencia()` build the right subclass from each text line |
| **Algorithms implemented from scratch** | QuickSort by priority, binary search by ID (to link incidents to machines at load time) and bubble sort by ID |
| **Custom exception hierarchy** | `SistemaException` → `ArchivoInvalido`, `FormatoInvalido` and `OperacionInconsistente` |
| **Random generation** | `std::mt19937` with `uniform_int_distribution` |
| **File I/O** | Data loaded from `datos.txt` and one report file written per day |

## Build & run

```bash
cmake -S . -B build
cmake --build build
cd build && ./maintenance_planner           # Windows: maintenance_planner.exe
```

CMake copies `data/datos.txt` next to the executable. The 30 reports are written to the folder you run the program from.

**Input format** (`datos.txt`):
```
EQ-001;criticidad=8;estado=100
INC;EQ-001;severidad=GRAVE;dia=3      # optional pre-scheduled incident
```

## What this project taught me

- **Design patterns solve real problems.** Before Strategy, the maintenance logic was a growing `if/else` chain. Afterwards, adding a new type of maintenance meant adding one class and changing nothing else (*Open/Closed principle*).
- **Algorithms matter more when you write them yourself.** Implementing QuickSort and binary search by hand made me understand *why* binary search needs sorted data, and why we sort by ID once at load time but by priority every day.
- **Exceptions as part of the design:** a small hierarchy of exception types makes errors meaningful and easy to catch at the right level.
- **Team workflow with Git:** small commits, pulling and merging my teammate's changes, and keeping the build green.

## Fixes after the course

When I put this portfolio together, the local copy **did not compile**. I fixed:

| Issue | Fix |
|---|---|
| A stray character after a class in `Excepciones.h` broke the build | Removed it |
| Renaming `TipoIncidencia::ALTA` to `GRAVE` had only been done in some files | Finished the rename in `Incidencia.h/.cpp` |
| Every machine kept a pointer to a strategy object that lived on the stack and was destroyed when the function returned (dangling pointer) | Made the strategy objects `static` |
| `datos.txt` said "15 critical, 35 medium, 50 standard", but all 100 machines had `criticidad=1`, so `EquipoCritico` was never used | Regenerated the file with the documented distribution |

The program now builds with no warnings and runs clean under AddressSanitizer.

## Possible improvements

- Use `std::unique_ptr` instead of raw pointers.
- Replace the load-time bubble sort with the existing QuickSort (or `std::sort`).
- Make the number of days and the daily maintenance capacity configurable.
