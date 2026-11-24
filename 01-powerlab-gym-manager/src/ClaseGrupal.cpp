#include "ClaseGrupal.h" // Daniel Castillo Jimenez- Programacion 1
#include "Cliente.h"
#include "Instructor.h"


ClaseGrupal::ClaseGrupal(string cod, string nom, string sal, string hor, int capacidad) {
    codigo = cod;
    nombreClase = nom;
    salon = sal;
    horario = hor;
    capClientes = capacidad;
    cantClientes = 0;
    instructor = nullptr;

    
    clientes = new Cliente * [capClientes];
    for (int i = 0; i < capClientes; i++) {
        clientes[i] = nullptr;
    }
}

ClaseGrupal::~ClaseGrupal() {

    delete[] clientes; //No eliminar lo que pertence a la Sucursal
}

string ClaseGrupal::getCodigo() { return codigo; }
string ClaseGrupal::getNombreClase() { return nombreClase; }
string ClaseGrupal::getSalon() { return salon; }
string ClaseGrupal::getHorario() { return horario; }

void ClaseGrupal::setInstructor(Instructor* i) {
    instructor = i;
}


bool ClaseGrupal::matricularCliente(Cliente* c) {
    if (cantClientes < capClientes) {
        clientes[cantClientes++] = c;
        return true;
    }
    return false;
}

string ClaseGrupal::listarClientes() {
    stringstream s;
    s << "Clientes matriculados en " << nombreClase << " (" << codigo << "):" << endl;
    for (int i = 0; i < cantClientes; i++) {
        if (clientes[i] != nullptr) {
            s << "- " << clientes[i]->getNombre() << " (" << clientes[i]->getCedula() << ")" << endl;
        }
    }
    return s.str();
}


string ClaseGrupal::toString() {
    stringstream s;
    s << "Clase Grupal: " << nombreClase << " (" << codigo << ")" << endl;
    s << "Salon: " << salon << " | Horario: " << horario << endl;
    s << "Capacidad: " << capClientes << " | Matriculados: " << cantClientes << endl;
    if (instructor != nullptr) {
        s << "Instructor asignado: " << instructor->getNombre() << endl;
    }
    else {
        s << "Instructor asignado: [NINGUNO]" << endl;
    }
    return s.str();
}

