package com.example.adoptapp.model;

import com.google.firebase.firestore.GeoPoint;

import java.util.Date;

public class ReporteRapido {

    private String descripcion;
    private GeoPoint ubicacion;
    private Date fecha;
    private String direccionFoto;
    private String idResponsable;
    private String nombreResponsable;
    private double distancia;

    public ReporteRapido() {
    }

    public ReporteRapido(String descripcion, String direccionFoto, Date fecha, String idResponsable,
                         String nombreResponsable) {
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.direccionFoto = direccionFoto;
        this.idResponsable = idResponsable;
        this.nombreResponsable = nombreResponsable;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public GeoPoint getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(GeoPoint ubicacion) {
        this.ubicacion = ubicacion;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getDireccionFoto() {
        return direccionFoto;
    }

    public void setDireccionFoto(String direccionFoto) {
        this.direccionFoto = direccionFoto;
    }

    public String getIdResponsable() {
        return idResponsable;
    }

    public void setIdResponsable(String idResponsable) {
        this.idResponsable = idResponsable;
    }

    public String getNombreResponsable() {
        return nombreResponsable;
    }

    public void setNombreResponsable(String nombreResponsable) {
        this.nombreResponsable = nombreResponsable;
    }

    public void setDistancia(double distancia) {
        this.distancia = distancia;
    }
}
