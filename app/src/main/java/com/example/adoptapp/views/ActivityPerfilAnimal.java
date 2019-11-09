package com.example.adoptapp.views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.adoptapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

public class ActivityPerfilAnimal extends AppCompatActivity {

    TextView textViewNombre;
    TextView textViewDescripcion;
    ImageView imageViewFotoPrincipal;

    FirebaseAuth mAuth;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_animal);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        Intent intent = getIntent();
        String nombre = intent.getStringExtra("Nombre");
        final String fotoPrincipal = intent.getStringExtra("Foto_principal");
        String descripcion = intent.getStringExtra("Descripcion");
        ArrayList<String> descriptores = new ArrayList<>(intent.getStringArrayListExtra("descriptores"));

        textViewNombre = findViewById(R.id.textViewNombrePerfilAnimal);
        textViewDescripcion = findViewById(R.id.textViewDatosPerfilAnimal);
        imageViewFotoPrincipal = findViewById(R.id.imageViewPerfilAnimal);

        descripcion = descripcion + "\n\nDescriptores:\n\n";
        for (int i = 0; i < descriptores.size(); i++) {
            descripcion = descripcion + descriptores.get(i)+"\n";
        }

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

                if (fotoPrincipal != null && !fotoPrincipal.equals("")) {
                    try {
                        InputStream URLcontent = (InputStream) new URL(fotoPrincipal).getContent();
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

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_sesion, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.MenuCerrarSesion:
                cerrarSesion();
                return true;
            default:
                return true;
        }
    }

    private void cerrarSesion(){
        if (currentUser == null) {
            mAuth.signOut();
        }
        //sesionCerrada = true;
        Intent intent = new Intent(ActivityPerfilAnimal.this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }*/

}
