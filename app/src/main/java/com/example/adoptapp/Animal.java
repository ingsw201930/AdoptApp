package com.example.adoptapp;

class Animal {

    private String nombre;
    private String tamano;
    private int edad;
    private String ciudad;

    Animal() {
    }

    String getNombre() {
        return nombre;
    }

    void setNombre(String nombre) {
        this.nombre = nombre;
    }

    String getTamano() {
        return tamano;
    }

    void setTamano(String tamano) {
        this.tamano = tamano;
    }

    int getEdad() {
        return edad;
    }

    void setEdad(int edad) {
        this.edad = edad;
    }

    String getCiudad() {
        return ciudad;
    }

    void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }
}
