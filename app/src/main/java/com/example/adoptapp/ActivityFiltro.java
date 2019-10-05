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
    EditText editTextFiltroDistancia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtro);

        buttonAplicarFiltro = findViewById(R.id.buttonAplicarFiltro);
        spinnerFiltroTipo = findViewById(R.id.spinnerFiltroTipo);
        spinnerFiltroTamano = findViewById(R.id.spinnerFiltroTamano);
        editTextFiltroEdad = findViewById(R.id.editTextFiltroEdad);
        editTextFiltroDistancia = findViewById(R.id.editTextFiltroDistancia);

        buttonAplicarFiltro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent returnIntent = new Intent();
                int numeroFiltrosAplicados = 0;

                if(!spinnerFiltroTipo.getSelectedItem().toString().equals("Tipo de animal")){
                    returnIntent.putExtra("Tipo", spinnerFiltroTipo.getSelectedItem().toString());
                    numeroFiltrosAplicados = numeroFiltrosAplicados+1;
                }else{
                    returnIntent.putExtra("Tipo", "");
                }

                if(!spinnerFiltroTamano.getSelectedItem().toString().equals("Tamaño de animal")){
                    returnIntent.putExtra("Tamano", spinnerFiltroTamano.getSelectedItem().toString());
                    numeroFiltrosAplicados = numeroFiltrosAplicados+1;
                }else{
                    returnIntent.putExtra("Tamano", "");
                }

                if(!editTextFiltroEdad.getText().toString().equals("")){
                    returnIntent.putExtra("Edad", editTextFiltroEdad.getText().toString());
                    numeroFiltrosAplicados = numeroFiltrosAplicados+1;
                }else{
                    returnIntent.putExtra("Edad", -1);
                }

                if(!editTextFiltroDistancia.getText().toString().equals("")){
                    returnIntent.putExtra("Distancia", editTextFiltroDistancia.getText().toString());
                    numeroFiltrosAplicados = numeroFiltrosAplicados+1;
                }else{
                    returnIntent.putExtra("Distancia", -1);
                }

                returnIntent.putExtra("numeroFiltrosAplicados", numeroFiltrosAplicados);

                setResult(Activity.RESULT_OK,returnIntent);
                finish();

                /*Intent returnIntent = new Intent();
                setResult(Activity.RESULT_CANCELED, returnIntent);
                finish();*/

            }
        });

    }


}
