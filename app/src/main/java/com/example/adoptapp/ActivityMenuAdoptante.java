package com.example.adoptapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class ActivityMenuAdoptante extends AppCompatActivity {

    ConstraintLayout constraintLayoutBuscarAnimales;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_adoptante);

        constraintLayoutBuscarAnimales = findViewById(R.id.ConstraintLayoutBuscarAnimales);

        constraintLayoutBuscarAnimales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ActivityBuscarAnimales.class);
                startActivity(intent);
            }
        });
    }
}
