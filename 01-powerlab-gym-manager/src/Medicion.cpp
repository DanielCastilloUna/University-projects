#include "Medicion.h" // Daniel Castillo Jimenez- Programacion 1
#include "Cliente.h"
#include "Instructor.h"

Medicion::Medicion(string fech, float pes, float est, float pG, float pM, int eM, float gV, float cin, float ca, float pe, float mu) {
    fecha = fech;
    peso = pes;
    estatura = est;

    porcentajeGrasa = pG;
    porcentajeMusculo = pM;
    edadMetabolica = eM;
    porcentajeGrasaVisceral = gV;
    cintura = cin;
    cadera = ca;
    pecho = pe;
    muslo = mu;
}


string Medicion::getFecha() { return fecha; }
void Medicion::setFecha(string fech) { fecha = fech; }
float Medicion::getPeso() { return peso; }
void Medicion::setPeso(float pes) { peso = pes; }
float Medicion::getEstatura() { return estatura; }
void Medicion::setEstatura(float est) { estatura = est; }



float Medicion::calcularIMC()  {
    return peso / (estatura * estatura);
}

float Medicion::calcularProteina(char sexo, bool haceEjercicio) {
    if (haceEjercicio) {
        if (sexo == 'M') return peso * 2.0;   
        else if (sexo == 'F') return peso * 1.7; 
    }
    return peso * 0.8;
}

int Medicion::calcularVasosAgua() {
    return (int)(peso / 7);
}

string Medicion::clasificarIMC() {
    float imc = calcularIMC();
    if (imc < 16.0) return "Delgadez severa";
    else if (imc < 17.0) return "Delgadez moderada";
    else if (imc < 18.5) return "Delgadez leve";
    else if (imc < 25.0) return "Normal";
    else if (imc < 30.0) return "Pre-obesidad";
    else if (imc < 35.0) return "Obesidad leve (Alto riesgo)";
    else if (imc < 40.0) return "Obesidad media (Alto riesgo)";
    else return "Obesidad morbida (Alto riesgo)";
}

string Medicion::toString(char sexo, bool haceEjercicio)  {
    stringstream s;
    s << "Fecha de la medicion: " << fecha << endl;
    s << "Estatura del cliente: " << estatura << endl;
    s << "Peso del cliente: " << peso << endl;
    s << "IMC: " << calcularIMC() << " (" << clasificarIMC() << ")" << endl;
    s << "Proteina recomendada: " << calcularProteina(sexo, haceEjercicio) << " g/dia" << endl;
    s << "Vasos de agua recomendados: " << calcularVasosAgua() << endl;

    s << "Grasa corporal: " << porcentajeGrasa << "%" << endl;
    s << "Musculo: " << porcentajeMusculo << "%" << endl;
    s << "Edad metabolica: " << edadMetabolica << " annios" << endl;
    s << "Grasa visceral: " << porcentajeGrasaVisceral << "%" << endl;
    s << "Medidas (cm) - Cintura: " << cintura
        << ", Cadera: " << cadera
        << ", Pecho: " << pecho
        << ", Muslo: " << muslo << endl;

    return s.str();
}

Medicion::~Medicion() {}


