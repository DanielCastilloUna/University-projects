#pragma once  // Daniel Castillo Jimenez- Programacion 1
#include <iostream>
#include <sstream>
using namespace std;

class Instructor {
private:
    string cedula;
    string nombre;
    string telefono;
    string correo;
    string fechaNacimiento;

    string** especialidades; 
    int cantEsp;             
    int capEsp;              

public:
    Instructor(string c, string n, string t, string mail, string fn, int capEsp);
    ~Instructor();

 
    string getCedula();
    string getNombre();
    void setNombre(string n);
    string getTelefono();
    void setTelefono(string t);
    string getCorreo();
    void setCorreo(string mail);
    string getFechaNacimiento();

  
    bool agregarEspecialidad(string esp);
    string listarEspecialidades();

  
    string toString();
};
