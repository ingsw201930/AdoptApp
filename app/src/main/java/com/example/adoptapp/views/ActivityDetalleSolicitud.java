package com.example.adoptapp.views;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.adoptapp.R;
import com.example.adoptapp.model.Solicitud;
import com.example.adoptapp.utils.FirebaseUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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

    }
}
