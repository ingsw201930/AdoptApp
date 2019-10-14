package com.example.adoptapp;

import android.os.Parcel;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

class Animal {

    private String id;
    private String nombre;
    private String tamano;
    private int edad;
    private String ciudad;
    private String urlFotoPrincipal;
    private String tipo;
    private String nombreResponsable;
    private String municipioResponsable;
    private double distancia;
    private Date fechaPublicacion;
    //private Map<String,Integer> descriptores;
    private ArrayList<String> descriptores;

    Animal() {
    }

    ArrayList<String> getDescriptores() {
        return descriptores;
    }

    void setDescriptores(ArrayList<String> descriptores) {
        this.descriptores = descriptores;
    }

    Date getFechaPublicacion() {
        return fechaPublicacion;
    }

    void setFechaPublicacion(Date fechaPublicacion) {
        this.fechaPublicacion = fechaPublicacion;
    }

    String getNombreResponsable() {
        return nombreResponsable;
    }

    void setNombreResponsable(String nombreResponsable) {
        this.nombreResponsable = nombreResponsable;
    }

    public String getMunicipioResponsable() {
        return municipioResponsable;
    }

    public void setMunicipioResponsable(String municipioResponsable) {
        this.municipioResponsable = municipioResponsable;
    }

    double getDistancia() {
        return distancia;
    }

    void setDistancia(double distancia) {
        this.distancia = distancia;
    }

    String getTipo() {
        return tipo;
    }

    void setTipo(String tipo) {
        this.tipo = tipo;
    }

    String getUrlFotoPrincipal() {
        return urlFotoPrincipal;
    }

    void setUrlFotoPrincipal(String urlFotoPrincipal) {
        this.urlFotoPrincipal = urlFotoPrincipal;
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
