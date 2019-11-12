package com.example.adoptapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class ActivityTipoRegistro extends AppCompatActivity {


    ConstraintLayout constraintLayoutAdoptante;
    ConstraintLayout constraintLayoutHogarPaso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tipo_registro);

        constraintLayoutAdoptante = findViewById(R.id.ConstraintLayoutAdoptante);
        constraintLayoutHogarPaso = findViewById(R.id.ConstraintLayoutHogarPaso);

        constraintLayoutAdoptante.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ActivityRegistrarPersona.class);
                startActivity(intent);
            }
        });

        constraintLayoutHogarPaso.setOnClickListener (new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ActivityRegistrarFundacion.class);
                startActivity(intent);
            }
        });
    }
}
