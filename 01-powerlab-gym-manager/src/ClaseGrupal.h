#pragma once // Daniel Castillo Jimenez- Programacion 1
#include <iostream>
#include <sstream>
#include <string>
using namespace std;


class Cliente;
class Instructor;

class ClaseGrupal {
private:
    string codigo;
    string nombreClase;    
    string salon;
    string horario;

    Instructor* instructor;   
    Cliente** clientes;       
    int cantClientes;         
    int capClientes;       

public:
    ClaseGrupal(string cod, string nom, string sal, string hor, int capacidad);
    ~ClaseGrupal();

    string getCodigo();
    string getNombreClase();
    string getSalon();
    string getHorario();

    void setInstructor(Instructor* i);
    bool matricularCliente(Cliente* c);
    string listarClientes();

    string toString();
};


