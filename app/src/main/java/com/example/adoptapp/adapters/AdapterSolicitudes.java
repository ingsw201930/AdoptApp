package com.example.adoptapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.adoptapp.R;
import com.example.adoptapp.model.Solicitud;
import com.example.adoptapp.utils.FirebaseUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class AdapterSolicitudes extends RecyclerView.Adapter<AdapterSolicitudes.MyViewHolder> {

    private ArrayList<Solicitud> listaSolicitudes;

    public AdapterSolicitudes(ArrayList<Solicitud> listaSolicitudes) {
        this.listaSolicitudes = listaSolicitudes;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
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

            textViewNombre = view.findViewById(R.id.tv_item_nombre_solicitud);
            textViewDatos = view.findViewById(R.id.tv_item_detalles_solicitud);
            imageViewFoto = view.findViewById(R.id.iv_item_solicitud);
        }
    }

    @Override
    public AdapterSolicitudes.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_item_solicitud, parent, false);

        return new AdapterSolicitudes.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final AdapterSolicitudes.MyViewHolder holder, int position) {
        final Solicitud solicitud = listaSolicitudes.get(position);
        holder.textViewNombre.setText("Solicitud de "+solicitud.getTipo());
        String fechaPublicacion = new SimpleDateFormat("dd/MM/yyyy").
                format(solicitud.getFecha());
        String datosSolicitud = "Realizada: " + fechaPublicacion+"\n"+
                "Por: "+solicitud.getNombrePersona();
        if(solicitud.getTipo().equals("Adopción") || solicitud.getTipo().equals("Apadrinamiento") ||
                solicitud.getTipo().equals("Donación")){
            datosSolicitud = datosSolicitud+"\nPara: "+solicitud.getNombreAnimal();
        }
        if(solicitud.getTipo().equals("Apadrinamiento")){
            datosSolicitud = datosSolicitud+"\nMonto mensual ofrecido: "+solicitud.getMonto();
        }

        if( !solicitud.isEstado() ){
            if( solicitud.isFormalizada()){
                datosSolicitud = datosSolicitud + "\n\nYa confirmada";
            }else{
                datosSolicitud = datosSolicitud + "\n\nNo confirmada";
            }
        }

        holder.textViewDatos.setText(datosSolicitud);

        new Thread(new Runnable() {
            public void run() {
                // a potentially time consuming task

                if (!solicitud.getFotoUrl().equals("")) {
                    FirebaseUtils.descargarFotoImageView(solicitud.getFotoUrl(), holder.imageViewFoto);
                }
            }
        }).start();
    }

    @Override
    public int getItemCount() {
        return listaSolicitudes.size();
    }

}
