#include "Sistema.h" // Daniel Castillo Jimenez- Programacion 1
#include <iostream>
using namespace std;

int main() {
    // Creamos el sistema con capacidad que queramos para las sucursales
    Sistema* sistema = new Sistema(5);

    // Ejecutamos el menú principal
    sistema->ejecutarMenu();

    // Liberar memoria antes de salir
    delete sistema;

    return 0;
}






		
