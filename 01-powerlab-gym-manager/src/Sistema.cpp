

#include "Sistema.h" // Daniel Castillo Jimenez- Programacion 1
#include "Cliente.h"
#include "Instructor.h"
#include "ClaseGrupal.h"
#include "Medicion.h"
#include "Rutina.h" 
#include <iostream>
#include <sstream>
#include <limits>
#include <cstdlib>
using namespace std;

// Clears the console on Windows (cls) and on Linux/macOS (clear).
static void limpiarPantalla() {
#ifdef _WIN32
    system("cls");
#else
    system("clear");
#endif
}

// Reads a menu option. If the user types something that is not a number,
// the stream is reset instead of looping forever. On end of input returns 0 (exit).
static int leerOpcion() {
    int opcion;
    while (!(cin >> opcion)) {
        if (cin.eof()) return 0;
        cin.clear();
        cin.ignore(numeric_limits<streamsize>::max(), '\n');
        cout << "Opcion invalida, ingrese un numero: ";
    }
    return opcion;
}


void Sistema::menuCliente(Cliente* cliente, Sucursal* sucursal) {
    int opcion = 0;
    do {
        limpiarPantalla();
        cout << "\n===== SUBMENU CLIENTE: " << cliente->getNombre() << " =====\n";
        cout << "1. Asignar instructor\n";
        cout << "2. Asignar rutina\n";
        cout << "3. Agregar ejercicio a rutina\n";
        cout << "4. Ver rutina\n";
        cout << "5. Eliminar instructor por cedula\n";
        cout << "0. Volver\n";
        cout << "Seleccione una opcion: ";
        opcion = leerOpcion();
        cin.ignore();

        switch (opcion) {
        case 1: {
            if (sucursal->getCantidadInstructores() > 0) {
                cout << "Cedula del instructor: ";
                string cedInst;
                getline(cin, cedInst);
                Instructor* inst = sucursal->buscarInstructor(cedInst);
                if (inst != nullptr) {
                    cliente->asignarInstructor(inst);
                    cout << "Instructor asignado.\n";
                }
                else {
                    cout << "Instructor no encontrado.\n";
                }
            }
            else {
                cout << "No hay instructores registrados.\n";
            }
            break;
        }
        case 2: {
            int capacidad;
            cout << "Capacidad total de ejercicios para la rutina: ";
            cin >> capacidad;
            cin.ignore();

            Rutina* r = new Rutina(capacidad);
            cliente->asignarRutina(r);
            cout << "Rutina asignada al cliente.\n";
            break;
        }
        case 3: {
            if (cliente->verRutina() != "No tiene una rutina asignada.") {
                string nombreEj;
                int codigo;
				string area;
                string descripcion;
                int series;
                int repeticiones;
                cout << "Codigo del ejercicio: ";
                cin >> codigo;
                cin.ignore();   

                cout << "Nombre del ejercicio: ";
                getline(cin, nombreEj);

                cout << "Area del ejercicio: ";
                getline(cin, area);

                cout << "Descripcion del ejercicio: ";
                getline(cin, descripcion);

                cout << "Series: ";
                cin >> series;
                cin.ignore();

                cout << "Repeticiones: ";
                cin >> repeticiones;
                cin.ignore();

                Ejercicio ejer(codigo, nombreEj, area, descripcion, series, repeticiones);
                if (cliente->getRutina()->agregarEjercicio(ejer)) {
                    cout << "Ejercicio agregado a la rutina.\n";
                }
                else {
                    cout << "No se pudo agregar ejercicio.\n";
                }
            }
            else {
                cout << "El cliente no tiene rutina asignada.\n";
            }
            break;
        }
       
        case 4: {
            cout << cliente->verRutina() << endl;
            break;
        }
        case 5: {
         

            if (cliente->getInstructor() != nullptr) {
                cout << "Ingrese la cedula del instructor a eliminar: ";
                string cedIn;
                getline(cin, cedIn);

                if (cliente->eliminarInstructor(cedIn)) {
                    cout << "Instructor eliminado del cliente.\n";
                }
                else {
                    cout << "La cedula no coincide con el instructor asignado.\n";
                }
            }
            else {
                cout << "El cliente no tiene instructor asignado.\n";
            }
            break;
        }
        case 0:
            cout << "Volviendo al menu de sucursal...\n";
            break;
        default:
            cout << "Opcion invalida.\n";
        }

        cout << "\nPresione ENTER para continuar...";
        cin.get();
    } while (opcion != 0);
}


