package com.example.adoptapp;

class Animal {

    private String id;
    private String nombre;
    private String tamano;
    private int edad;
    private String ciudad;

    Animal() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
