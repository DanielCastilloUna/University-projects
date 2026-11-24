#include "Rutina.h" // Daniel Castillo Jimenez- Programacion 1


Rutina::Rutina(int cap) {
    capacidad = cap;
    cantidadEjercicios = 0;
    ejercicios = new Ejercicio * [capacidad]; 
    for (int i = 0; i < capacidad; i++) {
        ejercicios[i] = nullptr;
    }
}

Rutina::~Rutina() {
    for (int i = 0; i < cantidadEjercicios; i++) {
        delete ejercicios[i]; 
    }
    delete[] ejercicios; 
}

bool Rutina::agregarEjercicio(const Ejercicio& ejer) {
    if (cantidadEjercicios < capacidad) {
        ejercicios[cantidadEjercicios] = new Ejercicio(ejer); // copia
        cantidadEjercicios++;
        return true;
    }
    cout << "Capacidad maxima alcanzada. No se puede agregar mas ejercicios." << endl;
    return false;
}

bool Rutina::eliminarEjercicio(int codigo) {
    for (int i = 0; i < cantidadEjercicios; i++) {
        if (ejercicios[i]->getCodigo() == codigo) {
            delete ejercicios[i]; 

            // Corrimiento a la izquierda desde i
            for (int j = i; j < cantidadEjercicios - 1; j++) {
                ejercicios[j] = ejercicios[j + 1];
            }

            ejercicios[cantidadEjercicios - 1] = nullptr; // limpiar último
            cantidadEjercicios--; // actualizar cantidad
            return true;
        }
    }
    cout << "Ejercicio no encontrado: " << codigo << endl;
    return false;
}

string Rutina::toString() {
    stringstream s;
    s << "Rutina de ejercicios:" << endl;
    for (int i = 0; i < cantidadEjercicios; i++) {
        if (ejercicios[i] != nullptr) {
            s << ejercicios[i]->toString() << endl;
        }
    }
    return s.str();
}


