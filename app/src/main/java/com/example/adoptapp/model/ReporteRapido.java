package com.example.adoptapp.model;

import com.google.firebase.firestore.GeoPoint;

import java.util.Date;

public class ReporteRapido {

    private String descripcion;
    private GeoPoint ubicacion;
    private Date fecha;
    private String direccionFoto;

    public ReporteRapido() {
    }

    public ReporteRapido(String descripcion, String foto, Date fecha) {
        this.descripcion = descripcion;
        this.direccionFoto = foto;
        this.fecha = fecha;
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
}
