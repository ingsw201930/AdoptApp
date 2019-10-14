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

                if(checkBoxAlegre.isChecked() == true){
                    listaDescriptores.add("Alegre");
                }
                if(checkBoxCalmado.isChecked() == true){
                    listaDescriptores.add("Calmado");
                }
                if(checkBoxJugueton.isChecked() == true){
                    listaDescriptores.add("Jugueton");
                }
                if(checkBoxComelon.isChecked() == true){
                    listaDescriptores.add("Comelon");
                }
                if(checkBoxTimido.isChecked() == true){
                    listaDescriptores.add("Timido");
                }
                if(checkBoxAnsioso.isChecked() == true){
                    listaDescriptores.add("Ansioso");
                }
                if(checkBoxEnergetico.isChecked() == true){
                    listaDescriptores.add("Energetico");
                }
                if(checkBoxFuerte.isChecked() == true){
                    listaDescriptores.add("Fuerte");
                }
                if(checkBoxEmpatico.isChecked() == true){
                    listaDescriptores.add("Empatico");
                }
                if(checkBoxNinos.isChecked() == true){
                    listaDescriptores.add("Ninos");
                }
                if(checkBoxDestructivo.isChecked() == true){
                    listaDescriptores.add("Destructivo");
                }
                if(checkBoxAgresivo.isChecked() == true){
                    listaDescriptores.add("Agresivo");
                }
                if(checkBoxAmoroso.isChecked() == true){
                    listaDescriptores.add("Amoroso");
                }
                if(checkBoxIndependiente.isChecked() == true){
                    listaDescriptores.add("Independiente");
                }
                if(checkBoxNervioso.isChecked() == true){
                    listaDescriptores.add("Nervioso");
                }
                if(checkBoxDominante.isChecked() == true){
                    listaDescriptores.add("Dominante");
                }
                if(checkBoxLeal.isChecked() == true){
                    listaDescriptores.add("Leal");
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
