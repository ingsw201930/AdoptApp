package com.example.adoptapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class ActivityBuscarAnimales extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    ListView listViewAnimales;
    private static final String TAG = "Buscar animales";
    ArrayList<Animal> arrayListAnimales;
    ImageButton imageButtonFiltrar;
    CustomAdapter customAdapter;

    static final int FILTRO_REQUEST = 1;

    String filtroTipo;
    String filtroTamano;
    int filtroEdad;
    int filtroDistancia;
    int numeroFiltrosAplicados;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar_animales);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitNetwork().build();
        StrictMode.setThreadPolicy(policy);

        listViewAnimales = findViewById(R.id.listViewAnimales);
        imageButtonFiltrar = findViewById(R.id.imageButtonFiltrar);

        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            signInAnonymously();
        }

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
        animal.setUrlFotoPrincipal("");
        //arrayListAnimales.add(animal);

        animal = new Animal();
        animal.setId("");
        animal.setNombre("Pepe");
        animal.setEdad(5);
        animal.setTamano("Grande");
        animal.setCiudad("Bogota");
        animal.setUrlFotoPrincipal("");
        //arrayListAnimales.add(animal);

        animal = new Animal();
        animal.setId("");
        animal.setNombre("Marlon");
        animal.setEdad(8);
        animal.setTamano("Pequeño");
        animal.setCiudad("Bogota");
        animal.setUrlFotoPrincipal("");
        //arrayListAnimales.add(animal);

        leerListaAnimalesSinFiltro();

        /*for (int i = 0; i < arrayListAnimales.size(); i++) {
            Log.i(TAG, "Esto es :"+arrayListAnimales.get(i).getNombre());
        }*/

    }

    public void mostrarListaAnimales() {
        if (arrayListAnimales.size() > 0){
            customAdapter = new CustomAdapter(this, arrayListAnimales);
            listViewAnimales.setAdapter(customAdapter);
        }
    }

    public void leerListaAnimalesSinFiltro(){

        db.collection("animales")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                Animal animal = new Animal();
                                animal.setId(document.getId());
                                animal.setNombre( (document.getString("Nombre") ));
                                animal.setEdad( Integer.parseInt(document.get("Edad").toString()) );
                                animal.setTamano( document.getString("Tamano") );
                                animal.setCiudad( document.getString("Ciudad") );
                                animal.setUrlFotoPrincipal( document.getString("FotoPrincipal") );
                                arrayListAnimales.add(animal);

                                Log.d(TAG, document.getId() + " => " + animal.getNombre());
                            }
                            mostrarListaAnimales();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    /*public void conseguirFotosAnimales(){

        StorageReference listRef = storage.getReference().child("animales/exx9WvDpQZM11MI3Cubi");

        listRef.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        for (StorageReference item : listResult.getItems()) {
                            // All the items under listRef.
                            //String downloadUri = item.getDownloadUrl().toString();
                            item.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Log.i(TAG, uri.toString());
                                }
                            });
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Uh-oh, an error occurred!
                        Log.i(TAG, e.toString());
                    }
                });
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Check which request we're responding to
        if (requestCode == FILTRO_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {

                arrayListAnimales.clear();
                listViewAnimales.setAdapter(null);

                filtroTipo = data.getStringExtra("Tipo");
                filtroTamano = data.getStringExtra("Tamano");
                filtroEdad = data.getIntExtra("Edad", -1);
                filtroDistancia = data.getIntExtra("Distancia", -1);
                numeroFiltrosAplicados = data.getIntExtra("numeroFiltrosAplicados", 0);
                aplicarFiltro();
                //Log.i(TAG, "Parámetros de filtro: "+result);
            }
        }
    }

    private void signInAnonymously() {
        mAuth.signInAnonymously().addOnSuccessListener(this, new  OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                // do your stuff
            }
        })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.e(TAG, "signInAnonymously:FAILURE", exception);
                    }
                });
    }

    public void aplicarFiltro(){

        if (numeroFiltrosAplicados > 0) {

            if (!filtroTipo.equals("")) {
                if (numeroFiltrosAplicados == 1){ //único filtro aplicado
                    aplicarFiltroSencillo("Tipo", filtroTipo, false);
                }else{ //al menos otro filtro aplicado
                    aplicarFiltroSencillo("Tipo", filtroTipo, true);
                }
            }
            if (!filtroTamano.equals("")) {
                if (numeroFiltrosAplicados == 1){ //único filtro aplicado
                    aplicarFiltroSencillo("Tamano", filtroTamano, false);
                }else{ //al menos otro filtro aplicado
                    aplicarFiltroSencillo("Tamano", filtroTamano, true);
                }
            }
            if (filtroEdad != -1) {
                if (numeroFiltrosAplicados == 1){ //único filtro aplicado
                    aplicarFiltroSencillo("Edad", filtroEdad, false);
                }else{ //al menos otro filtro aplicado
                    aplicarFiltroSencillo("Edad", filtroEdad, true);
                }
            }
            if (filtroDistancia != -1) {
                /*if (numeroFiltrosAplicados == 1){ //único filtro aplicado
                    //aplicarFiltroSencillo("Distancia", filtroDistancia, false);
                }else{ //al menos otro filtro aplicado
                    //aplicarFiltroSencillo("Distancia", filtroDistancia, true);
                }*/
            }

        }

    }

    public void aplicarFiltroSencillo(String campoFiltro, String valorCampo, final boolean requiereFiltroSecundario){

        db.collection("animales")
                .whereEqualTo(campoFiltro, valorCampo)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                Animal animal = new Animal();
                                animal.setId(document.getId());
                                animal.setNombre( (document.getString("Nombre") ));
                                animal.setEdad( Integer.parseInt(document.get("Edad").toString()) );
                                animal.setTamano( document.getString("Tamano") );
                                animal.setCiudad( document.getString("Ciudad") );
                                animal.setUrlFotoPrincipal( document.getString("FotoPrincipal") );
                                arrayListAnimales.add(animal);

                                Log.d(TAG, document.getId() + " => " + animal.getNombre());
                            }
                            if (requiereFiltroSecundario) {
                                agregarFiltrosSecundarios();
                            } else {
                                mostrarListaAnimales();
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    public void aplicarFiltroSencillo(String campoFiltro, int valorCampo, final boolean requiereFiltroSecundario){

        db.collection("animales")
                .whereEqualTo(campoFiltro, valorCampo)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                Animal animal = new Animal();
                                animal.setId(document.getId());
                                animal.setNombre( (document.getString("Nombre") ));
                                animal.setEdad( Integer.parseInt(document.get("Edad").toString()) );
                                animal.setTamano( document.getString("Tamano") );
                                animal.setCiudad( document.getString("Ciudad") );
                                animal.setUrlFotoPrincipal( document.getString("FotoPrincipal") );
                                arrayListAnimales.add(animal);

                                Log.d(TAG, document.getId() + " => " + animal.getNombre());
                            }
                            if (requiereFiltroSecundario) {
                                agregarFiltrosSecundarios();
                            } else {
                                mostrarListaAnimales();
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    public void agregarFiltrosSecundarios(){

        ArrayList<Animal> arrayAuxiliar;
        arrayAuxiliar = new ArrayList<>();

        if (!filtroTipo.equals("")) {
            for (int i = 0; i < arrayListAnimales.size(); i++) {
                if (arrayListAnimales.get(i).getTipo().equals(filtroTipo)) {
                    arrayAuxiliar.add(arrayListAnimales.get(i));
                }
            }
            arrayListAnimales.clear();
            arrayListAnimales = new ArrayList<>(arrayAuxiliar); //copiar los del auxiliar en el original
            arrayAuxiliar.clear();
        }

        if (!filtroTamano.equals("")) {
            for (int i = 0; i < arrayListAnimales.size(); i++) {
                if (arrayListAnimales.get(i).getTamano().equals(filtroTamano)){
                    arrayAuxiliar.add(arrayListAnimales.get(i));
                }
            }
            arrayListAnimales.clear();
            arrayListAnimales = new ArrayList<>(arrayAuxiliar); //copiar los del auxiliar en el original
            arrayAuxiliar.clear();
        }
        if (filtroEdad != -1) {
            for (int i = 0; i < arrayListAnimales.size(); i++) {
                if (arrayListAnimales.get(i).getEdad() == filtroEdad){
                    arrayAuxiliar.add(arrayListAnimales.get(i));
                }
            }
            arrayListAnimales.clear();
            arrayListAnimales = new ArrayList<>(arrayAuxiliar); //copiar los del auxiliar en el original
            arrayAuxiliar.clear();
        }

        /*if (filtroDistancia != -1) {
            /*for (int i = 0; i < arrayListAnimales.size(); i++) {
                if (arrayListAnimales.get(i).getDistancia == filtroDistancia){
                    arrayAuxiliar.add(arrayListAnimales.get(i));
                }
            }
            arrayListAnimales.clear();
            arrayListAnimales = new ArrayList<>(arrayAuxiliar); //copiar los del auxiliar en el original
            arrayAuxiliar.clear();
        }*/

        arrayAuxiliar.clear();

        mostrarListaAnimales();
    }

}

