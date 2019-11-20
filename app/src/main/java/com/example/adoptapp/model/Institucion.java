package com.example.adoptapp.model;

import com.google.firebase.firestore.GeoPoint;

public class Institucion {

    private String id;
    private String Email;
    private String Encargado;
    private String ImagenPrincipal;
    private String Municipio;
    private String Nombre;
    private long Telefono;
    private GeoPoint Ubicacion;
    private double Distancia;
    private String Descripcion;


    public Institucion() {

    }

    public Institucion(String email, String encargado, String municipio, String nombre, long telefono, GeoPoint ubicacion, String descripcion) {
        this.Email = email;
        this.Encargado = encargado;
        this.Municipio = municipio;
        this.Nombre = nombre;
        this.Telefono = telefono;
        this.Ubicacion = ubicacion;
        this.Descripcion = descripcion;
    }


    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        this.Email = email;
    }

    public String getEncargado() {
        return Encargado;
    }

    public void setEncargado(String encargado) {
        this.Encargado = encargado;
    }

    public String getImagenPrincipal() {
        return ImagenPrincipal;
    }

    public void setImagenPrincipal(String imagenPrincipal) {
        this.ImagenPrincipal = imagenPrincipal;
    }

    public String getMunicipio() {
        return Municipio;
    }

    public void setMunicipio(String municipio) {
        this.Municipio = municipio;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        this.Nombre = nombre;
    }

    public long getTelefono() {
        return Telefono;
    }

    public void setTelefono(long telefono) {
        this.Telefono = telefono;
    }

    public GeoPoint getUbicacion() {
        return Ubicacion;
    }

    public void setUbicacion(GeoPoint ubicacion) {
        this.Ubicacion = ubicacion;
    }

    public String getDescripcion() {
        return Descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.Descripcion = descripcion;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getDistancia() {
        return Distancia;
    }

    public void setDistancia(double distancia) {
        this.Distancia = distancia;
    }
}
