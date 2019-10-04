package com.example.adoptapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ActivityLogin extends AppCompatActivity {

    Button botonIngresar;
    EditText editTextEmail;
    EditText editTextContrasena;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        botonIngresar = findViewById(R.id.buttonIngresarLogin);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextContrasena = findViewById(R.id.editTextContrasena);

        botonIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if( editTextEmail.getText().toString().equals("persona") ){
                    Intent intent = new Intent(view.getContext(), ActivityInicioPersona.class);
                    startActivity(intent);
                }
                if( editTextEmail.getText().toString().equals("institucion") ){
                    Intent intent = new Intent(view.getContext(), ActivityMenuKeeper.class);
                    startActivity(intent);
                }

            }
        });

    }
}
