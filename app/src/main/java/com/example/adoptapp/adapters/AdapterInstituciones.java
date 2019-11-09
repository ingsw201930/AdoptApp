package com.example.adoptapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adoptapp.R;
import com.example.adoptapp.model.Institucion;
import com.example.adoptapp.utils.FirebaseUtils;

import java.util.ArrayList;

public class AdapterInstituciones extends RecyclerView.Adapter<AdapterInstituciones.MyViewHolder> {

    private ArrayList<Institucion> listaInstituciones;

    public AdapterInstituciones(ArrayList<Institucion> listaAnimales) {
        this.listaInstituciones = listaAnimales;
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

            textViewNombre = view.findViewById(R.id.tv_item_nombre_institucion);
            textViewDatos = view.findViewById(R.id.tv_item_detalles_institucion);
            imageViewFoto = view.findViewById(R.id.iv_item_institucion);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_item_institucion, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final Institucion institucion = listaInstituciones.get(position);
        holder.textViewNombre.setText(institucion.getNombre());
        String datosInstitucion = "Encargado: " + institucion.getEncargado()+"\n"+
                "Municipio: "+institucion.getMunicipio()+"\n"+
                "A "+institucion.getDistancia()+" km de tu ubicación\n";
        holder.textViewDatos.setText(datosInstitucion);

        new Thread(new Runnable() {
            public void run() {
                // a potentially time consuming task

                if (!institucion.getImagenPrincipal().equals("")) {
                    FirebaseUtils.descargarFotoImageView(institucion.getImagenPrincipal(), holder.imageViewFoto);
                }
            }
        }).start();
    }

    @Override
    public int getItemCount() {
        return listaInstituciones.size();
    }
}
