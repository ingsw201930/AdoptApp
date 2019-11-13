package com.example.adoptapp.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adoptapp.R;
import com.example.adoptapp.utils.FirebaseUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

public class ActivityPerfilAnimal extends AppCompatActivity {

    private TextView textViewNombre;
    private TextView textViewDescripcion;
    private ImageView imageViewFotoPrincipal;

    private ConstraintLayout constraintLayoutAdoptar;
    private ConstraintLayout constraintLayoutApadrinar;
    private ConstraintLayout constraintLayoutDonar;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private String id_animal;
    private String id_institucion;
    private String nombre_institucion;
    private String nombre_animal;
    private String foto_url;

    private String tipo_solicitud;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private String TAG = "ActivityPerfilAnimal";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_animal);

        constraintLayoutAdoptar = findViewById(R.id.ConstraintLayoutAdoptar);
        constraintLayoutApadrinar = findViewById(R.id.ConstraintLayoutApadrinar);
        constraintLayoutDonar = findViewById(R.id.ConstraintLayoutDonar);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        Intent intent = getIntent();
        String nombre = intent.getStringExtra("Nombre");
        final String fotoPrincipal = intent.getStringExtra("Foto_principal");
        String descripcion = intent.getStringExtra("Descripcion");
        ArrayList<String> descriptores = new ArrayList<>(intent.getStringArrayListExtra("descriptores"));

        id_animal = intent.getStringExtra("id_animal");
        id_institucion = intent.getStringExtra("id_institucion");
        nombre_institucion = intent.getStringExtra("nombre_institucion");
        nombre_animal = nombre;
        foto_url = fotoPrincipal;

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
                    /*try {
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
                    }*/
                    //descargar la imagen
                    FirebaseUtils.descargarFotoImageView(fotoPrincipal, imageViewFotoPrincipal);
                }

            }
        }).start();

        constraintLayoutAdoptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                tipo_solicitud = "Adopción";

                revisarSolicitudesActivas();
            }
        });

        constraintLayoutApadrinar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                tipo_solicitud = "Apadrinamiento";

                revisarSolicitudesActivas();
            }
        });

        constraintLayoutDonar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                tipo_solicitud = "Donación";

                revisarSolicitudesActivas();
            }
        });

    }

    private void revisarSolicitudesActivas(){

        db.collection("solicitudes")
                .whereEqualTo("idPersona", currentUser.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            boolean b = true;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                if ( document.getString("tipo").equals(tipo_solicitud) &&
                                        document.getBoolean("estado") == true &&
                                        document.getString("idAnimal").equals(id_animal) ){
                                    b = false;
                                }
                                /*if( tipo_solicitud.equals("Apadrinamiento") ){
                                    if( document.getBoolean("aceptada") == true ){
                                        c = false;
                                    }
                                }*/
                            }
                            if(b == true){
                                switch(tipo_solicitud) {
                                    case "Adopción":
                                        SolicitarAdopcion();
                                        break;
                                    case "Apadrinamiento":
                                        SolicitarApadrinamiento();
                                        break;
                                    case "Donación":
                                        SolicitarDonacion();
                                        break;
                                }
                            }else{
                                Toast.makeText(ActivityPerfilAnimal.this, "Ya tienes " +
                                                "una solicitud de " + tipo_solicitud + " activa para " +
                                                "este animalito. No se puede registrar una nueva.",
                                            Toast.LENGTH_LONG).show();
                                }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void SolicitarDonacion(){
        Intent intent = new Intent(ActivityPerfilAnimal.this, ActivityHacerSolicitud.class);
        intent.putExtra("tipo_solicitud", "Donación");

        intent.putExtra("id_animal", id_animal);
        intent.putExtra("id_institucion", id_institucion);
        intent.putExtra("nombre_institucion", nombre_institucion);
        intent.putExtra("nombre_animal", nombre_animal);
        intent.putExtra("foto_url", foto_url);

        startActivity(intent);
    }

    private void SolicitarAdopcion(){
        Intent intent = new Intent(ActivityPerfilAnimal.this, ActivityHacerSolicitud.class);
        intent.putExtra("tipo_solicitud", "Adopción");

        intent.putExtra("id_animal", id_animal);
        intent.putExtra("id_institucion", id_institucion);
        intent.putExtra("nombre_institucion", nombre_institucion);
        intent.putExtra("nombre_animal", nombre_animal);
        intent.putExtra("foto_url", foto_url);

        startActivity(intent);
    }

    private void SolicitarApadrinamiento(){
        Intent intent = new Intent(ActivityPerfilAnimal.this, ActivityHacerSolicitud.class);
        intent.putExtra("tipo_solicitud", "Apadrinamiento");

        intent.putExtra("id_animal", id_animal);
        intent.putExtra("id_institucion", id_institucion);
        intent.putExtra("nombre_institucion", nombre_institucion);
        intent.putExtra("nombre_animal", nombre_animal);
        intent.putExtra("foto_url", foto_url);

        startActivity(intent);
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
