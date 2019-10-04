package com.example.adoptapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class ActivityFiltro extends AppCompatActivity {

    Button buttonAplicarFiltro;
    Spinner spinnerFiltroTipo;
    Spinner spinnerFiltroTamano;
    EditText editTextFiltroEdad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtro);

        buttonAplicarFiltro = findViewById(R.id.buttonAplicarFiltro);
        spinnerFiltroTipo = findViewById(R.id.spinnerFiltroTipo);
        spinnerFiltroTamano = findViewById(R.id.spinnerFiltroTamano);
        editTextFiltroEdad = findViewById(R.id.editTextFiltroEdad);

        buttonAplicarFiltro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String filtro = "la edad del animal es "+editTextFiltroEdad.getText().toString();

                Intent returnIntent = new Intent();
                returnIntent.putExtra("result",filtro);
                setResult(Activity.RESULT_OK,returnIntent);
                finish();

                /*Intent returnIntent = new Intent();
                setResult(Activity.RESULT_CANCELED, returnIntent);
                finish();*/

            }
        });

    }


}
