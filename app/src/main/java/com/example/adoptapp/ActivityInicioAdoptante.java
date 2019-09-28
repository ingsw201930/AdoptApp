package com.example.adoptapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.view.View;

public class ActivityInicioAdoptante extends AppCompatActivity {

    ConstraintLayout constraintLayoutAdoptante;
    ConstraintLayout constraintLayoutHogarPaso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_adoptante);

        constraintLayoutAdoptante = findViewById(R.id.ConstraintLayoutAdoptante);
        constraintLayoutHogarPaso = findViewById(R.id.ConstraintLayoutHogarPaso);

        constraintLayoutAdoptante.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        constraintLayoutHogarPaso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }
}
