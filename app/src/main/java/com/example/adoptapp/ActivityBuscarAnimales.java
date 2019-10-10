package com.example.adoptapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class ActivityBuscarAnimales extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    ListView listViewAnimales;
    ProgressBar progressBarCargarLista;
    TextView textViewCargando;
    ImageButton imageButtonFiltrar;

    private static final String TAG = "Buscar animales";
    ArrayList<Animal> arrayListAnimales;
    CustomAdapter customAdapter;

    static final int FILTRO_REQUEST = 1;

    String filtroTipo;
    String filtroTamano;
    int filtroEdad;
    int filtroDistancia;
    int numeroFiltrosAplicados;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar_animales);

        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            signInAnonymously();
        }

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitNetwork().build();
        StrictMode.setThreadPolicy(policy);

        listViewAnimales = findViewById(R.id.listViewAnimales);
        imageButtonFiltrar = findViewById(R.id.imageButtonFiltrar);
        progressBarCargarLista = findViewById(R.id.progressBarListaAnimales);
        textViewCargando = findViewById(R.id.textViewCargaListaAnimales);
        imageButtonFiltrar.setEnabled(false);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            latitudActual = location.getLatitude();
                            longitudActual = location.getLongitude();
                        }
                    }
                });

        imageButtonFiltrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ActivityFiltro.class);
                startActivityForResult(intent, FILTRO_REQUEST);
            }
        });

        arrayListAnimales = new ArrayList<>();

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
        if (arrayListAnimales.size() > 0){
            customAdapter = new CustomAdapter(this, arrayListAnimales);
            listViewAnimales.setAdapter(customAdapter);
            //progressBarCargarLista.setVisibility(View.GONE);
            textViewCargando.setText("");
        }else{
            textViewCargando.setText(R.string.resultadosNoEncontrados);
            Toast.makeText(ActivityBuscarAnimales.this, "La búsqueda no " +
                    "ha encontrado resultados", Toast.LENGTH_SHORT).show();
        }
        progressBarCargarLista.setVisibility(View.GONE);
        imageButtonFiltrar.setEnabled(true);
    }

    public void leerListaAnimalesSinFiltro(){

        db.collection("animales")
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
                                animal.setCiudad( document.getString("Ciudad") );
                                animal.setUrlFotoPrincipal( document.getString("FotoPrincipal") );
                                animal.setCiudad( document.getString("MunicipioResponsable") );
                                animal.setNombreResponsable( document.getString("NombreResponsable") );
                                GeoPoint ubicacion = document.getGeoPoint("Ubicacion");
                                animal.setDistancia( calcularDistancia(latitudActual, longitudActual,
                                        ubicacion.getLatitude(),ubicacion.getLongitude()) );
                                animal.setFechaPublicacion( document.getDate("FechaPublicacion") );
                                arrayListAnimales.add(animal);

                                //Log.d(TAG, document.getId() + " => " + animal.getNombre());
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
        if (requestCode == FILTRO_REQUEST) { //se realizó solicitud de filtro
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {

                if (ContextCompat.checkSelfPermission(ActivityBuscarAnimales.this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {

                    arrayListAnimales.clear();
                    listViewAnimales.setAdapter(null);

                    textViewCargando.setText(R.string.mostrarCargando);
                    progressBarCargarLista.setVisibility(View.VISIBLE);
                    imageButtonFiltrar.setEnabled(false);

                    filtroTipo = data.getStringExtra("Tipo");
                    filtroTamano = data.getStringExtra("Tamano");
                    filtroEdad = data.getIntExtra("Edad", -1);
                    filtroDistancia = data.getIntExtra("Distancia", -1);
                    numeroFiltrosAplicados = data.getIntExtra("numeroFiltrosAplicados", 0);
                    aplicarFiltro();
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
                mFusedLocationClient.getLastLocation();
                leerListaAnimalesSinFiltro();
            } else {
                Toast.makeText(this,
                        "Sin acceso a localización, hardware deshabilitado!",
                        Toast.LENGTH_LONG).show();
                textViewCargando.setText("");
                progressBarCargarLista.setVisibility(View.GONE);
                imageButtonFiltrar.setEnabled(false);
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
                    mFusedLocationClient.getLastLocation(); //Todas las condiciones para recibir localizaciones
                    leerListaAnimalesSinFiltro();
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
                    textViewCargando.setText("");
                    progressBarCargarLista.setVisibility(View.GONE);
                    imageButtonFiltrar.setEnabled(false);
                }
            }
            break;

        }

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

}

