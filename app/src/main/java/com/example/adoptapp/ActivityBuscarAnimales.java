package com.example.adoptapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ActivityBuscarAnimales extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ListView listViewAnimales;
    private static final String TAG = "Buscar animales";
    ArrayList<Animal> arrayListAnimales;
    ImageButton imageButtonFiltrar;

    static final int FILTRO_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar_animales);

        listViewAnimales = findViewById(R.id.listViewAnimales);
        imageButtonFiltrar = findViewById(R.id.imageButtonFiltrar);

        imageButtonFiltrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ActivityFiltro.class);
                startActivityForResult(intent, FILTRO_REQUEST);
            }
        });

        arrayListAnimales = new ArrayList<>();

        Animal animal = new Animal();
        animal.setId("");
        animal.setNombre("Lulu");
        animal.setEdad(4);
        animal.setTamano("Mediano");
        animal.setCiudad("Bogota");
        //arrayListAnimales.add(animal);

        animal = new Animal();
        animal.setId("");
        animal.setNombre("Pepe");
        animal.setEdad(5);
        animal.setTamano("Grande");
        animal.setCiudad("Bogota");
        arrayListAnimales.add(animal);

        animal = new Animal();
        animal.setId("");
        animal.setNombre("Marlon");
        animal.setEdad(8);
        animal.setTamano("Pequeño");
        animal.setCiudad("Bogota");
        arrayListAnimales.add(animal);

        //traerListaAnimales();

        for (int i = 0; i < arrayListAnimales.size(); i++) {
            Log.i(TAG, "Esto es el nombre:"+arrayListAnimales.get(i).getNombre());
        }

        CustomAdapter customAdapter = new CustomAdapter(this, arrayListAnimales);
        listViewAnimales.setAdapter(customAdapter);
    }

    public void traerListaAnimales(){

        db.collection("animales")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                Animal animal = new Animal();
                                animal.setId(document.getId());
                                animal.setNombre( (String)document.get("Nombre") );
                                animal.setEdad( Integer.parseInt(document.get("Edad").toString()) );
                                animal.setTamano( (String)document.get("Tamano") );
                                animal.setCiudad( (String)document.get("Ciudad") );
                                arrayListAnimales.add(animal);

                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Check which request we're responding to
        if (requestCode == FILTRO_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                String result=data.getStringExtra("result");
                if (result != null) {
                    Log.i(TAG, result);
                }
            }
        }
    }
}
