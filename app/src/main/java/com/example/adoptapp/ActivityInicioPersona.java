package com.example.adoptapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ActivityInicioPersona extends AppCompatActivity {

    ConstraintLayout constraintLayoutAdoptante;
    ConstraintLayout constraintLayoutHogarPaso;

    FirebaseAuth mAuth;
    FirebaseUser currentUser;

    //Boolean sesionCerrada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_persona);

        //sesionCerrada = false;

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        constraintLayoutAdoptante = findViewById(R.id.ConstraintLayoutAdoptante);
        constraintLayoutHogarPaso = findViewById(R.id.ConstraintLayoutHogarPaso);

        constraintLayoutAdoptante.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ActivityMenuAdoptante.class);
                startActivity(intent);
            }
        });

        constraintLayoutHogarPaso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }

    @Override
    public void onBackPressed() {
        //moveTaskToBack(true);
        Toast.makeText(this,
                "Acción no disponible",
                Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menusesion, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.MenuCerrarSesion:
                cerrarSesion();
                return true;

                default:
                    return false;
        }
    }

    private void cerrarSesion(){
        if (currentUser == null) {
            mAuth.signOut();
        }
        //sesionCerrada = true;
        Intent intent = new Intent(ActivityInicioPersona.this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //if(sesionCerrada == true){
        finish();
        //}
    }

}
