package com.example.adoptapp;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class AdapterAnimales extends RecyclerView.Adapter<AdapterAnimales.MyViewHolder> {

    private ArrayList<Animal> listaAnimales;

    public AdapterAnimales(ArrayList<Animal> listaAnimales) {
        this.listaAnimales = listaAnimales;
    }

    public class MyViewHolder  extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView textViewNombre;
        public TextView textViewDatos;
        public ImageView imageViewFoto;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public MyViewHolder(View view) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(view);

            textViewNombre = view.findViewById(R.id.textViewItemNombreAnimal);
            textViewDatos = view.findViewById(R.id.textViewItemDetallesAnimal);
            imageViewFoto = view.findViewById(R.id.imageViewItemAnimal);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_item_animal, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        final Animal animal = listaAnimales.get(position);
        holder.textViewNombre.setText(animal.getNombre());
        String fechaPublicacion = new SimpleDateFormat("dd/MM/yyyy").
                format(animal.getFechaPublicacion());
        String edad;
        if (animal.getEdad()<12){
            edad = animal.getEdad()+" meses";
        }else{
            if (animal.getEdad()==12) {
                edad = "1 año";
            }else{ //animal.getEdad()>12
                if( animal.getEdad() % 12 == 0 ){ //al dividir entre 12 no hay residuo
                    edad = (animal.getEdad())/12+" años";
                }else{ //al dividir entre 12 hay residuo
                    String anio;
                    String mes;
                    int anioNumero = (int)(Math.floor( (animal.getEdad())/12 ));
                    int mesNumero = (int)(animal.getEdad() -
                            12*(Math.floor( (animal.getEdad())/12 )) );
                    if( anioNumero == 1 ){
                        anio = " año y ";
                    }else{
                        anio = " años y ";
                    }
                    if( mesNumero == 1 ){
                        mes = " mes";
                    }else{
                        mes = " meses";
                    }
                    edad = anioNumero + anio + mesNumero + mes;
                }
            }
        }
        String datosAnimal = animal.getSexo()+"\n"+animal.getTamano()+"\n"+edad+"\nEn "+animal.getCiudad()
                +"\nA "+animal.getDistancia()+" km de ti"+
                "\nEsperando hogar desde: "+fechaPublicacion
                +"\nResponsable: "+animal.getNombreResponsable();
        holder.textViewDatos.setText(datosAnimal);

        new Thread(new Runnable() {
            public void run() {
                // a potentially time consuming task

                if(!animal.getUrlFotoPrincipal().equals("") ) {
                    try {
                        String imageUrl = animal.getUrlFotoPrincipal();
                        InputStream URLcontent = (InputStream) new URL(imageUrl).getContent();
                        final Drawable image = Drawable.createFromStream(URLcontent, "your source link");

                        holder.imageViewFoto.post(new Runnable() {
                            public void run() {
                                holder.imageViewFoto.setImageDrawable(image);
                            }
                        });

                        //holder.imageViewFoto.setImageDrawable(image);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        }).start();


    }

    @Override
    public int getItemCount() {
        return listaAnimales.size();
    }
}
