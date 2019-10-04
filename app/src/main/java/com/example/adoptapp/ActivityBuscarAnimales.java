package com.example.adoptapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar_animales);

        listViewAnimales = findViewById(R.id.listViewAnimales);

        ArrayList<Animal> arrayList = new ArrayList<>();

        Animal animal = new Animal();
        animal.setNombre("Lulu");
        animal.setEdad(4);
        animal.setTamano("Mediano");
        animal.setCiudad("Bogota");
        arrayList.add(animal);

        animal = new Animal();
        animal.setNombre("Pepe");
        animal.setEdad(5);
        animal.setTamano("Grande");
        animal.setCiudad("Bogota");
        arrayList.add(animal);

        animal = new Animal();
        animal.setNombre("Marlon");
        animal.setEdad(8);
        animal.setTamano("Pequeño");
        animal.setCiudad("Bogota");
        arrayList.add(animal);

        CustomAdapter customAdapter = new CustomAdapter(this, arrayList);
        listViewAnimales.setAdapter(customAdapter);

        //traerListaAnimales();
    }

    public void traerListaAnimales(){

        db.collection("animales")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                document.getString("Nombre");

                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}
