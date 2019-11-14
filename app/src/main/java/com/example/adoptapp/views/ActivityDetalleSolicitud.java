package com.example.adoptapp.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;

public class ActivityDetalleSolicitud extends AppCompatActivity {

    private Solicitud solicitud;

    private TextView textViewNombre;
    private TextView textViewDescripcion;
    private ImageView imageViewFotoPrincipal;
    private Button btn_aceptar;
    private Button btn_rechazar;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private FirebaseFirestore db;

    private String decision;

    private String TAG = "Detalle solicitud";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_solicitud);

        textViewNombre = findViewById(R.id.tv_perfil_nombre_solicitud);
        textViewDescripcion = findViewById(R.id.tv_perfil_datos_solicitud);
        imageViewFotoPrincipal = findViewById(R.id.iv_perfil_foto_solicitud);
        btn_aceptar = findViewById(R.id.btn_aceptar_solicitud);
        btn_rechazar = findViewById(R.id.btn_rechazar_solicitud);

        solicitud = (Solicitud) getIntent().getSerializableExtra("solicitud");

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        String fechaPublicacion = new SimpleDateFormat("dd/MM/yyyy").
                format(solicitud.getFecha());
        String datosSolicitud = "Realizada: " + fechaPublicacion+"\n"+
                "Por: "+solicitud.getNombrePersona();
        if(solicitud.getTipo().equals("Adopción") || solicitud.getTipo().equals("Apadrinamiento") ||
                solicitud.getTipo().equals("Donación")){
            datosSolicitud = datosSolicitud+"\nPara: "+solicitud.getNombreAnimal();
        }
        if(solicitud.getTipo().equals("Apadrinamiento")){
            datosSolicitud = datosSolicitud+"\nMonto mensual ofrecido: "+solicitud.getMonto();
        }
        datosSolicitud = datosSolicitud + "\n\nDescripción:\n"+solicitud.getDescripcion();

        textViewNombre.setText("Solicitud de "+solicitud.getTipo());
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

}
