package com.example.adoptapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.URL;

public class ActivityPerfilAnimal extends AppCompatActivity {

    TextView textViewNombre;
    TextView textViewDescripcion;
    ImageView imageViewFotoPrincipal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_animal);

        Intent intent = getIntent();
        String nombre = intent.getStringExtra("Nombre");
        final String fotoPrincipal = intent.getStringExtra("Foto_principal");
        String descripcion = intent.getStringExtra("Descripcion");

        textViewNombre = findViewById(R.id.textViewNombrePerfilAnimal);
        textViewDescripcion = findViewById(R.id.textViewDatosPerfilAnimal);
        imageViewFotoPrincipal = findViewById(R.id.imageViewPerfilAnimal);

        textViewNombre.setText(nombre);
        textViewDescripcion.setText(descripcion);

        /*if(!fotoPrincipal.equals("") ) {
            try {
                String imageUrl = fotoPrincipal;
                InputStream URLcontent = (InputStream) new URL(imageUrl).getContent();
                Drawable image = Drawable.createFromStream(URLcontent, "your source link");
                imageViewFotoPrincipal.setImageDrawable(image);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/

        new Thread(new Runnable() {
            public void run() {
                // a potentially time consuming task

                if(!fotoPrincipal.equals("") ) {
                    try {
                        String imageUrl = fotoPrincipal;
                        InputStream URLcontent = (InputStream) new URL(imageUrl).getContent();
                        final Drawable image = Drawable.createFromStream(URLcontent, "your source link");

                        imageViewFotoPrincipal.post(new Runnable() {
                            public void run() {
                                imageViewFotoPrincipal.setImageDrawable(image);
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
}
