package com.example.adoptapp;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class CustomAdapter implements ListAdapter {

    private ArrayList<Animal> arrayList;
    private Context context;

    CustomAdapter(Context context, ArrayList<Animal> arrayList) {
        this.arrayList=arrayList;
        this.context=context;
    }
    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }
    @Override
    public boolean isEnabled(int position) {
        return true;
    }
    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
    }
    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
    }
    @Override
    public int getCount() {
        return arrayList.size();
    }
    @Override
    public Object getItem(int position) {
        return position;
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final Animal animal=arrayList.get(position);

        if(convertView==null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView=layoutInflater.inflate(R.layout.layout_item_animal, null);

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(view.getContext(), animal.getId(), Toast.LENGTH_SHORT).show();
                }
            });

            TextView textViewNombre =convertView.findViewById(R.id.textViewItemNombreAnimal);
            textViewNombre.setText(animal.getNombre());
            String datos = animal.getTamano()+"\n"+animal.getEdad()+"\n"+animal.getCiudad();
            TextView textViewDatos =convertView.findViewById(R.id.textViewItemDetallesAnimal);
            textViewDatos.setText(datos);

        }
        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
    @Override
    public int getViewTypeCount() {
        return arrayList.size();
    }
    @Override
    public boolean isEmpty() {
        return false;
    }
}
