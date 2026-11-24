#pragma once
#include <iostream> // Daniel Castillo Jimenez- Programacion 1
#include <sstream>
#include <string>
using namespace std;

class Cliente;
class Instructor;
class ClaseGrupal;

class Sucursal {
private:
    string codigo;
    string provincia;
    string canton;
    string correo;
    string telefono;

    Cliente** clientes;
    int cantClientes;
    int capClientes;

    Instructor** instructores;
    int cantInstructores;
    int capInstructores;

    ClaseGrupal** clases;
    int cantClases;
    int capClases;

public:
    Sucursal(string cod, string prov, string can, string mail, string tel,
        int capClientes, int capInstructores, int capClases);
    ~Sucursal();

    string getCodigo();
    string getProvincia();
    string getCanton();
    string getCorreo();
    string getTelefono();

    bool agregarCliente(Cliente* c);
    string listarClientes();

    bool agregarInstructor(Instructor* i);
    string listarInstructores();

    bool agregarClase(ClaseGrupal* c);
    string listarClases();

    string toString();
    Cliente* buscarCliente(const string& cedula);
    Instructor* buscarInstructor(const string& cedula);
    int getCantidadInstructores() const;
};


