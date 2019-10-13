package com.example.adoptapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ActivityMenuAdoptante extends AppCompatActivity {

    ConstraintLayout constraintLayoutBuscarAnimales;
    ConstraintLayout constraintLayoutCerrarSesion;

    FirebaseAuth mAuth;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_adoptante);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        constraintLayoutBuscarAnimales = findViewById(R.id.ConstraintLayoutBuscarAnimales);
        constraintLayoutCerrarSesion = findViewById(R.id.ConstraintLayoutCerrarSesion);

        constraintLayoutBuscarAnimales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ActivityBuscarAnimales.class);
                startActivity(intent);
            }
        });

        constraintLayoutCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
            }
        });
    }
}
