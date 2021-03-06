package com.example.adoptapp.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.adoptapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ActivityMenuKeeper extends AppCompatActivity {

    ConstraintLayout constraintLayoutRegistrarAnimal;
    ConstraintLayout constraintLayoutVerSolicitudes;
    ConstraintLayout constraintLayoutVerSolicitudesAceptadas;
    ConstraintLayout constraintLayoutRegistrarEvento;

    FirebaseAuth mAuth;
    FirebaseUser currentUser;

    //Boolean sesionCerrada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_keeper);

        //sesionCerrada = false;

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        constraintLayoutRegistrarAnimal = findViewById(R.id.ConstraintLayoutRegistrarAnimal);
        constraintLayoutVerSolicitudes = findViewById(R.id.ConstraintLayoutVerPeticiones);
        constraintLayoutVerSolicitudesAceptadas = findViewById(R.id.cl_ver_solicitudes_aceptadas);
        constraintLayoutRegistrarEvento = findViewById(R.id.cl_registrar_evento);

        constraintLayoutRegistrarAnimal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ActivityRegistrarAnimal.class);
                startActivity(intent);
                finish();
            }
        });

        constraintLayoutVerSolicitudes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ActivityVerSolicitudesInstitucion.class);
                intent.putExtra("solicitudes", "a_revisar");
                startActivity(intent);
                finish();
            }
        });

        constraintLayoutVerSolicitudesAceptadas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ActivityVerSolicitudesInstitucion.class);
                intent.putExtra("solicitudes", "aceptadas");
                startActivity(intent);
                finish();
            }
        });

        constraintLayoutRegistrarEvento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ActivityRegistrarEvento.class);
                startActivity(intent);
                finish();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_sesion, menu);
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
                return true;
        }
    }

    private void cerrarSesion(){
        if (currentUser != null) {
            mAuth.signOut();
        }
        //sesionCerrada = true;
        Intent intent = new Intent(ActivityMenuKeeper.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ActivityMenuKeeper.this, ActivityLogin.class);
        startActivity(intent);
        finish();
    }
}
