package com.example.adoptapp.model;

import com.google.firebase.firestore.GeoPoint;

public class Institucion {

    private String id;
    private String email;
    private String encargado;
    private String imagenPrincipal;
    private String municipio;
    private String nombre;
    private long telefono;
    private GeoPoint ubicacion;
    private double distancia;
    private String descripcion;

    public Institucion() {}

    public Institucion(String email, String encargado, String municipio, String nombre, long telefono, GeoPoint ubicacion, String descripcion) {
        this.email = email;
        this.encargado = encargado;
        this.municipio = municipio;
        this.nombre = nombre;
        this.telefono = telefono;
        this.ubicacion = ubicacion;
        this.descripcion = descripcion;
    }

    public Institucion(String id, String email, String encargado, String imagenPrincipal,
                       String municipio, String nombre, long telefono, GeoPoint ubicacion,
                       double distancia, String descripcion) {
        this.id = id;
        this.email = email;
        this.encargado = encargado;
        this.imagenPrincipal = imagenPrincipal;
        this.municipio = municipio;
        this.nombre = nombre;
        this.telefono = telefono;
        this.ubicacion = ubicacion;
        this.distancia = distancia;
        this.descripcion = descripcion;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEncargado() {
        return encargado;
    }

    public void setEncargado(String encargado) {
        this.encargado = encargado;
    }

    public String getImagenPrincipal() {
        return imagenPrincipal;
    }

    public void setImagenPrincipal(String imagenPrincipal) {
        this.imagenPrincipal = imagenPrincipal;
    }

    public String getMunicipio() {
        return municipio;
    }

    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public long getTelefono() {
        return telefono;
    }

    public void setTelefono(long telefono) {
        this.telefono = telefono;
    }

    public GeoPoint getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(GeoPoint ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getDistancia() {
        return distancia;
    }

    public void setDistancia(double distancia) {
        this.distancia = distancia;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
