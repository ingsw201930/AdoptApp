package com.example.adoptapp.model;

import com.google.firebase.firestore.GeoPoint;

public class Persona {

    private String id;
    private String nombre;
    private String apellido;
    private String email;
    private String imagenPrincipal;
    private String municipio;
    private long telefono;
    private long documento;
    private GeoPoint ubicacion;



    public Persona(String nombre, String apellido, String email, String municipio, long telefono, long documento, GeoPoint ubicacion) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.municipio = municipio;
        this.telefono = telefono;
        this.documento = documento;
        this.ubicacion = ubicacion;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public long getTelefono() {
        return telefono;
    }

    public void setTelefono(long telefono) {
        this.telefono = telefono;
    }

    public long getDocumento() {
        return documento;
    }

    public void setDocumento(long documento) {
        this.documento = documento;
    }

    public GeoPoint getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(GeoPoint ubicacion) {
        this.ubicacion = ubicacion;
    }
}

