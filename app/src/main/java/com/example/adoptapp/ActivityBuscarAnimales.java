package com.example.adoptapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Map;

public class ActivityBuscarAnimales extends AppCompatActivity {

    FirebaseFirestore db;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;

    RecyclerView recyclerViewAnimales;
    ProgressBar progressBarCargarLista;
    TextView textViewCargando;
    ConstraintLayout constraintLayoutFiltro;
    //ImageButton imageButtonFiltrar;

    private static final String TAG = "Buscar animales";
    ArrayList<Animal> arrayListAnimales;
    ArrayList<Animal> arrayListAnimalesFiltrados;
    MenuInflater menuInflaterOpciones;
    //CustomAdapter customAdapter;

    private AdapterAnimales mAdapter;

    static final int FILTRO_REQUEST = 2;

    String filtroTipo;
    String filtroTamano;
    String filtroSexo;
    int filtroEdad;
    String categoriaEdad;
    double filtroDistancia;
    int numeroFiltrosAplicados;
    ArrayList<String> listaDescriptores;

    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 0;
    private static final int REQUEST_CHECK_SETTINGS = 1;

    private double latitudActual;
    private double longitudActual;

    String[] PERMISSIONS = {
            android.Manifest.permission.ACCESS_FINE_LOCATION
    };

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;

    final int RADIUS_OF_EARTH_KM = 6371; //en km

