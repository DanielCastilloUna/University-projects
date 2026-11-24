#include "Sucursal.h" // Daniel Castillo Jimenez- Programacion 1
#include "Cliente.h"
#include "Instructor.h"
#include "ClaseGrupal.h"
#include "Rutina.h"


Sucursal::Sucursal(string cod, string prov, string can, string mail, string tel,
    int capClientes, int capInstructores, int capClases) {
    codigo = cod;
    provincia = prov;
    canton = can;
    correo = mail;
    telefono = tel;

    // Inicializar clientes
    this->capClientes = capClientes;
    cantClientes = 0;
    clientes = new Cliente * [capClientes];
    for (int i = 0; i < capClientes; i++) {
        clientes[i] = nullptr;
    }

    // Inicializar instructores
    this->capInstructores = capInstructores;
    cantInstructores = 0;
    instructores = new Instructor * [capInstructores];
    for (int i = 0; i < capInstructores; i++) {
        instructores[i] = nullptr;
    }

    // Inicializar clases
    this->capClases = capClases;
    cantClases = 0;
    clases = new ClaseGrupal * [capClases];
    for (int i = 0; i < capClases; i++) {
        clases[i] = nullptr;
    }
}

Sucursal::~Sucursal() { //Borrar todo lo que si le pertence a la sucursal
    // Clientes (recordar que la sucursal es dueña de ellos)
    for (int i = 0; i < cantClientes; i++) {
        delete clientes[i];
    }
    delete[] clientes;

    // Instructores (también propiedad de la sucursal)
    for (int i = 0; i < cantInstructores; i++) {
        delete instructores[i];
    }
    delete[] instructores;

    // Clases grupales (propiedad de la sucursal)
    for (int i = 0; i < cantClases; i++) {
        delete clases[i];
    }
    delete[] clases;
}

string Sucursal::getCodigo() { return codigo; }
string Sucursal::getProvincia() { return provincia; }
string Sucursal::getCanton() { return canton; }
string Sucursal::getCorreo() { return correo; }
string Sucursal::getTelefono() { return telefono; }


bool Sucursal::agregarCliente(Cliente* c) {
    if (cantClientes < capClientes) {
        clientes[cantClientes++] = c;
        return true;
    }
    return false;
}

string Sucursal::listarClientes() {
    stringstream s;
    s << "Clientes en sucursal " << codigo << ":" << endl;
    for (int i = 0; i < cantClientes; i++) {
        if (clientes[i] != nullptr) {
            s << "- " << clientes[i]->getNombre() << " (" << clientes[i]->getCedula() << ")" << endl;
        }
    }
    return s.str();
}

bool Sucursal::agregarInstructor(Instructor* i) {
    if (cantInstructores < capInstructores) {
        instructores[cantInstructores++] = i;
        return true;
    }
    return false;
}

string Sucursal::listarInstructores() {
    stringstream s;
    s << "Instructores en sucursal " << codigo << ":" << endl;
    for (int i = 0; i < cantInstructores; i++) {
        if (instructores[i] != nullptr) {
            s << "- " << instructores[i]->getNombre() << " (" << instructores[i]->getCedula() << ")" << endl;
        }
    }
    return s.str();
}

bool Sucursal::agregarClase(ClaseGrupal* c) {
    if (cantClases < capClases) {
        clases[cantClases++] = c;
        return true;
    }
    return false;
}

string Sucursal::listarClases() {
    stringstream s;
    s << "Clases en sucursal " << codigo << ":" << endl;
    for (int i = 0; i < cantClases; i++) {
        if (clases[i] != nullptr) {
            s << "- " << clases[i]->getNombreClase() << " (" << clases[i]->getCodigo() << ")" << endl;
        }
    }
    return s.str();
}

string Sucursal::toString() {
    stringstream s;
    s << "Sucursal: " << codigo << endl;
    s << "Provincia: " << provincia << " | Canton: " << canton << endl;
    s << "Correo: " << correo << " | Telefono: " << telefono << endl;
    s << "Clientes: " << cantClientes << "/" << capClientes << endl;
    s << "Instructores: " << cantInstructores << "/" << capInstructores << endl;
    s << "Clases: " << cantClases << "/" << capClases << endl;
    return s.str();
}

Cliente* Sucursal::buscarCliente(const string& cedula) {
    for (int i = 0; i < cantClientes; i++) {
        if (clientes[i] != nullptr && clientes[i]->getCedula() == cedula) {
            return clientes[i];
        }
    }
    return nullptr;
}

Instructor* Sucursal::buscarInstructor(const string& cedula) {
    for (int i = 0; i < cantInstructores; i++) {
        if (instructores[i] != nullptr && instructores[i]->getCedula() == cedula) {
            return instructores[i];
        }
    }
    return nullptr;
}

int Sucursal::getCantidadInstructores() const {
    return cantInstructores;
}
