package com.example.adoptapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;

public class ActivityFiltro extends AppCompatActivity {

    Button buttonAplicarFiltro;
    Spinner spinnerFiltroTipo;
    Spinner spinnerFiltroTamano;
    EditText editTextFiltroEdad;
    EditText editTextFiltroDistancia;

    CheckBox checkBoxAlegre;
    CheckBox checkBoxCalmado;
    CheckBox checkBoxJugueton;
    CheckBox checkBoxComelon;
    CheckBox checkBoxTimido;
    CheckBox checkBoxAnsioso;
    CheckBox checkBoxEnergetico;
    CheckBox checkBoxFuerte;
    CheckBox checkBoxEmpatico;
    CheckBox checkBoxNinos;
    CheckBox checkBoxDestructivo;
    CheckBox checkBoxAgresivo;
    CheckBox checkBoxAmoroso;
    CheckBox checkBoxIndependiente;
    CheckBox checkBoxNervioso;
    CheckBox checkBoxDominante;
    CheckBox checkBoxLeal;

    CheckBox checkBoxNecesidades;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtro);

        buttonAplicarFiltro = findViewById(R.id.buttonAplicarFiltro);
        spinnerFiltroTipo = findViewById(R.id.spinnerFiltroTipo);
        spinnerFiltroTamano = findViewById(R.id.spinnerFiltroTamano);
        editTextFiltroEdad = findViewById(R.id.editTextFiltroEdad);
        editTextFiltroDistancia = findViewById(R.id.editTextFiltroDistancia);

        checkBoxAlegre = findViewById(R.id.checkBoxAlegre);
        checkBoxCalmado = findViewById(R.id.checkBoxCalmado);
        checkBoxJugueton = findViewById(R.id.checkBoxJugueton);
        checkBoxComelon = findViewById(R.id.checkBoxComelon);
        checkBoxTimido = findViewById(R.id.checkBoxTimido);
        checkBoxAnsioso = findViewById(R.id.checkBoxAnsioso);
        checkBoxEnergetico = findViewById(R.id.checkBoxEnergetico);
        checkBoxFuerte = findViewById(R.id.checkBoxFuerte);
        checkBoxEmpatico = findViewById(R.id.checkBoxEmpatico);
        checkBoxNinos = findViewById(R.id.checkBoxNinos);
        checkBoxDestructivo = findViewById(R.id.checkBoxDestructivo);
        checkBoxAgresivo = findViewById(R.id.checkBoxAgresivo);
        checkBoxAmoroso = findViewById(R.id.checkBoxAmoroso);
        checkBoxIndependiente = findViewById(R.id.checkBoxIndependiente);
        checkBoxNervioso = findViewById(R.id.checkBoxNervioso);
        checkBoxDominante = findViewById(R.id.checkBoxDominante);
        checkBoxLeal = findViewById(R.id.checkBoxLeal);

        checkBoxNecesidades = findViewById(R.id.checkBoxNecesidadesEspeciales);

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
                    returnIntent.putExtra("Edad", Integer.parseInt(editTextFiltroEdad.getText().toString()) );
                    numeroFiltrosAplicados = numeroFiltrosAplicados+1;
                }else{
                    returnIntent.putExtra("Edad", -1);
                }

                if(!editTextFiltroDistancia.getText().toString().equals("")){
                    returnIntent.putExtra("Distancia", Double.parseDouble(editTextFiltroDistancia.getText().toString()) );
                    numeroFiltrosAplicados = numeroFiltrosAplicados+1;
                }else{
                    returnIntent.putExtra("Distancia", -1.0);
                }

                ArrayList<String> listaDescriptores = new ArrayList<>();

                if(checkBoxAlegre.isChecked()){
                    listaDescriptores.add("Alegre");
                }
                if(checkBoxCalmado.isChecked()){
                    listaDescriptores.add("Calmado");
                }
                if(checkBoxJugueton.isChecked()){
                    listaDescriptores.add("Jugueton");
                }
                if(checkBoxComelon.isChecked()){
                    listaDescriptores.add("Comelon");
                }
                if(checkBoxTimido.isChecked()){
                    listaDescriptores.add("Timido");
                }
                if(checkBoxAnsioso.isChecked()){
                    listaDescriptores.add("Ansioso");
                }
                if(checkBoxEnergetico.isChecked()){
                    listaDescriptores.add("Energetico");
                }
                if(checkBoxFuerte.isChecked()){
                    listaDescriptores.add("Fuerte");
                }
                if(checkBoxEmpatico.isChecked()){
                    listaDescriptores.add("Empatico");
                }
                if(checkBoxNinos.isChecked()){
                    listaDescriptores.add("Ninos");
                }
                if(checkBoxDestructivo.isChecked()){
                    listaDescriptores.add("Destructivo");
                }
                if(checkBoxAgresivo.isChecked()){
                    listaDescriptores.add("Agresivo");
                }
                if(checkBoxAmoroso.isChecked()){
                    listaDescriptores.add("Amoroso");
                }
                if(checkBoxIndependiente.isChecked()){
                    listaDescriptores.add("Independiente");
                }
                if(checkBoxNervioso.isChecked()){
                    listaDescriptores.add("Nervioso");
                }
                if(checkBoxDominante.isChecked()){
                    listaDescriptores.add("Dominante");
                }
                if(checkBoxLeal.isChecked()){
                    listaDescriptores.add("Leal");
                }

                if(checkBoxNecesidades.isChecked()){
                    listaDescriptores.add("Necesidades");
                }

                returnIntent.putExtra("numeroFiltrosAplicados", numeroFiltrosAplicados);
                returnIntent.putStringArrayListExtra("listaDescriptores",listaDescriptores);

                setResult(Activity.RESULT_OK,returnIntent);
                finish();

                /*Intent returnIntent = new Intent();
                setResult(Activity.RESULT_CANCELED, returnIntent);
                finish();*/

            }
        });

    }


}
