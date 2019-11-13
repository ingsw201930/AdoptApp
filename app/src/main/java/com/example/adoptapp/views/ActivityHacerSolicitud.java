package com.example.adoptapp.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adoptapp.R;
import com.example.adoptapp.model.Solicitud;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;
import java.util.Date;

public class ActivityHacerSolicitud extends AppCompatActivity {

    private EditText et_descripcion;
    private EditText et_monto;
    private TextView tv_titulo_descripcion;
    private TextView tv_titulo_monto;
    private TextView tv_titulo;
    private Button btn_hacer_solicitud;

    final String TAG = "actividad ha solicitud";

    private String id_animal;
    private String id_institucion;
    private String id_solicitante;
    private String tipo_solicitud;
    private String nombre_institucion;
    private String nombre_animal;
    private String foto_url;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hacer_solicitud);

        tv_titulo = findViewById(R.id.tv_titulo_hacer_solicitud);
        et_descripcion = findViewById(R.id.et_descripcion_hacer_solicitud);
        et_monto = findViewById(R.id.et_dinero_hacer_solicitud);
        tv_titulo_monto = findViewById(R.id.tv_dinero_hacer_solicitud);
        tv_titulo_descripcion = findViewById(R.id.tv_descripcion_hacer_solicitud);
        btn_hacer_solicitud = findViewById(R.id.btn_hacer_solicitud);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        Intent intent = getIntent();

        id_animal = intent.getStringExtra("id_animal");
        id_institucion = intent.getStringExtra("id_institucion");
        id_solicitante = currentUser.getUid();
        nombre_institucion = intent.getStringExtra("nombre_institucion");
        nombre_animal = intent.getStringExtra("nombre_animal");
        foto_url = intent.getStringExtra("foto_url");

        tipo_solicitud = intent.getStringExtra("tipo_solicitud");

        switch(tipo_solicitud) {
            case "Adopción":
                tv_titulo.setText("Realizar solicitud adopción");
                tv_titulo_descripcion.setText("Escribe por qué quieres adoptarlo(a)");
                et_monto.setVisibility(View.GONE);
                tv_titulo_monto.setVisibility(View.GONE);
                break;
            case "Apadrinamiento":
                tv_titulo.setText("Realizar solicitud apadrinar");
                tv_titulo_descripcion.setText("Escribe sobre tu solicitud");
                break;
            case "Donación":
                tv_titulo.setText("Ofrecer donación");
                tv_titulo_descripcion.setText("Escribe en qué consiste la donación");
                et_monto.setVisibility(View.GONE);
                tv_titulo_monto.setVisibility(View.GONE);
                break;
        }

        btn_hacer_solicitud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean b = true;
                switch(tipo_solicitud) {
                    case "Adopción":
                        if (et_descripcion.getText().toString().isEmpty()){
                            et_descripcion.setError("Campo obligatorio");
                            b = false;
                        }
                        break;
                    case "Apadrinamiento":
                        if (et_descripcion.getText().toString().isEmpty()){
                            et_descripcion.setError("Campo obligatorio");
                            b = false;
                        }
                        if (et_monto.getText().toString().isEmpty()){
                            et_monto.setError("Campo obligatorio");
                            b = false;
                        }
                        break;
                    case "Donación":
                        if (et_descripcion.getText().toString().isEmpty()){
                            et_descripcion.setError("Campo obligatorio");
                            b = false;
                        }
                        break;
                }
                if (b == true){
                    btn_hacer_solicitud.setEnabled(false);
                    lanzarDialogo();
                }
            }
        });

    }

    private void lanzarDialogo(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Confirmar");
        builder.setMessage("¿Estás seguro de hacer la solicitud?");

        builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {

                registrarSolicitud();

                // Do nothing, but close the dialog
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                btn_hacer_solicitud.setEnabled(true);
                // Do nothing
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();

    }

    private void registrarSolicitud(){

        Solicitud nuevaSolicitud = new Solicitud();
        nuevaSolicitud.setDescripcion(et_descripcion.getText().toString());
        nuevaSolicitud.setEstado(true);
        nuevaSolicitud.setIdAnimal(id_animal);
        nuevaSolicitud.setIdInstitucion(id_institucion);
        nuevaSolicitud.setIdPersona(id_solicitante);
        Date dateObj = Calendar.getInstance().getTime();
        nuevaSolicitud.setFecha(dateObj);
        nuevaSolicitud.setTipo(tipo_solicitud);
        if(tipo_solicitud.equals("Apadrinamiento")){
            nuevaSolicitud.setMonto( Long.parseLong( et_monto.getText().toString() ) );
        }
        nuevaSolicitud.setAceptada(false);
        nuevaSolicitud.setNombreAnimal(nombre_animal);
        nuevaSolicitud.setNombreInstitucion(nombre_institucion);
        nuevaSolicitud.setFotoUrl(foto_url);
        nuevaSolicitud.setNombrePersona(currentUser.getDisplayName());

        //buscar un nuevo id en FireStore
        DocumentReference referencia = db.collection("solicitudes").document();

        referencia
                .set(nuevaSolicitud)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                        Toast.makeText(ActivityHacerSolicitud.this, "Solicitud registrada con éxito",
                                Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                        Toast.makeText(ActivityHacerSolicitud.this, "Falló el registro de solicitud",
                                Toast.LENGTH_SHORT).show();
                        btn_hacer_solicitud.setEnabled(true);
                    }
                });
    }
}
