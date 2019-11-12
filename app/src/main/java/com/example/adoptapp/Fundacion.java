package com.example.adoptapp;

import android.graphics.Bitmap;

public class Fundacion {

    private String imagenPrincipal;
    private String nombre;
    private String encargado;
    private String email;
    private String contrasena;
    private int numero;
    private String municipio;
    private String direccion;
    private double latitud;
    private double longitud;
    private String descripcion;

    public Fundacion(String nombre, String encargado, String email, String contrasena, int numero, String municipio, String direccion, double latitud, double longitud, String descripcion) {
        this.nombre = nombre;
        this.encargado = encargado;
        this.email = email;
        this.contrasena = contrasena;
        this.numero = numero;
        this.municipio = municipio;
        this.direccion = direccion;
        this.latitud = latitud;
        this.longitud = longitud;
        this.descripcion = descripcion;
    }

    public String getImagenPrincipal() {
        return imagenPrincipal;
    }

    public void setImagenPrincipal(String imagenPrincipal) {
        this.imagenPrincipal = imagenPrincipal;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEncargado() {
        return encargado;
    }

    public void setEncargado(String encargado) {
        this.encargado = encargado;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public String getMunicipio() {
        return municipio;
    }

    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
