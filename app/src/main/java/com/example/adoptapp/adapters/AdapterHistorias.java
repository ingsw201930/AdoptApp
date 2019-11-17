package com.example.adoptapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.adoptapp.R;
import com.example.adoptapp.model.Adopcion;
import com.example.adoptapp.model.Institucion;
import com.example.adoptapp.utils.FirebaseUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class AdapterHistorias extends RecyclerView.Adapter<AdapterHistorias.MyViewHolder> {

    private ArrayList<Adopcion> listaItems;

    public AdapterHistorias(ArrayList<Adopcion> listaItems) {
        this.listaItems = listaItems;
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

            textViewNombre = view.findViewById(R.id.tv_titulo_historia);
            textViewDatos = view.findViewById(R.id.tv_item_historia_detalles);
            imageViewFoto = view.findViewById(R.id.iv_item_historia_foto);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_item_historia, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        final Adopcion adopcion = listaItems.get(position);

        holder.textViewNombre.setText("Historia de "+adopcion.getNombreAnimal());

        String fechaPublicacion = new SimpleDateFormat("dd/MM/yyyy").
                format(adopcion.getFecha());

        String datosInstitucion = "Adoptado(a) por:"+adopcion.getNombrePersona()+"\n\n"+
                "De: "+adopcion.getNombreInstitucion()+"\n\n"+
                "En: "+fechaPublicacion+"\n\n"+
                "Descripción:\n"+adopcion.getDescripcion();
        holder.textViewDatos.setText(datosInstitucion);

        new Thread(new Runnable() {
            public void run() {
                // a potentially time consuming task

                if (!adopcion.getFotoUrl().equals("")) {
                    FirebaseUtils.descargarFotoImageView(adopcion.getFotoUrl(), holder.imageViewFoto);
                }
            }
        }).start();
    }

    @Override
    public int getItemCount() {
        return listaItems.size();
    }
}
