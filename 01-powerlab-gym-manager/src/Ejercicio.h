#pragma once // Daniel Castillo Jimenez- Programacion 1
#include <string>
#include <sstream>
using namespace std;

class Ejercicio {
private:
    int codigo;
    string ejercicio;
    string area;
    string descripcion;
    int series;
    int repeticiones;

public:
    
    Ejercicio(int, string, string, string, int, int);

    int getCodigo();
    void setCodigo(int);

    string getEjercicio();
    void setEjercicio(string);

    string getArea();
    void setArea(string);

    string getDescripcion();
    void setDescripcion(string);

    int getSeries();
    void setSeries(int);

    int getRepeticiones();
    void setRepeticiones(int);

    string toString();
};


