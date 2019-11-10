package com.example.adoptapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.adoptapp.R;
import com.example.adoptapp.model.Institucion;
import com.example.adoptapp.model.ReporteRapido;
import com.example.adoptapp.utils.FirebaseUtils;

import java.util.ArrayList;

public class AdapterReportes extends RecyclerView.Adapter<AdapterReportes.MyViewHolder> {

    private ArrayList<ReporteRapido> listaReportes;

    public AdapterReportes(ArrayList<ReporteRapido> listaReportes) {
        this.listaReportes = listaReportes;
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
        final ReporteRapido reporte = listaReportes.get(position);
        holder.textViewNombre.setText("Reporte rápido");
        String datosReporte = "Descripcion:\n" + reporte.getDescripcion()+"\n"+
                "Fecha :" + reporte.getFecha()+"\n"+
                "Nombre responsable :" + reporte.getNombreResponsable();
        holder.textViewDatos.setText(datosReporte);

        new Thread(new Runnable() {
            public void run() {
                // a potentially time consuming task

                if (!reporte.getDireccionFoto().equals("")) {
                    FirebaseUtils.descargarFotoImageView(reporte.getDireccionFoto(), holder.imageViewFoto);
                }
            }
        }).start();
    }

    @Override
    public int getItemCount() {
        return listaReportes.size();
    }
}