void Sistema::menuSucursal(Sucursal* sucursal) {
    int opcion = 0;
    do {
        limpiarPantalla();

        cout << "\n===== MENU SUCURSAL " << sucursal->getCodigo() << " =====\n";
        cout << "1. Agregar cliente\n";
        cout << "2. Listar clientes\n";
        cout << "3. Agregar instructor\n";
        cout << "4. Listar instructores\n";
        cout << "5. Agregar clase grupal\n";
        cout << "6. Listar clases grupales\n";
        cout << "7. Registrar medicion a un cliente\n";
        cout << "8. Mostrar historial de mediciones de un cliente\n";
        cout << "9. Submenu de cliente\n";
        cout << "0. Volver al menu principal\n";
        cout << "Seleccione una opcion: ";
        opcion = leerOpcion();
        cin.ignore();

        switch (opcion) {
        case 1: {
            string ced, nom, tel, mail, fn, fi;
            char sexo;
            bool haceEj;
            int capMed, capClases;

            cout << "Cedula: "; getline(cin, ced);
            cout << "Nombre: "; getline(cin, nom);
            cout << "Telefono: "; getline(cin, tel);
            cout << "Correo: "; getline(cin, mail);
            cout << "Sexo (M/F): "; cin >> sexo; cin.ignore();
            cout << "Fecha nacimiento: "; getline(cin, fn);
            cout << "Fecha ingreso: "; getline(cin, fi);
            cout << "Hace ejercicio (1=Si, 0=No): "; cin >> haceEj;

            cout << "Capacidad de mediciones para hacerle: "; cin >> capMed;
            cout << "Capacidad de clases a las que puede inscribirse: "; cin >> capClases;
            cin.ignore();

            Cliente* c = new Cliente(ced, nom, tel, mail, sexo, fn, fi, haceEj, capMed, capClases);

            if (sucursal->getCantidadInstructores() > 0) {
                cout << "Seleccione instructor (cedula): ";
                string cedInst; getline(cin, cedInst);
                Instructor* inst = sucursal->buscarInstructor(cedInst);
                if (inst != nullptr) {
                    c->setInstructor(inst);
                }
            }

            if (sucursal->agregarCliente(c)) {
                cout << "Cliente agregado exitosamente.\n";
            }
            else {
                cout << "No se pudo agregar cliente (capacidad llena).\n";
                delete c;
            }
            break;
        }
        case 2: {
            stringstream ss;
            ss << "\n===== LISTADO DE CLIENTES =====\n";
            ss << sucursal->listarClientes();
            cout << ss.str() << endl;
            break;
        }
        case 3: {
            string ced, nom, tel, mail, fn;
            int capEsp;

            cout << "Cedula: "; getline(cin, ced);
            cout << "Nombre: "; getline(cin, nom);
            cout << "Telefono: "; getline(cin, tel);
            cout << "Correo: "; getline(cin, mail);
            cout << "Fecha nacimiento: "; getline(cin, fn);
            cout << "Capacidad especialidades: "; cin >> capEsp;
            cin.ignore();

            Instructor* i = new Instructor(ced, nom, tel, mail, fn, capEsp);
			cout << i->toString();
            if (sucursal->agregarInstructor(i)) {
                cout << "Instructor agregado exitosamente.\n";
            }
            else {
                cout << "No se pudo agregar instructor (capacidad llena).\n";
                delete i;
            }
            break;
        }
        case 4: {
            stringstream ss;
            ss << "\n===== LISTADO DE INSTRUCTORES =====\n";
            ss << sucursal->listarInstructores();
            cout << ss.str() << endl;
            break;
        }
        case 5: {
            string cod, nom, sal, hor;
            int cap;

            cout << "Codigo clase: "; getline(cin, cod);
            cout << "Nombre clase: "; getline(cin, nom);
            cout << "Salon: "; getline(cin, sal);
            cout << "Horario: "; getline(cin, hor);
            cout << "Capacidad de clientes: "; cin >> cap;
            cin.ignore();

            ClaseGrupal* cg = new ClaseGrupal(cod, nom, sal, hor, cap);
            if (sucursal->agregarClase(cg)) {
                cout << "Clase grupal agregada exitosamente.\n";
            }
            else {
                cout << "No se pudo agregar clase (capacidad llena).\n";
                delete cg;
            }
            break;
        }
        case 6: {
            stringstream ss;
            ss << "\n===== LISTADO DE CLASES GRUPALES =====\n";
            ss << sucursal->listarClases();
            cout << ss.str() << endl;
            break;
        }
        case 7: {
            string ced;
            cout << "Cedula del cliente: ";
            getline(cin, ced);
            Cliente* c = sucursal->buscarCliente(ced);
            if (c != nullptr) {
                string fecha;
                float peso, estatura, grasaVisceral, cintura, cadera, pecho, muslo;
                int edadMet; 

                cout << "Fecha de medicion (dd/mm/aaaa): "; getline(cin, fecha);
                cout << "Peso (kg): "; cin >> peso;
                cout << "Estatura (m): "; cin >> estatura;
                cout << "Grasa visceral: "; cin >> grasaVisceral;
                cout << "Edad metabolica del cliente: "; cin >> edadMet;

                float grasa, musculo;
                cout << "Porcentaje de grasa: "; cin >> grasa;
                cout << "Porcentaje de musculo: "; cin >> musculo;
                cout << "Medidas de cintura: "; cin >> cintura;
                cout << "Medidas de cadera:  "; cin >> cadera;
                cout << "Medidas de pecho: "; cin >> pecho;
                cout << "Medidas de muslo: "; cin >> muslo;

                cin.ignore();

                // Argument order matches Medicion(fecha, peso, estatura, %grasa, %musculo,
                // edadMetabolica, grasaVisceral, cintura, cadera, pecho, muslo)
                Medicion* m = new Medicion(fecha, peso, estatura, grasa, musculo, edadMet,
                                           grasaVisceral, cintura, cadera, pecho, muslo);

                if (c->agregarMedicion(m)) {
                    cout << "Medicion registrada.\n";
                }
                else {
                    cout << "No se pudo registrar (capacidad llena).\n";
                    delete m;
                }
            }
            else {
                cout << "Cliente no encontrado.\n";
            }
            break;
        }
        case 8: {
            string ced;
            cout << "Cedula del cliente: ";
            getline(cin, ced);
            Cliente* c = sucursal->buscarCliente(ced);
            if (c != nullptr) {
                cout << c->mostrarHistorialMediciones() << endl;
				
            }
            else {
                cout << "Cliente no encontrado.\n";
            }
            break;
        }
        case 9: {
            string ced;
            cout << "Cedula del cliente: ";
            getline(cin, ced);
            Cliente* c = sucursal->buscarCliente(ced);
            if (c != nullptr) {
                menuCliente(c, sucursal);
            }
            else {
                cout << "Cliente no encontrado.\n";
            }
            break;
        }
        case 0:
            cout << "Volviendo al menu principal...\n";
            break;
        default:
            cout << "Opcion invalida.\n";
        }

        cout << "\nPresione ENTER para continuar...";
        cin.get();

    } while (opcion != 0);
}


