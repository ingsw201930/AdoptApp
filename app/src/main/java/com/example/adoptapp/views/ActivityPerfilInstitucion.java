package com.example.adoptapp.views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.adoptapp.R;
import com.example.adoptapp.utils.FirebaseUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ActivityPerfilInstitucion extends AppCompatActivity {

    TextView textViewNombre;
    TextView textViewDescripcion;
    ImageView imageViewFotoPrincipal;

    FirebaseAuth mAuth;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_institucion);

        textViewNombre = findViewById(R.id.tv_perfil_nombre_fundacion);
        textViewDescripcion = findViewById(R.id.tv_perfil_datos_fundacion);
        imageViewFotoPrincipal = findViewById(R.id.iv_perfil_foto_fundacion);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        Intent intent = getIntent();
        String nombre = intent.getStringExtra("Nombre");
        final String fotoPrincipal = intent.getStringExtra("Foto_principal");
        String descripcion = intent.getStringExtra("Descripcion");

        textViewNombre.setText(nombre);
        textViewDescripcion.setText(descripcion);

        new Thread(new Runnable() {
            public void run() {
                // a potentially time consuming task
                if (fotoPrincipal != null && !fotoPrincipal.equals("")) {
                    //descargar la imagen
                    FirebaseUtils.descargarFotoImageView(fotoPrincipal, imageViewFotoPrincipal);
                }
            }
        }).start();
    }
}
