package com.example.adoptapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;

public class ActivityRegistrarPersona extends AppCompatActivity {

    private EditText et_nombre;
    private EditText et_apellido;
    private EditText et_email;
    private EditText et_cedula;
    private EditText et_celular;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_persona);
    }
}
