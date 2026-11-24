#pragma once // Daniel Castillo Jimenez- Programacion 1
#include <iostream>
#include <string>
#include <sstream>
using namespace std;

class Instructor; //Predeclarar clases
class Cliente;

class Medicion {
private:
    string fecha; //Atributos
    float peso;
    float estatura;

    float porcentajeGrasa;
    float porcentajeMusculo;
    int edadMetabolica;
    float porcentajeGrasaVisceral;
    float cintura;
    float cadera;
    float pecho;
    float muslo;

public:

    Medicion(string, float, float, float, float , int,float , float , float , float , float );

    string getFecha();
    void setFecha(string);
    float getPeso();
    void setPeso(float);
    float getEstatura();
    void setEstatura(float);

    
    void setPorcentajeGrasa(float g) { porcentajeGrasa = g; }
    float getPorcentajeGrasa() { return porcentajeGrasa; }

    void setPorcentajeMusculo(float m) { porcentajeMusculo = m; }
    float getPorcentajeMusculo() { return porcentajeMusculo; }

    void setEdadMetabolica(int e) { edadMetabolica = e; }
    int getEdadMetabolica() { return edadMetabolica; }

    void setCintura(float c) { cintura = c; }
    float getCintura() { return cintura; }

    float getPecho() { return pecho; }
    void setPecho(float pe) { pecho = pe; }

    float getMuslo() { return muslo; }
    void setMuslo(float mu) { muslo = mu; }

    float getCadera(){ return cadera; }
    void setCadera(float ca) { cadera = ca; }




    float calcularIMC();  //Metodos
    float calcularProteina(char, bool);
    int calcularVasosAgua();
    string clasificarIMC();
    string toString(char, bool);

    ~Medicion();
};
