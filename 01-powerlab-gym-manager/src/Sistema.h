#pragma once
#include "Sucursal.h" // Daniel Castillo Jimenez- Programacion 1


class Sistema {
private:
    Sucursal** sucursales;
    int cantSuc;
    int capSuc;

	void menuSucursal(Sucursal* sucursal); 

public:
    Sistema(int capacidad);
    ~Sistema();

    bool agregarSucursal(Sucursal* s);
    string listarSucursales();
    Sucursal* buscarSucursal(string codigo);
    string toString();

	void menuCliente(Cliente* cliente, Sucursal* sucursal); 
	void ejecutarMenu(); 
};
