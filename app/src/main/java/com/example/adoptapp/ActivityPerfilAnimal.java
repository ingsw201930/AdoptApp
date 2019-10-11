package com.example.adoptapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class ActivityPerfilAnimal extends AppCompatActivity {

    TextView textViewPrueba;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_animal);

        textViewPrueba = findViewById(R.id.textViewPerfil);

        Intent intent = getIntent();
        String perfil = intent.getStringExtra("Perfil");
        textViewPrueba.setText(perfil);
    }
}