    Boolean deseoRegresar, sesionCerrada, activacionFiltro;

    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar_animales);

        deseoRegresar = false;
        sesionCerrada = false;
        activacionFiltro = false;

        recyclerViewAnimales = findViewById(R.id.RecyclerViewAnimales);
        //imageButtonFiltrar = findViewById(R.id.imageButtonFiltrar);
        progressBarCargarLista = findViewById(R.id.progressBarListaAnimales);
        textViewCargando = findViewById(R.id.textViewCargaListaAnimales);
        constraintLayoutFiltro = findViewById(R.id.ConstraintLayoutFiltro);
        activacionFiltro = false;
        //imageButtonFiltrar.setEnabled(false);

        ConnectivityManager cm = (ConnectivityManager)ActivityBuscarAnimales.this.getSystemService
                (CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if(!isConnected){
            progressBarCargarLista.setVisibility(View.GONE);
            activacionFiltro = false;
            //imageButtonFiltrar.setEnabled(false);
            textViewCargando.setText(R.string.AvisoNoConexion);
            Toast.makeText(ActivityBuscarAnimales.this, "No hay conexión a internet",
                    Toast.LENGTH_SHORT).show();
        }

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitNetwork().build();
        StrictMode.setThreadPolicy(policy);

        db = FirebaseFirestore.getInstance();

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        /*if (currentUser == null) {
            signInAnonymously();
        }*/

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mLocationRequest = createLocationRequest();

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Location location = locationResult.getLastLocation();
                Log.i("LOCATION", "Location update in the callback: " + location);
                if (location != null) {
                    latitudActual = location.getLatitude();
                    longitudActual = location.getLongitude();
                    if (arrayListAnimales.size() == 0) {
                        leerListaAnimalesSinFiltro();
                    }
                    stopLocationUpdates();
                    Log.i(TAG, latitudActual+" "+longitudActual);
                }
            }
        };

        latitudActual = 200; //inicializar con latitud que no existe
        longitudActual = 200; //inicializar con longitud que no existe

        /*mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        Log.i("LOCATION", "onSuccess location");
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            latitudActual = location.getLatitude();
                            longitudActual = location.getLongitude();
                            Log.i(TAG, String.valueOf(latitudActual)+" "+String.valueOf(longitudActual));
                        }
                    }
                });*/

        /*imageButtonFiltrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ActivityFiltro.class);
                startActivityForResult(intent, FILTRO_REQUEST);
            }
        });*/

        arrayListAnimales = new ArrayList<>();
        arrayListAnimalesFiltrados = new ArrayList<>(arrayListAnimales);

        mAdapter = new AdapterAnimales(arrayListAnimalesFiltrados);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerViewAnimales.setLayoutManager(mLayoutManager);
        recyclerViewAnimales.setItemAnimator(new DefaultItemAnimator());
        //recyclerViewAnimales.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerViewAnimales.setAdapter(mAdapter);

        recyclerViewAnimales.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext()
                , recyclerViewAnimales, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Animal animal = arrayListAnimalesFiltrados.get(position);
                /*Toast.makeText(getApplicationContext(), animal.getNombre() + " is selected!",
                        Toast.LENGTH_SHORT).show();*/
                Intent intent = new Intent(view.getContext(), ActivityPerfilAnimal.class);
                intent.putExtra("Nombre", animal.getNombre());
                intent.putExtra("Foto_principal", animal.getUrlFotoPrincipal());

                String fechaPublicacion = new SimpleDateFormat("dd/MM/yyyy").
                        format(animal.getFechaPublicacion());
                String edad;
                if (animal.getEdad()<12){
                    edad = animal.getEdad()+" meses";
                }else{
                    if (animal.getEdad()==12) {
                        edad = "1 año";
                    }else{ //animal.getEdad()>12
                        if( animal.getEdad() % 12 == 0 ){ //al dividir entre 12 no hay residuo
                            edad = (animal.getEdad())/12+" años";
                        }else{ //al dividir entre 12 hay residuo
                            String anio;
                            String mes;
                            int anioNumero = (int)(Math.floor( (animal.getEdad())/12 ));
                            int mesNumero = (int)(animal.getEdad() -
                                    12*(Math.floor( (animal.getEdad())/12 )) );
                            if( anioNumero == 1 ){
                                anio = " año y ";
                            }else{
                                anio = " años y ";
                            }
                            if( mesNumero == 1 ){
                                mes = " mes";
                            }else{
                                mes = " meses";
                            }
                            edad = anioNumero + anio + mesNumero + mes;
                        }
                    }
                }
                String datosAnimal = animal.getSexo()+"\n"+animal.getTamano()+"\n"+edad+"\nEn "+animal.getCiudad()
                        +"\nA "+animal.getDistancia()+" km de ti"+
                        "\nEsperando hogar desde: "+fechaPublicacion
                        +"\nResponsable: "+animal.getNombreResponsable();

                intent.putExtra("Descripcion", datosAnimal);

                intent.putStringArrayListExtra("descriptores", animal.getDescriptores());

                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        //leerListaAnimalesSinFiltro();

        /*for (int i = 0; i < arrayListAnimales.size(); i++) {
            Log.i(TAG, "Esto es :"+arrayListAnimales.get(i).getNombre());
        }*/

        if (ContextCompat.checkSelfPermission(ActivityBuscarAnimales.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            revisarActivacionGPS();
        }else{
            requestPermission(ActivityBuscarAnimales.this, PERMISSIONS[0], "Acceso " +
                    "a localización necesario para listar animales", MY_PERMISSIONS_REQUEST_LOCATION);
        }
    }

    public void mostrarListaAnimales() {
        if (arrayListAnimalesFiltrados.size() > 0){
            //mAdapter = new AdapterAnimales(arrayAnimales);
            //customAdapter = new CustomAdapter(this, arrayAnimales);
            //listViewAnimales.setAdapter(customAdapter);
            //progressBarCargarLista.setVisibility(View.GONE);
            mAdapter = new AdapterAnimales(arrayListAnimalesFiltrados);
            recyclerViewAnimales.setAdapter(mAdapter);
            //mAdapter.notifyDataSetChanged();
            textViewCargando.setText("");
            constraintLayoutFiltro.setVisibility(View.GONE);
        }else{
            textViewCargando.setText(R.string.resultadosNoEncontrados);
            Toast.makeText(ActivityBuscarAnimales.this, "La búsqueda no " +
                    "ha encontrado resultados", Toast.LENGTH_SHORT).show();
        }
        progressBarCargarLista.setVisibility(View.GONE);
        //imageButtonFiltrar.setEnabled(true);
        activacionFiltro = true;
    }

    public void leerListaAnimalesSinFiltro(){

        db.collection("animales")
                .whereEqualTo("Estado", "Espera")
                .orderBy("FechaPublicacion", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                Animal animal = new Animal();
                                animal.setId(document.getId());
                                animal.setNombre( document.getString("Nombre") );
                                animal.setTipo( document.getString("Tipo") );
                                animal.setEdad( Integer.parseInt(document.get("Edad").toString()) );
                                animal.setTamano( document.getString("Tamano") );
                                animal.setSexo( document.getString("Sexo") );
                                animal.setCiudad( document.getString("Ciudad") );
                                animal.setUrlFotoPrincipal( document.getString("FotoPrincipal") );
                                animal.setCiudad( document.getString("MunicipioResponsable") );
                                animal.setNombreResponsable( document.getString("NombreResponsable") );
                                GeoPoint ubicacion = document.getGeoPoint("Ubicacion");
                                animal.setDistancia( calcularDistancia(latitudActual, longitudActual,
                                        ubicacion.getLatitude(),ubicacion.getLongitude()) );
                                animal.setFechaPublicacion( document.getDate("FechaPublicacion") );

                                ArrayList<String> descriptores = new ArrayList<>();

                                //retrieve descriptores
                                if (document.getBoolean("dAlegre")){
                                    descriptores.add("Alegre");
                                }
                                if (document.getBoolean("dCalmado")){
                                    descriptores.add("Calmado");
                                }
                                if (document.getBoolean("dJugueton")){
                                    descriptores.add("Jugueton");
                                }
                                if (document.getBoolean("dComelon")){
                                    descriptores.add("Comelon");
                                }
                                if (document.getBoolean("dTimido")){
                                    descriptores.add("Timido");
                                }
                                if (document.getBoolean("dAnsioso")){
                                    descriptores.add("Ansioso");
                                }
                                if (document.getBoolean("dEnergetico")){
                                    descriptores.add("Energetico");
                                }
                                if (document.getBoolean("dFuerte")){
                                    descriptores.add("Fuerte");
                                }
                                if (document.getBoolean("dEmpatico")){
                                    descriptores.add("Empatico");
                                }
                                if (document.getBoolean("dDestructivo")){
                                    descriptores.add("Destructivo");
                                }
                                if (document.getBoolean("dAgresivo")){
                                    descriptores.add("Agresivo");
                                }
                                if (document.getBoolean("dAmoroso")){
                                    descriptores.add("Amoroso");
                                }
                                if (document.getBoolean("dIndependiente")){
                                    descriptores.add("Independiente");
                                }
                                if (document.getBoolean("dNervioso")){
                                    descriptores.add("Nervioso");
                                }
                                if (document.getBoolean("dDominante")){
                                    descriptores.add("Dominante");
                                }
                                if (document.getBoolean("dLeal")){
                                    descriptores.add("Leal");
                                }

                                if (document.getBoolean("dNecesidades")){
                                    descriptores.add("Necesidades");
                                }

                                animal.setDescriptores(descriptores);
                                arrayListAnimales.add(animal);

                                //Log.d(TAG, document.getId() + " => " + animal.getNombre());
                            }
                            arrayListAnimalesFiltrados = new ArrayList<>(arrayListAnimales);
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
        if (requestCode == FILTRO_REQUEST) { //se realizó solicitud de filtro
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {

                if (ContextCompat.checkSelfPermission(ActivityBuscarAnimales.this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {

                    //arrayListAnimales.clear();
                    recyclerViewAnimales.setAdapter(null);
                    constraintLayoutFiltro.setVisibility(View.VISIBLE);
                    textViewCargando.setText(R.string.mostrarCargando);
                    progressBarCargarLista.setVisibility(View.VISIBLE);
                    //imageButtonFiltrar.setEnabled(false);
                    activacionFiltro = false;

                    filtroTipo = data.getStringExtra("Tipo");
                    filtroTamano = data.getStringExtra("Tamano");
                    filtroSexo = data.getStringExtra("Sexo");
                    filtroEdad = data.getIntExtra("Edad", -1);
                    categoriaEdad = data.getStringExtra("CategoriaEdad");
                    filtroDistancia = data.getDoubleExtra("Distancia", -1.0);
                    numeroFiltrosAplicados = data.getIntExtra("numeroFiltrosAplicados", 0);
                    listaDescriptores = new ArrayList<>(data.getStringArrayListExtra("listaDescriptores"));

                    //aplicarFiltro();
                    aplicarFiltros();
                    //Log.i(TAG, "Parámetros de filtro: "+result);

                }else{
                    Toast.makeText(this,
                            "Sin acceso a localización, el filtrado no pudo realizarse!",
                            Toast.LENGTH_LONG).show();
                    requestPermission(ActivityBuscarAnimales.this, PERMISSIONS[0], "Acceso " +
                            "a localización necesario para listar animales", MY_PERMISSIONS_REQUEST_LOCATION);
                }
            }
        }
        if (requestCode == REQUEST_CHECK_SETTINGS) { //se obtuvo acceso a hardware para localización
            if (resultCode == RESULT_OK) {
                //mFusedLocationClient.getLastLocation();
                startLocationUpdates();
            } else {
                Toast.makeText(this,
                        "Sin acceso a localización, hardware deshabilitado!",
                        Toast.LENGTH_LONG).show();
                constraintLayoutFiltro.setVisibility(View.GONE);
                textViewCargando.setText("");
                progressBarCargarLista.setVisibility(View.GONE);
                //imageButtonFiltrar.setEnabled(false);
                activacionFiltro = false;
            }
        }
    }

    /*private void signInAnonymously() {
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
    }*/

    /*public void aplicarFiltro(){

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
                }
            }

        }

    }*/

    /*public void aplicarFiltroSencillo(String campoFiltro, String valorCampo, final boolean requiereFiltroSecundario){

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
                                animal.setNombre( document.getString("Nombre") );
                                animal.setTipo( document.getString("Tipo") );
                                animal.setEdad( Integer.parseInt(document.get("Edad").toString()) );
                                animal.setTamano( document.getString("Tamano") );
                                animal.setCiudad( document.getString("Ciudad") );
                                animal.setUrlFotoPrincipal( document.getString("FotoPrincipal") );
                                arrayListAnimales.add(animal);

                                //Log.d(TAG, document.getId() + " => " + animal.getNombre());
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
                                animal.setNombre( document.getString("Nombre") );
                                animal.setTipo( document.getString("Tipo") );
                                animal.setEdad( Integer.parseInt(document.get("Edad").toString()) );
                                animal.setTamano( document.getString("Tamano") );
                                animal.setCiudad( document.getString("Ciudad") );
                                animal.setUrlFotoPrincipal( document.getString("FotoPrincipal") );
                                arrayListAnimales.add(animal);

                                //Log.d(TAG, document.getId() + " => " + animal.getNombre());
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

    }*/

    public void aplicarFiltros(){

        ArrayList<Animal> arrayAuxiliar1, arrayAuxiliar2;
        arrayAuxiliar1 = new ArrayList<>(arrayListAnimales);
        arrayAuxiliar2 = new ArrayList<>();

        if (!filtroTipo.equals("")) {
            for (int i = 0; i < arrayAuxiliar1.size(); i++) {
                if (arrayAuxiliar1.get(i).getTipo().equals(filtroTipo)) {
                    arrayAuxiliar2.add(arrayAuxiliar1.get(i));
                }
            }
            arrayAuxiliar1 = new ArrayList<>(arrayAuxiliar2);
            arrayAuxiliar2.clear();
        }

        if (!filtroTamano.equals("")) {
            for (int i = 0; i < arrayAuxiliar1.size(); i++) {
                if (arrayAuxiliar1.get(i).getTamano().equals(filtroTamano)){
                    arrayAuxiliar2.add(arrayAuxiliar1.get(i));
                }
            }
            arrayAuxiliar1 = new ArrayList<>(arrayAuxiliar2);
            arrayAuxiliar2.clear();
        }

        if (!filtroSexo.equals("")) {
            for (int i = 0; i < arrayAuxiliar1.size(); i++) {
                if (arrayAuxiliar1.get(i).getSexo().equals(filtroSexo)){
                    arrayAuxiliar2.add(arrayAuxiliar1.get(i));
                }
            }
            arrayAuxiliar1 = new ArrayList<>(arrayAuxiliar2);
            arrayAuxiliar2.clear();
        }

        if (filtroEdad != -1) {
            for (int i = 0; i < arrayAuxiliar1.size(); i++) {
                if(categoriaEdad.equals("Años")){
                    if ( (arrayAuxiliar1.get(i).getEdad())/12 == filtroEdad){
                        arrayAuxiliar2.add(arrayAuxiliar1.get(i));
                    }
                }else{ //en meses
                    if (arrayAuxiliar1.get(i).getEdad() == filtroEdad){
                        arrayAuxiliar2.add(arrayAuxiliar1.get(i));
                    }
                }
            }
            arrayAuxiliar1 = new ArrayList<>(arrayAuxiliar2);
            arrayAuxiliar2.clear();
        }

        if (filtroDistancia != -1.0) {
            for (int i = 0; i < arrayAuxiliar1.size(); i++) {
                if (arrayAuxiliar1.get(i).getDistancia() <= filtroDistancia ){
                    arrayAuxiliar2.add(arrayAuxiliar1.get(i));
                }
            }
            arrayAuxiliar1 = new ArrayList<>(arrayAuxiliar2);
            arrayAuxiliar2.clear();
        }

        if(listaDescriptores.size()>0) {

            ArrayList<String> descriptores;
            String descriptor;
            boolean cumpleConDescriptores;

            for (int i = 0; i < arrayAuxiliar1.size(); i++) {

                descriptores = new ArrayList<>(arrayAuxiliar1.get(i).getDescriptores());
                cumpleConDescriptores = true;

                for (int j = 0; j < listaDescriptores.size(); j++) {

                    Log.i(TAG, "Lista descriptores :" + listaDescriptores.get(j));
                    descriptor = listaDescriptores.get(j);

                    if (!descriptores.contains(descriptor)) { //si el animal no tiene ese descriptor
                        cumpleConDescriptores = false;
                        break;
                    }

                }

                if (cumpleConDescriptores) {
                    arrayAuxiliar2.add(arrayAuxiliar1.get(i));
                }
            }

            arrayAuxiliar1 = new ArrayList<>(arrayAuxiliar2);
            arrayAuxiliar2.clear();
        }

        /*for (int i = 0; i < arrayAuxiliar.size(); i++) {
            Log.i(TAG, "Esto es :"+arrayAuxiliar.get(i).getNombre());
        }*/

        arrayListAnimalesFiltrados = new ArrayList<>(arrayAuxiliar1);

        mostrarListaAnimales();
    }

    private void requestPermission(Activity context, String permiso, String justificacion, int idCode){
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(context, permiso) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            if (ActivityCompat.shouldShowRequestPermissionRationale(context, permiso)) {
                Toast toast = Toast.makeText(context, justificacion, Toast.LENGTH_SHORT);
                toast.show();
            }
            //Request the permission
            ActivityCompat.requestPermissions(context, new String[]{permiso}, idCode);
        }
    }

    protected LocationRequest createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000); //tasa de refresco en milisegundos
        mLocationRequest.setFastestInterval(1000); //máxima tasa de refresco
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return mLocationRequest;
    }

    private void revisarActivacionGPS() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            LocationSettingsRequest.Builder builder = new
                    LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
            SettingsClient client = LocationServices.getSettingsClient(this);
            Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

            task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                @Override
                public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                    //mFusedLocationClient.getLastLocation(); //Todas las condiciones para recibir localizaciones
                    startLocationUpdates();
                }
            });

            task.addOnFailureListener(this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    int statusCode = ((ApiException) e).getStatusCode();
                    switch (statusCode) {
                        case CommonStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied, but this can be fixed by showing the user a dialog.
                            try {// Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                ResolvableApiException resolvable = (ResolvableApiException) e;
                                resolvable.startResolutionForResult(ActivityBuscarAnimales.this,
                                        REQUEST_CHECK_SETTINGS);
                            } catch (IntentSender.SendIntentException sendEx) {
                                // Ignore the error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. No way to fix the settings so we won't show the dialog.
                            break;
                    }
                }
            });

        } else {
            Toast.makeText(this,
                    "Sin acceso a localización, permiso denegado!",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        switch (requestCode) {

            case MY_PERMISSIONS_REQUEST_LOCATION:
            {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, continue with task related to permission
                    revisarActivacionGPS();
                }else{
                    Toast.makeText(this,
                            "Sin acceso a localización, permiso denegado!",
                            Toast.LENGTH_LONG).show();
                    constraintLayoutFiltro.setVisibility(View.GONE);
                    textViewCargando.setText("");
                    progressBarCargarLista.setVisibility(View.GONE);
                    //imageButtonFiltrar.setEnabled(false);
                    activacionFiltro = false;
                }
            }
            break;

        }

    }

    private void startLocationUpdates() {
        //Verificación de permiso!!
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
        if (deseoRegresar == true){
            finish();
        }
        if (sesionCerrada == true){
            finish();
        }
    }

    private void stopLocationUpdates(){
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    public double calcularDistancia(double lat1, double long1, double lat2, double long2) {
        double latDistance = Math.toRadians(lat1 - lat2);
        double lngDistance = Math.toRadians(long1 - long2);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double result = RADIUS_OF_EARTH_KM * c;
        return Math.round(result*100.0)/100.0;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_filtro, menu);
        menuInflaterOpciones = inflater;
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.cerrarSesionMenu:
                cerrarSesion();
                return true;
            case R.id.mostrarOpcionesFiltroMenu:
                if (activacionFiltro) {
                    Intent intent = new Intent(ActivityBuscarAnimales.this, ActivityFiltro.class);
                    startActivityForResult(intent, FILTRO_REQUEST);
                }/*else{
                    Toast toast = Toast.makeText(ActivityBuscarAnimales.this, "No se puede " +
                            "filtrar porque no hay animales disponibles", Toast.LENGTH_LONG);
                    toast.show();
                }*/
                return true;
            default:
                return true;
        }
    }

    private void cerrarSesion(){
        if (currentUser == null) {
            mAuth.signOut();
        }
        sesionCerrada = true;
        Intent intent = new Intent(ActivityBuscarAnimales.this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        deseoRegresar = true;
        Intent intent = new Intent(ActivityBuscarAnimales.this, ActivityMenuAdoptante.class);
        startActivity(intent);
    }

}

