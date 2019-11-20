package com.example.adoptapp.model;

import android.os.Parcel;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class Animal {

    private String id;
    private String IdResponsable;
    private String nombre;
    private String tamano;
    private String sexo;
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

    public Animal() {
    }



    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public ArrayList<String> getDescriptores() {
        return descriptores;
    }

    public void setDescriptores(ArrayList<String> descriptores) {
        this.descriptores = descriptores;
    }

    public Date getFechaPublicacion() {
        return fechaPublicacion;
    }

    public void setFechaPublicacion(Date fechaPublicacion) {
        this.fechaPublicacion = fechaPublicacion;
    }

    public String getNombreResponsable() {
        return nombreResponsable;
    }

    public void setNombreResponsable(String nombreResponsable) {
        this.nombreResponsable = nombreResponsable;
    }

    public String getMunicipioResponsable() {
        return municipioResponsable;
    }

    public void setMunicipioResponsable(String municipioResponsable) {
        this.municipioResponsable = municipioResponsable;
    }

    public double getDistancia() {
        return distancia;
    }

    public void setDistancia(double distancia) {
        this.distancia = distancia;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getUrlFotoPrincipal() {
        return urlFotoPrincipal;
    }

    public void setUrlFotoPrincipal(String urlFotoPrincipal) {
        this.urlFotoPrincipal = urlFotoPrincipal;
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

    public String getTamano() {
        return tamano;
    }

    public void setTamano(String tamano) {
        this.tamano = tamano;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getIdResponsable() {
        return IdResponsable;
    }

    public void setIdResponsable(String idResponsable) {
        IdResponsable = idResponsable;
    }
}
