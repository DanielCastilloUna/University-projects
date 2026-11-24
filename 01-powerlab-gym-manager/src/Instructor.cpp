#include "Instructor.h" // Daniel Castillo Jimenez- Programacion 1


Instructor::Instructor(string c, string n, string t, string mail, string fn, int capEsp) {
    cedula = c;
    nombre = n;
    telefono = t;
    correo = mail;
    fechaNacimiento = fn;

    this->capEsp = capEsp;
    cantEsp = 0;

    especialidades = new string * [capEsp];
    for (int i = 0; i < capEsp; i++) {
        especialidades[i] = nullptr;
    }
}

Instructor::~Instructor() {
    for (int i = 0; i < cantEsp; i++) {
        delete especialidades[i]; 
    }
    delete[] especialidades; 
}

string Instructor::getCedula() { return cedula; }

string Instructor::getNombre() { return nombre; }
void Instructor::setNombre(string n) { nombre = n; }

string Instructor::getTelefono() { return telefono; }
void Instructor::setTelefono(string t) { telefono = t; }

string Instructor::getCorreo() { return correo; }
void Instructor::setCorreo(string mail) { correo = mail; }

string Instructor::getFechaNacimiento() { return fechaNacimiento; }


bool Instructor::agregarEspecialidad(string esp) {
    if (cantEsp < capEsp) {
        especialidades[cantEsp] = new string(esp);
        cantEsp++;
        return true;
    }
    return false;
}

string Instructor::listarEspecialidades() {
    stringstream s;
    for (int i = 0; i < cantEsp; i++) {
        if (especialidades[i] != nullptr) {
            s << "- " << *especialidades[i] << endl;
        }
    }
    return s.str();
}

string Instructor::toString() {
    stringstream s;
    s << "Instructor: " << nombre << " (" << cedula << ")" << endl;
    s << "Telefono: " << telefono << " | Correo: " << correo << endl;
    s << "Fecha Nacimiento: " << fechaNacimiento << endl;
    s << "Especialidades: " << endl << listarEspecialidades();
    return s.str();
}
