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

public class ActivityMenuAdoptante extends AppCompatActivity {

    ConstraintLayout constraintLayoutBuscarAnimales;

    FirebaseAuth mAuth;
    FirebaseUser currentUser;

    //Boolean sesionCerrada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_adoptante);

        //sesionCerrada = false;

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        constraintLayoutBuscarAnimales = findViewById(R.id.ConstraintLayoutBuscarAnimales);

        constraintLayoutBuscarAnimales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ActivityBuscarAnimales.class);
                startActivity(intent);
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
        if (currentUser == null) {
            mAuth.signOut();
        }
        //sesionCerrada = true;
        Intent intent = new Intent(ActivityMenuAdoptante.this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ActivityMenuAdoptante.this, ActivityInicioPersona.class);
        startActivity(intent);
    }

}
