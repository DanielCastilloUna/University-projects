#include "Cliente.h" // Daniel Castillo Jimenez- Programacion 1
#include "Rutina.h"
#include "Medicion.h"
#include "Instructor.h"
#include "ClaseGrupal.h"

#include <sstream> 


Cliente::Cliente(string ced, string nom, string t, string mail, char s, string fn, string fi, bool haceEj, int capMed, int capClases)
{
    nombre = nom;
    cedula = ced;
    telefono = t;
    correo = mail;
    sexo = s;
    fechaNacimiento = fn;
    fechaIngreso = fi;
    haceEjercicio = haceEj;

    cantClases = 0;
    cantMed = 0;

    this->capMed = capMed;
    this->capClases = capClases;

    mediciones = new Medicion * [capMed];
    for (int i = 0; i < capMed; i++) {
        mediciones[i] = nullptr;
    }

    clasesMatriculadas = new ClaseGrupal * [capClases];
    for (int i = 0; i < capClases; i++) {
        clasesMatriculadas[i] = nullptr;
    }

    rutina = nullptr;
    instructor = nullptr;
}

Cliente::~Cliente() {
    for (int i = 0; i < cantMed; i++) {
        delete mediciones[i];
    }
    delete[] mediciones;

    delete[] clasesMatriculadas;

    delete rutina;
    
}



string Cliente::getNombre() { return nombre; }
void Cliente::setNombre(string n) { nombre = n; }

Rutina* Cliente::getRutina() { return rutina; }

string Cliente::getCedula() { return cedula; }
void Cliente::setCedula(string c) { cedula = c; }

string Cliente::getTelefono() { return telefono; }
void Cliente::setTelefono(string t) { telefono = t; }

string Cliente::getMail() { return correo; }
void Cliente::setMail(string mail) { correo = mail; }

string Cliente::getFechaNacimiento() { return fechaNacimiento; }
void Cliente::setFechaNacimiento(string fn) { fechaNacimiento = fn; }

string Cliente::getFechaIngreso() { return fechaIngreso; }
void Cliente::setFechaIngreso(string fi) { fechaIngreso = fi; }


bool Cliente::agregarMedicion(Medicion* m) {
    if (cantMed < capMed) {
        mediciones[cantMed++] = m;
        return true;
    }
    return false;
}

string Cliente::mostrarHistorialMediciones() {
    stringstream r;
    for (int i = 0; i < cantMed; i++) {
        r << "Medicion #" << (i + 1) << endl;
        if (mediciones[i] != nullptr) {
            r << mediciones[i]->toString(sexo, haceEjercicio) << endl;
        }
    }
    return r.str();
}

Instructor* Cliente:: getInstructor() { return instructor; }

void Cliente::setRutina(Rutina* r) {
    if (rutina != nullptr) {
        delete rutina;
    }
    rutina = r;
}

void Cliente::asignarRutina(Rutina* r) {
    if (rutina != nullptr) {
        delete rutina;
    }
    rutina = r;
}

void Cliente::setInstructor(Instructor* i) { instructor = i; }
void Cliente::asignarInstructor(Instructor* i) { instructor = i; }
bool Cliente::eliminarInstructor(string cedulaInstructor) 
{
    if (instructor != nullptr && instructor->getCedula() == cedulaInstructor) {
        instructor = nullptr;
        return true;
    }
    return false;
}

string Cliente::verRutina() {
    if (rutina != nullptr) {
        return rutina->toString();
    }
    return "No tiene una rutina asignada.";
}


bool Cliente::matricularClase(ClaseGrupal* c) {
    if (cantClases < capClases) {
        clasesMatriculadas[cantClases++] = c;
        return true;
    }
    return false;
}


string Cliente::toString() {
    stringstream ss;
    ss << "===== DATOS DEL CLIENTE =====" << endl;
    ss << "Nombre: " << nombre << endl;
    ss << "Cedula: " << cedula << endl;
    ss << "Telefono: " << telefono << endl;
    ss << "Correo: " << correo << endl;
    ss << "Sexo: " << sexo << endl;
    ss << "Fecha de Nacimiento: " << fechaNacimiento << endl;
    ss << "Fecha de Ingreso: " << fechaIngreso << endl;
    ss << "Hace Ejercicio: " << (haceEjercicio ? "Si" : "No") << endl;
    ss << "===== INFO ADICIONAL =====" << endl;
    ss << "Cantidad de Mediciones: " << cantMed << endl;
    ss << "Cantidad de Clases Matriculadas: " << cantClases << endl;

    if (instructor != nullptr) {
        ss << "Instructor Asignado: " << instructor->getNombre() << endl;
    }
    else {
        ss << "Instructor Asignado: Ninguno" << endl;
    }

    if (rutina != nullptr) {
        ss << "Rutina Asignada: Si tiene rutina, corresponde a: "  << rutina->toString()<< endl;
    }
    else {
        ss << "Rutina Asignada: No" << endl;
    }
    return ss.str();
}