Sistema::Sistema(int capacidad) {
    capSuc = capacidad;
    cantSuc = 0;
    sucursales = new Sucursal * [capacidad];
    for (int i = 0; i < capacidad; i++) {
        sucursales[i] = nullptr;
    }
}

Sistema::~Sistema() {
    for (int i = 0; i < cantSuc; i++) {
        delete sucursales[i];
    }
    delete[] sucursales;
}

bool Sistema::agregarSucursal(Sucursal* s) {
    if (cantSuc < capSuc) {
        sucursales[cantSuc++] = s;
        return true;
    }
    return false;
}

string Sistema::listarSucursales() {
    stringstream r;
    for (int i = 0; i < cantSuc; i++) {
        r << sucursales[i]->toString() << endl;
    }
    return r.str();
}

Sucursal* Sistema::buscarSucursal(string codigo) {
    for (int i = 0; i < cantSuc; i++) {
        if (sucursales[i]->getCodigo() == codigo) {
            return sucursales[i];
        }
    }
    return nullptr;
}

string Sistema::toString() {
    stringstream r;
    r << "Sistema con " << cantSuc << " sucursales." << endl;
    for (int i = 0; i < cantSuc; i++) {
        r << sucursales[i]->toString() << endl;
    }
    return r.str();
}

void Sistema::ejecutarMenu() {
    int opcion = 0;
    do {
        limpiarPantalla();
        cout << "\n===== MENU PRINCIPAL =====\n";
        cout << "1. Agregar sucursal\n";
        cout << "2. Listar sucursales\n";
        cout << "3. Acceder a sucursal\n";
        cout << "0. Salir\n";
        cout << "Seleccione una opcion: ";
        opcion = leerOpcion();
        cin.ignore();

        switch (opcion) {
        case 1: {
            string cod, prov, can, mail, tel;
            int capCli, capIns, capCla;
            cout << "Nombre sucursal: "; getline(cin, cod);
            cout << "Provincia: "; getline(cin, prov);
            cout << "Canton: "; getline(cin, can);
            cout << "Correo: "; getline(cin, mail);
            cout << "Telefono: "; getline(cin, tel);
            cout << "Capacidad de clientes: "; cin >> capCli;
            cout << "Capacidad de instructores: "; cin >> capIns;
            cout << "Capacidad de clases: "; cin >> capCla;
            cin.ignore();

            Sucursal* s = new Sucursal(cod, prov, can, mail, tel, capCli, capIns, capCla);
            if (agregarSucursal(s)) {
                cout << "Sucursal agregada exitosamente.\n";
            }
            else {
                cout << "No se pudo agregar sucursal (capacidad llena).\n";
                delete s;
            }
            cout << "\nPresione ENTER para continuar...";
            cin.get();
            break;
        }
        case 2: {
            stringstream ss;
            ss << "\n===== LISTADO DE SUCURSALES =====\n";
            ss << listarSucursales();
            cout << ss.str() << endl;
            cout << "\nPresione ENTER para continuar...";
            cin.get();
            break;
        }
        case 3: {
            string cod;
            cout << "Ingrese el nombre de la sucursal: ";
            getline(cin, cod);
            Sucursal* s = buscarSucursal(cod);
            if (s != nullptr) {
                menuSucursal(s);
            }
            else {
                cout << "Sucursal no encontrada.\n";
            }
            break;
        }
        case 0:
            cout << "Saliendo del sistema...\n";
            break;
        default:
            cout << "Opcion invalida.\n";
            cout << "\nPresione ENTER para continuar...";
            cin.get();
        }
    } while (opcion != 0);
}
