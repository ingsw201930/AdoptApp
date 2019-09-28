package com.example.adoptapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class ActivityMenuKeeper extends AppCompatActivity {

    ConstraintLayout constraintLayoutRegistrarAnimal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_keeper);

        constraintLayoutRegistrarAnimal = findViewById(R.id.ConstraintLayoutRegistrarAnimal);

        constraintLayoutRegistrarAnimal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ActivityRegistrarAnimal.class);
                startActivity(intent);
            }
        });
    }
}
