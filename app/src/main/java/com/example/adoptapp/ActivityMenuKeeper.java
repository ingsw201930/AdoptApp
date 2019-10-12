package com.example.adoptapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ActivityMenuKeeper extends AppCompatActivity {

    ConstraintLayout constraintLayoutRegistrarAnimal;
    ConstraintLayout constraintLayoutCerrarSesion;

    FirebaseAuth mAuth;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_keeper);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        constraintLayoutRegistrarAnimal = findViewById(R.id.ConstraintLayoutRegistrarAnimal);
        constraintLayoutCerrarSesion = findViewById(R.id.ConstraintLayoutCerrarSesionKeeper);

        constraintLayoutRegistrarAnimal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ActivityRegistrarAnimal.class);
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
