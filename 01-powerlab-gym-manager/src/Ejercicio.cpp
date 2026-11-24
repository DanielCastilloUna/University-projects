#include "Ejercicio.h" // Daniel Castillo Jimenez- Programacion 1
#include <sstream>  


Ejercicio::Ejercicio(int cod, string ejer, string are, string desc, int ser, int rep) {
    codigo = cod;
    ejercicio = ejer;
    area = are;
    descripcion = desc;
    series = ser;
    repeticiones = rep;
}

int Ejercicio::getCodigo() { return codigo; }
void Ejercicio::setCodigo(int cod) { codigo = cod; }

string Ejercicio::getEjercicio() { return ejercicio; }
void Ejercicio::setEjercicio(string ejer) { ejercicio = ejer; }

string Ejercicio::getArea() { return area; }
void Ejercicio::setArea(string are) { area = are; }

string Ejercicio::getDescripcion() { return descripcion; }
void Ejercicio::setDescripcion(string desc) { descripcion = desc; }

int Ejercicio::getSeries() { return series; }
void Ejercicio::setSeries(int ser) { series = ser; }

int Ejercicio::getRepeticiones() { return repeticiones; }
void Ejercicio::setRepeticiones(int rep) { repeticiones = rep; }

string Ejercicio::toString() {
    stringstream ss;
    ss << "Codigo: " << codigo << endl;
    ss << "Ejercicio: " << ejercicio << endl;
    ss << "Area: " << area << endl;
    ss << "Descripcion: " << descripcion << endl;
    ss << "Series: " << series << endl;
    ss << "Repeticiones: " << repeticiones << endl;
    return ss.str();
}

