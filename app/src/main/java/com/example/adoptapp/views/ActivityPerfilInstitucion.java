package com.example.adoptapp.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adoptapp.R;
import com.example.adoptapp.model.Usuario;
import com.example.adoptapp.utils.FirebaseUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.Normalizer;

public class ActivityPerfilInstitucion extends AppCompatActivity {

    private TextView textViewNombre;
    private TextView textViewDescripcion;
    private ImageView imageViewFotoPrincipal;
    private ConstraintLayout constraintLayoutDonar;
    private ConstraintLayout constraintLayoutVoluntariado;
    private ConstraintLayout constraintLayoutChat;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;

    private String TAG = "ActivityPerfilInstitucion";

    private String tipo_solicitud;

    private String nombre;
    private String direccionFoto;
    private String idInstitucion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_institucion);

        textViewNombre = findViewById(R.id.tv_perfil_nombre_fundacion);
        textViewDescripcion = findViewById(R.id.tv_perfil_datos_fundacion);
        imageViewFotoPrincipal = findViewById(R.id.iv_perfil_foto_fundacion);
        constraintLayoutDonar = findViewById(R.id.cl_donar_fundacion);
        constraintLayoutVoluntariado = findViewById(R.id.cl_voluntariado);
        constraintLayoutChat = findViewById(R.id.cl_chat_con_fundacion);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();


        Intent intent = getIntent();
        nombre = intent.getStringExtra("Nombre");
        final String fotoPrincipal = intent.getStringExtra("Foto_principal");
        direccionFoto = fotoPrincipal;
        String descripcion = intent.getStringExtra("Descripcion");
        idInstitucion = intent.getStringExtra("idInstitucion");

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

        constraintLayoutDonar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tipo_solicitud = "DonaciónAInstitución";
                revisarSolicitudesActivas();
            }
        });

        constraintLayoutVoluntariado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tipo_solicitud = "Voluntariado";
                revisarSolicitudesActivas();
            }
        });

        constraintLayoutChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tipo_solicitud = "Chat";
                iniciarChat();
            }
        });

    }

    private void revisarSolicitudesActivas(){

        db.collection("solicitudes")
                .whereEqualTo("idInstitucion", idInstitucion )
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            boolean b = true;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                if ( document.getString("tipo").equals(tipo_solicitud) &&
                                        document.getBoolean("estado") == true ){
                                    b = false;
                                }

                            }
                            if(b == true){
                                switch(tipo_solicitud) {
                                    case "Voluntariado":
                                        SolicitarVoluntariado();
                                        break;
                                    case "DonaciónAInstitución":
                                        SolicitarDonacion();
                                        break;
                                }
                            }else{
                                Toast.makeText(ActivityPerfilInstitucion.this, "Ya tienes " +
                                                "una solicitud de " + tipo_solicitud + " activa para " +
                                                "esta institución. No se puede registrar una nueva.",
                                        Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void SolicitarDonacion(){
        Intent intent = new Intent(ActivityPerfilInstitucion.this, ActivityHacerSolicitud.class);
        intent.putExtra("tipo_solicitud", "DonaciónAInstitución");

        String idAnimal = null, nombreAnimal = null;

        intent.putExtra("id_animal", idAnimal);
        intent.putExtra("id_institucion", idInstitucion);
        intent.putExtra("nombre_institucion", nombre);
        intent.putExtra("nombre_animal", nombreAnimal);
        intent.putExtra("foto_url", direccionFoto);

        startActivity(intent);
    }

    private void SolicitarVoluntariado(){
        Intent intent = new Intent(ActivityPerfilInstitucion.this, ActivityHacerSolicitud.class);
        intent.putExtra("tipo_solicitud", "Voluntariado");

        String idAnimal = null, nombreAnimal = null;

        intent.putExtra("id_animal", idAnimal);
        intent.putExtra("id_institucion", idInstitucion);
        intent.putExtra("nombre_institucion", nombre);
        intent.putExtra("nombre_animal", nombreAnimal);
        intent.putExtra("foto_url", direccionFoto);

        startActivity(intent);
    }

    private void iniciarChat()
    {
        nombre = nombre.replaceAll("\\s","");
        nombre = Normalizer.normalize(nombre,Normalizer.Form.NFKD);
        nombre = nombre.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");

        Usuario.chatWith = nombre;
        Log.println(Log.ERROR,"bandera",nombre.replaceAll("\\s",""));
        Intent intent = new Intent(ActivityPerfilInstitucion.this, Chat.class);

        startActivity(intent);
    }
}
