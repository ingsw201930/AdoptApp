package com.example.adoptapp.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adoptapp.R;
import com.example.adoptapp.adapters.AdapterHistorias;
import com.example.adoptapp.adapters.AdapterInstituciones;
import com.example.adoptapp.model.Adopcion;
import com.example.adoptapp.model.Institucion;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ActivityVerHistorias extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private RecyclerView recyclerViewItems;
    private ProgressBar progressBarCargarLista;
    private TextView textViewCargando;
    private ConstraintLayout constraintLayoutSuperior;

    private static final String TAG = "Buscar historias";
    private ArrayList<Adopcion> arrayListItems;

    private AdapterHistorias mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar_historias);

        recyclerViewItems = findViewById(R.id.rv_lista_historias);
        progressBarCargarLista = findViewById(R.id.pb_lista_historias);
        textViewCargando = findViewById(R.id.tv_cargando_lista_historias);
        constraintLayoutSuperior = findViewById(R.id.cl_superior_historias);

        ConnectivityManager cm = (ConnectivityManager)ActivityVerHistorias.this.getSystemService
                (CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if(!isConnected) {
            progressBarCargarLista.setVisibility(View.GONE);
            textViewCargando.setText(R.string.AvisoNoConexion);
            Toast.makeText(ActivityVerHistorias.this, "No hay conexión a internet",
                    Toast.LENGTH_SHORT).show();
        }

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitNetwork().build();
        StrictMode.setThreadPolicy(policy);

        db = FirebaseFirestore.getInstance();

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        arrayListItems = new ArrayList<>();

        mAdapter = new AdapterHistorias(arrayListItems);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerViewItems.setLayoutManager(mLayoutManager);
        recyclerViewItems.setItemAnimator(new DefaultItemAnimator());
        //recyclerViewAnimales.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerViewItems.setAdapter(mAdapter);

        leerListaItems();
    }

    public void mostrarListaItems() {
        if (arrayListItems.size() > 0){
            //mAdapter = new AdapterAnimales(arrayAnimales);
            //customAdapter = new CustomAdapter(this, arrayAnimales);
            //listViewAnimales.setAdapter(customAdapter);
            //progressBarCargarLista.setVisibility(View.GONE);
            mAdapter = new AdapterHistorias(arrayListItems);
            recyclerViewItems.setAdapter(mAdapter);
            //mAdapter.notifyDataSetChanged();
            textViewCargando.setText("");
            constraintLayoutSuperior.setVisibility(View.GONE);
        }else{
            textViewCargando.setText(R.string.resultadosNoEncontrados);
            Toast.makeText(ActivityVerHistorias.this, "La búsqueda no " +
                    "ha encontrado resultados", Toast.LENGTH_SHORT).show();
        }
        progressBarCargarLista.setVisibility(View.GONE);
        //imageButtonFiltrar.setEnabled(true);
    }

    public void leerListaItems() {

        db.collection("adopciones")
                /*.whereEqualTo("Estado", "Espera")*/
                .orderBy("fecha", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Adopcion historia = document.toObject(Adopcion.class);
                                arrayListItems.add(historia);
                            }
                            /*arrayListReportes = new ArrayList<>(arrayListAnimales);*/
                            mostrarListaItems();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ActivityVerHistorias.this, ActivityMenuAdoptante.class);
        startActivity(intent);
        finish();
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
        Intent intent = new Intent(ActivityVerHistorias.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
