#pragma once // Daniel Castillo Jimenez- Programacion 1
#include "Ejercicio.h"
#include <sstream>
#include <iostream>
using namespace std;

class Rutina {
private:
    Ejercicio** ejercicios;  //Comenzar a usar contenido dinamico
    int cantidadEjercicios; 
    int capacidad;          

public:
    Rutina(int);
    ~Rutina();

    bool agregarEjercicio(const Ejercicio&); // Agrega un ejercicio (copia por referencia)
    bool eliminarEjercicio(int codigo);      // Elimina un ejercicio por su código
    string toString();                       
};


