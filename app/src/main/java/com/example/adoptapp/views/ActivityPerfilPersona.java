package com.example.adoptapp.views;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.adoptapp.R;
import com.example.adoptapp.utils.FirebaseUtils;

public class ActivityPerfilPersona extends AppCompatActivity {

    private TextView textViewNombre;
    private TextView textViewDescripcion;
    private ImageView imageViewFoto;
    private Button btn_iniciar_chat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_persona);

        textViewNombre = findViewById(R.id.tv_perfil_nombre_persona);
        textViewDescripcion = findViewById(R.id.tv_perfil_datos_persona);
        imageViewFoto = findViewById(R.id.iv_perfil_foto_persona);
        btn_iniciar_chat = findViewById(R.id.btn_chat_con_persona);

        String nombre = getIntent().getStringExtra("nombre");
        String descripcion = getIntent().getStringExtra("descripcion");
        String genero = getIntent().getStringExtra("genero");
        String ciudad = getIntent().getStringExtra("ciudad");
        String direccion = getIntent().getStringExtra("direccion");
        Long telefono = getIntent().getLongExtra("telefono", 0);
        final String fotoUrl = getIntent().getStringExtra("fotoUrl");

        textViewNombre.setText(nombre);
        String descripcionCompleta = "Género: "+genero+"\n\n"+
                "Ciudad: "+ciudad+"\n\n"+
                "Dirección: "+direccion+"\n\n"+
                "Teléfono: "+direccion+"\n\n"+
                "Descripción: "+descripcion+"\n\n";
        textViewDescripcion.setText(descripcionCompleta);

        new Thread(new Runnable() {
            public void run() {
                // a potentially time consuming task
                if (fotoUrl != null && !fotoUrl.equals("")) {
                    //descargar la imagen
                    FirebaseUtils.descargarFotoImageView(fotoUrl, imageViewFoto);
                }
            }
        }).start();

        btn_iniciar_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}
