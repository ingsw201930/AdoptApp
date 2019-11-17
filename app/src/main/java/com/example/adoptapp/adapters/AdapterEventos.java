package com.example.adoptapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.adoptapp.R;
import com.example.adoptapp.model.Evento;
import com.example.adoptapp.model.ReporteRapido;
import com.example.adoptapp.utils.FirebaseUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class AdapterEventos extends RecyclerView.Adapter<AdapterEventos.MyViewHolder> {

    private ArrayList<Evento> listaEventos;

    public AdapterEventos(ArrayList<Evento> listaEventos) {
        this.listaEventos = listaEventos;
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

            textViewNombre = view.findViewById(R.id.tv_item_nombre_evento);
            textViewDatos = view.findViewById(R.id.tv_item_detalles_evento);
            imageViewFoto = view.findViewById(R.id.iv_item_evento);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_item_evento, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        final Evento evento = listaEventos.get(position);

        holder.textViewNombre.setText( evento.getTitulo() );

        String fechaPublicacion = new SimpleDateFormat("dd/MM/yyyy").
                format(evento.getFecha());

        String datosReporte = "Fecha: " + fechaPublicacion+"\n"+
                "Horario: "+evento.getHoraInicio()+ " a "+evento.getHoraFin()+"\n"+
                "Organizado por :" + evento.getNombreInstitucion()+"\n"+
                "En :" + evento.getDireccion()+"\n"+
                "A "+evento.getDistancia()+" km de tu ubicación\n";
        holder.textViewDatos.setText(datosReporte);

        new Thread(new Runnable() {
            public void run() {
                // a potentially time consuming task

                if (!evento.getFotoUrl().equals("")) {
                    FirebaseUtils.descargarFotoImageView(evento.getFotoUrl(), holder.imageViewFoto);
                }
            }
        }).start();
    }

    @Override
    public int getItemCount() {
        return listaEventos.size();
    }
}
