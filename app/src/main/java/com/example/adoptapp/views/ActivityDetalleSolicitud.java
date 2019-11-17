package com.example.adoptapp.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Guideline;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adoptapp.R;
import com.example.adoptapp.model.Solicitud;
import com.example.adoptapp.utils.FirebaseUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ActivityDetalleSolicitud extends AppCompatActivity {

    private Solicitud solicitud;

    private TextView textViewNombre;
    private TextView textViewDescripcion;
    private ImageView imageViewFotoPrincipal;
    private Button btn_aceptar;
    private Button btn_rechazar;
    private Button btn_perfil_solicitante;
    private Button btn_proceder_formalizacion;
    private Guideline guidelineBotones;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private FirebaseFirestore db;

    private String decision;

    private String TAG = "Detalle solicitud";

    private ArrayList<String> documentosActualizar;

    private String tipoSolicitud;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_solicitud);

        textViewNombre = findViewById(R.id.tv_perfil_nombre_solicitud);
        textViewDescripcion = findViewById(R.id.tv_perfil_datos_solicitud);
        imageViewFotoPrincipal = findViewById(R.id.iv_perfil_foto_solicitud);
        btn_aceptar = findViewById(R.id.btn_aceptar_solicitud);
        btn_rechazar = findViewById(R.id.btn_rechazar_solicitud);
        btn_perfil_solicitante = findViewById(R.id.btn_perfil_solicitante);
        btn_proceder_formalizacion = findViewById(R.id.btn_proceder_formalizacion);
        guidelineBotones = findViewById(R.id.guideline3);

        btn_proceder_formalizacion.setVisibility(View.GONE);

        solicitud = (Solicitud) getIntent().getSerializableExtra("solicitud");

        Intent intent = getIntent();
        tipoSolicitud = intent.getStringExtra("tipoSolicitud");

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        textViewNombre.setText("Solicitud de "+solicitud.getTipo());

        if(tipoSolicitud.equals("aceptada")){
            btn_aceptar.setVisibility(View.GONE);
            btn_rechazar.setVisibility(View.GONE);
            guidelineBotones.setGuidelinePercent(1);
            if( solicitud.getTipo().equals("Adopción") ){
                confirmarFormalizacionAdopcion();
            }else{
                btn_proceder_formalizacion.setText(R.string.marcar_hecho);
                confirmarFormalizacionOtras();
            }
        }

        String fechaPublicacion = new SimpleDateFormat("dd/MM/yyyy").
                format(solicitud.getFecha());
        String datosSolicitud = "Realizada: " + fechaPublicacion+"\n"+
                "Por: "+solicitud.getNombrePersona();
        if(solicitud.getTipo().equals("Adopción") || solicitud.getTipo().equals("Apadrinamiento") ||
                solicitud.getTipo().equals("Donación")){
            datosSolicitud = datosSolicitud+"\nPara: "+solicitud.getNombreAnimal();
        }
        if(solicitud.getTipo().equals("DonaciónAInstitución") || solicitud.getTipo().equals("Voluntariado") ){
            datosSolicitud = datosSolicitud+"\nPara: "+solicitud.getNombreInstitucion();
        }
        if(solicitud.getTipo().equals("Apadrinamiento")){
            datosSolicitud = datosSolicitud+"\nMonto mensual ofrecido: "+solicitud.getMonto();
        }
        datosSolicitud = datosSolicitud + "\n\nDescripción:\n"+solicitud.getDescripcion();

        textViewDescripcion.setText(datosSolicitud);

        new Thread(new Runnable() {
            public void run() {
                // a potentially time consuming task
                if (solicitud.getFotoUrl() != null && !solicitud.getFotoUrl().equals("")) {
                    //descargar la imagen
                    FirebaseUtils.descargarFotoImageView(solicitud.getFotoUrl(), imageViewFotoPrincipal);
                }
            }
        }).start();

        btn_aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_aceptar.setEnabled(false);
                btn_rechazar.setEnabled(false);
                decision = "aceptar";
                lanzarDialogo();
            }
        });

        btn_rechazar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_aceptar.setEnabled(false);
                btn_rechazar.setEnabled(false);
                decision = "rechazar";
                lanzarDialogo();
            }
        });

        btn_proceder_formalizacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //confirmarFormalizacionAdopcion();
                btn_proceder_formalizacion.setVisibility(View.GONE);
                if( solicitud.getTipo().equals("Adopción") ) {
                    Intent intent = new Intent(ActivityDetalleSolicitud.this,
                            ActivityFormalizarAdopcion.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("solicitud", solicitud);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }else{
                    actualizarEstadoOtras();
                }
            }
        });

        btn_perfil_solicitante.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hacerBusquedaSolicitante();
            }
        });

    }

    private void lanzarDialogo(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Confirmar");
        if (decision.equals("aceptar")){
            builder.setMessage("¿Estás seguro de aceptar la solicitud?");
        }else{
            builder.setMessage("¿Estás seguro de rechazar la solicitud?");
        }

        builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {

                if (decision.equals("aceptar")){
                    aceptarSolicitud();
                }else{
                    rechazarSolicitud();
                }

                // Do nothing, but close the dialog
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                btn_aceptar.setEnabled(true);
                btn_rechazar.setEnabled(true);
                // Do nothing
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();

    }

    private void aceptarSolicitud(){

        DocumentReference referencia = db.collection("solicitudes").document(solicitud.getId());

        referencia
                .update("aceptada", true)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");

                        aceptarSolicitud2();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                        Toast.makeText(ActivityDetalleSolicitud.this, "Ocurrió un problema" +
                                "intentando aceptar la solicitud. Intentalo de nuevo", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void aceptarSolicitud2(){

        DocumentReference referencia = db.collection("solicitudes").document(solicitud.getId());

        referencia
                .update("estado", false)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");

                        actualizarEstadoAnimal();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                        Toast.makeText(ActivityDetalleSolicitud.this, "Ocurrió un problema" +
                                "intentando aceptar la solicitud. Intentalo de nuevo", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void actualizarEstadoAnimal(){

        if(solicitud.getTipo().equals("Adopción")){

            DocumentReference referencia = db.collection("animales").document(solicitud.getIdAnimal());

            referencia
                    .update("Estado", "En proceso")
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "DocumentSnapshot successfully updated!");

                            buscarOtrasSolicitudes();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error updating document", e);
                            Toast.makeText(ActivityDetalleSolicitud.this, "Ocurrió un problema" +
                                    "intentando aceptar la solicitud. Intentalo de nuevo", Toast.LENGTH_SHORT).show();
                        }
                    });

        }else{
            finish();
        }
    }

    private void buscarOtrasSolicitudes(){

        documentosActualizar = new ArrayList<>();

        Query query = db.collection("solicitudes")
                .whereEqualTo("idInstitucion", currentUser.getUid())
                .whereEqualTo("estado", true)
                .whereEqualTo("tipo", solicitud.getTipo())
                .whereEqualTo("idAnimal", solicitud.getIdAnimal());

        query
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Bien buscando documentos");
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Solicitud solicitud = document.toObject(Solicitud.class);
                                documentosActualizar.add(solicitud.getId());
                            }
                            actualizarOtrasSolicitudes();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void actualizarOtrasSolicitudes(){

        if (documentosActualizar.size()>0) {

            // Get a new write batch
            WriteBatch batch = db.batch();

            String aux;
            for (int i = 0; i < documentosActualizar.size(); i++) {

                aux = documentosActualizar.get(i);

                DocumentReference sfRef = db.collection("solicitudes").document(aux);
                batch.update(sfRef, "estado", false);

            }

            // Commit the batch

            batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(ActivityDetalleSolicitud.this, "Solicitud aceptada " +
                                "con éxito", Toast.LENGTH_SHORT).show();
                        finish();
                    }else{
                        Toast.makeText(ActivityDetalleSolicitud.this, "Ocurrió un problema, " +
                                "vuelve a intentarlo", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }else{
            Toast.makeText(ActivityDetalleSolicitud.this, "Solicitud aceptada " +
                    "con éxito", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void rechazarSolicitud(){

        DocumentReference referencia = db.collection("solicitudes").document(solicitud.getId());

        referencia
                .update("estado", false)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                        Toast.makeText(ActivityDetalleSolicitud.this, "Solicitud rechazada" +
                                "con éxito", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                        Toast.makeText(ActivityDetalleSolicitud.this, "Ocurrió un problema" +
                                "intentando rechazar la solicitud. Intentalo de nuevo", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void hacerBusquedaSolicitante(){

        db.collection("personas").document(solicitud.getIdPersona())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {

                                String nombre = document.getString("Nombre");
                                String descripcion = document.getString("Descripcion");
                                Long telefono = document.getLong("Telefono");
                                String direccion = document.getString("Direccion");
                                String ciudad = document.getString("Ciudad");
                                String genero = document.getString("Genero");
                                String fotoUrl = document.getString("fotoUrl");

                                Log.d("TAG", "Se obtuvo documento de "+document.getString("Nombre"));

                                Intent intent = new Intent(ActivityDetalleSolicitud.this, ActivityPerfilPersona.class);
                                intent.putExtra("nombre", nombre);
                                intent.putExtra("descripcion", descripcion);
                                intent.putExtra("telefono", telefono);
                                intent.putExtra("direccion", direccion);
                                intent.putExtra("ciudad", ciudad);
                                intent.putExtra("genero", genero);
                                intent.putExtra("fotoUrl", fotoUrl);

                                startActivity(intent);
                            }
                        }
                    }
                });
    }

    private void confirmarFormalizacionAdopcion(){
        Query query = db.collection("adopciones")
                .whereEqualTo("idSolicitud", solicitud.getId());

        query
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int contadorDocumentos = 0;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                contadorDocumentos = contadorDocumentos+1;
                                Log.d(TAG, "Encontrado documento: "+document.getData().toString());
                            }
                            if(contadorDocumentos == 0){
                                textViewNombre.setText("Solicitud de "+solicitud.getTipo()+" no formalizada");
                                btn_proceder_formalizacion.setVisibility(View.VISIBLE);
                            }else{
                                textViewNombre.setText("Solicitud de "+solicitud.getTipo()+" ya formalizada");
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void confirmarFormalizacionOtras(){
        Query query = db.collection("solicitudes")
                .whereEqualTo("id", solicitud.getId())
                .whereEqualTo("formalizada", true);

        query
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int contadorDocumentos = 0;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                contadorDocumentos = contadorDocumentos+1;
                                Log.d(TAG, "Encontrado documento: "+document.getData().toString());
                            }
                            if(contadorDocumentos == 0){
                                textViewNombre.setText("Solicitud de "+solicitud.getTipo()+" no confirmada");
                                btn_proceder_formalizacion.setVisibility(View.VISIBLE);
                            }else{
                                textViewNombre.setText("Solicitud de "+solicitud.getTipo()+" ya confirmada");
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void actualizarEstadoOtras(){

        DocumentReference referencia = db.collection("solicitudes").document(solicitud.getId());

        referencia
                .update("formalizada", true)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                        Toast.makeText(ActivityDetalleSolicitud.this, "Solicitud actualizada" +
                                "con éxito", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                        Toast.makeText(ActivityDetalleSolicitud.this, "Ocurrió un problema" +
                                "intentando rechazar la solicitud. Intentalo de nuevo", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
