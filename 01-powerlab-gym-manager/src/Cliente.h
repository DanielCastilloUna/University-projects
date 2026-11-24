#pragma once // Daniel Castillo Jimenez- Programacion 1
#include <string>
using namespace std;

class Rutina;
class Medicion;
class Instructor;
class ClaseGrupal;

class Cliente {
private:
    string nombre;
    string cedula;
    string telefono;
    string correo;
    char sexo;
    bool haceEjercicio;
    string fechaNacimiento;
    string fechaIngreso;

    Rutina* rutina;
    Instructor* instructor;

    Medicion** mediciones;
    int cantMed;
    int capMed;

    ClaseGrupal** clasesMatriculadas;
    int cantClases;
    int capClases;

public:
    Cliente(string c, string n, string t, string mail, char s, string fn, string fi,
        bool haceEjercicio, int capMed, int capClases);
    ~Cliente();

    
    string getNombre();
    void setNombre(string n);

    Rutina* getRutina();

    string getCedula();
    void setCedula(string c);

    string getTelefono();
    void setTelefono(string t);

    string getMail();
    void setMail(string mail);

    string getFechaNacimiento();
    void setFechaNacimiento(string fn);

    string getFechaIngreso();
    void setFechaIngreso(string fi);

    // Gestión de mediciones
    bool agregarMedicion(Medicion* m);
    string mostrarHistorialMediciones();

    Instructor* getInstructor();

    // Rutina e instructor
    void setRutina(Rutina* r);
    void setInstructor(Instructor* i);

    // Clases grupales
    bool matricularClase(ClaseGrupal* c);

	void asignarInstructor(Instructor* i);
    void asignarRutina(Rutina* r);
    bool eliminarInstructor(string);
	string verRutina();
    string toString();
};


